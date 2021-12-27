package essentialclient.clientscript.extensions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import essentialclient.clientscript.ClientScript;
import essentialclient.clientscript.events.MinecraftScriptEvent;
import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.values.*;
import essentialclient.config.clientrule.ClientRule;
import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.interfaces.ChatHudAccessor;
import essentialclient.utils.keyboard.KeyboardHelper;
import essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

public class ArucasMinecraftClientMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.minecraftClientFunctions;
	}

	@Override
	public Class<MinecraftClientValue> getValueType() {
		return MinecraftClientValue.class;
	}

	@Override
	public String getName() {
		return "MinecraftClientMemberFunctions";
	}

	private final Set<MemberFunction> minecraftClientFunctions = Set.of(
		new MemberFunction("screenshot", this::screenshot),
		new MemberFunction("pressKey", "key", this::pressKey),
		new MemberFunction("releaseKey", "key", this::releaseKey),
		new MemberFunction("holdKey", List.of("key", "ms"), this::holdKey),
		new MemberFunction("clearChat", this::clearChat),
		new MemberFunction("getLatestChatMessage", this::getLatestChatMessage),
		new MemberFunction("addCommand", List.of("commandName", "arguments"), this::addCommand),
		new MemberFunction("isInSinglePlayer", (context, function) -> BooleanValue.of(this.getClient(context, function).isInSingleplayer())),
		new MemberFunction("getServerName", this::getServerName),
		new MemberFunction("getPing", this::getPing),
		new MemberFunction("getScriptsPath", (context, function) -> new StringValue(ClientScript.getDir().toString())),
		new MemberFunction("setEssentialClientRule", List.of("ruleName", "value"), this::setEssentialClientRule),
		new MemberFunction("resetEssentialClientRule", "ruleName", this::resetEssentialClientRule),
		new MemberFunction("getEssentialClientValue", "ruleName", this::getEssentialClientRuleValue),

		new MemberFunction("getPlayer", this::getPlayer),
		new MemberFunction("getWorld", this::getWorld),

		new MemberFunction("addGameEvent", List.of("eventName", "function"), this::addGameEvent),
		new MemberFunction("removeGameEvent", List.of("eventName", "id"), this::removeGameEvent),
		new MemberFunction("removeAllGameEvents", this::removeAllGameEvents),
		new MemberFunction("itemFromString", "name", this::itemFromString),
		new MemberFunction("blockFromString", "name", this::blockFromString),
		new MemberFunction("entityFromString", "name", this::entityFromString),
		new MemberFunction("textFromString", "text", this::textFromString),
		new MemberFunction("createFakeScreen", List.of("screenTitle", "rows"), this::createFakeScreen),
		new MemberFunction("playSound", List.of("soundName", "volume", "pitch"), this::playSound),
		new MemberFunction("renderFloatingItem", "itemStack", this::renderFloatingItem),
		new MemberFunction("stripFormatting", "string", this::stripFormatting),

		new MemberFunction("importUtils", "util", this::importUtils)
	);

	private Value<?> screenshot(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ScreenshotUtils.saveScreenshot(
			client.runDirectory,
			client.getWindow().getWidth(),
			client.getWindow().getHeight(),
			client.getFramebuffer(),
			text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
		);
		return NullValue.NULL;
	}

	private Value<?> pressKey(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		String key = function.getParameterValueOfType(context, StringValue.class, 1).value;
		final int keyCode = KeyboardHelper.translateStringToKey(key);
		if (keyCode == -1) {
			throw new RuntimeError("Tried to press unknown key", function.syntaxPosition, context);
		}
		client.execute(() -> {
			long handler = client.getWindow().getHandle();
			int scanCode = GLFW.glfwGetKeyScancode(keyCode);
			client.keyboard.onKey(handler, keyCode, scanCode, 1, 0);
		});
		return NullValue.NULL;
	}

	private Value<?> releaseKey(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		String key = function.getParameterValueOfType(context, StringValue.class, 1).value;
		final int keyCode = KeyboardHelper.translateStringToKey(key);
		if (keyCode == -1) {
			throw new RuntimeError("Tried to press unknown key", function.syntaxPosition, context);
		}
		client.execute(() -> {
			long handler = client.getWindow().getHandle();
			int scanCode = GLFW.glfwGetKeyScancode(keyCode);
			client.keyboard.onKey(handler, keyCode, scanCode, 0, 0);
		});
		return NullValue.NULL;
	}

	private Value<?> holdKey(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		String key = function.getParameterValueOfType(context, StringValue.class, 1).value;
		final int milliseconds = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
		int keyCode = KeyboardHelper.translateStringToKey(key);
		if (keyCode == -1) {
			throw new RuntimeError("Tried to press unknown key", function.syntaxPosition, context);
		}
		final long handler = client.getWindow().getHandle();
		final int scanCode = GLFW.glfwGetKeyScancode(keyCode);
		ClientScript.getInstance().runAsyncFunctionInContext(null, ctx -> {
			client.execute(() -> client.keyboard.onKey(handler, keyCode, scanCode, 1, 0));
			int ms = milliseconds;
			while (ms > 50) {
				client.execute(() -> client.keyboard.onKey(handler, keyCode, scanCode, 2, 0));
				Thread.sleep(50);
				ms -= 50;
			}
			client.execute(() -> client.keyboard.onKey(handler, keyCode, scanCode, 0, 0));
		});
		return NullValue.NULL;
	}

	private Value<?> clearChat(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function).inGameHud.getChatHud().clear(true);
		return NullValue.NULL;
	}

	private Value<?> getLatestChatMessage(Context context, MemberFunction function) throws CodeError {
		final ChatHudLine<?>[] chat = ((ChatHudAccessor) this.getClient(context, function).inGameHud.getChatHud()).getMessages().toArray(ChatHudLine[]::new);
		if (chat.length == 0) {
			return NullValue.NULL;
		}
		return new StringValue(((Text) chat[0].getText()).getString());
	}

	private Value<?> addCommand(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 2);

		CommandHelper.functionCommands.add(stringValue.value);
		LiteralCommandNode<ServerCommandSource> command = CommandManager.literal(stringValue.value).build();
		List<ArgumentCommandNode<ServerCommandSource, String>> arguments = new ArrayList<>();

		int i = 1;
		for (Value<?> value : listValue.value) {
			if (!(value instanceof ListValue suggestionListValue)) {
				throw new RuntimeError("You must pass in a list of lists as parameter 2 for addCommand()", function.syntaxPosition, context);
			}
			List<String> suggestionList = new ArrayList<>();
			for (Value<?> suggestion : suggestionListValue.value) {
				suggestionList.add(suggestion.toString());
			}
			arguments.add(CommandManager.argument("arg" + i, StringArgumentType.string()).suggests((c, b) -> CommandSource.suggestMatching(suggestionList.toArray(String[]::new), b)).build());
			i++;
		}

		ListIterator<ArgumentCommandNode<ServerCommandSource, String>> listIterator = arguments.listIterator(arguments.size());

		ArgumentCommandNode<ServerCommandSource, String> finalArguments = null;
		while (listIterator.hasPrevious()) {
			ArgumentCommandNode<ServerCommandSource, String> argument = listIterator.previous();
			if (finalArguments == null) {
				finalArguments = argument;
				continue;
			}
			argument.addChild(finalArguments);
			finalArguments = argument;
		}
		if (finalArguments != null) {
			command.addChild(finalArguments);
		}

		CommandHelper.functionCommandNodes.add(command);
		MinecraftClient client = this.getClient(context, function);
		ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
		client.execute(() -> player.networkHandler.onCommandTree(CommandHelper.getCommandPacket()));
		return NullValue.NULL;
	}

	private Value<?> getServerName(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ServerInfo serverInfo = client.getCurrentServerEntry();
		if (serverInfo == null) {
			throw new RuntimeError("Failed to get server name", function.syntaxPosition, context);
		}
		return new StringValue(serverInfo.name);
	}

	private Value<?> getPing(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ServerInfo serverInfo = client.getCurrentServerEntry();
		if (serverInfo == null) {
			throw new RuntimeError("Failed to get server ping", function.syntaxPosition, context);
		}
		return new NumberValue(serverInfo.ping);
	}

	private Value<?> setEssentialClientRule(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		String clientRuleValue = function.getParameterValue(context, 2).toString();
		ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
		if (clientRule == null) {
			throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
		}
		try {
			clientRule.setValueFromString(clientRuleValue);
			ClientRuleHelper.writeSaveFile();
			clientRule.run();
		}
		catch (Exception e) {
			throw new RuntimeError("Cannot set that value", function.syntaxPosition, context);
		}
		return NullValue.NULL;
	}

	private Value<?> resetEssentialClientRule(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
		if (clientRule == null) {
			throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
		}
		clientRule.resetToDefault();
		ClientRuleHelper.writeSaveFile();
		clientRule.run();
		return NullValue.NULL;
	}

	private Value<?> getEssentialClientRuleValue(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
		if (clientRule == null) {
			throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
		}
		return new StringValue(clientRule.getValue().toString());
	}

	private Value<?> addGameEvent(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		String eventName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 2);
		MinecraftScriptEvent event = MinecraftScriptEvents.getEvent(eventName);
		if (event == null) {
			throw new RuntimeError("The event name must be a predefined event", function.syntaxPosition, context);
		}
		return new NumberValue(event.addFunction(context, functionValue));
	}

	private Value<?> removeGameEvent(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		String eventName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		int eventId = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
		MinecraftScriptEvent event = MinecraftScriptEvents.getEvent(eventName);
		if (event == null) {
			throw new RuntimeError("The event name must be a predefined event", function.syntaxPosition, context);
		}
		if (!event.removeFunction(eventId)) {
			throw new RuntimeError("Invalid eventId", function.syntaxPosition, context);
		}
		return NullValue.NULL;
	}

	private Value<?> removeAllGameEvents(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		MinecraftScriptEvents.clearEventFunctions();
		return NullValue.NULL;
	}

	private Value<?> itemFromString(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		return new ItemStackValue(Registry.ITEM.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, stringValue.value)).getDefaultStack());
	}

	private Value<?> blockFromString(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		return new BlockStateValue(Registry.BLOCK.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, stringValue.value)).getDefaultState());
	}

	private Value<?> entityFromString(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ClientWorld world = ArucasMinecraftExtension.getWorld(client);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		return EntityValue.getEntityValue(Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, stringValue.value)).create(world));
	}

	private Value<?> textFromString(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		return new TextValue(new LiteralText(stringValue.value));
	}

	private Value<?> createFakeScreen(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 2);
		try {
			return new FakeInventoryScreenValue(new FakeInventoryScreen(player.inventory, stringValue.value, numberValue.value.intValue()));
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
		}
	}

	private Value<?> playSound(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		Double volume = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		Double pitch = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		SoundEvent soundEvent = Registry.SOUND_EVENT.get(new Identifier(stringValue.value));
		player.playSound(soundEvent, SoundCategory.MASTER, volume.floatValue(), pitch.floatValue());
		return NullValue.NULL;
	}

	private Value<?> renderFloatingItem(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
		client.gameRenderer.showFloatingItem(itemStack);
		return NullValue.NULL;
	}

	private Value<?> stripFormatting(Context context, MemberFunction function) throws CodeError {
		this.getClient(context, function);
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		return new StringValue(stringValue.value.replaceAll("§[0-9a-gk-or]", ""));
	}

	private Value<?> importUtils(Context context, MemberFunction function) throws CodeError{
		MinecraftClient client = this.getClient(context, function);
		String util = function.getParameterValueOfType(context, StringValue.class, 1).value;
		ResourceManager resourceManager = client.getResourceManager();
		Identifier identifier = ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, "essentialclient:clientscript/%s.arucas".formatted(util));
		if (!resourceManager.containsResource(identifier)) {
			throw new RuntimeError("That util does not exist", function.syntaxPosition, context);
		}
		try {
			InputStream stream = resourceManager.getResource(identifier).getInputStream();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				String file = reader.lines().collect(Collectors.joining("\n", "", ""));
				Context childContext = context.createChildContext("Util - %s".formatted(util));
				return Run.run(childContext, util, file);
			}
		}
		catch (IOException e) {
			throw new RuntimeError("Failed to run Util file", function.syntaxPosition, context);
		}
	}

	private Value<?> getPlayer(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		return new PlayerValue(ArucasMinecraftExtension.getPlayer(client));
	}

	private Value<?> getWorld(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = this.getClient(context, function);
		return new WorldValue(ArucasMinecraftExtension.getWorld(client));
	}

	private MinecraftClient getClient(Context context, MemberFunction function) throws CodeError {
		MinecraftClient client = function.getParameterValueOfType(context, MinecraftClientValue.class, 0).value;
		if (client == null) {
			throw new RuntimeError("Client was null", function.syntaxPosition, context);
		}
		return client;
	}
}
