package ai.arcblroth.wumpusrumpus;

public class CommandWrapper {

	public static String wrapBasicMessage(String message) {
		message = message.replace("\"", "\\\"");
		message = message.replace("\n", "\\n");
		return "{" + "\"content\":\"" + message + "\"" + "}";
	}

}
