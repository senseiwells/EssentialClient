package me.senseiwells.essentialclient.utils.render;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

//#if MC < 11900
//$$import net.minecraft.text.LiteralText;
//$$import net.minecraft.text.TranslatableText;
//#endif

public class Texts {
	public static final Text
		EMPTY = literal(""),
		CLIENT_SCREEN = literal("Essential Client Options"),
		SERVER_SCREEN = literal("Carpet Server Options"),
		GAME_RULE_SCREEN = literal("Game Rule Options"),
		SCRIPT_SCREEN = literal("Client Script Options"),
		CHUNK_SCREEN = literal("Chunk Debug Map"),
		CONTROLS_SCREEN = literal("Client KeyBinds"),
		RESET = translatable("controls.reset"),
		REFRESH = literal("Refresh"),
		DONE = translatable("gui.done"),
		DOCUMENTATION = literal("Documentation"),
		NEW = literal("New"),
		DOWNLOAD = literal("Download"),
		TRUE = literal("true").formatted(Formatting.DARK_GREEN),
		FALSE = literal("false").formatted(Formatting.DARK_RED),
		START = literal("Start").formatted(Formatting.DARK_GREEN),
		STOP = literal("Stop").formatted(Formatting.DARK_RED);

	// These allow for easy porting to later versions
	public static MutableText literal(String message) {
		//#if MC >= 11900
		return Text.literal(message);
		//#else
		//$$return new LiteralText(message);
		//#endif
	}

	public static MutableText translatable(String translatable, Object... args) {
		//#if MC >= 11900
		return Text.translatable(translatable, args);
		//#else
		//$$return new TranslatableText(translatable, args);
		//#endif
	}

	public static String getTranslatableKey(Text text) {
		//#if MC >= 11900
		if (text.getContent() instanceof TranslatableTextContent translatableText) {
			//#else
			//$$if (text instanceof TranslatableText translatableText) {
			//#endif
			return translatableText.getKey();
		}
		return null;
	}
}
