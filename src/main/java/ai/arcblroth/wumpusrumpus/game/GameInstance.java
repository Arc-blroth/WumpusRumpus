package ai.arcblroth.wumpusrumpus.game;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import ai.arcblroth.wumpusrumpus.WumpusRumpusClient;
import ai.arcblroth.wumpusrumpus.game.tile.EmptyTile;
import ai.arcblroth.wumpusrumpus.game.tile.GameTile;
import ai.arcblroth.wumpusrumpus.game.tile.HamsterTile;
import ai.arcblroth.wumpusrumpus.game.tile.RackTile;
import ai.arcblroth.wumpusrumpus.game.tile.RouterTile;
import ai.arcblroth.wumpusrumpus.util.CommandWrapper;
import ai.arcblroth.wumpusrumpus.util.ResourceLoader;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class GameInstance {
	
	private static long gameCount = 0;
	private Logger logger = Logger.getLogger("GameInstance");
	private final WumpusRumpusClient wrc;
	private final String guild_id, channel_id;
	private final ArrayList<String> player_ids = new ArrayList<String>(8);
	private final ArrayList<WumpusPlayer> players = new ArrayList<WumpusPlayer>(8);
	private GameInitThread gt;
	private GameMap gm;
	private boolean gameStarted = false;
	private final int TURNS_IN_A_GAME;
	private int turn = 0;
	private int currentPlayer = 0;
	private boolean currentPlayerHasExecutedMoveYet = false;
	//0=not waiting, 1=rack, 2=router, 3=bug
	private int waitingForPlayerResponse = 0;
	private boolean isGameOver = false;

	public GameInstance(WumpusRumpusClient wrc, String guild_id, String player) {
		this.wrc = wrc;
		this.guild_id = guild_id;
		this.player_ids.add(player);
		
		int tiag;
		try {
			tiag = Integer.parseInt(strings_config.getProperty("game.total_turns_per"));
		} catch (NumberFormatException e) {
			tiag = 10;
		}
		TURNS_IN_A_GAME = tiag;

		logger.log(Level.INFO, 
				"Creating channel via POST...");
		
		gameCount++;
		
		//Create a new channel to host the game
		String newchannel = ResourceLoader.getResourceAsString("newchannel.json");
		newchannel = newchannel.replace("{name}",
				CommandWrapper.sanitizeJson(strings_config.getProperty("new_channel.name").replace("{number}",
						Long.toString(gameCount)))
		);
		newchannel = newchannel.replace("{topic}",
				CommandWrapper.sanitizeJson(strings_config.getProperty("new_channel.topic").replace("{number}",
						Long.toString(gameCount)))
		);

		HttpResponse<String> result = Unirest.post("https://discordapp.com/api/guilds/{guild_id}/channels")
						.routeParam("guild_id", guild_id)
						.header("Authorization", "Bot " + bot_config.getProperty("token"))
						.header("Content-Type", "application/json")
						.body(newchannel)
				.asString();

		logger.log(Level.INFO, "Channel creation resulted in " + result.getStatus() + "/" + result.getStatusText());
		
		this.channel_id = gson.fromJson(result.getBody(), JsonObject.class).get("id").getAsString();
		
		// Tell everyone about the game
		wrc.postWrappedMessage(channel_id, strings_config.getProperty("new_channel.intro"));

	}

	public String getChannelId() {
		return channel_id;
	}

	public boolean tryJoinPlayer(String joinMessageId, String player_id) {

		//Why would you join a started game!?
		if (gameStarted) {
			wrc.deleteMessage(channel_id, joinMessageId);
			return false;
		}

		if (player_ids.size() < 8 /* && !player_ids.contains(player_id) */) {
			player_ids.add(player_id);

			// delete the !wr join message since its annoying :)
			wrc.deleteMessage(channel_id, joinMessageId);
			wrc.postWrappedMessage(channel_id,
							strings_config.getProperty("command.join.success")
							.replace("{name}", "<@" + player_id + ">")
					);
			return true;
		} else {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.join.failure"));
			return false;
		}
	}

	public void startGame(String message_id, String player_id) {
		
		//No restarting
		if (gameStarted) {
			wrc.deleteMessage(channel_id, message_id);
			return;
		}
		
		// Only the person who made a game can start it.
		if (!player_id.equals(player_ids.get(0)))
			return;

		// You can't play by yourself.
		if (player_ids.size() < 2) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.start.solofail"));
			return;
		}

		// Let's begin!
		gameStarted = true;
		wrc.postWrappedMessage(channel_id,
				strings_config.getProperty("command.start.countdown").replace("{seconds}", "10"));
		
		wrc.postMessage(channel_id,
				CommandWrapper.wrapEmbedMessage(
						strings_config.getProperty("command.instructions.title"),
						strings_config.getProperty("command.instructions.text"))
				);

		gt = this.new GameInitThread();
		gt.start();

	}
	
	public void displayMap() {
		wrc.postMessage(channel_id, 
				CommandWrapper.wrapEmbedMessage(
						strings_config.getProperty("command.map.title"),
						"```" + gm.renderMapToAscii(players, currentPlayer) + "```")
				);
	}

	private void nextTurn() {
		new Thread(() -> {
			// Give player time to react
			try {Thread.sleep(4000);} catch (InterruptedException e) {}

			int tileInt = players.get(currentPlayer).getCoordinate().getMapInt(gm.getWidth());
			
			if(gm.getMap().get(tileInt) instanceof HamsterTile) {
				//remove hamster tile since player did not attack it
				gm.replaceTileWithEmpty(players.get(currentPlayer).getCoordinate());
			}
			
			currentPlayerHasExecutedMoveYet = false;
			waitingForPlayerResponse = 0;
			currentPlayer++;
			if (currentPlayer >= players.size()) {
				turn++;
				currentPlayer = 0;
				
				if (turn < TURNS_IN_A_GAME) {
					wrc.postMessage(channel_id, CommandWrapper.wrapEmbedMessage(
								strings_config.getProperty("game.turn.newturn")
								.replace("{turn}", Integer.toString(turn))
								.replace("{turns_left}", Integer.toString(TURNS_IN_A_GAME + 1 - turn))
							, "")
						);
					try {Thread.sleep(2000);} catch (InterruptedException e) {}
				} else if (turn == TURNS_IN_A_GAME) {
					wrc.postMessage(channel_id, CommandWrapper.wrapEmbedMessage(
							strings_config.getProperty("game.turn.finalturn")
							.replace("{turn}", Integer.toString(turn))
							.replace("{turns_left}", Integer.toString(TURNS_IN_A_GAME + 1 - turn))
						, "")
					);
					try {Thread.sleep(2000);} catch (InterruptedException e) {}
				} else {
					wrc.postMessage(channel_id, CommandWrapper.wrapEmbedMessage(
							strings_config.getProperty("game.turn.gameover")
						, "")
					);
					try {Thread.sleep(2000);} catch (InterruptedException e) {}
					gameOver();
					return; // to prevent playTurn();
				}
			}
			playTurn();
		}).start();
	}

	public void playTurn() {
		
		WumpusPlayer wp = players.get(currentPlayer);
		
		//Display map
		displayMap();
		
		//Tell player what they can do.
		boolean canMoveUp, canMoveDown, canMoveRight, canMoveLeft;
		int playerMapInt = wp.getCoordinate().getMapInt(gm.getWidth());
		canMoveUp = (playerMapInt - gm.getWidth()) >= 0;
		canMoveDown = (playerMapInt + gm.getWidth()) < gm.getWidth() * gm.getHeight();
		canMoveLeft = ((playerMapInt % gm.getWidth()) - 1) >= 0;
		canMoveRight = ((playerMapInt % gm.getWidth()) + 1) < gm.getWidth();
		
		wrc.postWrappedMessage(channel_id, 
				strings_config.getProperty("game.turn")
				.replace("{player}", "<@" + wp.getPlayer_id() + ">")
				.replace("{directions}", 
						   (canMoveUp ? (strings_config.getProperty("game.turn.dir.up") + " ") : "")
						+  (canMoveDown ? (strings_config.getProperty("game.turn.dir.down") + " ") : "")
						+  (canMoveLeft ? (strings_config.getProperty("game.turn.dir.left") + " ") : "")
						+  (canMoveRight ? (strings_config.getProperty("game.turn.dir.right")) : "")
					)
			);
	}

	private boolean checkIfIsPlayersTurn(String player_id, String message_id) {
		
		//If game is not started or has ended, just stop. Get some help.
		if(!gameStarted || isGameOver) {
			wrc.deleteMessage(channel_id, message_id);
			return false;
		}
		
		// Only can be done if its is a persons' turn
		if (!players.get(currentPlayer).getPlayer_id().equals(player_id)) {
			wrc.deleteMessage(channel_id, message_id);
			return false;
		} else
			return true;
	}

	public void turnMove(String player_id, String message_id, String dir) {

		WumpusPlayer wp = players.get(currentPlayer);

		if(!checkIfIsPlayersTurn(player_id, message_id)) return;
		
		// Just stop breaking my christian minecraft server, please
		if (currentPlayerHasExecutedMoveYet) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.move.moved_already"));
			return;
		}

		// if we can move in X direction, do it
		// otherwise AHHH
		boolean canMoveUp, canMoveDown, canMoveRight, canMoveLeft;
		int playerMapInt = wp.getCoordinate().getMapInt(gm.getWidth());
		// when you're too lazy to actually implement a map so you
		// just use modulo operations
		canMoveUp = (playerMapInt - gm.getWidth()) >= 0;
		canMoveDown = (playerMapInt + gm.getWidth()) < gm.getWidth() * gm.getHeight();
		canMoveLeft = ((playerMapInt % gm.getWidth()) - 1) >= 0;
		canMoveRight = ((playerMapInt % gm.getWidth()) + 1) < gm.getWidth();

		if (dir.equals(strings_config.getProperty("command.move.up")) && canMoveUp) {
			wp.getCoordinate().setMapInt(playerMapInt - gm.getWidth(), gm.getWidth());
			turnCheckTile(dir);
		} else if (dir.equals(strings_config.getProperty("command.move.down")) && canMoveDown) {
			wp.getCoordinate().setMapInt(playerMapInt + gm.getWidth(), gm.getWidth());
			turnCheckTile(dir);
		} else if (dir.equals(strings_config.getProperty("command.move.left")) && canMoveLeft) {
			wp.getCoordinate().setMapInt(playerMapInt - 1, gm.getWidth());
			turnCheckTile(dir);
		} else if (dir.equals(strings_config.getProperty("command.move.right")) && canMoveRight) {
			wp.getCoordinate().setMapInt(playerMapInt + 1, gm.getWidth());
			turnCheckTile(dir);
		} else {
			wrc.postWrappedMessage(channel_id, 
					strings_config.getProperty("command.move.fail")
					.replace("{direction}", dir)
				);
		}
	}

	private void turnCheckTile(String dir) {
		WumpusPlayer wp = players.get(currentPlayer);

		currentPlayerHasExecutedMoveYet = true;
		displayMap();
		
		wrc.postWrappedMessage(channel_id, 
				strings_config.getProperty("command.move.success")
				.replace("{direction}", dir));

		//what tile are we on?
		GameTile gt = gm.getMap().get(wp.getCoordinate().getMapInt(gm.getWidth()));
		int autoAdvance = gt.onPlayerStep(wrc, channel_id, wp, gm);
		
		//advance turn
		if(autoAdvance == 0) nextTurn();
		else waitingForPlayerResponse = autoAdvance;
	}

	public void turnSkip(String player_id, String message_id) {
		if(waitingForPlayerResponse == 0) {
			//advance turn
			nextTurn();
		}
	}

	public void turnOpenInventory(String player_id, String message_id) {
		WumpusPlayer wp = players.get(currentPlayer);

		if(!checkIfIsPlayersTurn(player_id, message_id)) return;
		
		StringBuilder inventoryBuilder = new StringBuilder();
		
		ArrayList<Loot> playerLoot = wp.getPlayerLoot();
		
		if (playerLoot.size() == 0) {
			inventoryBuilder.append(strings_config.getProperty("command.inventory.empty"));
		} else {
			for(Loot l : playerLoot) {
				inventoryBuilder.append(
						strings_config.getProperty("command.inventory.entry")
						.replace("{loot_name}", l.getName())
						.replace("{loot_flavor}", l.getFlavorText())
						.replace("{loot_save_mod}", Double.toString(l.getSaveModifer()))
						.replace("{loot_defeat_mod}", Double.toString(l.getDefeatModifer()))
				);
			}
		}
		
		wrc.postMessage(channel_id, 
				CommandWrapper.wrapEmbedMessage(
						strings_config.getProperty("command.inventory.title")
							.replace("{player}", wrc.getUsername(player_id)),
						inventoryBuilder.toString()
					)
			);

	}


	public void turnStats(String player_id, String message_id) {
		WumpusPlayer wp = players.get(currentPlayer);

		if(!checkIfIsPlayersTurn(player_id, message_id)) return;
		
		StringBuilder statsBuilder = new StringBuilder();
		
		statsBuilder.append(strings_config.getProperty("command.stats.racks_saved")
				.replace("{num}", Integer.toString(wp.getRackTilesSaved().size())) + "\n");
		statsBuilder.append(strings_config.getProperty("command.stats.routers_saved")
				.replace("{num}", Integer.toString(wp.getRouterTilesSaved().size())) + "\n");
		statsBuilder.append(strings_config.getProperty("command.stats.hamsters_saved")
				.replace("{num}", Integer.toString(wp.getHamstersSaved())) + "\n");
		statsBuilder.append(strings_config.getProperty("command.stats.bugs_defeated")
				.replace("{num}", Integer.toString(wp.getBugsDefeated())) + "\n");
		statsBuilder.append(strings_config.getProperty("command.stats.loot_found")
				.replace("{num}", Integer.toString(wp.getPlayerLoot().size())) + "\n");
		statsBuilder.append(strings_config.getProperty("command.stats.points")
				.replace("{num}", Integer.toString(wp.getTotalPoints())) + "\n");
		
		wrc.postMessage(channel_id, 
				CommandWrapper.wrapEmbedMessage(
						strings_config.getProperty("command.stats.title")
							.replace("{player}", wrc.getUsername(player_id)),
						statsBuilder.toString()
					)
			);
	}

	public void turnYes(String player_id, String message_id) {
		WumpusPlayer wp = players.get(currentPlayer);

		if(!checkIfIsPlayersTurn(player_id, message_id)) return;
		
		/* Whether or not you succeed or not is determined before
		 * the fixing wait period starts.
		 * I feel like this is a metaphor for life.
		 */
		
		if(waitingForPlayerResponse == 1) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.rack.yes"));
			boolean isSuccessful = new Random().nextDouble() < wp.getServerSaveChance();
			try {Thread.sleep(4500);} catch (InterruptedException e) {}
			
			if(isSuccessful) {
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.rack.fix.success"));
				GameTile gt = gm.getMap().get(wp.getCoordinate().getMapInt(gm.getWidth()));
				if(gt instanceof RackTile) {
					((RackTile) gt).saved();
					wp.getRackTilesSaved().add(((RackTile) gt));
				}
			} else {
				// KABOOM
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.rack.fix.fail"));
				gm.replaceTileWithEmpty(wp.getCoordinate());
			}
			nextTurn();
		} else if(waitingForPlayerResponse == 2) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.router.yes"));
			boolean isSuccessful = new Random().nextDouble() < wp.getServerSaveChance();
			try {Thread.sleep(6000);} catch (InterruptedException e) {}
			
			if(isSuccessful) {
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.router.fix.success"));
				GameTile gt = gm.getMap().get(wp.getCoordinate().getMapInt(gm.getWidth()));
				if (gt instanceof RouterTile) {
					((RouterTile) gt).saved();
					wp.getRouterTilesSaved().add(((RouterTile) gt));
				}
			} else {
				// KABOOM 2
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.router.fix.fail"));
				gm.replaceTileWithEmpty(wp.getCoordinate());
			}
			nextTurn();
			
		}
		
	}

	public void turnNo(String player_id, String message_id) {
		WumpusPlayer wp = players.get(currentPlayer);

		if(!checkIfIsPlayersTurn(player_id, message_id)) return;

		if (waitingForPlayerResponse == 1) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.rack.no"));
			nextTurn();
		} else if (waitingForPlayerResponse == 2) {
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.tile.onstep.router.no"));
			nextTurn();
		}
	}

	public void turnAttack(String player_id, String message_id) {

		if (!checkIfIsPlayersTurn(player_id, message_id))
			return;

		WumpusPlayer wp = players.get(currentPlayer);
		int tileInt = wp.getCoordinate().getMapInt(gm.getWidth());
		ArrayList<Coordinate> player_coords = new ArrayList<Coordinate>();
		for (WumpusPlayer p : players) {
			if (p != wp)
				player_coords.add(p.getCoordinate());
		}
		
		if (waitingForPlayerResponse == 3) {

			// Actual Bug Battling
			boolean isSuccessful = new Random().nextDouble() < wp.getBugDefeatChance();
			try {Thread.sleep(2000);} catch (InterruptedException e) {}
			
			if(isSuccessful) {
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.attack.at.bug.success"));
				gm.replaceTileWithEmpty(wp.getCoordinate());
				wp.setBugsDefeated(wp.getBugsDefeated() + 1);
				
				Loot l = LootLoader.getRandomLoot();
				wp.getPlayerLoot().add(l);
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.loot.bug")
						.replace("{loot_name}", l.getName())
						.replace("{loot_flavor}", l.getFlavorText())
						.replace("{loot_saveModifer}", Double.toString(l.getSaveModifer()))
						.replace("{loot_defeatModifer}", Double.toString(l.getDefeatModifer()))
						);
				
			} else {
				// OW OW OWEE
				wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.attack.at.bug.fail"));
			}
			nextTurn();
			
		} else if (player_coords.contains(wp.getCoordinate())) {
			// The definition of friendly fire.
			wrc.postWrappedMessage(channel_id, 
					strings_config.getProperty("command.attack.at.player")
					.replace("{player1}", wrc.getUsername(wp.getPlayer_id())));
		} else if (waitingForPlayerResponse == 1) { //The following are fun!
			// SABATAGE
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.attack.at.rack"));
			gm.replaceTileWithEmpty(wp.getCoordinate());
			GameTile gt = gm.getMap().get(wp.getCoordinate().getMapInt(gm.getWidth()));
			if (gt instanceof RackTile) {
				for (WumpusPlayer wpe : players) {
					wpe.getRackTilesSaved().remove((RackTile) gt);
				}
			}
			nextTurn();
		} else if (waitingForPlayerResponse == 2) {
			// SABATAGE 2
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.attack.at.router"));
			gm.replaceTileWithEmpty(wp.getCoordinate());
			GameTile gt = gm.getMap().get(wp.getCoordinate().getMapInt(gm.getWidth()));
			if (gt instanceof RouterTile) {
				for (WumpusPlayer wpe : players) {
					wpe.getRouterTilesSaved().remove((RouterTile) gt);
				}
			}
			nextTurn();
		} else if(gm.getMap().get(tileInt) instanceof HamsterTile) {
			// Monster.
			wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.attack.at.hamster"));
			gm.replaceTileWithEmpty(wp.getCoordinate());
			wp.setHamstersSaved(wp.getHamstersSaved() - 1);
		}
	}

	private void gameOver() {
		
		// Step 1: sort players by points (see WumpusPlayer#compareTo(WumpusPlayer other))
		Collections.sort(players);
		
		String title = strings_config.getProperty("game.over.won")
				.replace("{player}", wrc.getUsername(players.get(0).getPlayer_id()));
		
		StringBuilder leaderboardBuilder = new StringBuilder();
		
		for(int count = 0; count < players.size(); count++) {
			String key = "rest";
			if(count == 0) key = "first";
			if(count == 1) key = "second";
			if(count == 2) key = "third";
			leaderboardBuilder.append(
					strings_config.getProperty("game.over." + key)
					.replace("{player}", "<@" + players.get(0).getPlayer_id() + ">")
					.replace("{points}", Integer.toString(players.get(0).getTotalPoints()))
					+ "\n");
		}
		leaderboardBuilder.append("\n");
		leaderboardBuilder.append(strings_config.getProperty("game.over.epilogue"));

		wrc.postMessage(channel_id, CommandWrapper.wrapEmbedMessage(title, leaderboardBuilder.toString()));
		try {Thread.sleep(500);} catch(Exception e) {}
		wrc.postWrappedMessage(channel_id, strings_config.getProperty("game.over.candelete"));
		isGameOver = true;
	}

	public boolean endGame(String player_id, String message_id) {

		if (!gameStarted)
			return false;

		/*
		 * Only a player can end the game with !wr end_game 
		 * This is to prevent trolls from not letting people
		 * see the results of the Rumpus.
		 */
		if (!player_ids.contains(player_id)) {
			wrc.deleteMessage(player_id, message_id);
			return false;
		}
		
		if(!isGameOver) {
			gameOver();
			return false;
		}

		logger.log(Level.INFO, 
				"Deleting game channel '" + channel_id + "'. Server returned:" +
				Unirest.delete("https://discordapp.com/api/channels/{channel_id}")
				.routeParam("channel_id", channel_id)
				.header("Authorization", "Bot " + bot_config.getProperty("token"))
				.header("Content-Type", "application/json")
				.asJson().getStatus());
		return true;
	}

	class GameInitThread extends Thread {

		public void run() {
			try {
				// Countdown
				for (byte b = 10; b > 0; b--) {
					if (b <= 5) {
						wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.start.countdown")
								.replace("{seconds}", Byte.toString(b)));
					}
					Thread.sleep(1000);
				}

				// Setup
				// Map is a 10x8
				Random r = new Random();
				for (String p : player_ids) {
					players.add(new WumpusPlayer(p, new Coordinate(r.nextInt(10), r.nextInt(8))));
				}
				gm = new GameMap(10, 8);

				for (WumpusPlayer p : players) {
					// Make sure all players start on an empty tile
					gm.replaceTileWithEmpty(p.getCoordinate());
				}

				wrc.postWrappedMessage(channel_id, strings_config.getProperty("command.start.started"));

				turn = 1;

				wrc.postMessage(channel_id, CommandWrapper.wrapEmbedMessage(
							strings_config.getProperty("game.turn.newturn")
							.replace("{turn}", Integer.toString(turn))
							.replace("{turns_left}", Integer.toString(TURNS_IN_A_GAME + 1 - turn))
						, "")
					);
				
				// try {Thread.sleep(2000);} catch (InterruptedException e) {}
				
				playTurn();

			} catch (Exception e) {
				logger.log(Level.SEVERE, "GameInitThread error: ", e);
			}
		}

	}

}
