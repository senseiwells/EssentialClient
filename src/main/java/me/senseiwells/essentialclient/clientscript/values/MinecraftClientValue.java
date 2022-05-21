package me.senseiwells.essentialclient.clientscript.values;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
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
import java.util.UUID;

import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.MINECRAFT_CLIENT;

public class MinecraftClientValue extends GenericValue<MinecraftClient> {
	public static MinecraftClientValue INSTANCE = new MinecraftClientValue(EssentialUtils.getClient());

	private MinecraftClientValue(MinecraftClient client) {
		super(client);
	}

	@Override
	public GenericValue<MinecraftClient> copy(Context context) throws CodeError {
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
	public boolean isEquals(Context context, Value value) {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return MINECRAFT_CLIENT;
	}

	/**
	 * MinecraftClient class for Arucas. This allows for many core interactions with the MinecraftClient <br>
	 * Import the class with <code>import MinecraftClient from Minecraft;</code> <br>
	 * Fully Documented.
	 *
	 * @author senseiwells
	 */
	public static class ArucasMinecraftClientMembers extends ArucasClassExtension {
		public ArucasMinecraftClientMembers() {
			super(MINECRAFT_CLIENT);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("getClient", this::getClientStatic)
			);
		}

		/**
		 * Name: <code>MinecraftClient.getClient()</code> <br>
		 * Description: Returns the MinecraftClient instance <br>
		 * Returns - MinecraftClient: the MinecraftClient instance <br>
		 * Example: <code>MinecraftClient.getClient();</code>
		 */
		private Value getClientStatic(Arguments arguments) {
			return MinecraftClientValue.INSTANCE;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("syncToTick", this::syncToTick),
				MemberFunction.of("getRunDirectory", this::getRunDirectory),
				MemberFunction.of("screenshot", this::screenshot),
				MemberFunction.of("screenshot", 1, this::screenshotNamed),
				MemberFunction.of("pressKey", 1, this::pressKey),
				MemberFunction.of("releaseKey", 1, this::releaseKey),
				MemberFunction.of("holdKey", 2, this::holdKey),
				MemberFunction.of("clearChat", this::clearChat),
				MemberFunction.of("getLatestChatMessage", this::getLatestChatMessage),
				MemberFunction.of("addCommand", 1, this::addCommand),
				MemberFunction.of("isInSinglePlayer", this::isInSinglePlayer),
				MemberFunction.of("playerNameFromUuid", 1, this::playerNameFromUuid),
				MemberFunction.of("uuidFromPlayerName", 1, this::uuidFromPlayerName),
				MemberFunction.of("getServerIp", this::getServerIp),
				MemberFunction.of("getServerName", this::getServerName),
				MemberFunction.of("getPing", this::getPing),
				MemberFunction.of("getScriptsPath", this::getScriptPath),
				MemberFunction.of("setEssentialClientRule", 2, this::setEssentialClientRule),
				MemberFunction.of("resetEssentialClientRule", 1, this::resetEssentialClientRule),
				MemberFunction.of("getEssentialClientValue", 1, this::getEssentialClientRuleValue),
				MemberFunction.of("getModList", this::getModList),
				MemberFunction.of("getFps", this::getFps),

				MemberFunction.of("getPlayer", this::getPlayer),
				MemberFunction.of("getWorld", this::getWorld),
				MemberFunction.of("getVersion", this::getVersion),

				MemberFunction.of("parseStringToNbt", 1, this::stringToNbt),
				MemberFunction.of("removeAllGameEvents", this::removeAllGameEvents, "Use 'GameEvent.unregisterAll()'"),
				MemberFunction.of("itemFromString", 1, this::itemFromString, "Use 'ItemStack.of(material)'"),
				MemberFunction.of("blockFromString", 1, this::blockFromString, "Use 'Block.of(material)'"),
				MemberFunction.of("entityFromString", 1, this::entityFromString, "Use 'Entity.of(str)'"),
				MemberFunction.of("textFromString", 1, this::textFromString, "Use 'Text.of(str)'"),
				MemberFunction.of("createFakeScreen", 2, this::createFakeScreen, "Use 'new FakeScreen(str, int)'"),
				MemberFunction.of("playSound", 3, this::playSound),
				MemberFunction.of("renderFloatingItem", 1, this::renderFloatingItem),
				MemberFunction.of("stripFormatting", 1, this::stripFormatting),
				MemberFunction.of("getCursorStack", this::getCursorStack),
				MemberFunction.of("setCursorStack", 1, this::setCursorStack),
				MemberFunction.of("getClientRenderDistance", this::getClientRenderDistance),
				MemberFunction.of("setClientRenderDistance", 1, this::setClientRenderDistance),
				MemberFunction.of("runOnMainThread", 1, this::runOnMainThread),
				MemberFunction.of("tick", this::tick)
			);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.syncToTick()</code> <br>
		 * Description: Synchronizes the current thread in Arucas to the next game tick <br>
		 * Throws - Error: <code>"Tried to sync non Arucas Thread"</code> if the current thread is not safe to sync <br>
		 * Example: <code>client.syncToTick();</code>
		 */
		private Value syncToTick(Arguments arguments) throws CodeError {
			ClientTickSyncer.syncToTick();
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getRunDirectory()</code> <br>
		 * Description: Returns the directory where the client is running <br>
		 * Returns - File: The Minecraft run directory <br>
		 * Example: <code>client.getRunDirectory();</code>
		 */
		private Value getRunDirectory(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return FileValue.of(client.runDirectory);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.screenshot()</code> <br>
		 * Description: This makes the client take a screenshot <br>
		 * Example: <code>client.screenshot();</code>
		 */
		private Value screenshot(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
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
		private Value screenshotNamed(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			StringValue stringValue = arguments.getNextString();
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
		private Value pressKey(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			String key = arguments.getNextGeneric(StringValue.class);
			final int keyCode = KeyboardHelper.translateStringToKey(key);
			if (keyCode == -1) {
				throw arguments.getError("Tried to press unknown key");
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
		private Value releaseKey(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			String key = arguments.getNextGeneric(StringValue.class);
			final int keyCode = KeyboardHelper.translateStringToKey(key);
			if (keyCode == -1) {
				throw arguments.getError("Tried to press unknown key");
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
		private Value holdKey(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			String key = arguments.getNextGeneric(StringValue.class);
			int milliseconds = arguments.getNextGeneric(NumberValue.class).intValue();
			int keyCode = KeyboardHelper.translateStringToKey(key);
			if (keyCode == -1) {
				throw arguments.getError("Tried to press unknown key");
			}
			long handler = client.getWindow().getHandle();
			int scanCode = GLFW.glfwGetKeyScancode(keyCode);
			Context context = arguments.getContext();
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
					throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", arguments.getPosition());
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
		private Value clearChat(Arguments arguments) throws CodeError {
			this.getClient(arguments).inGameHud.getChatHud().clear(true);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getLatestChatMessage()</code> <br>
		 * Description: This will return the latest chat message <br>
		 * Returns - Text/Null: the latest chat message, null if there is none <br>
		 * Example: <code>client.getLatestChatMessage();</code>
		 */
		private Value getLatestChatMessage(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
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
		private Value addCommand(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			Value value = arguments.getNext();
			CommandNode<ServerCommandSource> commandNode;
			if (value instanceof CommandBuilderValue builderValue) {
				commandNode = builderValue.value.build();
			}
			else if (value instanceof MapValue mapValue) {
				commandNode = ClientScriptUtils.mapToCommand(mapValue.value, arguments.getContext(), arguments.getPosition()).build();
			}
			else {
				throw arguments.getError("Expected CommandBuilder or command map");
			}
			if (!(commandNode instanceof LiteralCommandNode<ServerCommandSource> literalCommandNode)) {
				throw arguments.getError("Expected a literal command builder as root");
			}
			CommandHelper.addComplexCommand(arguments.getContext(), literalCommandNode);
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
		private Value isInSinglePlayer(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getClient(arguments).isInSingleplayer());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.playerNameFromUuid(uuid)</code> <br>
		 * Description: This will return the player name from the given uuid <br>
		 * Parameters - String: the uuid as a String <br>
		 * Returns - String/Null: the player name, null if the uuid is not found <br>
		 * Example: <code>client.playerNameFromUuid("d4fca8c4-e083-4300-9a73-bf438847861c");</code>
		 */
		private Value playerNameFromUuid(Arguments arguments) throws CodeError {
			String uuidAsString = arguments.skip().getNextGeneric(StringValue.class);
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
		private Value uuidFromPlayerName(Arguments arguments) throws CodeError {
			String name = arguments.skip().getNextGeneric(StringValue.class);
			UUID uuid = MojangAPI.getUuidFromName(name);
			if (uuid == null) {
				return NullValue.NULL;
			}
			return StringValue.of(uuid.toString());
		}

		@FunctionDoc(
			name = "getServerIp",
			desc = "This will return the server ip",
			returns = {STRING, "The server ip, null if in single player"},
			example = "client.getServerIp();"
		)
		private Value getServerIp(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				return NullValue.NULL;
			}
			return StringValue.of(serverInfo.address);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getServerName()</code> <br>
		 * Description: This gets the current connected server's name that you have set it to in the multiplayer screen <br>
		 * Returns - String: the server name <br>
		 * Throws - Error: <code>"Failed to get server name"</code> <br>
		 * Example: <code>client.getServerName();</code>
		 */
		private Value getServerName(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw arguments.getError("Failed to get server name");
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
		private Value getPing(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw arguments.getError("Failed to get server ping");
			}
			return NumberValue.of(serverInfo.ping);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getScriptPath()</code> <br>
		 * Description: This gets the script directory path, this is where all scripts are stored <br>
		 * Returns - String: the script directory path <br>
		 * Example: <code>client.getScriptPath();</code>
		 */
		private Value getScriptPath(Arguments arguments) {
			return StringValue.of(ClientScript.INSTANCE.getScriptDirectory().toString());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.setEssentialClientRule(clientRule, value)</code> <br>
		 * Description: This sets the given client rule to the given value <br>
		 * Parameters - String, Value: the client rule, the new value for the rule <br>
		 * Throws - Error: <code>"Invalid ClientRule name"</code>, <code>"Cannot set that value"</code> <br>
		 * Example: <code>client.setEssentialClientRule("highlightLavaSources", false);</code>
		 */
		private Value setEssentialClientRule(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			String clientRuleName = arguments.getNextGeneric(StringValue.class);
			String clientRuleValue = arguments.getNext().getAsString(arguments.getContext());
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw arguments.getError("Invalid ClientRule name");
			}
			try {
				client.execute(() -> clientRule.setValueFromString(clientRuleValue));
			}
			catch (RuntimeException e) {
				throw arguments.getError("Cannot set that value: ", e);
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
		private Value resetEssentialClientRule(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			String clientRuleName = arguments.getNextGeneric(StringValue.class);
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw arguments.getError("Invalid ClientRule name");
			}
			client.execute(clientRule::resetToDefault);
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
		private Value getEssentialClientRuleValue(Arguments arguments) throws CodeError {
			String clientRuleName = arguments.skip().getNextGeneric(StringValue.class);
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw arguments.getError("Invalid ClientRule name");
			}
			return arguments.getContext().convertValue(clientRule.getValue());
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getModList()</code> <br>
		 * Description: This gets a list of all the mod ids of the mods installed <br>
		 * Returns - List: the mod ids <br>
		 * Example: <code>client.getModList();</code>
		 */
		private Value getModList(Arguments arguments) {
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
		private Value getFps(Arguments arguments) {
			return NumberValue.of(MinecraftClientAccessor.getFps());
		}

		/**
		 * Deprecated: You should use the GameEvent class instead: <code>GameEvent.unregisterAll();</code> <br>
		 * Name: <code>&lt;MinecraftClient>.unregisterAllGameEvents()</code> <br>
		 * Description: This unregisters all game events <br>
		 * Example: <code>client.unregisterAllGameEvents();</code>
		 */
		private Value removeAllGameEvents(Arguments arguments) {
			MinecraftScriptEvents.clearEventFunctions(arguments.getContext());
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.stringToNbt(string)</code> <br>
		 * Description: This parses a string and turns it into a Nbt compound <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Value: the Nbt compound <br>
		 * Example: <code>client.stringToNbt("{\"test\":\"test\"}");</code>
		 */
		private Value stringToNbt(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return NbtUtils.nbtToValue(arguments.getContext(), NbtUtils.rawStringToNbt(stringValue.value), 10);
		}

		/**
		 * Deprecated: You should use the ItemStack class instead: <code>ItemStack.of("dirt");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.itemFromString(string)</code> <br>
		 * Description: This creates an item stack from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - ItemStack: the item stack <br>
		 * Example: <code>client.itemFromString("dirt");</code>
		 */
		private Value itemFromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return new ItemStackValue(Registry.ITEM.get(ArucasMinecraftExtension.getId(arguments, stringValue.value)).getDefaultStack());
		}

		/**
		 * Deprecated: You should use the Block class instead: <code>Block.of("dirt");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.blockFromString(string)</code> <br>
		 * Description: This creates a block from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Block: the block <br>
		 * Example: <code>client.blockFromString("dirt");</code>
		 */
		private Value blockFromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return new BlockValue(Registry.BLOCK.get(ArucasMinecraftExtension.getId(arguments, stringValue.value)).getDefaultState());
		}

		/**
		 * Deprecated: You should use the Entity class instead: <code>Entity.of("pig");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.entityFromString(string)</code> <br>
		 * Description: This creates an entity from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Entity: the entity <br>
		 * Example: <code>client.entityFromString("pig");</code>
		 */
		private Value entityFromString(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ClientWorld world = ArucasMinecraftExtension.getWorld(client);
			StringValue stringValue = arguments.getNextString();
			return arguments.getContext().convertValue(Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getId(arguments, stringValue.value)).create(world));
		}

		/**
		 * Deprecated: You should use the Text class instead: <code>Text.of("Any Text!");</code> <br>
		 * Name: <code>&lt;MinecraftClient>.textFromString(string)</code> <br>
		 * Description: This creates a text from the given string <br>
		 * Parameters - String: the string to parse <br>
		 * Returns - Text: the text <br>
		 * Example: <code>client.textFromString("Any Text!");</code>
		 */
		private Value textFromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
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
		private Value createFakeScreen(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
			StringValue stringValue = arguments.getNextString();
			NumberValue numberValue = arguments.getNextNumber();
			try {
				return new FakeInventoryScreenValue(new FakeInventoryScreen(player.getInventory(), stringValue.value, numberValue.value.intValue()));
			}
			catch (IllegalArgumentException e) {
				throw arguments.getError(e.getMessage());
			}
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.playSound(soundId, volume, pitch)</code> <br>
		 * Description: This plays the given sound with the given volume and pitch around the player <br>
		 * Parameters - String, Number, Number: the sound id [here](https://minecraft.fandom.com/wiki/Sounds.json#Sound_events),
		 * volume of the sound, pitch of the sound <br>
		 * Example: <code>client.playSound("entity.lightning_bolt.thunder", 1, 1);</code>
		 */
		private Value playSound(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer(client);
			StringValue stringValue = arguments.getNextString();
			Double volume = arguments.getNextGeneric(NumberValue.class);
			Double pitch = arguments.getNextGeneric(NumberValue.class);
			SoundEvent soundEvent = Registry.SOUND_EVENT.get(ArucasMinecraftExtension.getId(arguments, stringValue.value));
			player.playSound(soundEvent, SoundCategory.MASTER, volume.floatValue(), pitch.floatValue());
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.renderFloatingItem(itemStack)</code> <br>
		 * Description: This renders an item in front of the player using the totem of undying animation <br>
		 * Parameters - ItemStack: the item stack to render <br>
		 * Example: <code>client.renderFloatingItem(Material.DIAMOND.asItemStack());</code>
		 */
		private Value renderFloatingItem(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ItemStack itemStack = arguments.getNextGeneric(ItemStackValue.class);
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
		private Value stripFormatting(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return StringValue.of(stringValue.value.replaceAll("§[0-9a-gk-or]", ""));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getCursorStack()</code> <br>
		 * Description: This returns the item stack that is currently being held by the cursor <br>
		 * Returns - ItemStack: the item stack, will be Air if there is nothing <br>
		 * Example: <code>client.getCursorStack();</code>
		 */
		private Value getCursorStack(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return new ItemStackValue(InventoryUtils.getCursorStack(client));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.setCursorStack(itemStack)</code> <br>
		 * Description: This sets the item stack that is currently being held by the cursor, this does not work
		 * in normal screens only in FakeScreens, this does not actually pick up an item just display like you have <br>
		 * Parameters - ItemStack: the item stack to set <br>
		 * Example: <code>client.setCursorStack(Material.DIAMOND.asItemStack());</code>
		 */
		private Value setCursorStack(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			if (client.currentScreen instanceof FakeInventoryScreen) {
				ItemStack itemStack = arguments.getNextGeneric(ItemStackValue.class);
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
		private Value getClientRenderDistance(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return NumberValue.of(client.options.viewDistance);
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.setClientRenderDistance(number)</code> <br>
		 * Description: This sets the render distance on the client <br>
		 * Parameters - Number: the render distance <br>
		 * Example: <code>client.setClientRenderDistance(10);</code>
		 */
		private Value setClientRenderDistance(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			NumberValue numberValue = arguments.getNextNumber();
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
		private Value runOnMainThread(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			FunctionValue functionValue = arguments.getNextFunction();
			Context branchedContext = arguments.getContext().createBranch();
			client.execute(() -> functionValue.callSafe(branchedContext, ArrayList::new));
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.tick()</code> <br>
		 * Description: This ticks the client <br>
		 * Example: <code>client.tick();</code>
		 */
		private Value tick(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			client.execute(client::tick);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getPlayer()</code> <br>
		 * Description: This returns the current player on the client <br>
		 * Returns - Player: the main player <br>
		 * Example: <code>client.getPlayer();</code>
		 */
		private Value getPlayer(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return new PlayerValue(ArucasMinecraftExtension.getPlayer(client));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getWorld()</code> <br>
		 * Description: This returns the world that is currently being played on <br>
		 * Returns - World: the world <br>
		 * Example: <code>client.getWorld();</code>
		 */
		private Value getWorld(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return new WorldValue(ArucasMinecraftExtension.getWorld(client));
		}

		/**
		 * Name: <code>&lt;MinecraftClient>.getVersion()</code> <br>
		 * Description: This returns the current version of Minecraft you are playing <br>
		 * Returns - String: the version for example: <code>"1.17.1"</code> <br>
		 * Example: <code>client.getVersion();</code>
		 */
		private Value getVersion(Arguments arguments) {
			return StringValue.of(EssentialUtils.getMinecraftVersion());
		}

		private MinecraftClient getClient(Arguments arguments) throws CodeError {
			MinecraftClient client = arguments.getNextGeneric(MinecraftClientValue.class);
			if (client == null) {
				throw arguments.getError("Client was null");
			}
			return client;
		}

		@Override
		public Class<MinecraftClientValue> getValueClass() {
			return MinecraftClientValue.class;
		}
	}
}
