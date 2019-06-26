package ai.arcblroth.wumpusrumpus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

import ai.arcblroth.wumpusrumpus.util.InternetLoader;
import ai.arcblroth.wumpusrumpus.util.ResourceLoader;
import kong.unirest.Unirest;

public class WumpusRumpusBot {

	private static Logger mainLogger = Logger.getLogger("main");
	public static Properties bot_config = new Properties();
	public static Properties strings_config = new Properties();
	public static Gson gson = new Gson();

	public static void main(String[] wumpusArgs) throws URISyntaxException {

		mainLogger.log(Level.INFO, "Starting the Rumpus...");

		mainLogger.log(Level.INFO, "Loading bot_config...");

		try {
			bot_config.load(ResourceLoader.getResource("config/bot.config"));
			strings_config.load(ResourceLoader.getResource("config/strings.config"));
		} catch (Exception e) {
			mainLogger.log(Level.SEVERE, "", e);
			mainLogger.log(Level.SEVERE, "Configs not found, exiting...");
			System.exit(-1);
		}

		mainLogger.log(Level.INFO, "Config loaded.");

		String gateway_url = InternetLoader.getJSONFromURL("https://discordapp.com/api/gateway").get("url")
				.getAsString() + "?v=6&encoding=json";

		mainLogger.log(Level.INFO, "Connecting to Gateway at \"" + gateway_url + "\"");

		new WumpusRumpusClient(new URI(gateway_url)).connect();

	}

}
