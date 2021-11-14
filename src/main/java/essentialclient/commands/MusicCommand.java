package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.MusicSounds;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MusicCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!ClientRules.COMMAND_MUSIC.getBoolean())
            return;

        CommandHelper.clientCommands.add("music");

        dispatcher.register(literal("music")//.requires((p) -> ClientRules.COMMAND_MUSIC.getBoolean())
            .then(literal("skip")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client == null)
                        return 0;
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
                        if (client == null)
                            return 0;
                        client.options.setSoundVolume(SoundCategory.MUSIC, (float) percent / 100);
                        if (percent == 0) {
                            client.getMusicTracker().stop();
                        }
                        EssentialUtils.sendMessageToActionBar("§aMusic volume set to " + percent + "%");

                        //client.getSoundManager().updateSoundVolume(SoundCategory.MUSIC, context.getArgument("volume", Integer.class));
                        return 0;
                    })
                )
            )
            .then(literal("play")
                .then(argument("musictype", StringArgumentType.word())
                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"overworld", "nether", "end", "creative", "menu", "credits"}, builder))
                    .executes(context -> {
                        String arg = context.getArgument("musictype", String.class);
                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client == null)
                            return 0;
                        MusicSound musicSound;
                        switch (arg) {
                            case "overworld" -> musicSound = MusicType.GAME;
                            case "nether" -> musicSound = MusicSounds.getRandomNetherMusic();
                            case "end" -> musicSound = MusicType.END;
                            case "creative" -> musicSound = MusicType.CREATIVE;
                            case "menu" -> musicSound = MusicType.MENU;
                            case "credits" -> musicSound = MusicType.CREDITS;
                            default -> {
                                EssentialUtils.sendMessage("§cThat is not a valid music type");
                                return 0;
                            }
                        }
                        client.getMusicTracker().stop();
                        client.getMusicTracker().play(musicSound);
                        EssentialUtils.sendMessageToActionBar("§aYou will now hear a piece from: " + arg);
                        return 0;
                    })
                )
            )
        );
    }
}