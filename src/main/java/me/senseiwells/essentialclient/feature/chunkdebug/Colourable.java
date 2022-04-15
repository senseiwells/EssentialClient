package me.senseiwells.essentialclient.feature.chunkdebug;

public interface Colourable {
	int getColour();
	int getPriority();
	String getName();

	default boolean hasColour() {
		return this.getColour() >= 0;
	}

	static Colourable getHighestPriority(Colourable colourable, Colourable... colourables) {
		Colourable highest = colourable;
		for (Colourable other : colourables) {
			if (other != null && other.getPriority() > highest.getPriority()) {
				highest = other;
			}
		}
		return highest;
	}
}
