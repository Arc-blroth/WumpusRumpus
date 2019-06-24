package ai.arcblroth.wumpusrumpus.help;

import com.google.gson.JsonObject;

import ai.arcblroth.wumpusrumpus.CommandWrapper;

public class CommandHelp {

	public static String execute(String guild_id, String channel_id, JsonObject user, String content) {
		// TODO Add actual help.
		return CommandWrapper.wrapBasicMessage("No help for you");
	}

}
