package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.GameMap;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class LootTile extends GameTile {

	public LootTile(Coordinate c) {
		super(c);
	}

	@Override
	public int onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, GameMap map) {
		wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.loot"));
		// TODO: add loot
		return 0;
	}

	@Override
	public char render() {
		return strings_config.getProperty("game.map.loot").charAt(0);
	}

}
