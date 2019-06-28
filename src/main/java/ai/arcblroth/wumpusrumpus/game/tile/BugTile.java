package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.GameMap;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class BugTile extends GameTile {

	public BugTile(Coordinate c) {
		super(c);
	}

	@Override
	public int onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, GameMap map) {
		wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.bug"));
		// TODO: battle!
		return 3;
	}

	@Override
	public char render() {
		//return "\u1f41b".charAt(0);
		return strings_config.getProperty("game.map.bug").charAt(0);
	}

}
