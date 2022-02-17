package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import essentialclient.clientrule.ClientRules;
import essentialclient.feature.MusicSounds;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.command.EnumArgumentType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MusicCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		if (!ClientRules.COMMAND_MUSIC.getValue()) {
			return;
		}

		CommandHelper.clientCommands.add("music");

		dispatcher.register(literal("music")
			.then(literal("skip")
				.executes(context -> {
					MinecraftClient client = MinecraftClient.getInstance();
					client.getMusicTracker().stop();
					EssentialUtils.sendMessageToActionBar("§aMusic skipped!");
					return 0;
				})
			)
			.then(literal("volume")
				.then(argument("percent", IntegerArgumentType.integer(0, 100))
					.executes(context -> {
						MinecraftClient client = MinecraftClient.getInstance();
						int percent = context.getArgument("percent", Integer.class);
						client.options.setSoundVolume(SoundCategory.MUSIC, (float) percent / 100);
						if (percent == 0) {
							client.getMusicTracker().stop();
						}
						EssentialUtils.sendMessageToActionBar("§aMusic volume set to " + percent + "%");
						return 0;
					})
				)
			)
			.then(literal("play")
				.then(argument("musictype", EnumArgumentType.enumeration(EnumMusicType.class))
					.executes(context -> {
						EnumMusicType musicType = EnumArgumentType.getEnumeration(context, "musictype", EnumMusicType.class);
						MinecraftClient client = MinecraftClient.getInstance();
						client.getMusicTracker().stop();
						client.getMusicTracker().play(musicType.musicSound);
						EssentialUtils.sendMessageToActionBar("§aYou will now hear a piece from: " + musicType);
						return 0;
					})
				)
			)
		);
	}

	enum EnumMusicType {
		OVERWORLD(MusicType.GAME),
		NETHER(MusicSounds.getRandomNetherMusic()),
		END(MusicType.END),
		CREATIVE(MusicType.CREATIVE),
		MENU(MusicType.MENU),
		CREDITS(MusicType.CREDITS);

		private final MusicSound musicSound;

		EnumMusicType(MusicSound musicSound) {
			this.musicSound = musicSound;
		}
	}
}