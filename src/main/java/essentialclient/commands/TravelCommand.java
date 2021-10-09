package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.utils.command.CommandHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class TravelCommand {

    public static boolean enabled = false;
    public static Vec3d destination;

    private static ClientPlayerEntity player;
    private static String ping;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        if (!ClientRules.COMMAND_TRAVEL.getBoolean())
            return;

        CommandHelper.clientCommands.add("travel");

        dispatcher.register(literal("travel")//.requires((p) -> ClientRules.COMMAND_TRAVEL.getBoolean())
                .then(literal("start")
                        .then(argument("x", DoubleArgumentType.doubleArg())
                                .suggests( ((context, builder) -> CommandHelper.suggestLocation(builder, "x")))
                                .then(argument("z", DoubleArgumentType.doubleArg())
                                        .suggests( ((context, builder) -> CommandHelper.suggestLocation(builder, "z")))
                                        .executes(context -> {
                                            player = CommandHelper.getPlayer();
                                            destination = new Vec3d(context.getArgument("x", Double.class), player.getY() + 1, context.getArgument("z", Double.class));
                                            player.sendMessage(new LiteralText("§6You will travel to " + destination.x + ", " + destination.z), false);
                                            player.sendMessage(new LiteralText("§6To stop type /travel stop"), false);
                                            enabled = true;
                                            return 0;
                                        })
                                )
                        )
                )
                .then(literal("stop")
                        .executes(context -> {
                            player = CommandHelper.getPlayer();
                            enabled = false;
                            MinecraftClient.getInstance().options.keyForward.setPressed(false);
                            player.sendMessage(new LiteralText("§6You have stopped travelling"), false);
                            return 0;
                        })
                )
                .then(literal("ping")
                        .then(argument("discordId", StringArgumentType.string())
                                .suggests( ((context, builder) -> CommandSource.suggestMatching(new String[]{"546239692048302091"}, builder)))
                                .executes(context -> {
                                    player = CommandHelper.getPlayer();
                                    ping = context.getArgument("discordId", String.class);
                                    player.sendMessage(new LiteralText("§6You will now ping <@" + ping + ">, after you have reached your destination"), false);
                                    return 0;
                                })
                        )
                )
        );
    }

    public static void tickTravel() {
        if (Math.round(player.getX()) == destination.getX() && Math.round(player.getZ()) == destination.getZ()) {
            player.sendMessage(new LiteralText("§6You have reached your destination"), false);
            if (ping != null)
                player.sendChatMessage("<@" + ping + ">, I have reached my destination :)");
            MinecraftClient.getInstance().options.keyForward.setPressed(false);
            enabled = false;
            return;
        }
        player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, destination);
        MinecraftClient.getInstance().options.keyForward.setPressed(true);
    }
}
