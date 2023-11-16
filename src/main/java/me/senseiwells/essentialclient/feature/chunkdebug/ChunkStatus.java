package me.senseiwells.essentialclient.feature.chunkdebug;

import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.Text;

public enum ChunkStatus implements Colourable {
	EMPTY(Texts.EMPTY_STATUS, -1),
	STRUCTURE_STARTS(Texts.STRUCTURE_STARTS, 0x98F859), // Pale green
	STRUCTURE_REFERENCES(Texts.STRUCTURE_REFERENCES, 0xFFBC2C), // Light orange
	BIOMES(Texts.BIOMES, 0x8FB815), // Dark lime green
	NOISE(Texts.NOISE, 0x61857F), // Grey-blue
	SURFACE(Texts.SURFACE, 0x013220), // Pine green
	CARVERS(Texts.CARVERS, 0x88631F), // Brown
	FEATURES(Texts.FEATURES, 0x3659DE), // Blue
	INITIALIZE_LIGHT(Texts.TICKET_LIGHT, -1),
	LIGHT(Texts.TICKET_LIGHT, -1),
	SPAWN(Texts.SPAWN, 0xBFFF00), // Lime green
	FULL(Texts.FULL, 0x0A18D8); // Dark blue

	private static final int COUNT = ChunkStatus.values().length;

	private final Text prettyName;
	private final int colour;

	ChunkStatus(Text prettyName, int colour) {
		this.prettyName = prettyName;
		this.colour = colour;
	}

	@Override
	public int getColour() {
		return this.colour;
	}

	@Override
	public int getPriority() {
		return this.ordinal() > 0 && this.ordinal() < (COUNT - 1) ? 3 : 0;
	}

	@Override
	public Text getName() {
		return this.prettyName;
	}

	public static ChunkStatus decodeChunkStatus(int code) {
		if (code < 1 || code >= COUNT) {
			return EMPTY;
		}
		return ChunkStatus.values()[code];
	}
}
