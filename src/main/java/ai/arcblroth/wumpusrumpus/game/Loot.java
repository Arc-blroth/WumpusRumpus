package ai.arcblroth.wumpusrumpus.game;

public interface Loot {
	
	public String getName();
	public String getFlavorText();

	public double getSaveModifer();

	public double getDefeatModifer();
	
}
