package me.senseiwells.essentialclient.utils.render;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Texts {
	public static final Text
		CLIENT_SCREEN = new LiteralText("Essential Client Options"),
		SERVER_SCREEN = new LiteralText("Carpet Server Options"),
		SCRIPT_SCREEN = new LiteralText("Client Script Options"),
		CHUNK_SCREEN = new LiteralText("Chunk Debug Map"),
		CONTROLS_SCREEN = new LiteralText("Client KeyBinds"),
		RESET = new TranslatableText("controls.reset"),
		REFRESH = new LiteralText("Refresh"),
		DONE = new TranslatableText("gui.done"),
		DOCUMENTATION = new LiteralText("Documentation"),
		NEW = new LiteralText("New"),
		DOWNLOAD = new LiteralText("Download"),
		TRUE = new LiteralText("true").formatted(Formatting.DARK_GREEN),
		FALSE = new LiteralText("false").formatted(Formatting.DARK_RED),
		START = new LiteralText("Start").formatted(Formatting.DARK_GREEN),
		STOP = new LiteralText("Stop").formatted(Formatting.DARK_RED);
}
