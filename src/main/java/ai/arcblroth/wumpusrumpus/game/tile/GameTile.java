package ai.arcblroth.wumpusrumpus.game.tile;

import java.util.Map;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public abstract class GameTile {
	
	private Coordinate c;

	public GameTile(Coordinate c) {
		this.c = c;
	}

	public abstract char render();

	public abstract void onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, Map<Integer, GameTile> map);

	public Coordinate getCoordinate() {
		return c;
	}

}
