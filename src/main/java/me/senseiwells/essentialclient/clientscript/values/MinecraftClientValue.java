package me.senseiwells.essentialclient.clientscript.values;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.rule.client.ClientRule;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientTickSyncer;
import me.senseiwells.essentialclient.utils.clientscript.NbtUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.interfaces.ChatHudAccessor;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.essentialclient.utils.keyboard.KeyboardHelper;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
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
import java.util.List;
import java.util.stream.Collectors;

public class MinecraftClientValue extends Value<MinecraftClient> {
	public static MinecraftClientValue INSTANCE = new MinecraftClientValue(EssentialUtils.getClient());

	private MinecraftClientValue(MinecraftClient client) {
		super(client);
	}

	@Override
	public Value<MinecraftClient> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "MinecraftClient";
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> value) {
		return this.value == value.value;
	}

	@Override
	public String getTypeName() {
		return "MinecraftClient";
	}

	public static class ArucasMinecraftClientMembers extends ArucasClassExtension {
		public ArucasMinecraftClientMembers() {
			super("MinecraftClient");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("getClient", this::getClient)
			);
		}

		private Value<?> getClient(Context context, BuiltInFunction function) {
			return MinecraftClientValue.INSTANCE;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("syncToTick", this::syncToTick),
				new MemberFunction("getRunDirectory", this::getRunDirectory),
				new MemberFunction("screenshot", this::screenshot),
				new MemberFunction("screenshot", "name", this::screenshotNamed),
				new MemberFunction("pressKey", "key", this::pressKey),
				new MemberFunction("releaseKey", "key", this::releaseKey),
				new MemberFunction("holdKey", List.of("key", "ms"), this::holdKey),
				new MemberFunction("clearChat", this::clearChat),
				new MemberFunction("getLatestChatMessage", this::getLatestChatMessage),
				new MemberFunction("addCommand", "commandNode", this::addCommand),
				new MemberFunction("isInSinglePlayer", this::isInSinglePlayer),
				new MemberFunction("getServerName", this::getServerName),
				new MemberFunction("getPing", this::getPing),
				new MemberFunction("getScriptsPath", this::getScriptPath),
				new MemberFunction("setEssentialClientRule", List.of("ruleName", "value"), this::setEssentialClientRule),
				new MemberFunction("resetEssentialClientRule", "ruleName", this::resetEssentialClientRule),
				new MemberFunction("getEssentialClientValue", "ruleName", this::getEssentialClientRuleValue),
				new MemberFunction("getModList", this::getModList),

				new MemberFunction("getPlayer", this::getPlayer),
				new MemberFunction("getWorld", this::getWorld),
				new MemberFunction("getVersion", this::getVersion),

				new MemberFunction("parseStringToNbt", "string", this::stringToNbt),
				new MemberFunction("removeAllGameEvents", this::removeAllGameEvents, "Use 'GameEvent.unregisterAll()'"),
				new MemberFunction("itemFromString", "name", this::itemFromString, "Use 'ItemStack.of(material)'"),
				new MemberFunction("blockFromString", "name", this::blockFromString, "Use 'Block.of(material)'"),
				new MemberFunction("entityFromString", "name", this::entityFromString, "Use 'Entity.of(str)'"),
				new MemberFunction("textFromString", "text", this::textFromString, "Use 'Text.of(str)'"),
				new MemberFunction("createFakeScreen", List.of("screenTitle", "rows"), this::createFakeScreen, "Use 'new FakeScreen(str, int)'"),
				new MemberFunction("playSound", List.of("soundName", "volume", "pitch"), this::playSound),
				new MemberFunction("renderFloatingItem", "itemStack", this::renderFloatingItem),
				new MemberFunction("stripFormatting", "string", this::stripFormatting),
				new MemberFunction("getCursorStack", this::getCursorStack),
				new MemberFunction("setCursorStack", "itemStack", this::setCursorStack),
				new MemberFunction("getClientRenderDistance", this::getClientRenderDistance),
				new MemberFunction("setClientRenderDistance", "distance", this::setClientRenderDistance),
				new MemberFunction("runOnMainThread", "function", this::runOnMainThread),
				new MemberFunction("tick", this::tick)
			);
		}

		private Value<?> syncToTick(Context context, MemberFunction function) throws CodeError {
			ClientTickSyncer.syncToTick();
			return NullValue.NULL;
		}

		private Value<?> getRunDirectory(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			return FileValue.of(client.runDirectory);
		}

		private Value<?> screenshot(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ScreenshotRecorder.saveScreenshot(
				client.runDirectory,
				client.getFramebuffer(),
				text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
			);
			return NullValue.NULL;
		}

		private Value<?> screenshotNamed(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			ScreenshotRecorder.saveScreenshot(
				client.runDirectory,
				stringValue.value,
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
			context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), ctx -> {
				client.execute(() -> client.keyboard.onKey(handler, keyCode, scanCode, 1, 0));
				int ms = milliseconds;
				try {
					while (ms > 50) {
						client.execute(() -> client.keyboard.onKey(handler, keyCode, scanCode, 2, 0));
						Thread.sleep(50);
						ms -= 50;
					}
				}
				catch (InterruptedException interruptedException) {
					throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", function.syntaxPosition);
				}
				client.execute(() -> client.keyboard.onKey(handler, keyCode, scanCode, 0, 0));
			}, "Holding Key Thread");
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
			return StringValue.of(((Text) chat[0].getText()).getString());
		}

		private Value<?> addCommand(Context context, MemberFunction function) throws CodeError {
			Value<?> value = function.getParameterValue(context, 1);
			CommandNode<ServerCommandSource> commandNode;
			if (value instanceof CommandBuilderValue builderValue) {
				commandNode = builderValue.value.build();
			}
			else if (value instanceof MapValue mapValue) {
				commandNode = ClientScriptUtils.mapToCommand(mapValue.value, context, function.syntaxPosition).build();
			}
			else {
				throw new RuntimeError("Expected CommandBuilder or map of a command", function.syntaxPosition, context);
			}
			if (!(commandNode instanceof LiteralCommandNode<ServerCommandSource> literalCommandNode)) {
				throw new RuntimeError("Expected a literal command builder as root", function.syntaxPosition, context);
			}
			CommandHelper.addComplexCommand(context, literalCommandNode);
			MinecraftClient client = this.getClient(context, function);
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
			client.execute(() -> player.networkHandler.onCommandTree(CommandHelper.getCommandPacket()));
			return NullValue.NULL;
		}

		private Value<?> isInSinglePlayer(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getClient(context, function).isInSingleplayer());
		}

		private Value<?> getServerName(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw new RuntimeError("Failed to get server name", function.syntaxPosition, context);
			}
			return StringValue.of(serverInfo.name);
		}

		private Value<?> getPing(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw new RuntimeError("Failed to get server ping", function.syntaxPosition, context);
			}
			return NumberValue.of(serverInfo.ping);
		}

		private Value<?> getScriptPath(Context context, MemberFunction function) {
			return StringValue.of(ClientScript.INSTANCE.getScriptDirectory().toString());
		}

		private Value<?> setEssentialClientRule(Context context, MemberFunction function) throws CodeError {
			String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			String clientRuleValue = function.getParameterValue(context, 2).getAsString(context);
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
			}
			try {
				this.getClient(context, function).execute(() -> clientRule.setValueFromString(clientRuleValue));
			}
			catch (RuntimeException e) {
				throw new RuntimeError("Cannot set that value", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> resetEssentialClientRule(Context context, MemberFunction function) throws CodeError {
			String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
			}
			this.getClient(context, function).execute(clientRule::resetToDefault);
			return NullValue.NULL;
		}

		private Value<?> getEssentialClientRuleValue(Context context, MemberFunction function) throws CodeError {
			String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
			}
			return StringValue.of(clientRule.getValue().toString());
		}

		private Value<?> getModList(Context context, MemberFunction function) {
			ArucasList modList = new ArucasList();
			for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
				modList.add(StringValue.of(modContainer.getMetadata().getId()));
			}
			return new ListValue(modList);
		}

		@Deprecated
		private Value<?> removeAllGameEvents(Context context, MemberFunction function) {
			MinecraftScriptEvents.clearEventFunctions(context);
			return NullValue.NULL;
		}

		private Value<?> stringToNbt(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return NbtUtils.nbtToValue(context, NbtUtils.rawStringToNbt(stringValue.value), 10);
		}

		@Deprecated
		private Value<?> itemFromString(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return new ItemStackValue(Registry.ITEM.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).getDefaultStack());
		}

		@Deprecated
		private Value<?> blockFromString(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return new BlockValue(Registry.BLOCK.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).getDefaultState());
		}

		@Deprecated
		private Value<?> entityFromString(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ClientWorld world = ArucasMinecraftExtension.getWorld(client);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return context.convertValue(Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).create(world));
		}

		@Deprecated
		private Value<?> textFromString(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return new TextValue(new LiteralText(stringValue.value));
		}

		@Deprecated
		private Value<?> createFakeScreen(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 2);
			try {
				return new FakeInventoryScreenValue(new FakeInventoryScreen(player.getInventory(), stringValue.value, numberValue.value.intValue()));
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
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return StringValue.of(stringValue.value.replaceAll("§[0-9a-gk-or]", ""));
		}

		private Value<?> getCursorStack(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			return new ItemStackValue(InventoryUtils.getCursorStack(client));
		}

		private Value<?> setCursorStack(Context context, MemberFunction function) throws CodeError {
			// In 1.17+ this will be done through the screen handler
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (client.currentScreen instanceof FakeInventoryScreen) {
				ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
				return BooleanValue.of(InventoryUtils.setCursorStack(client, itemStack));
			}
			return BooleanValue.FALSE;
		}

		private Value<?> getClientRenderDistance(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			return NumberValue.of(client.options.viewDistance);
		}

		private Value<?> setClientRenderDistance(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			client.options.viewDistance = numberValue.value.intValue();
			client.worldRenderer.scheduleTerrainUpdate();
			return NullValue.NULL;
		}

		private Value<?> runOnMainThread(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			client.execute(() -> {
				Context branchedContext = context.createBranch();
				try {
					functionValue.call(branchedContext, new ArucasList());
				}
				catch (CodeError codeError) {
					branchedContext.getThreadHandler().tryError(branchedContext, codeError);
				}
			});
			return NullValue.NULL;
		}

		private Value<?> tick(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			client.execute(client::tick);
			return NullValue.NULL;
		}

		@Deprecated
		private Value<?> importUtils(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			String util = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ResourceManager resourceManager = client.getResourceManager();
			Identifier identifier = ArucasMinecraftExtension.getId(context, function.syntaxPosition, "me.senseiwells.essentialclient:clientscript/%s.arucas".formatted(util));
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

		private Value<?> getVersion(Context context, MemberFunction function) {
			return StringValue.of(EssentialUtils.getMinecraftVersion());
		}

		private MinecraftClient getClient(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = function.getParameterValueOfType(context, MinecraftClientValue.class, 0).value;
			if (client == null) {
				throw new RuntimeError("Client was null", function.syntaxPosition, context);
			}
			return client;
		}

		@Override
		public Class<MinecraftClientValue> getValueClass() {
			return MinecraftClientValue.class;
		}
	}
}
