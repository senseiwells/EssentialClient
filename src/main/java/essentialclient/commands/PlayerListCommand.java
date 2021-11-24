package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.command.PlayerListCommandHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        if (!ClientRules.COMMAND_PLAYER_LIST.getValue() || !ClientRules.COMMAND_PLAYER_CLIENT.getValue()) {
            return;
        }

        CommandHelper.clientCommands.add("pl");
        CommandHelper.clientCommands.add("playerlist");

        LiteralCommandNode<ServerCommandSource> playerlistNode =
            literal("playerlist")//.requires((p) -> ClientRules.COMMAND_PLAYERCLIENT.getBoolean() && ClientRules.COMMAND_PLAYERLIST.getBoolean())
            .build();
        LiteralCommandNode<ServerCommandSource> plNode =
            literal("pl")//.requires((p) -> ClientRules.COMMAND_PLAYERCLIENT.getBoolean() && ClientRules.COMMAND_PLAYERLIST.getBoolean())
            .build();
        LiteralCommandNode<ServerCommandSource> createlistNode =
            literal("createlist")
                .then(argument("listname", StringArgumentType.word())
                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"mobswitches", "allplayers"}, builder))
                    .executes(PlayerListCommandHelper::createList)
                )
            .build();
        LiteralCommandNode<ServerCommandSource> deletelistNode =
            literal("deletelist")
                .then(argument("listname", StringArgumentType.word())
                    .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                    .executes(PlayerListCommandHelper::deleteList)
                )
            .build();
        LiteralCommandNode<ServerCommandSource> spawnlistNode =
            literal("spawnlist")
                .then(argument("listname", StringArgumentType.word())
                    .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                    .executes(PlayerListCommandHelper::spawnFromList)
                )
            .build();
        LiteralCommandNode<ServerCommandSource> addplayerNode =
            literal("addplayer")
                .then(argument("listname", StringArgumentType.word())
                    .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                    .then(argument("playername", StringArgumentType.word())
                        .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                        .executes(PlayerListCommandHelper::addToList)
                    )
                )
            .build();

        //Stitching commands
        dispatcher.getRoot().addChild(playerlistNode);
        playerlistNode.addChild(createlistNode);
        playerlistNode.addChild(deletelistNode);
        playerlistNode.addChild(spawnlistNode);
        playerlistNode.addChild(addplayerNode);
        dispatcher.getRoot().addChild(plNode);
        plNode.addChild(createlistNode);
        plNode.addChild(deletelistNode);
        plNode.addChild(spawnlistNode);
        plNode.addChild(addplayerNode);
    }
}
