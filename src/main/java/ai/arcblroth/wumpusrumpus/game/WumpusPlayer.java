package ai.arcblroth.wumpusrumpus.game;

import java.util.*;

import ai.arcblroth.wumpusrumpus.game.tile.*;


public class WumpusPlayer {

	private String player_id;
	private Coordinate c;
	private ArrayList<RackTile> rackTilesSaved = new ArrayList<RackTile>();
	private ArrayList<RouterTile> routerTilesSaved = new ArrayList<RouterTile>();
  //private ArrayList<Loot> playerLoot = new ArrayList<Loot>();
	
	public WumpusPlayer(String player_id, Coordinate c) {
		this.player_id = player_id;
		this.c = c;
	}

	public String getPlayer_id() {
		return player_id;
	}

	public Coordinate getCoordinate() {
		return c;
	}
	
}
