package me.senseiwells.essentialclient.feature.chunkdebug;

public enum TicketType implements Colourable {
	START("Spawn", 0xBFFF00), // Lime green
	DRAGON("Dragon", 0xCC00CC), // Magenta
	PLAYER("Player", -1), // Green
	FORCED("Forced", 0x336FFF), // Blue
	LIGHT("Light", -1),
	PORTAL("Portal", 0x472483), // Purple
	POST_TELEPORT("Teleport", 0xFF6600), // Orange
	CHONK("Chonk", 0x72FF13), // Light Green
	UNKNOWN("Unknown", -1);

	private final String prettyName;
	private final int colour;

	TicketType(String prettyName, int colour) {
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
	public String getName() {
		return this.prettyName;
	}

	public static TicketType decodeTicketType(int code) {
		if (code < 1 || code > 9) {
			return null;
		}
		return TicketType.values()[code - 1];
	}
}
