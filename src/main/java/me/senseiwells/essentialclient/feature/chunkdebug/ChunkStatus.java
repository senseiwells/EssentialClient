package me.senseiwells.essentialclient.feature.chunkdebug;

public enum ChunkStatus implements Colourable {
	EMPTY("Empty", -1),
	STRUCTURE_STARTS("Structure Start", 0x98F859), // Pale green
	STRUCTURE_REFERENCES("Structure References", 0xFFBC2C), // Light orange
	BIOMES("Biomes", 0x8FB815), // Dark lime green
	NOISE("Noise", 0x61857F), // Grey-blue
	SURFACE("Surface", 0x013220), // Pine green
	CARVERS("Carvers", 0x88631F), // Brown
	LIQUID_CARVERS("Liquid Carvers", 0x9EEEF5), // Baby blue
	FEATURES("Features", 0x3659DE), // Blue
	LIGHT("Light", -1),
	SPAWN("Spawn", 0xBFFF00), // Lime green
	HEIGHTMAPS("Heightmaps", 0xF8518D), // Pink
	FULL("Full", 0x0A18D8); // Dark blue

	private final String prettyName;
	private final int colour;

	ChunkStatus(String prettyName, int colour) {
		this.prettyName = prettyName;
		this.colour = colour;
	}

	@Override
	public int getColour() {
		return this.colour;
	}

	@Override
	public int getPriority() {
		return this.ordinal() > 0 && this.ordinal() < 12 ? 3 : 0;
	}

	@Override
	public String getName() {
		return this.prettyName;
	}

	public static ChunkStatus decodeChunkStatus(int code) {
		if (code < 1 || code > 12) {
			return EMPTY;
		}
		return ChunkStatus.values()[code];
	}
}
