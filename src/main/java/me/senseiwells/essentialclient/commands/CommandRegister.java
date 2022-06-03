package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegister {
	private static CommandRegistryAccess registryAccess;

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
		registryAccess = access;

		CommandHelper.clearClientCommands();
		PlayerClientCommand.register(dispatcher);
		PlayerListCommand.register(dispatcher);
		RegionCommand.register(dispatcher);
		ClientNickCommand.register(dispatcher);
		UpdateClientCommand.register(dispatcher);
		AlternateDimensionCommand.register(dispatcher);
		ClientScriptCommand.register(dispatcher);
		CommandHelper.registerFunctionCommands(dispatcher);
	}

	public static CommandRegistryAccess getRegistryAccess() {
		return registryAccess;
	}
}
