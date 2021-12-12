package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.render.ChatColour;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class PlayerClientCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        if (!ClientRules.COMMAND_PLAYER_CLIENT.getValue()) {
            return;
        }

        CommandHelper.clientCommands.add("playerclient");
        CommandHelper.clientCommands.add("pc");

        LiteralCommandNode<ServerCommandSource> playerclientNode = CommandManager
            .literal("playerclient")
            .build();
        LiteralCommandNode<ServerCommandSource> pcNode = CommandManager
            .literal("pc")
            .build();
        LiteralCommandNode<ServerCommandSource> spawnNode = CommandManager
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
        LiteralCommandNode<ServerCommandSource> addNode = CommandManager
            .literal("add")
                .then(argument("playername", StringArgumentType.word())
                    .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                    .then(literal("spawn")
                        .then(literal("at")
                            .then(argument("pos", Vec3ArgumentType.vec3())
                                .then(literal("facing")
                                    // Can't use RotationArgumentType - Needs Server
                                    .then(argument("yaw", DoubleArgumentType.doubleArg())
                                        .suggests((context, builder) -> CommandHelper.suggestLocation(builder, "yaw"))
                                        .then(argument("pitch", DoubleArgumentType.doubleArg())
                                            .suggests((context, builder) -> CommandHelper.suggestLocation(builder, "pitch"))
                                            .then(literal("in")
                                                // Can't use DimensionArgumentType - Needs Server
                                                .then(argument("dimension", StringArgumentType.string())
                                                    .suggests((context, builder) -> CommandHelper.suggestLocation(builder, "dimension"))
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
        LiteralCommandNode<ServerCommandSource> removeNode =
            literal("remove")
                .then(argument("playername", StringArgumentType.word())
                    .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                    .executes(context -> {
                        PlayerClientCommandHelper data = PlayerClientCommandHelper.playerClientHelperMap.remove(context.getArgument("playername", String.class));
                        if (data == null)
                            EssentialUtils.sendMessage(ChatColour.RED + "That player is not in your config");
                        else
                            EssentialUtils.sendMessage(ChatColour.GOLD + "That player has been removed from your config");
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
