package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.literal;

public class AlternateDimensionCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		if (!ClientRules.COMMAND_ALTERNATE_DIMENSION.getValue()) {
			return;
		}

		CommandHelper.CLIENT_COMMANDS.add("alternatedimension");

		LiteralArgumentBuilder<ServerCommandSource> commandBuilder = literal("alternatedimension");
		commandBuilder.executes(context -> {
			ClientPlayerEntity playerEntity = EssentialUtils.getPlayer();
			String dimension;
			double newX;
			double newZ;
			if (playerEntity.world.getRegistryKey() == World.OVERWORLD) {
				dimension = "the_nether";
				newX = playerEntity.getX() / 8;
				newZ = playerEntity.getZ() / 8;
			} else if (playerEntity.world.getRegistryKey() == World.NETHER) {
				dimension = "overworld";
				newX = playerEntity.getX() * 8;
				newZ = playerEntity.getZ() * 8;
			} else {
				throw new SimpleCommandExceptionType(Texts.INVALID_DIMENSION).create();
			}
			MutableText message = Texts.COORDINATES.generate(dimension).formatted(Formatting.GREEN);
			MutableText clickable = Texts.literal(" [%s, %s, %s]".formatted(
				CommandHelper.DECIMAL_FORMAT.format(newX),
				CommandHelper.DECIMAL_FORMAT.format(playerEntity.getY()),
				CommandHelper.DECIMAL_FORMAT.format(newZ)
			)).styled(style -> style.withClickEvent(new ClickEvent(
				ClickEvent.Action.RUN_COMMAND,
				"/execute in %s run tp @s %f %f %f".formatted(dimension, newX, playerEntity.getY(), newZ)
			))).formatted(Formatting.GOLD, Formatting.BOLD);
			EssentialUtils.sendMessage(message.append(clickable));
			return 1;
		});

		ClientPlayerEntity player = EssentialUtils.getPlayer();
		if (player != null && player.hasPermissionLevel(2)) {
			commandBuilder.then(literal("teleport")
				.executes(context -> {
					ClientPlayerEntity playerEntity = EssentialUtils.getPlayer();
					String dimension;
					double newX;
					double newZ;
					if (playerEntity.world.getRegistryKey() == World.OVERWORLD) {
						dimension = "the_nether";
						newX = playerEntity.getX() / 8;
						newZ = playerEntity.getZ() / 8;
					} else if (playerEntity.world.getRegistryKey() == World.NETHER) {
						dimension = "overworld";
						newX = playerEntity.getX() * 8;
						newZ = playerEntity.getZ() * 8;
					} else {
						throw new SimpleCommandExceptionType(Texts.INVALID_DIMENSION).create();
					}
					EssentialUtils.sendChatMessage("/execute in %s run tp @s %f %f %f".formatted(
						dimension,
						newX,
						playerEntity.getY(),
						newZ
					));
					return 1;
				})
			);
		}

		dispatcher.register(commandBuilder);
	}
}
