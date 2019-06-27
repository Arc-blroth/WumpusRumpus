package ai.arcblroth.wumpusrumpus.game;

import java.util.*;

import ai.arcblroth.wumpusrumpus.game.tile.*;


public class WumpusPlayer {

	private String player_id;
	private Coordinate c;
	private static final double server_save_chance = 0.5;
	private static final double bug_defeat_chance = 0.6;
	private ArrayList<RackTile> rackTilesSaved = new ArrayList<RackTile>();
	private ArrayList<RouterTile> routerTilesSaved = new ArrayList<RouterTile>();
	private ArrayList<Loot> playerLoot = new ArrayList<Loot>();
	private int hamstersSaved = 0;

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

	public double getServerSaveChance() {
		double chance = server_save_chance;
		for (Loot l : playerLoot) {
			chance *= l.getSaveModifer();
		}
		chance = Math.max(Math.min(chance, 1.0D), 0.0D);
		return chance;
	}

	public double getBugDefeatChance() {
		double chance = bug_defeat_chance;
		for (Loot l : playerLoot) {
			chance *= l.getDefeatModifer();
		}
		chance = Math.max(Math.min(chance, 1.0D), 0.0D);
		return chance;
	}

	public ArrayList<RackTile> getRackTilesSaved() {
		return rackTilesSaved;
	}

	public ArrayList<RouterTile> getRouterTilesSaved() {
		return routerTilesSaved;
	}

	public ArrayList<Loot> getPlayerLoot() {
		return playerLoot;
	}
	
	public int getHamstersSaved() {
		return hamstersSaved;
	}

	public void setHamstersSaved(int hamstersSaved) {
		this.hamstersSaved = hamstersSaved;
	}

}
