package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import essentialclient.gui.clientrule.ClientRules;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.command.PlayerClientCommandHelper;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class PlayerClientCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> playerclientNode = ClientCommandManager
                .literal("playerclient").requires((p) -> ClientRules.COMMANDPLAYERCLIENT.getBoolean())
                .build();
        LiteralCommandNode<FabricClientCommandSource> pcNode = ClientCommandManager
                .literal("pc").requires((p) -> ClientRules.COMMANDPLAYERCLIENT.getBoolean())
                .build();
        LiteralCommandNode<FabricClientCommandSource> spawnNode = ClientCommandManager
                .literal("spawn")
                        .then(argument("playername", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                .executes(context -> PlayerClientCommandHelper.spawnPlayer(context, false))
                                .then(literal("offset")
                                        .then(argument("x-axis", DoubleArgumentType.doubleArg())
                                                .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"0", "160", "-160", "256", "-256"}, builder))
                                                .then(argument("y-axis", DoubleArgumentType.doubleArg())
                                                        .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"0", "128"}, builder))
                                                        .then(argument("z-axis", DoubleArgumentType.doubleArg())
                                                                .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"0", "160", "-160", "256", "-256"}, builder))
                                                                .executes(context -> PlayerClientCommandHelper.spawnPlayer(context, true))
                                                        )
                                                )
                                        )
                                )
                        )
                .build();
        LiteralCommandNode<FabricClientCommandSource> addNode = ClientCommandManager
                .literal("add")
                        .then(argument("playername", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                .then(literal("spawn")
                                        .then(literal("at")
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
                                                                                                                .executes(context -> PlayerClientCommandHelper.createNewPlayerClient(context, false, false))
                                                                                                                .then(literal("in")
                                                                                                                        .then(argument("gamemode", StringArgumentType.word())
                                                                                                                                .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"spectator", "survival", "any"}, builder))
                                                                                                                                .executes(context -> PlayerClientCommandHelper.createNewPlayerClient(context, false, true))
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
                                        .then(literal("here")
                                                .executes(context -> PlayerClientCommandHelper.createNewPlayerClient(context, true, false))
                                                .then(literal("in")
                                                        .then(argument("gamemode", StringArgumentType.word())
                                                                .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"spectator", "survival", "any"}, builder))
                                                                .executes(context -> PlayerClientCommandHelper.createNewPlayerClient(context, true, true))
                                                        )
                                                )
                                        )
                                )
                        )
                .build();
        LiteralCommandNode<FabricClientCommandSource> removeNode = ClientCommandManager
                .literal("remove")
                        .then(argument("playername", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                .executes(context -> {
                                    PlayerClientCommandHelper data = PlayerClientCommandHelper.playerClientHelperMap.remove(context.getArgument("playername", String.class));
                                    if (data == null)
                                        context.getSource().sendFeedback(new LiteralText("§cThat player is not in your config"));
                                    else
                                        context.getSource().sendFeedback(new LiteralText("§6That player has been removed from your config"));
                                    PlayerClientCommandHelper.writeSaveFile();
                                    return 0;
                                })
                        )
                .build();

        //Stitching commands
        dispatcher.getRoot().addChild(playerclientNode);
        playerclientNode.addChild(spawnNode);
        playerclientNode.addChild(addNode);
        playerclientNode.addChild(removeNode);
        dispatcher.getRoot().addChild(pcNode);
        pcNode.addChild(spawnNode);
        pcNode.addChild(addNode);
        pcNode.addChild(removeNode);

        //Can't do this because of: https://github.com/Mojang/brigadier/issues/46
        //dispatcher.register(literal("pc").redirect(dispatcher.getRoot().getChild("playerclient").getRedirect()));
    }
}
