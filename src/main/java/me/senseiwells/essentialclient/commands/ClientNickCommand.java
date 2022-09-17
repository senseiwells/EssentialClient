package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ClientNickCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

		if (!ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			return;
		}

		CommandHelper.CLIENT_COMMANDS.add("clientnick");

		dispatcher.register(literal("clientnick")
			.then(literal("set")
				.then(argument("playername1", StringArgumentType.string())
					.suggests((context, builder) -> CommandHelper.suggestOnlinePlayers(builder))
					.then(argument("playername2", StringArgumentType.greedyString())
						.executes(context -> {
							String playerName1 = context.getArgument("playername1", String.class);
							String playerName2 = context.getArgument("playername2", String.class);
							playerName2 = playerName2.replaceAll("&", "§") + "§r";
							ConfigClientNick.INSTANCE.set(playerName1, playerName2);
							EssentialUtils.sendMessage(Texts.NEW_DISPLAY.generate(playerName1, playerName2).formatted(Formatting.GREEN));
							return 0;
						})
					)
				)
			)
			.then(literal("delete")
				.then(argument("playername", StringArgumentType.string())
					.suggests((context, builder) -> ConfigClientNick.INSTANCE.suggestPlayerRename(builder))
					.executes(context -> {
						String playerName = context.getArgument("playername", String.class);
						String name = ConfigClientNick.INSTANCE.remove(playerName);
						if (name == null) {
							EssentialUtils.sendMessage(Texts.NOT_RENAMED.generate(playerName).formatted(Formatting.RED));
						} else {
							EssentialUtils.sendMessage(Texts.NO_LONGER_RENAMED.generate(playerName).formatted(Formatting.GOLD));
						}
						return 0;
					})
				)
			)
			.then(literal("get")
				.then(argument("playername", StringArgumentType.string())
					.suggests((context, builder) -> ConfigClientNick.INSTANCE.suggestPlayerRename(builder))
					.executes(context -> {
						String playerName = context.getArgument("playername", String.class);
						String name = ConfigClientNick.INSTANCE.get(playerName);
						if (name == null) {
							EssentialUtils.sendMessage(Texts.IS_NOT_RENAMED.generate(playerName).formatted(Formatting.RED));
						} else {
							EssentialUtils.sendMessage(Texts.IS_RENAMED_TO.generate(playerName, name).formatted(Formatting.GOLD));
						}
						return 0;
					})
				)
			)
		);
	}
}
