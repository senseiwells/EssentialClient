package essentialclient.utils.render;

import net.minecraft.util.math.MathHelper;

public class PingColour {

	/**
	 * This code is from vladmarica's better-ping-display mod
	 */

	public static final int
		PING_START = 0,
		PING_MID = 150,
		PING_END = 300,

		COLOUR_GREY = 0x535353,
		COLOUR_START = 0x00E676,
		COLOUR_MID = 0xD6CD30,
		COLOUR_END = 0xE53935;

	public static int calculate(int ping) {
		if (ping < PING_START) {
			return COLOUR_GREY;
		}
		if (ping < PING_MID) {
			return interpolate(COLOUR_START, COLOUR_MID, computeOffset(ping));
		}
		return interpolate(COLOUR_MID, COLOUR_END, Math.min(ping, PING_END));
	}

	private static float computeOffset(int value) {
		float offset =  (value - PingColour.PING_START) / (float) (PingColour.PING_MID - PingColour.PING_START);
		return MathHelper.clamp(offset, 0.0F, 1.0F);
	}

	private static int interpolate(int colorStart, int colorEnd, float offset) {
		if (offset < 0 || offset > 1) {
			return COLOUR_END;
		}

		int redDiff = getRed(colorEnd) - getRed(colorStart);
		int greenDiff = getGreen(colorEnd) - getGreen(colorStart);
		int blueDiff = getBlue(colorEnd) - getBlue(colorStart);

		int newRed = Math.round(getRed(colorStart) + (redDiff * offset));
		int newGreen = Math.round(getGreen(colorStart) + (greenDiff * offset));
		int newBlue = Math.round(getBlue(colorStart) + (blueDiff * offset));

		return (newRed << 16) | (newGreen << 8) | newBlue;
	}

	private static int getRed(int color) {
		return (color >> 16) & 0xFF;
	}

	private static int getGreen(int color) {
		return (color >> 8) & 0xFF;
	}

	private static int getBlue(int color) {
		return color & 0xFF;
	}
}
