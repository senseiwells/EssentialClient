package me.senseiwells.essentialclient.clientscript.values;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
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
import me.senseiwells.essentialclient.EssentialClient;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

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

	@ClassDoc(
		name = MINECRAFT_CLIENT,
		desc = "This allows for many core interactions with the MinecraftClient",
		importPath = "Minecraft"
	)
	public static class ArucasMinecraftClientMembers extends ArucasClassExtension {
		public ArucasMinecraftClientMembers() {
			super(MINECRAFT_CLIENT);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("getClient", this::getClientStatic),
				BuiltInFunction.of("get", this::getClientStaticShort)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "getClient",
			desc = "Returns the MinecraftClient instance",
			returns = {MINECRAFT_CLIENT, "the MinecraftClient instance"},
			example = "MinecraftClient.getClient();"
		)
		private Value getClientStatic(Arguments arguments) {
			return MinecraftClientValue.INSTANCE;
		}

		@FunctionDoc(
			isStatic = true,
			name = "get",
			desc = "Returns the MinecraftClient instance",
			returns = {MINECRAFT_CLIENT, "the MinecraftClient instance"},
			example = "MinecraftClient.get();"
		)
		private Value getClientStaticShort(Arguments arguments) {
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
				MemberFunction.of("getScriptsPath", this::getScriptsPath),
				MemberFunction.of("setEssentialClientRule", 2, this::setEssentialClientRule),
				MemberFunction.of("resetEssentialClientRule", 1, this::resetEssentialClientRule),
				MemberFunction.of("getEssentialClientValue", 1, this::getEssentialClientRuleValue),
				MemberFunction.of("getModList", this::getModList),
				MemberFunction.of("getFps", this::getFps),

				MemberFunction.of("getPlayer", this::getPlayer),
				MemberFunction.of("getWorld", this::getWorld),
				MemberFunction.of("getVersion", this::getVersion),

				MemberFunction.of("parseStringToNbt", 1, this::parseStringToNbt),
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
				MemberFunction.of("tick", this::tick),

				MemberFunction.of("canSendScriptPacket", this::canSendScriptPacket),
				MemberFunction.arbitrary("sendScriptPacket", this::sendScriptPacket)
			);
		}

		@FunctionDoc(
			name = "syncToTick",
			desc = "Synchronizes the current thread in Arucas to the next game tick",
			throwMsgs = "Tried to sync non Arucas Thread",
			example = "client.syncToTick();"
		)
		private Value syncToTick(Arguments arguments) throws CodeError {
			ClientTickSyncer.syncToTick();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getRunDirectory",
			desc = "Returns the directory where the client is running",
			returns = {FILE, "the Minecraft run directory"},
			example = "client.getRunDirectory();"
		)
		private Value getRunDirectory(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return FileValue.of(client.runDirectory);
		}

		@FunctionDoc(
			name = "screenshot",
			desc = "This makes the client take a screenshot",
			example = "client.screenshot();"
		)
		private Value screenshot(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ScreenshotRecorder.saveScreenshot(
				client.runDirectory,
				client.getFramebuffer(),
				text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
			);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "screenshot",
			desc = "This makes the client take a screenshot and saves it with a given name",
			params = {STRING, "name", "the name of the file"},
			example = "client.screenshot('screenshot.png');"
		)
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

		@FunctionDoc(
			name = "pressKey",
			desc = "This allows you to simulate a key press inside of Minecraft, this will only press the key down",
			params = {STRING, "key", "the key to press"},
			throwMsgs = "Tried to press key outside of Minecraft",
			example = "client.pressKey('f');"
		)
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

		@FunctionDoc(
			name = "releaseKey",
			desc = {
				"This allows you to simulate a key release inside of Minecraft, this",
				"is useful for keys that only work on release, for example `F3`"
			},
			params = {STRING, "key", "the key to release"},
			throwMsgs = "Tried to press unknown key",
			example = "client.releaseKey('f');"
		)
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

		@FunctionDoc(
			name = "holdKey",
			desc = "This allows you to simulate a key being held inside of Minecraft, this will press, hold, and release",
			params = {
				STRING, "key", "the key to hold",
				NUMBER, "milliseconds", "the number of milliseconds you want it held for"
			},
			throwMsgs = "Tried to press unknown key",
			example = "client.holdKey('f', 100);"
		)
		private Value holdKey(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			String key = arguments.getNextGeneric(StringValue.class);
			int milliseconds = arguments.getNextGeneric(NumberValue.class).intValue();
			int keyCode = KeyboardHelper.translateStringToKey(key);
			if (keyCode == -1) {
				throw arguments.getError("Tried to press unknown key");
			}
			long handler = client.getWindow().getHandle();
			long startTime = System.currentTimeMillis();
			int scanCode = GLFW.glfwGetKeyScancode(keyCode);

			Context context = arguments.getContext().createBranch();
			Function<Integer, Runnable> keyPartial = action -> () -> client.keyboard.onKey(handler, keyCode, scanCode, action, 0);

			client.execute(keyPartial.apply(1));
			context.getThreadHandler().loopAsyncFunctionInThreadPool(
				50, TimeUnit.MILLISECONDS,
				() -> System.currentTimeMillis() - startTime < milliseconds,
				() -> client.execute(keyPartial.apply(0)),
				null, c -> client.execute(keyPartial.apply(2))
			);

			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "clearChat",
			desc = "This will clear the chat hud",
			example = "client.clearChat();"
		)
		private Value clearChat(Arguments arguments) throws CodeError {
			this.getClient(arguments).inGameHud.getChatHud().clear(true);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getLatestChatMessage",
			desc = "This will return the latest chat message",
			returns = {TEXT, "the latest chat message, null if there is none"},
			example = "client.getLatestChatMessage();"
		)
		private Value getLatestChatMessage(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			@SuppressWarnings("unchecked")
			ChatHudLine<Text>[] chat = ((ChatHudAccessor) client.inGameHud.getChatHud()).getMessages().toArray(ChatHudLine[]::new);
			if (chat.length == 0) {
				return NullValue.NULL;
			}
			return new TextValue(chat[0].getText().copy());
		}

		@FunctionDoc(
			name = "addCommand",
			desc = "This allows you to register your own client side command in game",
			params = {MAP, "command", "a command map or a command builder"},
			example = """
			client.addCommand({
			    "name": "example",
			    "subcommands": { },
			    "arguments": { }
			});
			"""
		)
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

		@FunctionDoc(
			name = "isInSinglePlayer",
			desc = "This will return true if the client is in single player mode",
			returns = {BOOLEAN, "true if the client is in single player mode"},
			example = "client.isInSinglePlayer();"
		)
		private Value isInSinglePlayer(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getClient(arguments).isInSingleplayer());
		}

		@FunctionDoc(
			name = "playerNameFromUuid",
			desc = "This will return the player name from the given uuid",
			params = {STRING, "uuid", "the uuid as a string"},
			returns = {STRING, "the player name, null if the uuid is not found"},
			example = "client.playerNameFromUuid('d4fca8c4-e083-4300-9a73-bf438847861c');"
		)
		private Value playerNameFromUuid(Arguments arguments) throws CodeError {
			String uuidAsString = arguments.skip().getNextGeneric(StringValue.class);
			String name = MojangAPI.getNameFromUuid(UUID.fromString(uuidAsString));
			if (name == null) {
				return NullValue.NULL;
			}
			return StringValue.of(name);
		}

		@FunctionDoc(
			name = "uuidFromPlayerName",
			desc = "This will return the uuid from the given player name",
			params = {STRING, "name", "the player name"},
			returns = {STRING, "the uuid, null if the player name is not found"},
			example = "client.uuidFromPlayerName('senseiwells');"
		)
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

		@FunctionDoc(
			name = "getServerName",
			desc = "This gets the current connected server's name that you have set it to in the multiplayer screen",
			returns = {STRING, "the server name"},
			example = "client.getServerName();"
		)
		private Value getServerName(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw arguments.getError("Failed to get server name");
			}
			return StringValue.of(serverInfo.name);
		}

		@FunctionDoc(
			name = "getPing",
			desc = "This gets the current connected server's ping",
			returns = {NUMBER, "the server ping in milliseconds"},
			throwMsgs = "Failed to get server ping",
			example = "client.getPing();"
		)
		private Value getPing(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ServerInfo serverInfo = client.getCurrentServerEntry();
			if (serverInfo == null) {
				throw arguments.getError("Failed to get server ping");
			}
			return NumberValue.of(serverInfo.ping);
		}

		@FunctionDoc(
			name = "getScriptsPath",
			desc = "This gets the script directory path, this is where all scripts are stored",
			returns = {STRING, "the script directory path"},
			example = "client.getScriptPath();"
		)
		private Value getScriptsPath(Arguments arguments) {
			return StringValue.of(ClientScript.INSTANCE.getScriptDirectory().toString());
		}

		@FunctionDoc(
			name = "setEssentialClientRule",
			desc = "This sets the given client rule to the given value",
			params = {
				STRING, "ruleName", "the client rule",
				ANY, "value", "the new value for the rule"
			},
			throwMsgs = {
				"Invalid ClientRule name",
				"Cannot set that value"
			},
			example = "client.setEssentialClientRule('highlightLavaSources', false);"
		)
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

		@FunctionDoc(
			name = "resetEssentialClientRule",
			desc = "This resets the given client rule to its default value",
			params = {STRING, "ruleName", "the client rule"},
			throwMsgs = "Invalid ClientRule name",
			example = "client.resetEssentialClientRule('highlightLavaSources');"
		)
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

		@FunctionDoc(
			name = "getEssentialClientValue",
			desc = "This gets the value of the given client rule",
			params = {STRING, "ruleName", "the client rule"},
			returns = {ANY, "the value of the client rule"},
			throwMsgs = "Invalid ClientRule name",
			example = "client.getEssentialClientValue('overrideCreativeWalkSpeed');"
		)
		private Value getEssentialClientRuleValue(Arguments arguments) throws CodeError {
			String clientRuleName = arguments.skip().getNextGeneric(StringValue.class);
			ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
			if (clientRule == null) {
				throw arguments.getError("Invalid ClientRule name");
			}
			return arguments.getContext().convertValue(clientRule.getValue());
		}

		@FunctionDoc(
			name = "getModList",
			desc = "This gets a list of all the mod ids of the mods installed",
			returns = {LIST, "the mod ids"},
			example = "client.getModList();"
		)
		private Value getModList(Arguments arguments) {
			ArucasList modList = new ArucasList();
			for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
				modList.add(StringValue.of(modContainer.getMetadata().getId()));
			}
			return new ListValue(modList);
		}

		@FunctionDoc(
			name = "getFps",
			desc = "This gets the current fps",
			returns = {NUMBER, "the fps"},
			example = "client.getFps();"
		)
		private Value getFps(Arguments arguments) {
			return NumberValue.of(MinecraftClientAccessor.getFps());
		}

		@FunctionDoc(
			deprecated = "You should use 'GameEvent.unregisterAll()' instead",
			name = "removeAllGameEvents",
			desc = "This unregisters all game events",
			example = "client.removeAllGameEvents();"
		)
		private Value removeAllGameEvents(Arguments arguments) {
			MinecraftScriptEvents.clearEventFunctions(arguments.getContext());
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "parseStringToNbt",
			desc = "This parses a string and turns it into a Nbt compound",
			params = {STRING, "string", "the string to parse"},
			returns = {ANY, "the Nbt compound"},
			example = "client.parseStringToNbt('{\"test\":\"test\"}');"
		)
		private Value parseStringToNbt(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return NbtUtils.nbtToValue(arguments.getContext(), NbtUtils.rawStringToNbt(stringValue.value), 10);
		}

		@FunctionDoc(
			deprecated = "You should use 'ItemStack.of(string)' instead",
			name = "itemFromString",
			desc = "This creates an item stack from the given string",
			params = {STRING, "string", "the string to parse"},
			returns = {ANY, "the item stack"},
			example = "client.itemFromString('dirt');"
		)
		private Value itemFromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return new ItemStackValue(Registry.ITEM.get(ArucasMinecraftExtension.getId(arguments, stringValue.value)).getDefaultStack());
		}

		@FunctionDoc(
			deprecated = "You should use 'Block.of(string)' instead",
			name = "blockFromString",
			desc = "This creates a block from the given string",
			params = {STRING, "string", "the string to parse"},
			returns = {ANY, "the block"},
			example = "client.blockFromString('dirt');"
		)
		private Value blockFromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return new BlockValue(Registry.BLOCK.get(ArucasMinecraftExtension.getId(arguments, stringValue.value)).getDefaultState());
		}

		@FunctionDoc(
			deprecated = "You should use 'Entity.of(string)' instead",
			name = "entityFromString",
			desc = "This creates an entity from the given string",
			params = {STRING, "string", "the string to parse"},
			returns = {ANY, "the entity"},
			example = "client.entityFromString('pig');"
		)
		private Value entityFromString(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ClientWorld world = ArucasMinecraftExtension.getWorld(client);
			StringValue stringValue = arguments.getNextString();
			return arguments.getContext().convertValue(Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getId(arguments, stringValue.value)).create(world));
		}

		@FunctionDoc(
			deprecated = "You should use 'Text.of(string)' instead",
			name = "textFromString",
			desc = "This creates a text from the given string",
			params = {STRING, "string", "the string to parse"},
			returns = {ANY, "the text"},
			example = "client.textFromString('Any Text!');"
		)
		private Value textFromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return new TextValue((MutableText) Text.of(stringValue.value));
		}

		@FunctionDoc(
			deprecated = "You should use 'new FakeScreen(string, rows)' instead",
			name = "createFakeScreen",
			desc = "This creates a fake screen with the given name and number of rows of slots available (1 - 6)",
			params = {
				STRING, "screenName", "the name of the screen",
				NUMBER, "rows", "number of rows"
			},
			returns = {ANY, "the fake screen"},
			example = "client.createFakeScreen('Name', 3);"
		)
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

		@FunctionDoc(
			name = "playSound",
			desc = {
				"This plays the given sound with the given volume and pitch around the player",
				"sound id's can be found [here](https://minecraft.fandom.com/wiki/Sounds.json#Sound_events)",
			},
			params = {
				STRING, "soundId", "the sound id you want to play",
				NUMBER, "volume", "the volume of the sound",
				NUMBER, "pitch", "the pitch of the sound"
			},
			example = "client.playSound('entity.lightning_bolt.thunder', 1, 1);"
		)
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

		@FunctionDoc(
			name = "renderFloatingItem",
			desc = "This renders an item in front of the player using the totem of undying animation",
			params = {ITEM_STACK, "itemStack", "the item stack to render"},
			example = "client.renderFloatingItem(Material.DIAMOND.asItemStack());"
		)
		private Value renderFloatingItem(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			ItemStack itemStack = arguments.getNextGeneric(ItemStackValue.class);
			client.gameRenderer.showFloatingItem(itemStack);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "stripFormatting",
			desc = "This strips the formatting from the given string",
			params = {STRING, "string", "the string to strip"},
			returns = {STRING, "the stripped string"},
			example = "client.stripFormatting('§cHello§r');"
		)
		private Value stripFormatting(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			return StringValue.of(stringValue.value.replaceAll("§[0-9a-gk-or]", ""));
		}

		@FunctionDoc(
			name = "getCursorStack",
			desc = "This returns the item stack that is currently being held by the cursor",
			returns = {ITEM_STACK, "the item stack, will be Air if there is nothing"},
			example = "client.getCursorStack();"
		)
		private Value getCursorStack(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return new ItemStackValue(InventoryUtils.getCursorStack(client));
		}

		@FunctionDoc(
			name = "setCursorStack",
			desc = {
				"This sets the item stack that is currently being held by the cursor, this does not work",
				"in normal screens only in FakeScreens, this does not actually pick up an item just display like you have"
			},
			params = {ITEM_STACK, "itemStack", "the item stack to set"},
			example = "client.setCursorStack(Material.DIAMOND.asItemStack());"
		)
		private Value setCursorStack(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			if (client.currentScreen instanceof FakeInventoryScreen) {
				ItemStack itemStack = arguments.getNextGeneric(ItemStackValue.class);
				return BooleanValue.of(InventoryUtils.setCursorStack(client, itemStack));
			}
			return BooleanValue.FALSE;
		}

		@FunctionDoc(
			name = "getClientRenderDistance",
			desc = "This returns the current render distance set on the client",
			returns = {NUMBER, "the render distance"},
			example = "client.getClientRenderDistance();"
		)
		private Value getClientRenderDistance(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return NumberValue.of(client.options.viewDistance);
		}

		@FunctionDoc(
			name = "setClientRenderDistance",
			desc = "This sets the render distance on the client",
			params = {NUMBER, "number", "the render distance"},
			example = "client.setClientRenderDistance(10);"
		)
		private Value setClientRenderDistance(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			client.options.viewDistance = numberValue.value.intValue();
			client.worldRenderer.scheduleTerrainUpdate();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "runOnMainThread",
			desc = "This runs the given function on the main thread",
			params = {FUNCTION, "function", "the function to run"},
			example = "client.runOnMainThread(fun() { print('Do something'); });"
		)
		private Value runOnMainThread(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			FunctionValue functionValue = arguments.getNextFunction();
			Context branchedContext = arguments.getContext().createBranch();
			client.execute(() -> functionValue.callSafe(branchedContext, ArrayList::new));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "tick",
			desc = "This ticks the client",
			example = "client.tick();"
		)
		private Value tick(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			client.execute(client::tick);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getPlayer",
			desc = "This returns the current player on the client",
			returns = {PLAYER, "the main player"},
			example = "client.getPlayer();"
		)
		private Value getPlayer(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return new PlayerValue(ArucasMinecraftExtension.getPlayer(client));
		}

		@FunctionDoc(
			name = "getWorld",
			desc = "This returns the world that is currently being played on",
			returns = {WORLD, "the world"},
			example = "client.getWorld();"
		)
		private Value getWorld(Arguments arguments) throws CodeError {
			MinecraftClient client = this.getClient(arguments);
			return new WorldValue(ArucasMinecraftExtension.getWorld(client));
		}

		@FunctionDoc(
			name = "getVersion",
			desc = "This returns the current version of Minecraft you are playing",
			returns = {STRING, "the version for example: '1.17.1'"},
			example = "client.getVersion();"
		)
		private Value getVersion(Arguments arguments) {
			return StringValue.of(EssentialUtils.getMinecraftVersion());
		}

		@FunctionDoc(
			name = "canSendScriptPacket",
			desc = "Returns whether the server supports client script packets",
			returns = {BOOLEAN, "Whether the client can send packets to the server"},
			example = "client.canSendScriptPacket()"
		)
		private Value canSendScriptPacket(Arguments arguments) {
			return BooleanValue.of(EssentialClient.SCRIPT_NET_HANDLER.isAvailable());
		}

		@FunctionDoc(
			isVarArgs = true,
			name = "sendScriptPacket",
			desc = {
				"This sends a script packet to the server",
				"You can send the follow types of values:",
				"Boolean, Number, String, List (of numbers), Text, ItemStack, Pos, and NbtMaps",
				"You can send byte, int, and long arrays by using the strings 'b', 'i', and 'l' at the start of the list",
			},
			params = {ANY, "values...", "the data you want to send to the server"},
			example = "client.sendScriptPacket('test', false, ['l', 9999, 0, 45]);"
		)
		private Value sendScriptPacket(Arguments arguments) throws CodeError {
			EssentialClient.SCRIPT_NET_HANDLER.sendScriptPacket(arguments.skip());
			return NullValue.NULL;
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
