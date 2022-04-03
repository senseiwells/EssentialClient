package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.misc.ClientUpdater;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class UpdateClientCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CommandHelper.clientCommands.add("updateclient");

		dispatcher.register(literal("updateclient")
			.executes(context -> {
				ClientUpdater.INSTANCE.tryUpdate();
				return 0;
			})
		);
	}
}
