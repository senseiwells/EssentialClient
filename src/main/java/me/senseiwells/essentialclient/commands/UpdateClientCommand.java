package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.network.ClientUpdater;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class UpdateClientCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CommandHelper.CLIENT_COMMANDS.add("updateclient");

		dispatcher.register(literal("updateclient")
			.executes(context -> {
				ClientUpdater.INSTANCE.tryUpdate();
				return 0;
			})
		);
	}
}
