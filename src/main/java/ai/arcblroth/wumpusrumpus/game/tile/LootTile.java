package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import org.jgrapht.Graph;

import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class LootTile extends GameTile {

	public LootTile(Coordinate c) {
		super(c);
	}

	@Override
	public void onPlayerStep(WumpusPlayer wp, Graph map) {

	}

	@Override
	public char render() {
		return strings_config.getProperty("map.loot").charAt(0);
	}

}
