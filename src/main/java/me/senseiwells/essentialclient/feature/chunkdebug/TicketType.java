package me.senseiwells.essentialclient.feature.chunkdebug;

import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.Text;

public enum TicketType implements Colourable {
	START(Texts.TICKET_SPAWN, 0xBFFF00), // Lime green
	DRAGON(Texts.DRAGON, 0xCC00CC), // Magenta
	PLAYER(Texts.PLAYER, -1), // Green
	FORCED(Texts.FORCED, 0x336FFF), // Blue
	LIGHT(Texts.LIGHT, -1),
	PORTAL(Texts.PORTAL, 0x472483), // Purple
	POST_TELEPORT(Texts.TELEPORT, 0xFF6600), // Orange
	CHONK(Texts.CHONK, 0x72FF13), // Light Green
	UNKNOWN(Texts.TICKET_UNKNOWN, -1);

	private final Text prettyName;
	private final int colour;

	TicketType(Text prettyName, int colour) {
		this.prettyName = prettyName;
		this.colour = colour;
	}

	@Override
	public int getColour() {
		return this.colour;
	}

	@Override
	public int getPriority() {
		return this.hasColour() ? 2 : 0;
	}

	@Override
	public Text getName() {
		return this.prettyName;
	}

	public static TicketType decodeTicketType(int code) {
		if (code < 1 || code > 9) {
			return null;
		}
		return TicketType.values()[code - 1];
	}
}
