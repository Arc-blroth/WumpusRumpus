package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import org.jgrapht.Graph;

import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class RackTile extends GameTile {

	public RackTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusPlayer wp, Graph map) {

	}

	@Override
	public char render() {
		//return "\u2593".charAt(0);
		return strings_config.getProperty("map.rack").charAt(0);
	}

}
