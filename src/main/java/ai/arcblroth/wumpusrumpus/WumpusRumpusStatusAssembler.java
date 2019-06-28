package ai.arcblroth.wumpusrumpus;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Creates the string that sets WumpusRumpus' custom status. Based on a
 * thourough analysis of <a href=
 * "https://github.com/discordjs/discord.js/blob/master/src/structures/ClientPresence.js#L46">discord.js</a>
 * since the offical documentation doesn't really describe this too well.<br/>
 * <br/>
 * EDIT: The documentation actually does describe this - in the Rich Presence
 * SDK section.&nbsp;&nbsp;/facepalm
 * 
 * @author Arc
 */
public class WumpusRumpusStatusAssembler {
	
	private static final int opcode_status_update = 3;
	
	public static String assembleStatusString() {
		
		JsonObject presenceUpdate = new JsonObject();
		presenceUpdate.add("op", new JsonPrimitive(opcode_status_update));

		JsonObject presence = new JsonObject();
		presence.add("afk", new JsonPrimitive(false));
		presence.add("since", JsonNull.INSTANCE);
		presence.add("status", new JsonPrimitive(strings_config.getProperty("bot.presence.status")));
		
		JsonObject game = new JsonObject();
		game.add("type", new JsonPrimitive(0));
		game.add("name", new JsonPrimitive(strings_config.getProperty("bot.presence.game.name")));
		game.add("url", new JsonPrimitive(strings_config.getProperty("bot.presence.game.url")));

		presence.add("game", game);

		presenceUpdate.add("d", presence);

		Logger.getLogger("WumpusRumpusStatusAssembler").log(Level.INFO, presenceUpdate.toString());

		return presenceUpdate.toString();
	}

}
