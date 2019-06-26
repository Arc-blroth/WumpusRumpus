package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import java.util.Map;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class EmptyTile extends GameTile {

	public EmptyTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, Map<Integer, GameTile> map) {
		wrc.postWrappedMessage(channel_id, 
				strings_config.getProperty("game.tile.onstep.empty")
		);
	}

	@Override
	public char render() {
		return "\u00A0".charAt(0);
	}

}
