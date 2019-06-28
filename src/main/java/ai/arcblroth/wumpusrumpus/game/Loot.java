package ai.arcblroth.wumpusrumpus.game;

public class Loot {
	
	private final String name, flavor;

	private final double saveModifer, defeatModifer;
	
	public Loot(String name, String flavor, double saveModifer, double defeatModifer) {
		super();
		this.name = name;
		this.flavor = flavor;
		this.saveModifer = saveModifer;
		this.defeatModifer = defeatModifer;
	}
	
	public String getName() {
		return name;
	}

	public String getFlavorText() {
		return flavor;
	}

	public double getSaveModifer() {
		return saveModifer;
	}

	public double getDefeatModifer() {
		return defeatModifer;
	}
	
}
