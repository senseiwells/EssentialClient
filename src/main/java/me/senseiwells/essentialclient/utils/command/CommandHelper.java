package me.senseiwells.essentialclient.utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandHelper {

	private static final Map<UUID, Set<LiteralCommandNode<ServerCommandSource>>> FUNCTION_COMMAND_NODES = new HashMap<>();
	public static final Set<String> CLIENT_COMMANDS = new HashSet<>();
	public static final Set<String> FUNCTION_COMMANDS = new HashSet<>();
	public static final DecimalFormat decimalFormat = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.UK));

	private static CommandTreeS2CPacket fakeCommandPacket;
	public static MethodHandle argumentHandle;

	static {
		decimalFormat.setGroupingUsed(false);
	}

	public static CompletableFuture<Suggestions> suggestOnlinePlayers(SuggestionsBuilder builder) {
		ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
		if (networkHandler == null || networkHandler.getPlayerList() == null) {
			return CommandSource.suggestMatching(List.of(), builder);
		}
		List<String> playerList = new ArrayList<>();
		networkHandler.getPlayerList().forEach(p -> playerList.add(p.getProfile().getName()));
		return CommandSource.suggestMatching(playerList.toArray(String[]::new), builder);
	}

	public static boolean isClientCommand(String command) {
		if (CLIENT_COMMANDS.contains(command)) {
			return true;
		}
		for (Set<LiteralCommandNode<ServerCommandSource>> commandNodes : FUNCTION_COMMAND_NODES.values()) {
			for (LiteralCommandNode<ServerCommandSource> commandNode : commandNodes) {
				if (commandNode.getLiteral().equals(command)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void addComplexCommand(UUID uuid, LiteralCommandNode<ServerCommandSource> commandNode) {
		Set<LiteralCommandNode<ServerCommandSource>> commandNodeSet = FUNCTION_COMMAND_NODES.get(uuid);
		commandNodeSet = commandNodeSet != null ? commandNodeSet : new HashSet<>();
		commandNodeSet.add(commandNode);
		FUNCTION_COMMAND_NODES.put(uuid, commandNodeSet);
	}

	public static void registerFunctionCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		for (Set<LiteralCommandNode<ServerCommandSource>> commandNodes : FUNCTION_COMMAND_NODES.values()) {
			for (LiteralCommandNode<ServerCommandSource> commandNode : commandNodes) {
				dispatcher.getRoot().addChild(commandNode);
			}
		}
	}

	public static void clearClientCommands() {
		CLIENT_COMMANDS.clear();
	}

	public static void removeComplexCommand(Context context) {
		FUNCTION_COMMAND_NODES.remove(context.getContextId());
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
				EssentialUtils.sendMessage(text);
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
		ArucasList arguments = new ArucasList();
		for (String argument : message.split(" ")) {
			arguments.add(StringValue.of(argument));
		}
		StringValue command = (StringValue) arguments.remove(0);
		if (FUNCTION_COMMANDS.contains(command.value)) {
			MinecraftScriptEvents.ON_COMMAND.run(command, new ListValue(arguments));
			return true;
		}
		return false;
	}

	public static void setCommandPacket(CommandTreeS2CPacket packet) {
		if (packet == null) {
			return;
		}
		Collection<CommandNode<CommandSource>> commandNodes = packet.getCommandTree().getChildren();
		RootCommandNode<CommandSource> newRootCommandNode = new RootCommandNode<>();
		for (CommandNode<CommandSource> commandNode : commandNodes) {
			newRootCommandNode.addChild(commandNode);
		}
		fakeCommandPacket = new CommandTreeS2CPacket(newRootCommandNode);
	}

	public static CommandTreeS2CPacket getCommandPacket() {
		return fakeCommandPacket;
	}

	public static Collection<ParsedArgument<?, ?>> getArguments(CommandContext<ServerCommandSource> context) {
		try {
			if (argumentHandle == null) {
				Field field = CommandContext.class.getDeclaredField("arguments");
				field.setAccessible(true);
				MethodHandles.Lookup lookup = MethodHandles.lookup();
				argumentHandle = lookup.unreflectGetter(field);
			}
			@SuppressWarnings("unchecked")
			Map<String, ParsedArgument<?, ?>> parsedArgumentMap = (Map<String, ParsedArgument<?, ?>>) argumentHandle.invokeExact(context);
			return parsedArgumentMap.values();
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return null;
	}
}
