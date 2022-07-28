package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec2f;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RegionCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

		if (!ClientRules.COMMAND_REGION.getValue()) {
			return;
		}

		CommandHelper.CLIENT_COMMANDS.add("region");

		dispatcher.register(literal("region")
			.then(literal("get")
				.then(argument("pos", Vec2ArgumentType.vec2())
					.executes(context -> {
						Vec2f v = Vec2ArgumentType.getVec2(context, "pos");
						EssentialUtils.sendMessage(ChatColour.GOLD + "Those coordinates are in region: " + ChatColour.GREEN + (int) Math.floor(v.x / 512) + "." + (int) Math.floor(v.y / 512));
						return 0;
					})
				)
				.executes(context -> {
					ClientPlayerEntity playerEntity = EssentialUtils.getPlayer();
					EssentialUtils.sendMessage(ChatColour.GOLD + "You are in region: " + ChatColour.GREEN + (int) Math.floor(playerEntity.getX() / 512) + "." + (int) Math.floor(playerEntity.getZ() / 512));
					return 0;
				})
			)
		);
	}
}
