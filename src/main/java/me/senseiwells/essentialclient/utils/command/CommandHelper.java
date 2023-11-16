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
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHelper {
	private static final Map<UUID, Set<LiteralCommandNode<ServerCommandSource>>> FUNCTION_COMMAND_NODES = new ConcurrentHashMap<>();
	public static final Set<String> CLIENT_COMMANDS = new HashSet<>();
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.UK));

	private static CommandTreeS2CPacket fakeCommandPacket;
	private static MethodHandle argumentHandle;

	static {
		DECIMAL_FORMAT.setGroupingUsed(false);
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

	public static void addComplexCommand(Interpreter interpreter, LiteralCommandNode<ServerCommandSource> commandNode) {
		if (interpreter.isRunning()) {
			Set<LiteralCommandNode<ServerCommandSource>> commandNodeSet = FUNCTION_COMMAND_NODES.computeIfAbsent(interpreter.getProperties().getId(), id -> {
				interpreter.addStopEvent(() -> FUNCTION_COMMAND_NODES.remove(id));
				return ConcurrentHashMap.newKeySet();
			});
			commandNodeSet.add(commandNode);
		}
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

	public static void executeCommand(StringReader reader, String command) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		try {
			player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
		} catch (CommandException e) {
			EssentialUtils.sendMessage(ChatColour.RED + e.getTextMessage());
		} catch (CommandSyntaxException e) {
			EssentialUtils.sendMessage(ChatColour.RED + e.getMessage());
			if (e.getInput() != null && e.getCursor() >= 0) {
				int cursor = Math.min(e.getCursor(), e.getInput().length());
				MutableText text = Text.literal("").formatted(Formatting.GRAY)
					.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
				if (cursor > 10) {
					text.append("...");
				}

				text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));
				if (cursor < e.getInput().length()) {
					text.append(Text.literal(e.getInput().substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE));
				}

				text.append(Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
				EssentialUtils.sendMessage(text);
			}
		} catch (Exception e) {
			Text error = Text.literal(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
			EssentialUtils.getPlayer().sendMessage(Text.translatable("command.failed")
				.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))), false);
			e.printStackTrace();
		}
	}

	public static void setCommandPacket(CommandTreeS2CPacket packet, CommandRegistryAccess registryAccess) {
		if (packet == null) {
			return;
		}
		Collection<CommandNode<CommandSource>> commandNodes = packet.getCommandTree(registryAccess).getChildren();

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
		} catch (Throwable throwable) {
			EssentialClient.LOGGER.error(throwable);
		}
		return null;
	}
}
