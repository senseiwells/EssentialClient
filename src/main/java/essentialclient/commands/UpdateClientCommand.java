package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class UpdateClientCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CommandHelper.clientCommands.add("updateclient");

		dispatcher.register(literal("updateclient")
			.executes(context -> {
				EssentialUtils.tryUpdateClient();
				return 0;
			})
		);
	}
}
