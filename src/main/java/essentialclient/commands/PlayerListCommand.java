package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import essentialclient.gui.clientrule.ClientRule;
import essentialclient.gui.clientrule.ClientRules;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.command.PlayerListCommandHelper;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class PlayerListCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("playerlist").requires((p) -> ClientRule.getBoolean(ClientRules.commandPlayerClient) && ClientRule.getBoolean(ClientRules.commandPlayerList))
                .then(literal("createlist")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"mobswitches", "allplayers"}, builder))
                                .executes(PlayerListCommandHelper::createList)
                        )
                )
                .then(literal("deletelist")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                                .executes(PlayerListCommandHelper::deleteList)
                        )
                )
                .then(literal("addplayer")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                                .then(argument("playername", StringArgumentType.word())
                                        .suggests((context, builder) -> PlayerClientCommandHelper.suggestPlayerClient(builder))
                                        .executes(PlayerListCommandHelper::addToList)
                                )
                        )
                )
                .then(literal("spawnlist")
                        .then(argument("listname", StringArgumentType.word())
                                .suggests((context, builder) -> PlayerListCommandHelper.suggestPlayerList(builder))
                                .executes(PlayerListCommandHelper::spawnFromList)
                        )
                )
        );
    }
}
