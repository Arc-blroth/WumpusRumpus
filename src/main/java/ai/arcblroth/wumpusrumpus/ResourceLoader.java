package ai.arcblroth.wumpusrumpus;

import java.io.*;
import java.util.ArrayList;

/**
 * 
 * A small custom class to load in files as strings. Highly inefficient, but
 * then again I only had 4 days to make this bot :)
 * 
 * @author Arc
 *
 */
public class ResourceLoader {
	
	public static BufferedReader getResource(String filename) {
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)));
			return bis;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static ArrayList<String> getResourceAsStrings(String filename) {
		ArrayList<String> lines = new ArrayList<String>();
		try (BufferedReader bis = getResource(filename)) {
			if (bis != null) {
				String line = "";
				while ((line = bis.readLine()) != null) {
					lines.add(line);
				}
				return lines;
			} else
				return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getResourceAsString(String filename) {
		try(BufferedReader bis = getResource(filename)) {
			if(bis != null) {
				String out = "", line = "";
				while ((line = bis.readLine()) != null) {
					out = out + line + "\n";
				}
				return out;
			} else return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

}
