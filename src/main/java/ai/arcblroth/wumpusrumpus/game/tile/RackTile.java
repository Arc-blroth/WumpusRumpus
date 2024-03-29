package ai.arcblroth.wumpusrumpus.game.tile;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.strings_config;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.Coordinate;
import ai.arcblroth.wumpusrumpus.game.GameMap;
import ai.arcblroth.wumpusrumpus.game.WumpusPlayer;

public class RackTile extends GameTile {

	private boolean saved = false;

	public RackTile(Coordinate c) {
		super(c);
	}

	@Override
	public int onPlayerStep(WumpusRumpusClient wrc, String channel_id, WumpusPlayer wp, GameMap map) {
		if (!saved) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.rack"));
			return 1;
		} else {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.savedrack"));
			return 0;
		}
	}

	public void saved() {
		this.saved = true;
	}

	@Override
	public char render() {
		//return "\u2593".charAt(0);
		return saved 
				? strings_config.getProperty("game.map.rack.saved").charAt(0)
				: strings_config.getProperty("game.map.rack.unsaved").charAt(0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RackTile other = (RackTile) obj;
		if (saved != other.saved)
			return false;
		if (getCoordinate() != other.getCoordinate())
			return false;
		return true;
	}

}
