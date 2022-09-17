package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.command.EnumArgumentType;
import me.senseiwells.essentialclient.utils.command.PlayerData;
import me.senseiwells.essentialclient.utils.command.WorldEnum;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerClient;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerClientCommand {
	public static final DynamicCommandExceptionType NO_PLAYER = new DynamicCommandExceptionType(Texts.NO_CONFIG::generate);
	public static final Dynamic2CommandExceptionType WRONG_GAMEMODE = new Dynamic2CommandExceptionType(Texts.WRONG_GAMEMODE::generate);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		if (!ClientRules.COMMAND_PLAYER_CLIENT.getValue()) {
			return;
		}

		CommandHelper.CLIENT_COMMANDS.add("playerclient");
		CommandHelper.CLIENT_COMMANDS.add("pc");

		LiteralCommandNode<ServerCommandSource> playerclientNode = CommandManager.literal("playerclient").build();
		LiteralCommandNode<ServerCommandSource> pcNode = CommandManager.literal("pc").build();

		LiteralCommandNode<ServerCommandSource> spawnNode = CommandManager.literal("spawn").then(argument("playername", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerClient.INSTANCE.suggestPlayer(builder))
			.executes(context -> spawnPlayer(context, false))
			.then(literal("offset")
				.then(argument("x-axis", DoubleArgumentType.doubleArg())
					.suggests((context, builder) -> CommandSource.suggestMatching(List.of("0", "160", "-160", "256", "-256"), builder))
					.then(argument("y-axis", DoubleArgumentType.doubleArg())
						.suggests((context, builder) -> CommandSource.suggestMatching(List.of("0", "128"), builder))
						.then(argument("z-axis", DoubleArgumentType.doubleArg())
							.suggests((context, builder) -> CommandSource.suggestMatching(List.of("0", "160", "-160", "256", "-256"), builder))
							.executes(context -> spawnPlayer(context, true))
						)
					)
				)
			)
		).build();

		LiteralCommandNode<ServerCommandSource> addNode = CommandManager.literal("add").then(argument("playername", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerClient.INSTANCE.suggestPlayer(builder))
			.then(literal("spawn")
				.then(literal("at")
					.then(argument("pos", Vec3ArgumentType.vec3())
						.then(literal("facing")
							.then(argument("rotation", RotationArgumentType.rotation())
								.then(literal("in")
									.then(argument("dimension", EnumArgumentType.enumeration(WorldEnum.class))
										.executes(context -> constructPlayer(context, false, false))
										.then(literal("in")
											.then(argument("gamemode", EnumArgumentType.enumeration(GameMode.class))
												.executes(context -> constructPlayer(context, false, true))
											)
										)
									)
								)
							)
						)
					)
				)
				.then(literal("here")
					.executes(context -> constructPlayer(context, true, false))
					.then(literal("in")
						.then(argument("gamemode", EnumArgumentType.enumeration(GameMode.class))
							.executes(context -> constructPlayer(context, true, true))
						)
					)
				)
			)
		).build();

		LiteralCommandNode<ServerCommandSource> removeNode = literal("remove").then(argument("playername", StringArgumentType.word())
			.suggests((context, builder) -> ConfigPlayerClient.INSTANCE.suggestPlayer(builder))
			.executes(context -> {
				String playerName = context.getArgument("playername", String.class);
				if (ConfigPlayerClient.INSTANCE.remove(playerName) == null) {
					throw NO_PLAYER.create(playerName);
				}
				EssentialUtils.sendMessage(Texts.REMOVED_CONFIG.generate(playerName).formatted(Formatting.GOLD));
				return 0;
			})
		).build();

		//Stitching commands
		dispatcher.getRoot().addChild(playerclientNode);
		playerclientNode.addChild(spawnNode);
		playerclientNode.addChild(addNode);
		playerclientNode.addChild(removeNode);
		dispatcher.getRoot().addChild(pcNode);
		pcNode.addChild(spawnNode);
		pcNode.addChild(addNode);
		pcNode.addChild(removeNode);

		//Can't do this because of: https://github.com/Mojang/brigadier/issues/46
		//dispatcher.register(literal("pc").redirect(dispatcher.getRoot().getChild("playerclient").getRedirect()));
	}

	public static int constructPlayer(CommandContext<ServerCommandSource> context, boolean isHere, boolean hasGamemode) {
		ServerCommandSource source = context.getSource();
		if (!(source.getEntity() instanceof ClientPlayerEntity player)) {
			return 0;
		}
		String playerName = context.getArgument("playername", String.class);
		Vec3d playerPos = isHere ? source.getPosition() : Vec3ArgumentType.getVec3(context, "pos");
		Vec2f playerRotation = isHere ? source.getRotation() : RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
		WorldEnum playerWorld = isHere ? WorldEnum.fromRegistryKey(player.world.getRegistryKey()) : EnumArgumentType.getEnumeration(context, "dimension", WorldEnum.class);
		GameMode playerGamemode = hasGamemode ? null : EnumArgumentType.getEnumeration(context, "gamemode", GameMode.class);
		PlayerData playerData = new PlayerData(playerPos, playerRotation, playerWorld, playerGamemode);
		ConfigPlayerClient.INSTANCE.set(playerName, playerData);
		return 1;
	}

	public static int spawnPlayer(CommandContext<ServerCommandSource> context, boolean hasOffset) throws CommandSyntaxException {
		String playerName = context.getArgument("playername", String.class);
		PlayerData playerData = ConfigPlayerClient.INSTANCE.get(playerName);
		return !hasOffset ? sendCommand(playerName, playerData) : sendCommand(
			playerName,
			playerData,
			context.getArgument("x-axis", Double.class),
			context.getArgument("y-axis", Double.class),
			context.getArgument("z-axis", Double.class)
		);
	}

	public static int sendCommand(String playerName, PlayerData playerData) throws CommandSyntaxException {
		return sendCommand(playerName, playerData, 0, 0, 0);
	}

	private static int sendCommand(String playerName, PlayerData playerData, double x, double y, double z) throws CommandSyntaxException {
		if (playerData == null) {
			throw NO_PLAYER.create(playerName);
		}
		GameMode playerGameMode = EssentialUtils.getPlayerListEntry().getGameMode();
		if (playerData.gamemode() != null && playerGameMode != playerData.gamemode()) {
			throw WRONG_GAMEMODE.create(playerGameMode, playerData.gamemode());
		}
		EssentialUtils.sendChatMessage("/player %s spawn at %s %s %s facing %s %s in %s".formatted(
			playerName,
			playerData.pos().x + x,
			playerData.pos().y + y,
			playerData.pos().z + z,
			playerData.rotation().y,
			playerData.rotation().x,
			playerData.world().name().toLowerCase()
		));
		return 1;
	}
}
