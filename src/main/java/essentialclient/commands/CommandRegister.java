package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class CommandRegister {
    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        PlayerClientCommand.register(dispatcher);
        PlayerListCommand.register(dispatcher);
        RegionCommand.register(dispatcher);
        TravelCommand.register(dispatcher);
    }
}
