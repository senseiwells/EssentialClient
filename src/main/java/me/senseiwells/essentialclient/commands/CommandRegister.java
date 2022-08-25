package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.utils.command.CommandHelper;

//#if MC >= 11900
import net.minecraft.command.CommandRegistryAccess;
//#endif

import net.minecraft.server.command.ServerCommandSource;

public class CommandRegister {
	//#if MC >= 11900
	private static CommandRegistryAccess registryAccess;

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
		registryAccess = access;
		//#else
		//$$public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		//#endif

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

	//#if MC >= 11900
	public static CommandRegistryAccess getRegistryAccess() {
		return registryAccess;
	}
	//#endif
}
