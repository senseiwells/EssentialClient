package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TravelCommand {

    public static boolean enabled = false;
    public static Vec3d destination;
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
                            ClientPlayerEntity player = EssentialUtils.getPlayer();
                            destination = new Vec3d(context.getArgument("x", Double.class), player.getY() + 1, context.getArgument("z", Double.class));
                            EssentialUtils.sendMessage("§6You will travel to " + destination.x + ", " + destination.z);
                            EssentialUtils.sendMessage("§6To stop type /travel stop");
                            enabled = true;
                            return 0;
                        })
                    )
                )
            )
            .then(literal("stop")
                .executes(context -> {
                    ClientPlayerEntity player = EssentialUtils.getPlayer();
                    enabled = false;
                    MinecraftClient.getInstance().options.keyForward.setPressed(false);
                    player.sendMessage(new LiteralText("§6You have stopped travelling"), false);
                    return 0;
                })
            )
            .then(literal("ping")
                .then(argument("discordId", StringArgumentType.string())
                    .executes(context -> {
                        ping = context.getArgument("discordId", String.class);
                        EssentialUtils.sendMessage("§6You will now ping <@" + ping + ">, after you have reached your destination");
                        return 0;
                    })
                )
            )
        );
    }

    public static void registerTickTravel() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity playerEntity = client.player;
            if (playerEntity == null || !enabled)
                return;
            if (Math.round(playerEntity.getX()) == destination.getX() && Math.round(playerEntity.getZ()) == destination.getZ()) {
                playerEntity.sendMessage(new LiteralText("§6You have reached your destination"), false);
                if (ping != null)
                    playerEntity.sendChatMessage("<@" + ping + ">, I have reached my destination :)");
                MinecraftClient.getInstance().options.keyForward.setPressed(false);
                enabled = false;
                return;
            }
            playerEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, destination);
            MinecraftClient.getInstance().options.keyForward.setPressed(true);
        });
    }
}
