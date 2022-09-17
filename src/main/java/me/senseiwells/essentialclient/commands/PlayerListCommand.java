package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.command.PlayerData;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerClient;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerList;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerListCommand {
	public static final DynamicCommandExceptionType EXISTS = new DynamicCommandExceptionType(Texts.LIST_EXISTS::generate);
	public static final DynamicCommandExceptionType NOT_EXIST = new DynamicCommandExceptionType(Texts.LIST_NOT_EXISTS::generate);
	public static final DynamicCommandExceptionType EMPTY = new DynamicCommandExceptionType(Texts.LIST_EMPTY::generate);
	public static final Dynamic2CommandExceptionType HAS_PLAYER = new Dynamic2CommandExceptionType(Texts.LIST_HAS_PLAYER::generate);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		if (!ClientRules.COMMAND_PLAYER_LIST.getValue() || !ClientRules.COMMAND_PLAYER_CLIENT.getValue()) {
			return;
		}

		CommandHelper.CLIENT_COMMANDS.add("pl");
		CommandHelper.CLIENT_COMMANDS.add("playerlist");

		LiteralCommandNode<ServerCommandSource> playerListNode = literal("playerlist").build();
		LiteralCommandNode<ServerCommandSource> plNode = literal("pl").build();

		LiteralCommandNode<ServerCommandSource> createListNode = literal("createlist").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> CommandSource.suggestMatching(List.of("mobswitches", "allplayers"), builder))
			.executes(PlayerListCommand::createList)
		).build();

		LiteralCommandNode<ServerCommandSource> deleteListNode = literal("deletelist").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerList.INSTANCE.suggestList(builder))
			.executes(PlayerListCommand::deleteList)
		).build();

		LiteralCommandNode<ServerCommandSource> spawnListNode = literal("spawnlist").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerList.INSTANCE.suggestList(builder))
			.executes(PlayerListCommand::spawnFromList)
		).build();

		LiteralCommandNode<ServerCommandSource> playerNode = literal("addplayer").then(argument("listname", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerList.INSTANCE.suggestList(builder))
			.then(argument("playername", StringArgumentType.word())
				.suggests((context, builder) -> ConfigPlayerClient.INSTANCE.suggestPlayer(builder))
				.executes(PlayerListCommand::addToList)
			)
		).build();

		// Stitching commands
		dispatcher.getRoot().addChild(playerListNode);
		playerListNode.addChild(createListNode);
		playerListNode.addChild(deleteListNode);
		playerListNode.addChild(spawnListNode);
		playerListNode.addChild(playerNode);
		dispatcher.getRoot().addChild(plNode);
		plNode.addChild(createListNode);
		plNode.addChild(deleteListNode);
		plNode.addChild(spawnListNode);
		plNode.addChild(playerNode);
	}

	public static int createList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String listName = context.getArgument("listname", String.class);
		List<String> players = ConfigPlayerList.INSTANCE.get(listName);
		if (players != null) {
			throw EXISTS.create(listName);
		}
		ConfigPlayerList.INSTANCE.set(listName, new ArrayList<>());
		EssentialUtils.sendMessage(Texts.LIST_CREATED.generate(listName).formatted(Formatting.GREEN));
		return 1;
	}

	public static int deleteList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String listName = context.getArgument("listname", String.class);
		List<String> players = ConfigPlayerList.INSTANCE.remove(listName);
		if (players == null) {
			throw NOT_EXIST.create(listName);
		}
		EssentialUtils.sendMessage(Texts.LIST_DELETED.generate(listName).formatted(Formatting.RED));
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
		EssentialUtils.sendMessage(Texts.LIST_PLAYER_ADDED.generate(playerName, listName).formatted(Formatting.GOLD));
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
