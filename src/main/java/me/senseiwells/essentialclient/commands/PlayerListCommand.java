package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.essentialclient.clientrule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.command.PlayerData;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerClient;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerList;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerListCommand {
	public static final DynamicCommandExceptionType EXISTS = new DynamicCommandExceptionType(o -> new LiteralText("List %s already exists".formatted(o)));
	public static final DynamicCommandExceptionType NOT_EXIST = new DynamicCommandExceptionType(o -> new LiteralText("List %s doesn't exist".formatted(o)));
	public static final DynamicCommandExceptionType EMPTY = new DynamicCommandExceptionType(o -> new LiteralText("%s is empty".formatted(o)));
	public static final Dynamic2CommandExceptionType HAS_PLAYER = new Dynamic2CommandExceptionType((o, p) -> new LiteralText("%s is already in %s".formatted(o, p)));


	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		if (!ClientRules.COMMAND_PLAYER_LIST.getValue() || !ClientRules.COMMAND_PLAYER_CLIENT.getValue()) {
			return;
		}

		CommandHelper.clientCommands.add("pl");
		CommandHelper.clientCommands.add("playerlist");

		LiteralCommandNode<ServerCommandSource> playerlistNode = literal("playerlist").build();
		LiteralCommandNode<ServerCommandSource> plNode = literal("pl").build();

		LiteralCommandNode<ServerCommandSource> createlistNode = literal("createlist").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> CommandSource.suggestMatching(List.of("mobswitches", "allplayers"), builder))
			.executes(PlayerListCommand::createList)
		).build();

		LiteralCommandNode<ServerCommandSource> deletelistNode = literal("deletelist").then(argument("listname", StringArgumentType.word())
				.suggests((context, builder) -> ConfigPlayerList.INSTANCE.suggestList(builder))
				.executes(PlayerListCommand::deleteList)
			)
			.build();

		LiteralCommandNode<ServerCommandSource> spawnlistNode = literal("spawnlist").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerList.INSTANCE.suggestList(builder))
			.executes(PlayerListCommand::spawnFromList)
		).build();

		LiteralCommandNode<ServerCommandSource> addplayerNode = literal("addplayer").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerList.INSTANCE.suggestList(builder))
			.then(argument("playername", StringArgumentType.word())
				.suggests((context, builder) -> ConfigPlayerClient.INSTANCE.suggestPlayer(builder))
				.executes(PlayerListCommand::addToList)
			)
		).build();

		// Stitching commands
		dispatcher.getRoot().addChild(playerlistNode);
		playerlistNode.addChild(createlistNode);
		playerlistNode.addChild(deletelistNode);
		playerlistNode.addChild(spawnlistNode);
		playerlistNode.addChild(addplayerNode);
		dispatcher.getRoot().addChild(plNode);
		plNode.addChild(createlistNode);
		plNode.addChild(deletelistNode);
		plNode.addChild(spawnlistNode);
		plNode.addChild(addplayerNode);
	}

	public static int createList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String listName = context.getArgument("listname", String.class);
		List<String> players = ConfigPlayerList.INSTANCE.get(listName);
		if (players != null) {
			throw EXISTS.create(listName);
		}
		ConfigPlayerList.INSTANCE.set(listName, new ArrayList<>());
		EssentialUtils.sendMessage("§a%s has been created".formatted(listName));
		return 1;
	}

	public static int deleteList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String listName = context.getArgument("listname", String.class);
		List<String> players = ConfigPlayerList.INSTANCE.remove(listName);
		if (players == null) {
			throw NOT_EXIST.create(listName);
		}
		EssentialUtils.sendMessage("§6%s has been deleted".formatted(listName));
		return 1;
	}

	public static int addToList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String listName = context.getArgument("listname", String.class);
		List<String> players = ConfigPlayerList.INSTANCE.get(listName);
		if (players == null) {
			throw NOT_EXIST.create(listName);
		}
		String playerName = context.getArgument("playername", String.class);
		PlayerData playerData = ConfigPlayerClient.INSTANCE.get(playerName);
		if (playerData == null) {
			throw PlayerClientCommand.NO_PLAYER.create(playerName);
		}
		if (players.contains(playerName)) {
			throw HAS_PLAYER.create(playerName, listName);
		}
		players.add(playerName);
		EssentialUtils.sendMessage("§6%s has been added to %s".formatted(playerName, listName));
		return 1;
	}

	public static int spawnFromList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String listName = context.getArgument("listname", String.class);
		List<String> players = ConfigPlayerList.INSTANCE.get(listName);
		if (players == null) {
			throw NOT_EXIST.create(listName);
		}
		if (players.isEmpty()) {
			throw EMPTY.create(listName);
		}
		for (String playerName : players) {
			PlayerData playerData = ConfigPlayerClient.INSTANCE.get(playerName);
			PlayerClientCommand.sendCommand(playerName, playerData);
		}
		return 1;
	}
}
