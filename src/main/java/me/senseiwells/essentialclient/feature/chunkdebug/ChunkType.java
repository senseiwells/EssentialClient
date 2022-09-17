package me.senseiwells.essentialclient.feature.chunkdebug;

import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.Text;

public enum ChunkType implements Colourable {
	UNLOADED(Texts.UNLOADED, 0x404040), // Dark grey
	BORDER(Texts.BORDER, 0x4FC3F7), // Light blue
	LAZY(Texts.LAZY, 0xFFA219), // Orange-yellow
	ENTITY_TICKING(Texts.ENTITY_TICKING, 0x198C19); // Green

	private final Text prettyName;
	private final int colour;

	ChunkType(Text prettyName, int colour) {
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
	public Text getName() {
		return this.prettyName;
	}
}
