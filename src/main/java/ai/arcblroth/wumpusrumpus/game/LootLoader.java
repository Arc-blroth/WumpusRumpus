package ai.arcblroth.wumpusrumpus.game;

import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

import static ai.arcblroth.wumpusrumpus.WumpusRumpusBot.*;
import ai.arcblroth.wumpusrumpus.util.ResourceLoader;

public class LootLoader {
	
	private static Vector<Loot> lootTable;
	
	public static Loot getRandomLoot() {
		
		if (lootTable == null) {
			lootTable = new Vector<Loot>();
			Logger.getLogger("LootLoader").log(Level.INFO, "Loading loot from config/loot.config.json");
			JsonArray lootJson = gson.fromJson(ResourceLoader.getResourceAsString("config/loot.config.json"),
					JsonArray.class);
			for (JsonElement je : lootJson) {
				JsonObject jo = je.getAsJsonObject();
				String title = jo.get("name").getAsString();
				String flavor = jo.get("flavor").getAsString();
				double saveModifer = jo.get("saveModifer").getAsDouble();
				double defeatModifer = jo.get("defeatModifer").getAsDouble();
				lootTable.add(new Loot(title, flavor, saveModifer, defeatModifer));
			}
		}
		
		return lootTable.get(new Random().nextInt(lootTable.size()));
	}
	
}
