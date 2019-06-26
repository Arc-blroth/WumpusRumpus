/* 
 * (c) Copyright 2019, Andrew Kuai
 * 
 * Portions of the graph generation in this class' constructor
 * were derived from the GridGraphGenerator in JGraphT.
 * The original file may be found at 
 * https://github.com/jgrapht/jgrapht/blob/master/jgrapht-core/src/main/java/org/jgrapht/generate/GridGraphGenerator.java
 * 
 * The original license is included below.
 * 
 * ===========================================================
 * 
 * (C) Copyright 2011-2018, by Assaf Mizrachi and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 * 
 */

package ai.arcblroth.wumpusrumpus.game;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ai.arcblroth.wumpusrumpus.game.tile.*;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

public class GameMap {
	
	private final Random r = new Random();
	private final int w, h;
	private Map<Integer, GameTile> map;
	private SimpleGraph<GameTile, DefaultEdge> graph;
	
	private static final String ROOM = // This variable isn't used,
			  "+---+\n" + 			   // just here for example.
			"| {char} |\n" + 
			  "|   |\n" + 
			  "+---+";

	public GameMap(int w, int h) {
		this.w = w;
		this.h = h;
		this.graph = new SimpleGraph<GameTile, DefaultEdge>(DefaultEdge.class);

		// Add all the rooms

		map = new TreeMap<Integer, GameTile>();

		for (int i = 0; i < w * h; i++) {
			GameTile vertex = generateNewGameTile(i % h, Math.floorDiv(i, h));
			graph.addVertex(vertex);
			map.put(i + 1, vertex);
		}

		for (int i : map.keySet()) {
			for (int j : map.keySet()) {
				if ((((i % h) > 0) && ((i + 1) == j)) || ((i + h) == j)) {
					graph.addEdge(map.get(i), map.get(j));
					graph.addEdge(map.get(j), map.get(i));
				}
			}
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
	
	public String renderMapToAscii(ArrayList<WumpusPlayer> players) {

		String cr = strings_config.getProperty("map.corner");
		String wl = strings_config.getProperty("map.wall");

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
					char c = '@';
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
		out.append("\n" + strings_config.getProperty("map.legend"));

		return out.toString().trim();
	}
	
}
