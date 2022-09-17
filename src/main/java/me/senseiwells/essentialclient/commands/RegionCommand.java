package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
						Text coords = Texts.literal((int) Math.floor(v.x / 512) + "." + (int) Math.floor(v.y / 512)).formatted(Formatting.GREEN);
						EssentialUtils.sendMessage(Texts.DISTANT_REGION.generate(coords).formatted(Formatting.GOLD));
						return 0;
					})
				)
				.executes(context -> {
					ClientPlayerEntity playerEntity = EssentialUtils.getPlayer();
					Text coords = Texts.literal((int) Math.floor(playerEntity.getX() / 512) + "." + (int) Math.floor(playerEntity.getZ() / 512)).formatted(Formatting.GREEN);
					EssentialUtils.sendMessage(Texts.CURRENT_REGION.generate(coords).formatted(Formatting.GOLD));
					return 0;
				})
			)
		);
	}
}
