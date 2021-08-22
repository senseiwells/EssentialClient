package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import essentialclient.gui.clientrule.ClientRuleHelper;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.command.PlayerClientCommandHelper;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class PlayerClientCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("playerclient").requires((p) -> ClientRuleHelper.getBoolean("commandPlayerClient"))
                .then(literal("spawn")
                        .then(argument("playername", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                .executes(context -> {
                                    if (PlayerClientCommandHelper.playerClientHelperMap.isEmpty()) {
                                        context.getSource().sendFeedback(new LiteralText("§cYou have no players in your config"));
                                        return 0;
                                    }
                                    PlayerClientCommandHelper data = PlayerClientCommandHelper.playerClientHelperMap.get(context.getArgument("playername", String.class));
                                    if (data == null)
                                        context.getSource().sendFeedback(new LiteralText("§cThat player doesn't exist in your config"));
                                    else
                                        PlayerClientCommandHelper.sendCommand(context.getSource().getPlayer(), data);
                                    return 0;
                                })
                        )
                )
                .then(literal("add")
                        .then(argument("playername", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                .then(literal("spawn")
                                        .then(argument("x", DoubleArgumentType.doubleArg())
                                                .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "x"))
                                                .then(argument("y", DoubleArgumentType.doubleArg())
                                                        .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "y"))
                                                        .then(argument("z", DoubleArgumentType.doubleArg())
                                                                .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "z"))
                                                                .then(literal("facing")
                                                                        .then(argument("yaw", DoubleArgumentType.doubleArg())
                                                                                .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "yaw"))
                                                                                .then(argument("pitch", DoubleArgumentType.doubleArg())
                                                                                        .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "pitch"))
                                                                                        .then(literal("in")
                                                                                                .then(argument("dimension", StringArgumentType.greedyString())
                                                                                                        .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "dimension"))
                                                                                                        .executes(PlayerClientCommandHelper::createNewPlayerClient)
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(literal("remove")
                        .then(argument("playername", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                .executes(context -> {
                                    PlayerClientCommandHelper data = PlayerClientCommandHelper.playerClientHelperMap.remove(context.getArgument("playername", String.class));
                                    if (data == null)
                                        context.getSource().sendFeedback(new LiteralText("§cThat player is not in your config"));
                                    else
                                        context.getSource().sendFeedback(new LiteralText("§6That player has been removed from your config"));
                                    PlayerClientCommandHelper.writeSaveFile(PlayerClientCommandHelper.playerClientHelperMap);
                                    return 0;
                                })
                        )
                )
        );
    }
}
