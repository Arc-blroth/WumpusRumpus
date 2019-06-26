package ai.arcblroth.wumpusrumpus.util;

public class CommandWrapper {

	public static String wrapBasicMessage(String message) {
		message = sanitizeJson(message);
		return "{" + "\"content\":\"" + message + "\"" + "}";
	}

	public static String wrapEmbedMessage(String title, String description) {
		title = sanitizeJson(title);
		description = sanitizeJson(description);
		return ResourceLoader.getResourceAsString("embed.json")
				.replace("{title}", title)
				.replace("{description}", description);
	}

	public static String sanitizeJson(String message) {
		message = message.replace("\n", "\\n");
		message = message.replace("\"", "\\\"");
		return message;
	}
}
