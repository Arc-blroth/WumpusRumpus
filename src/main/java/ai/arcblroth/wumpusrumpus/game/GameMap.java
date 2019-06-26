package ai.arcblroth.wumpusrumpus.game;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import ai.arcblroth.wumpusrumpus.game.tile.*;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

public class GameMap {
	
	private final Random r = new Random();
	private final int w, h;
	private Map<Integer, GameTile> map;
	
	private static final String ROOM = // This variable isn't used,
			  "+---+\n" + 			   // just here for example.
			"| {char} |\n" + 
			  "+---+";

	public GameMap(int w, int h) {
		this.w = w;
		this.h = h;

		// Add all the rooms

		map = new TreeMap<Integer, GameTile>();

		for (int i = 0; i < w * h; i++) {
			GameTile vertex = generateNewGameTile(i % h, Math.floorDiv(i, h));
			map.put(i, vertex);
		}

	}

	private GameTile generateNewGameTile(int x, int y) {
		
		/*  Probablilites for GameTiles:
		 *  40% - Racks
		 *  30% - Nothing
		 *  10% - Bugs
		 *  10% - Hamsters
		 *  5% - Routers
		 *  5% - Loot
		 */
		
		Coordinate coord = new Coordinate(x, y);
		
		int random = r.nextInt(20);
		if (random < 8) {
			return new RackTile(coord);
		} else if (random < 14) {
			return new EmptyTile(coord);
		} else if (random < 16) {
			return new BugTile(coord);
		} else if (random < 18) {
			return new HamsterTile(coord);
		} else if (random < 19) {
			return new RouterTile(coord);
		} else {
			return new LootTile(coord);
		}
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}
	
	public Map<Integer, GameTile> getMap() {
		return map;
	}

	public String renderMapToAscii(ArrayList<WumpusPlayer> players, int currentPlayerTurn) {

		String cr = strings_config.getProperty("game.map.corner");
		String wl = strings_config.getProperty("game.map.wall");

		ArrayList<Coordinate> player_coords = new ArrayList<Coordinate>();
		for (WumpusPlayer p : players) {
			player_coords.add(p.getCoordinate());
		}

		StringBuilder out = new StringBuilder();
		for(int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				out.append(cr + "---");
			}
			out.append(cr + "\n");
			for (int x = 0; x < w; x++) {
				if (player_coords.contains(new Coordinate(x, y))) {
					char c = strings_config.getProperty("game.map.player").charAt(0);
					if (players.get(currentPlayerTurn).getCoordinate().equals(new Coordinate(x, y))) {
						c = strings_config.getProperty("game.map.currentplayer").charAt(0);
					}
					out.append(wl + " ");
					out.append(c);
					out.append(" ");
				} else {
					char c = map.get(new Integer(x + y * w)).render();
					out.append(wl + " ");
					out.append(c);
					out.append(" ");
				}
			}
			out.append(wl + "\n");
		}
		for (int x = 0; x < w; x++) {
			out.append(cr + "---");
		}
		out.append(cr + "\n");
		out.append("\n" + strings_config.getProperty("game.map.legend"));

		return out.toString().trim();
	}
	
}
