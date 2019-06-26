package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;
import org.jgrapht.Graph;

import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class BugTile extends GameTile {

	public BugTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusPlayer wp, Graph map) {

	}

	@Override
	public char render() {
		//return "\u1f41b".charAt(0);
		return strings_config.getProperty("map.bug").charAt(0);
	}

}
