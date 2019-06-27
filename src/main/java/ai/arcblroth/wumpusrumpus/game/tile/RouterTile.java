package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import java.util.Map;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class RouterTile extends GameTile {
	
	private boolean saved = false;
	
	public RouterTile(Coordinate c) {
		super(c);
	}

	@Override
	public int onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, Map<Integer, GameTile> map) {
		if (!saved) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.router"));
			return 2;
		} else {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.savedrouter"));
			return 0;
		}
	}

	public void saved() {
		this.saved = true;
	}

	@Override
	public char render() {
		//return "\u1f4e1".charAt(0);
		return saved 
				? strings_config.getProperty("game.map.router.saved").charAt(0)
				: strings_config.getProperty("game.map.router.unsaved").charAt(0);
	}

}
