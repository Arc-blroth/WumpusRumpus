package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import java.util.Map;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class HamsterTile extends GameTile {

	public HamsterTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, Map<Integer, GameTile> map) {

	}

	@Override
	public char render() {
		// return "\u1f439".charAt(0);
		return strings_config.getProperty("game.map.hamster").charAt(0);
	}

}
