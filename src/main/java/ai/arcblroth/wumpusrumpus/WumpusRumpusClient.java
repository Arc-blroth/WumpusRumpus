package ai.arcblroth.wumpusrumpus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import java.util.logging.*;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonObject;

import kong.unirest.Unirest;

import ai.arcblroth.wumpusrumpus.game.*;
import ai.arcblroth.wumpusrumpus.util.CommandWrapper;
import ai.arcblroth.wumpusrumpus.util.ResourceLoader;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

public class WumpusRumpusClient extends WebSocketClient {

	private Logger logger = Logger.getLogger("WumpusRumpusClient");
	private long heartbeat_interval = 45000;
	private String session_id = "";
	private long last_seqnum = 0;
	private boolean ack_recieved = true;
	private ArrayList<GameInstance> games = new ArrayList<GameInstance>();

	public WumpusRumpusClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		logger.log(Level.INFO, "Opened new connection to " + getURI());
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		logger.log(Level.INFO, "Closed with exit code " + code + " for reason: " + reason);
	}

	@Override
	public void onMessage(String message) {
		JsonObject theMessage = (JsonObject) gson.fromJson(message, JsonObject.class);
		switch(theMessage.get("op").getAsInt()) {
		case 0: {
			String t = theMessage.get("t").getAsString();
			
			if(t.equals("READY")) {
				//op:ready
				session_id = theMessage.get("d")
						.getAsJsonObject().get("session_id")
						.getAsString();
				last_seqnum = theMessage.get("s")
						.getAsLong();
				logger.log(Level.INFO, "Gateway session_id: " + session_id);
				startHeartbeatThread();
				break;
			} else if (t.equals("MESSAGE_CREATE")) {
				// Why is GSON so complicated come on
				last_seqnum = theMessage.get("s")
						.getAsLong();
				String id = theMessage.get("d")
						.getAsJsonObject().get("id")
						.getAsString();
				String content = theMessage.get("d")
						.getAsJsonObject().get("content")
						.getAsString();
				String user_id = theMessage.get("d")
						.getAsJsonObject().get("author")
						.getAsJsonObject().get("id")
						.getAsString();
				String guild_id = theMessage.get("d")
						.getAsJsonObject().get("guild_id")
						.getAsString();
				String channel_id = theMessage.get("d")
						.getAsJsonObject().get("channel_id")
						.getAsString();
				proccessMessage(id, guild_id, channel_id, user_id, content);
				break;
			}
			break;
		}
		case 10: {
			try {
				//op:hello
				heartbeat_interval = theMessage.get("d")
						.getAsJsonObject().get("heartbeat_interval")
						.getAsLong();
				sendIdentity();
				break;
			} catch (NullPointerException e) {} //No idea how this is triggered, so shut it up like a pog
		}
		case 11: {
			//op:ack!
			ack_recieved = true;
			break;
		}
		default: {
			//op:unknown
			break;
		}
		}
	}

	private void sendIdentity() {
		this.send(gson.fromJson(
					ResourceLoader.getResourceAsString(
						"config/identity.json"
					), JsonObject.class
				).toString().replace("{token}", 
						bot_config.getProperty("token"))
		);
	}

	private void startHeartbeatThread() {
		Timer heart = new Timer();
		TimerTask beat = new TimerTask() {
			@Override
			public void run() {
				if(ack_recieved) {
					send("{"
							+ "\"op\":1,"
							+ "\"d\":" + last_seqnum + ""
							+ "}");
					logger.log(Level.FINE, "Sent Heartbeat");
					ack_recieved = false;
				} else {
					heart.cancel();
					close(4000);
					Unirest.shutDown();
					// Too lazy to restart bot
					System.exit(0);
				}
			}
		};
		heart.scheduleAtFixedRate(beat, 0, heartbeat_interval);
	}

	private void proccessMessage(String message_id, String guild_id, String channel_id, String user_id, String content) {
		logger.log(Level.INFO, "Recieved message: " + content);

		// Is it a command?
		if (content.startsWith(strings_config.getProperty("command_character"))) {
			// Remove the char :)
			content = content.substring(strings_config.getProperty("command_character").length()).toLowerCase();
			// Get all the command arguments
			String[] args = content.split(" ");
			// Is the command a WumpusRumpus command?
			if (args[0].equals(strings_config.getProperty("base_command"))) {

				// !wr - displays help
				if (args.length < 2) {
					postWrappedMessage(channel_id, strings_config.getProperty("command.help.text"));
				} else {
					//General commands - these can be executed from anywhere 
					if (args[1].equals(strings_config.getProperty("command.help"))) {
						postWrappedMessage(channel_id, strings_config.getProperty("command.help.text"));
					}
					if (  args[1].equals(strings_config.getProperty("command.instructions"))
						||args[1].equals(strings_config.getProperty("command.directions"))
						||args[1].equals(strings_config.getProperty("command.lore"))) {
						postMessage(channel_id,
								CommandWrapper.wrapEmbedMessage(
										strings_config.getProperty("command.instructions.title"),
										strings_config.getProperty("command.instructions.text"))
								);
					}
					if (args[1].equals(strings_config.getProperty("command.new_game"))) {
						games.add(new GameInstance(this, guild_id, user_id));
					}
					//Game instance commands - these can only be executed in the context of a game channel.
					for (GameInstance g : games) {
						if (g.getChannelId().equals(channel_id)) {
							
							//Joining a game
							if (args[1].equals(strings_config.getProperty("command.join"))) {
								g.tryJoinPlayer(message_id, user_id);
								break;
							}
							
							//Starting a game
							if (args[1].equals(strings_config.getProperty("command.start_game"))) {
								g.startGame(message_id, user_id);
								break;
							}
							
							// Display map
							if (args[1].equals(strings_config.getProperty("command.map"))) {
								g.displayMap();
								break;
							}

							// Move/Skip
							if (args.length == 3 && args[1].equals(strings_config.getProperty("command.move"))) {
								g.turnMove(user_id, message_id, args[2]);
								break;
							}
							if (args[1].equals(strings_config.getProperty("command.skip"))) {
								g.turnSkip(user_id, message_id);
								break;
							}

						} else {
							continue;
						}
					}
				}
			}
		}
	}
	
	public void postMessage(String channel_id, String result) {
		logger.log(Level.INFO, 
				"Sent message via POST. Server response: " +
				Unirest.post("https://discordapp.com/api/channels/{channel_id}/messages")
				.routeParam("channel_id", channel_id)
				.header("Authorization", "Bot " + bot_config.getProperty("token"))
				.header("Content-Type", "application/json")
				.body(result)
				.asJson().getStatus());
	}
	
	public void postWrappedMessage(String channel_id, String result) {
		result = CommandWrapper.wrapBasicMessage(result);
		postMessage(channel_id, result);
	}
	
	public void deleteMessage(String channel_id, String message_id) {
		logger.log(Level.INFO, 
				"Deleted message via POST. Server response: " +
				Unirest.delete("https://discordapp.com/api/channels/{channel_id}/messages/{message_id}")
				.routeParam("channel_id", channel_id)
				.routeParam("message_id", message_id)
				.header("Authorization", "Bot " + bot_config.getProperty("token"))
				.header("Content-Type", "application/json")
				.asEmpty().getStatus());
	}

	@Override
	public void onError(Exception ex) {
		logger.log(Level.SEVERE, "A websocket error occurred!", ex);
	}

}
