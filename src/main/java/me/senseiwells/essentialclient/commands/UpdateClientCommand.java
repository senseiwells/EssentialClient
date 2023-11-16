package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class UpdateClientCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		// CommandHelper.CLIENT_COMMANDS.add("updateclient");
		//
		// dispatcher.register(literal("updateclient")
		// 	.executes(context -> {
		// 		ClientUpdater.INSTANCE.tryUpdate();
		// 		return 0;
		// 	})
		// );
	}
}
