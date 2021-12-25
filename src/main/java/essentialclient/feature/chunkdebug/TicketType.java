package essentialclient.feature.chunkdebug;

public enum TicketType {
	START("Spawn", 0x00FF00),
	DRAGON("Dragon", 0xCC00CC),
	PLAYER("Player", 0x198C19),
	FORCED("Forced", 0x336FFF),
	LIGHT("Light", 0),
	PORTAL("Portal", 0x472483),
	POST_TELEPORT("Teleport", 0xFF6600),
	UNKNOWN("Unknown", 0);

	public final String prettyName;
	public final int colour;

	TicketType(String prettyName, int colour) {
		this.prettyName = prettyName;
		this.colour = colour;
	}

	public boolean hasColour() {
		return this.colour != 0;
	}

	public static TicketType decodeTicketType(int code) {
		if (code < 1 || code > 8) {
			return null;
		}
		return TicketType.values()[code - 1];
	}
}
