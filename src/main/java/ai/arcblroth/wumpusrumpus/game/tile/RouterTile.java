package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import org.jgrapht.Graph;

import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class RouterTile extends GameTile {

	public RouterTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusPlayer wp, Graph map) {

	}

	@Override
	public char render() {
		//return "\u1f4e1".charAt(0);
		return strings_config.getProperty("map.router").charAt(0);
	}

}
