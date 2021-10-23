package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.utils.command.CommandHelper;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegister {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        CommandHelper.clearCommands();
        PlayerClientCommand.register(dispatcher);
        PlayerListCommand.register(dispatcher);
        RegionCommand.register(dispatcher);
        TravelCommand.register(dispatcher);
        MusicCommand.register(dispatcher);
        CommandHelper.functionCommands.forEach(command -> dispatcher.getRoot().addChild(command));
    }
}
