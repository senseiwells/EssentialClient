package essentialclient.utils.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.render.ChatColour;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandHelper {
	private static CommandTreeS2CPacket fakeCommandPacket;

	public static final Set<String> clientCommands = new HashSet<>();
	public static final Set<String> functionCommands = new HashSet<>();
	public static final Set<LiteralCommandNode<ServerCommandSource>> functionCommandNodes = new HashSet<>();
	public static final DecimalFormat decimalFormat = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.UK));

	static {
		decimalFormat.setGroupingUsed(false);
	}

	public static CompletableFuture<Suggestions> suggestLocation(SuggestionsBuilder builder, String type) {
		return switch (type) {
			case "x" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(EssentialUtils.getPlayer().getX()))}, builder);
			case "y" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(EssentialUtils.getPlayer().getY()))}, builder);
			case "z" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(EssentialUtils.getPlayer().getZ()))}, builder);
			case "yaw" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(EssentialUtils.getPlayer().yaw))}, builder);
			case "pitch" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(EssentialUtils.getPlayer().pitch))}, builder);
			case "dimension" -> CommandSource.suggestMatching(new String[]{"overworld", "the_nether", "the_end"}, builder);
			default -> null;
		};
	}

	public static CompletableFuture<Suggestions> suggestOnlinePlayers(SuggestionsBuilder builder) {
		ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
		if (networkHandler == null || networkHandler.getPlayerList() == null) {
			return CommandSource.suggestMatching(new String[0], builder);
		}
		List<String> playerList = new ArrayList<>();
		networkHandler.getPlayerList().forEach(p -> playerList.add(p.getProfile().getName()));
		return CommandSource.suggestMatching(playerList.toArray(String[]::new), builder);
	}

	public static boolean isClientCommand(String command) {
		return clientCommands.contains(command);
	}

	public static void clearCommands() {
		clientCommands.clear();
	}

	public static void executeCommand(StringReader reader, String command) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		try {
			player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
		}
		catch (CommandException e) {
			EssentialUtils.sendMessage(ChatColour.RED + e.getTextMessage());
		}
		catch (CommandSyntaxException e) {
			EssentialUtils.sendMessage(ChatColour.RED + e.getMessage());
			if (e.getInput() != null && e.getCursor() >= 0) {
				int cursor = Math.min(e.getCursor(), e.getInput().length());
				MutableText text = new LiteralText("").formatted(Formatting.GRAY)
					.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
				if (cursor > 10) {
					text.append("...");
				}

				text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));
				if (cursor < e.getInput().length()) {
					text.append(new LiteralText(e.getInput().substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE));
				}

				text.append(new TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
				EssentialUtils.getPlayer().sendMessage(text, false);
			}
		}
		catch (Exception e) {
			LiteralText error = new LiteralText(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
			EssentialUtils.getPlayer().sendMessage(new TranslatableText("command.failed")
				.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))), false);
			e.printStackTrace();
		}
	}

	public static boolean tryRunFunctionCommand(String message) {
		message = message.replaceFirst("/", "");
		ArucasValueList arguments = new ArucasValueList();
		for (String argument : message.split(" ")) {
			arguments.add(new StringValue(argument));
		}
		StringValue command = (StringValue) arguments.remove(0);
		if (functionCommands.contains(command.value)) {
			List<Value<?>> parameters = List.of(command, new ListValue(arguments));
			MinecraftScriptEvents.ON_COMMAND.run(parameters);
			return true;
		}
		return false;
	}

	public static void setCommandPacket(CommandTreeS2CPacket packet) {
		if (packet == null) {
			return;
		}
		Collection<CommandNode<CommandSource>> commandNodes =  packet.getCommandTree().getChildren();
		RootCommandNode<CommandSource> newRootCommandNode = new RootCommandNode<>();
		for (CommandNode<CommandSource> commandNode : commandNodes) {
			newRootCommandNode.addChild(commandNode);
		}
		fakeCommandPacket = new CommandTreeS2CPacket(newRootCommandNode);
	}

	public static CommandTreeS2CPacket getCommandPacket() {
		return fakeCommandPacket;
	}
}
