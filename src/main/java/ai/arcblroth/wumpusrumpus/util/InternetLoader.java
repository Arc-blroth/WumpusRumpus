package ai.arcblroth.wumpusrumpus.util;

import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import kong.unirest.Unirest;

/**
 * 
 * A small custom class to REST the internet. Highly inefficient, but then again
 * I only had 4 days to make this bot :)
 * 
 * @author Arc
 *
 */
public class InternetLoader {
	
	private static Gson gson = new Gson();
	
	public static JsonObject getJSONFromURL(String url) {
		return (JsonObject) gson.fromJson(
				Unirest.get("https://discordapp.com/api/gateway").asString().getBody(),
				JsonObject.class);
	}

}
