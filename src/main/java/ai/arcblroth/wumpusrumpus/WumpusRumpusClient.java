package ai.arcblroth.wumpusrumpus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonObject;

import ai.arcblroth.wumpusrumpus.help.CommandHelp;
import kong.unirest.Unirest;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

public class WumpusRumpusClient extends WebSocketClient {

	private Logger logger = Logger.getLogger("WumpusRumpusClient");
	private long heartbeat_interval = 45000;
	private String session_id = "";
	private long last_seqnum = 0;
	private boolean ack_recieved = true;

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
				String content = theMessage.get("d")
						.getAsJsonObject().get("content")
						.getAsString();
				JsonObject user = theMessage.get("d")
						.getAsJsonObject().get("author")
						.getAsJsonObject();
				String guild_id = theMessage.get("d")
						.getAsJsonObject().get("guild_id")
						.getAsString();
				String channel_id = theMessage.get("d")
						.getAsJsonObject().get("channel_id")
						.getAsString();
				proccessMessage(guild_id, channel_id, user, content);
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
						bot_config.getProperty("identity_location")
					), JsonObject.class
				).toString()
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
					logger.log(Level.INFO, "Sent Heartbeat");
					ack_recieved = false;
				} else {
					heart.cancel();
					close(4000);
					// Too lazy to restart bot
				}
			}
		};
		heart.scheduleAtFixedRate(beat, 0, heartbeat_interval);
	}

	private void proccessMessage(String guild_id, String channel_id, JsonObject user, String content) {
		logger.log(Level.INFO, "Recieved message: " + content);

		// Is it a command?
		if (content.startsWith(settings_config.getProperty("command_character"))) {
			// Remove the char :)
			content = content.substring(settings_config.getProperty("command_character").length()).toLowerCase();
			// Get all the command arguments
			String[] args = content.split(" ");
			// Is the command a WumpusRumpus command?
			if (args[0].equals(settings_config.getProperty("base_command"))) {
				if (args.length < 2) {
					//maybe need help?
					postMessage(channel_id, CommandHelp.execute(guild_id, channel_id, user, content));
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
				.body(result)
				.asJson().getStatus());
	}

	@Override
	public void onError(Exception ex) {
		logger.log(Level.SEVERE, "A websocket error occurred!", ex);
	}

}
