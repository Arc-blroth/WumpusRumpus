package ai.arcblroth.wumpusrumpus.game.tile;


import org.jgrapht.Graph;

import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class EmptyTile extends GameTile {

	public EmptyTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusPlayer wp, Graph map) {

	}

	@Override
	public char render() {
		return "\u00A0".charAt(0);
	}

}
