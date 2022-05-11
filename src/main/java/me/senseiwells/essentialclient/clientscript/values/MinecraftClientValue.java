package me.senseiwells.essentialclient.clientscript.values;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.ArucasClassExtension;
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
import me.senseiwells.essentialclient.mixins.clientScript.MinecraftClientAccessor;
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
import me.senseiwells.essentialclient.utils.network.MojangAPI;
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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	/**
	 * MinecraftClient class for Arucas. This allows for many core interactions with the MinecraftClient <br>
	 * Import the class with <code>import MinecraftClient from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
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

		/**
		 * Name: <code>MinecraftClient.getClient()</code> <br>
		 * Description: Returns the MinecraftClient instance <br>
		 * Returns - MinecraftClient: the MinecraftClient instance <br>
		 * Example: <code>MinecraftClient.getClient();</code>
		 */
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
				new MemberFunction("playerNameFromUuid", "uuid", this::playerNameFromUuid),
				new MemberFunction("uuidFromPlayerName", "playerName", this::uuidFromPlayerName),
				new MemberFunction("getServerName", this::getServerName),
				new MemberFunction("getPing", this::getPing),
				new MemberFunction("getScriptsPath", this::getScriptPath),
				new MemberFunction("setEssentialClientRule", List.of("ruleName", "value"), this::setEssentialClientRule),
				new MemberFunction("resetEssentialClientRule", "ruleName", this::resetEssentialClientRule),
				new MemberFunction("getEssentialClientValue", "ruleName", this::getEssentialClientRuleValue),
				new MemberFunction("getModList", this::getModList),
				new MemberFunction("getFps", this::getFps),

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

		/**
		 * Name: <code>&lt;MinecraftClient>.syncToTick()</code> <br>
		 * Description: Synchronizes the current thread in Arucas to the next game tick <br>
		 * Throws - Error: <code>"Tried to sync non Arucas Thread"</code> if the current thread is not safe to sync <br>
		 * Example: <code>client.syncToTick();</code>
		 */
		private Value<?> syncToTick(Context context, MemberFunction function) throws CodeError {
			ClientTickSyncer.syncToTick();
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getRunDirectory()</code> <br>
		 * Description: Returns the directory where the client is running <br>
		 * Returns - File: The Minecraft run directory <br>
		 * Example: <code>client.getRunDirectory();</code>
		 */
		private Value<?> getRunDirectory(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			return FileValue.of(client.runDirectory);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.screenshot()</code> <br>
		 * Description: This makes the client take a screenshot <br>
		 * Example: <code>client.screenshot();</code>
		 */
		private Value<?> screenshot(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ScreenshotRecorder.saveScreenshot(
				client.runDirectory,
				client.getFramebuffer(),
				text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
			);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.screenshotNamed(name)</code> <br>
		 * Description: This makes the client take a screenshot and saves it with a given name <br>
		 * Parameter - String: the name of the file <br>
		 * Example: <code>client.screenshotNamed("screenshot.png");</code>
		 */
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

		/**
		 * Name: <code>&lt;MinecraftClient>.pressKey(key)</code> <br>
		 * Description: This allows you to simulate a key press inside of Minecraft, this will only press the key down <br>
		 * Parameter - String: the key to press <br>
		 * Throws - Error: <code>"Tried to press key outside of Minecraft"</code> if the given key was unknown <br>
		 * Example: <code>client.pressKey("f");</code>
		 */
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

		/**
		 * Name: <code>&lt;MinecraftClient>.releaseKey(key)</code> <br>
		 * Description: This allows you to simulate a key release inside of Minecraft, this is useful for keys that only
		 * work on release, for example <code>F3</code> <br>
		 * Parameter - String: the key to release <br>
		 * Throws - Error: <code>"Tried to press unknown key"</code> if the given key was unknown <br>
		 * Example: <code>client.releaseKey("f");</code>
		 */
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

		/**
		 * Name: <code>&lt;MinecraftClient>.holdKey(key, milliseconds)</code> <br>
		 * Description: This allows you to simulate a key being held inside of Minecraft, this will press, hold, and release <br>
		 * Parameters - String, Number: the key to hold, the number of milliseconds you want it held for <br>
		 * Throws - Error: <code>"Tried to press unknown key"</code> if the given key was unknown <br>
		 * Example: <code>client.holdKey("f", 100);</code>
		 */
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
			context.getThreadHandler().runAsyncFunctionInThreadPool(context.createBranch(), ctx -> {
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
			});
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.clearChat()</code> <br>
		 * Description: This will clear the chat hud <br>
		 * Example: <code>client.clearChat();</code>
		 */
		private Value<?> clearChat(Context context, MemberFunction function) throws CodeError {
			this.getClient(context, function).inGameHud.getChatHud().clear(true);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getLatestChatMessage()</code> <br>
		 * Description: This will return the latest chat message <br>
		 * Returns - Text/Null: the latest chat message, null if there is none <br>
		 * Example: <code>client.getLatestChatMessage();</code>
		 */
		private Value<?> getLatestChatMessage(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			@SuppressWarnings("unchecked")
			ChatHudLine<Text>[] chat = ((ChatHudAccessor) client.inGameHud.getChatHud()).getMessages().toArray(ChatHudLine[]::new);
			if (chat.length == 0) {
				return NullValue.NULL;
			}
			return new TextValue(chat[0].getText().shallowCopy());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.addCommand(command)</code> <br>
		 * Description: This allows you to register your own client side command in game <br>
		 * Parameters - CommandBuilder/Map: the command builder, or a map that can be parsed as a command <br>
		 * Example: <code>client.addCommand(CommandBuilder.literal("example"));</code>
		 */
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

		/**
		 * Name: <code>&lt;MinecraftClient>.isInSinglePlayer()</code> <br>
		 * Description: This will return true if the client is in single player mode <br>
		 * Returns - Boolean: true if the client is in single player mode <br>
		 * Example: <code>client.isInSinglePlayer();</code>
		 */
		private Value<?> isInSinglePlayer(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getClient(context, function).isInSingleplayer());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.playerNameFromUuid(uuid)</code> <br>
		 * Description: This will return the player name from the given uuid <br>
		 * Parameters - String: the uuid as a String <br>
		 * Returns - String/Null: the player name, null if the uuid is not found <br>
		 * Example: <code>client.playerNameFromUuid("d4fca8c4-e083-4300-9a73-bf438847861c");</code>
		 */
		private Value<?> playerNameFromUuid(Context context, MemberFunction function) throws CodeError {
			String uuidAsString = function.getParameterValueOfType(context, StringValue.class, 1).value;
			String name = MojangAPI.getNameFromUuid(UUID.fromString(uuidAsString));
			if (name == null) {
				return NullValue.NULL;
			}
			return StringValue.of(name);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.uuidFromPlayerName(name)</code> <br>
		 * Description: This will return the uuid from the given player name <br>
		 * Parameters - String: the player name <br>
		 * Returns - String/Null: the uuid, null if the player name is not found <br>
		 * Example: <code>client.uuidFromPlayerName("senseiwells");</code>
		 */
		private Value<?> uuidFromPlayerName(Context context, MemberFunction function) throws CodeError {
			String name = function.getParameterValueOfType(context, StringValue.class, 1).value;
			UUID uuid = MojangAPI.getUuidFromName(name);
			if (uuid == null) {
				return NullValue.NULL;
			}
			return StringValue.of(uuid.toString());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getServerName()</code> <br>
		 * Description: This gets the current connected server's name that you have set it to in the multiplayer screen <br>
		 * Returns - String: the server name <br>
		 * Throws - Error: <code>"Failed to get server name"</code> <br>
		 * Example: <code>client.getServerName();</code>
		 */
		private Value<?> getServerName(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw new RuntimeError("Failed to get server name", function.syntaxPosition, context);
			}
			return StringValue.of(serverInfo.name);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getPing()</code> <br>
		 * Description: This gets the current connected server's ping <br>
		 * Returns - Number: the server ping in milliseconds <br>
		 * Throws - Error: <code>"Failed to get server ping"</code> <br>
		 * Example: <code>client.getPing();</code>
		 */
		private Value<?> getPing(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw new RuntimeError("Failed to get server ping", function.syntaxPosition, context);
			}
			return NumberValue.of(serverInfo.ping);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getScriptPath()</code> <br>
		 * Description: This gets the script directory path, this is where all scripts are stored <br>
		 * Returns - String: the script directory path <br>
		 * Example: <code>client.getScriptPath();</code>
		 */
		private Value<?> getScriptPath(Context context, MemberFunction function) {
			return StringValue.of(ClientScript.INSTANCE.getScriptDirectory().toString());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.setEssentialClientRule(clientRule, value)</code> <br>
		 * Description: This sets the given client rule to the given value <br>
		 * Parameters - String, Value: the client rule, the new value for the rule <br>
		 * Throws - Error: <code>"Invalid ClientRule name"</code>, <code>"Cannot set that value"</code> <br>
		 * Example: <code>client.setEssentialClientRule("highlightLavaSources", false);</code>
		 */
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

		/**
		 * Name: <code>&lt;MinecraftClient>.resetEssentialClientRule(clientRule)</code> <br>
		 * Description: This resets the given client rule to its default value <br>
		 * Parameters - String: the client rule <br>
		 * Throws - Error: <code>"Invalid ClientRule name"</code> <br>
		 * Example: <code>client.resetEssentialClientRule("highlightLavaSources");</code>
		 */
		private Value<?> resetEssentialClientRule(Context context, MemberFunction function) throws CodeError {
			String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
			}
			this.getClient(context, function).execute(clientRule::resetToDefault);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getEssentialClientRule(clientRule)</code> <br>
		 * Description: This gets the value of the given client rule <br>
		 * Parameters - String: the client rule <br>
		 * Returns - Value: the value of the client rule <br>
		 * Throws - Error: <code>"Invalid ClientRule name"</code> <br>
		 * Example: <code>client.getEssentialClientRule("overrideCreativeWalkSpeed");</code>
		 */
		private Value<?> getEssentialClientRuleValue(Context context, MemberFunction function) throws CodeError {
			String clientRuleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw new RuntimeError("Invalid ClientRule name", function.syntaxPosition, context);
			}
			return context.convertValue(clientRule.getValue());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getModList()</code> <br>
		 * Description: This gets a list of all the mod ids of the mods installed <br>
		 * Returns - List: the mod ids <br>
		 * Example: <code>client.getModList();</code>
		 */
		private Value<?> getModList(Context context, MemberFunction function) {
			ArucasList modList = new ArucasList();
			for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
				modList.add(StringValue.of(modContainer.getMetadata().getId()));
			}
			return new ListValue(modList);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getFps()</code> <br>
		 * Description: This gets the current fps <br>
		 * Returns - Number: the fps <br>
		 * Example: <code>client.getFps();</code>
		 */
		private Value<?> getFps(Context context, MemberFunction function) {
			return NumberValue.of(MinecraftClientAccessor.getFps());
		}

		/**
		 * Deprecated: You should use the GameEvent class instead: <code>GameEvent.unregisterAll();</code> <br>
		 * Name: <code>&lt;MinecraftClient>.unregisterAllGameEvents()</code> <br>
		 * Description: This unregisters all game events <br>
		 * Example: <code>client.unregisterAllGameEvents();</code>
		 */
		@Deprecated
		private Value<?> removeAllGameEvents(Context context, MemberFunction function) {
			MinecraftScriptEvents.clearEventFunctions(context);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.stringToNbt(string)</code> <br>
		 * Description: This parses a string and turns it into a Nbt compound <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Value: the Nbt compound <br>
		 * Example: <code>client.stringToNbt("{\"test\":\"test\"}");</code>
		 */
		private Value<?> stringToNbt(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return NbtUtils.nbtToValue(context, NbtUtils.rawStringToNbt(stringValue.value), 10);
		}

		/**
		 * Deprecated: You should use the ItemStack class instead: <code>ItemStack.of("dirt");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.itemFromString(string)</code> <br>
		 * Description: This creates an item stack from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - ItemStack: the item stack <br>
		 * Example: <code>client.itemFromString("dirt");</code>
		 */
		@Deprecated
		private Value<?> itemFromString(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return new ItemStackValue(Registry.ITEM.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).getDefaultStack());
		}

		/**
		 * Deprecated: You should use the Block class instead: <code>Block.of("dirt");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.blockFromString(string)</code> <br>
		 * Description: This creates a block from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Block: the block <br>
		 * Example: <code>client.blockFromString("dirt");</code>
		 */
		@Deprecated
		private Value<?> blockFromString(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return new BlockValue(Registry.BLOCK.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).getDefaultState());
		}

		/**
		 * Deprecated: You should use the Entity class instead: <code>Entity.of("pig");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.entityFromString(string)</code> <br>
		 * Description: This creates an entity from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Entity: the entity <br>
		 * Example: <code>client.entityFromString("pig");</code>
		 */
		@Deprecated
		private Value<?> entityFromString(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ClientWorld world = ArucasMinecraftExtension.getWorld(client);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return context.convertValue(Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).create(world));
		}

		/**
		 * Deprecated: You should use the Text class instead: <code>Text.of("Any Text!");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.textFromString(string)</code> <br>
		 * Description: This creates a text from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Text: the text <br>
		 * Example: <code>client.textFromString("Any Text!");</code>
		 */
		@Deprecated
		private Value<?> textFromString(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return new TextValue(new LiteralText(stringValue.value));
		}

		/**
		 * Deprecated: You should use the FakeScreen class instead: <code>new FakeScreen("Name", 3)</code> <br>
		 * Name: <code>&lt;MinecraftClient>.createFakeScreen(screenName, rows)</code> <br>
		 * Description: This creates a fake screen with the given name and number of rows of slots available (1 - 6) <br>
		 * Parameters - String, Number: the name of the screen, number of rows <br>
		 * Returns - FakeScreen: the fake screen <br>
		 * Example: <code>client.createFakeScreen("Name", 3);</code>
		 */
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

		/**
		 * Name: <code>&lt;MinecraftClient>.playSound(soundId, volume, pitch)</code> <br>
		 * Description: This plays the given sound with the given volume and pitch around the player <br>
		 * Parameters - String, Number, Number: the sound id [here](https://minecraft.fandom.com/wiki/Sounds.json#Sound_events),
		 * volume of the sound, pitch of the sound <br>
		 * Example: <code>client.playSound("entity.lightning_bolt.thunder", 1, 1);</code>
		 */
		private Value<?> playSound(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			Double volume = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			Double pitch = function.getParameterValueOfType(context, NumberValue.class, 3).value;
			SoundEvent soundEvent = Registry.SOUND_EVENT.get(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value));
			player.playSound(soundEvent, SoundCategory.MASTER, volume.floatValue(), pitch.floatValue());
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.renderFloatingItem(itemStack)</code> <br>
		 * Description: This renders an item in front of the player using the totem of undying animation <br>
		 * Parameters - ItemStack: the item stack to render <br>
		 * Example: <code>client.renderFloatingItem(Material.DIAMOND.asItemStack());</code>
		 */
		private Value<?> renderFloatingItem(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
			client.gameRenderer.showFloatingItem(itemStack);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.stripFormatting(string)</code> <br>
		 * Description: This strips the formatting from the given string <br>
		 * Parameters - String: the string to strip <br>
		 * Returns - String: the stripped string <br>
		 * Example: <code>client.stripFormatting("§cHello§r");</code>
		 */
		private Value<?> stripFormatting(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			return StringValue.of(stringValue.value.replaceAll("§[0-9a-gk-or]", ""));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getCursorStack()</code> <br>
		 * Description: This returns the item stack that is currently being held by the cursor <br>
		 * Returns - ItemStack: the item stack, will be Air if there is nothing <br>
		 * Example: <code>client.getCursorStack();</code>
		 */
		private Value<?> getCursorStack(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			return new ItemStackValue(InventoryUtils.getCursorStack(client));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.setCursorStack(itemStack)</code> <br>
		 * Description: This sets the item stack that is currently being held by the cursor, this does not work
		 * in normal screens only in FakeScreens, this does not actually pick up an item just display like you have <br>
		 * Parameters - ItemStack: the item stack to set <br>
		 * Example: <code>client.setCursorStack(Material.DIAMOND.asItemStack());</code>
		 */
		private Value<?> setCursorStack(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (client.currentScreen instanceof FakeInventoryScreen) {
				ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
				return BooleanValue.of(InventoryUtils.setCursorStack(client, itemStack));
			}
			return BooleanValue.FALSE;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getClientRenderDistance()</code> <br>
		 * Description: This returns the current render distance set on the client <br>
		 * Returns - Number: the render distance <br>
		 * Example: <code>client.getClientRenderDistance();</code>
		 */
		private Value<?> getClientRenderDistance(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			return NumberValue.of(client.options.viewDistance);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.setClientRenderDistance(number)</code> <br>
		 * Description: This sets the render distance on the client <br>
		 * Parameters - Number: the render distance <br>
		 * Example: <code>client.setClientRenderDistance(10);</code>
		 */
		private Value<?> setClientRenderDistance(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			client.options.viewDistance = numberValue.value.intValue();
			client.worldRenderer.scheduleTerrainUpdate();
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.runOnMainThread(function)</code> <br>
		 * Description: This runs the given function on the main thread <br>
		 * Parameters - Function: the function to run <br>
		 * Example: <code>client.runOnMainThread(fun() { print("Do something"); });</code>
		 */
		private Value<?> runOnMainThread(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			Context branchedContext = context.createBranch();
			client.execute(() -> {
				functionValue.safeCall(branchedContext, ArrayList::new);
			});
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.tick()</code> <br>
		 * Description: This ticks the client <br>
		 * Example: <code>client.tick();</code>
		 */
		private Value<?> tick(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			client.execute(client::tick);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getPlayer()</code> <br>
		 * Description: This returns the current player on the client <br>
		 * Returns - Player: the main player <br>
		 * Example: <code>client.getPlayer();</code>
		 */
		private Value<?> getPlayer(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			return new PlayerValue(ArucasMinecraftExtension.getPlayer(client));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getWorld()</code> <br>
		 * Description: This returns the world that is currently being played on <br>
		 * Returns - World: the world <br>
		 * Example: <code>client.getWorld();</code>
		 */
		private Value<?> getWorld(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = this.getClient(context, function);
			return new WorldValue(ArucasMinecraftExtension.getWorld(client));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getVersion()</code> <br>
		 * Description: This returns the current version of Minecraft you are playing <br>
		 * Returns - String: the version for example: <code>"1.17.1"</code> <br>
		 * Example: <code>client.getVersion();</code>
		 */
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
