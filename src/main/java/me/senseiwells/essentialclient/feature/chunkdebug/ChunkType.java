package me.senseiwells.essentialclient.feature.chunkdebug;

public enum ChunkType {
	UNLOADED("Unloaded", 0x404040),
	BORDER("Border", 0x4FC3F7),
	LAZY("Lazy", 0xFFA219),
	ENTITY_TICKING("Entity Ticking", 0x198C19);

	public final String prettyName;
	private final int colour;

	ChunkType(String prettyName, int colour) {
		this.prettyName = prettyName;
		this.colour = colour;
	}

	public int getColour() {
		return this.colour;
	}

	public static ChunkType decodeChunkType(int code) {
		if (code < 1 || code > 4) {
			return UNLOADED;
		}
		return ChunkType.values()[code];
	}
}
