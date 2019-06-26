package ai.arcblroth.wumpusrumpus.game.tile;

import org.jgrapht.Graph;

import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public abstract class GameTile {
	
	private Coordinate c;

	public GameTile(Coordinate c) {
		this.c = c;
	}

	public abstract char render();

	public abstract void onPlayerStep(WumpusPlayer wp, Graph map);

	public Coordinate getCoordinate() {
		return c;
	}

}
