package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import essentialclient.gui.clientrule.ClientRules;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.command.PlayerListCommandHelper;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;

public class PlayerListCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> playerlistNode = ClientCommandManager
                .literal("playerlist").requires((p) -> ClientRules.COMMANDPLAYERCLIENT.getBoolean() && ClientRules.COMMANDPLAYERLIST.getBoolean())
                .build();
        LiteralCommandNode<FabricClientCommandSource> plNode = ClientCommandManager
                .literal("pl").requires((p) -> ClientRules.COMMANDPLAYERCLIENT.getBoolean() && ClientRules.COMMANDPLAYERLIST.getBoolean())
                .build();
        LiteralCommandNode<FabricClientCommandSource> createlistNode = ClientCommandManager
                .literal("createlist")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"mobswitches", "allplayers"}, builder))
                                .executes(PlayerListCommandHelper::createList)
                        )
                .build();
        LiteralCommandNode<FabricClientCommandSource> deletelistNode = ClientCommandManager
                .literal("deletelist")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                                .executes(PlayerListCommandHelper::deleteList)
                        )
                .build();
        LiteralCommandNode<FabricClientCommandSource> spawnlistNode = ClientCommandManager
                .literal("spawnlist")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                                .executes(PlayerListCommandHelper::spawnFromList)
                        )
                .build();
        LiteralCommandNode<FabricClientCommandSource> addplayerNode = ClientCommandManager
                .literal("addplayer")
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
