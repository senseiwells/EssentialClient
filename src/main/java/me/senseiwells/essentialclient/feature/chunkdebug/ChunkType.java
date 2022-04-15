package me.senseiwells.essentialclient.feature.chunkdebug;

public enum ChunkType implements Colourable {
	UNLOADED("Unloaded", 0x404040), // Dark grey
	BORDER("Border", 0x4FC3F7), // Light blue
	LAZY("Lazy", 0xFFA219), // Orange-yellow
	ENTITY_TICKING("Entity Ticking", 0x198C19); // Green

	private final String prettyName;
	private final int colour;

	ChunkType(String prettyName, int colour) {
		this.prettyName = prettyName;
		this.colour = colour;
	}

	public static ChunkType decodeChunkType(int code) {
		if (code < 1 || code > 4) {
			return UNLOADED;
		}
		return ChunkType.values()[code];
	}

	@Override
	public int getColour() {
		return this.colour;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public String getName() {
		return this.prettyName;
	}
}
