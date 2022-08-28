package me.senseiwells.essentialclient.clientscript.definitions;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.builtin.MapDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.mixins.clientScript.MinecraftClientAccessor;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.rule.client.ClientRule;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientTickSyncer;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.interfaces.ChatHudAccessor;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.essentialclient.utils.keyboard.KeyboardHelper;
import me.senseiwells.essentialclient.utils.misc.Scheduler;
import me.senseiwells.essentialclient.utils.network.MojangAPI;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = MINECRAFT_CLIENT,
	desc = "This allows for many core interactions with the MinecraftClient",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class MinecraftClientDef extends PrimitiveDefinition<MinecraftClient> {
	public final ClassInstance instance = this.create(EssentialUtils.getClient());

	public MinecraftClientDef(Interpreter interpreter) {
		super(MinecraftAPI.MINECRAFT_CLIENT, interpreter);
	}

	@Override
	public boolean canExtend() {
		return false;
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return "MinecraftClient";
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("getClient", this::getClientStatic),
			BuiltInFunction.of("get", this::getClientStaticShort)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "getClient",
		desc = "Returns the MinecraftClient instance",
		returns = {MINECRAFT_CLIENT, "the MinecraftClient instance"},
		examples = "MinecraftClient.getClient();"
	)
	private ClassInstance getClientStatic(Arguments arguments) {
		return this.instance;
	}

	@FunctionDoc(
		isStatic = true,
		name = "get",
		desc = "Returns the MinecraftClient instance",
		returns = {MINECRAFT_CLIENT, "the MinecraftClient instance"},
		examples = "MinecraftClient.get();"
	)
	private ClassInstance getClientStaticShort(Arguments arguments) {
		return this.instance;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("syncToTick", this::syncToTick),
			MemberFunction.of("getRunDirectory", this::getRunDirectory),
			MemberFunction.of("screenshot", this::screenshot),
			MemberFunction.of("screenshot", 1, this::screenshotNamed),
			MemberFunction.of("pressKey", 1, this::pressKey),
			MemberFunction.of("releaseKey", 1, this::releaseKey),
			MemberFunction.of("holdKey", 2, this::holdKey),
			MemberFunction.arb("editSign", this::editSignFull),
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
			MemberFunction.of("playSound", 3, this::playSound),
			MemberFunction.of("renderFloatingItem", 1, this::renderFloatingItem),
			MemberFunction.of("stripFormatting", 1, this::stripFormatting),
			MemberFunction.of("getCursorStack", this::getCursorStack),
			MemberFunction.of("setCursorStack", 1, this::setCursorStack),
			MemberFunction.of("getClientRenderDistance", this::getClientRenderDistance),
			MemberFunction.of("setClientRenderDistance", 1, this::setClientRenderDistance),
			MemberFunction.of("runOnMainThread", 1, this::runOnMainThread, "Use 'client.run(func)'"),
			MemberFunction.of("tick", this::tick),

			MemberFunction.of("canSendScriptPacket", this::canSendScriptPacket),
			MemberFunction.arb("sendScriptPacket", this::sendScriptPacket)
		);
	}

	@FunctionDoc(
		name = "syncToTick",
		desc = "Synchronizes the current thread in Arucas to the next game tick",
		examples = "client.syncToTick();"
	)
	private Object syncToTick(Arguments arguments) {
		ClientTickSyncer.syncToTick();
		return null;
	}

	@FunctionDoc(
		name = "getRunDirectory",
		desc = "Returns the directory where the client is running",
		returns = {FILE, "the Minecraft run directory"},
		examples = "client.getRunDirectory();"
	)
	private File getRunDirectory(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		return client.runDirectory;
	}

	@FunctionDoc(
		name = "screenshot",
		desc = "This makes the client take a screenshot",
		examples = "client.screenshot();"
	)
	private Object screenshot(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		ScreenshotRecorder.saveScreenshot(
			client.runDirectory,
			client.getFramebuffer(),
			text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
		);
		return null;
	}

	@FunctionDoc(
		name = "screenshot",
		desc = "This makes the client take a screenshot and saves it with a given name",
		params = {STRING, "name", "the name of the file"},
		examples = "client.screenshot('screenshot.png');"
	)
	private Void screenshotNamed(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		String name = arguments.nextPrimitive(StringDef.class);
		ScreenshotRecorder.saveScreenshot(
			client.runDirectory,
			name,
			client.getFramebuffer(),
			text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
		);
		return null;
	}

	@FunctionDoc(
		name = "pressKey",
		desc = {
			"This allows you to simulate a key press inside of Minecraft, this will only press the key down.",
			"This will throw an error if the key is unknown"
		},
		params = {STRING, "key", "the key to press"},
		examples = "client.pressKey('f');"
	)
	private Void pressKey(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		String key = arguments.nextPrimitive(StringDef.class);
		final int keyCode = KeyboardHelper.translateStringToKey(key);
		if (keyCode == -1) {
			throw new RuntimeError("Tried to press unknown key");
		}
		ClientScriptUtils.ensureMainThread("pressKey", arguments.getInterpreter(), () -> {
			long handler = client.getWindow().getHandle();
			int scanCode = GLFW.glfwGetKeyScancode(keyCode);
			client.keyboard.onKey(handler, keyCode, scanCode, 1, 0);
		});
		return null;
	}

	@FunctionDoc(
		name = "releaseKey",
		desc = {
			"This allows you to simulate a key release inside of Minecraft, this",
			"is useful for keys that only work on release, for example `F3`, this",
			"will throw an error if the key is unknown"
		},
		params = {STRING, "key", "the key to release"},
		examples = "client.releaseKey('f');"
	)
	private Void releaseKey(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		String key = arguments.nextPrimitive(StringDef.class);
		final int keyCode = KeyboardHelper.translateStringToKey(key);
		if (keyCode == -1) {
			throw new RuntimeError("Tried to press unknown key");
		}
		ClientScriptUtils.ensureMainThread("releaseKey", arguments.getInterpreter(), () -> {
			long handler = client.getWindow().getHandle();
			int scanCode = GLFW.glfwGetKeyScancode(keyCode);
			client.keyboard.onKey(handler, keyCode, scanCode, 0, 0);
		});
		return null;
	}

	@FunctionDoc(
		name = "holdKey",
		desc = {
			"This allows you to simulate a key being held inside of Minecraft, this will press, hold, and release.",
			"This will throw an error if the given key is unknown"
		},
		params = {
			STRING, "key", "the key to hold",
			NUMBER, "milliseconds", "the number of milliseconds you want it held for"
		},
		examples = "client.holdKey('f', 100);"
	)
	private Void holdKey(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		String key = arguments.nextPrimitive(StringDef.class);
		int milliseconds = arguments.nextPrimitive(NumberDef.class).intValue();
		int keyCode = KeyboardHelper.translateStringToKey(key);
		if (keyCode == -1) {
			throw new RuntimeError("Tried to press unknown key");
		}
		long handler = client.getWindow().getHandle();
		int scanCode = GLFW.glfwGetKeyScancode(keyCode);
		Function<Integer, Runnable> keyPartial = action -> () -> client.keyboard.onKey(handler, keyCode, scanCode, action, 0);

		int ticks = MathHelper.ceil(milliseconds / 50.0F);
		if (ticks < 0) {
			throw new RuntimeError("Ticks cannot be negative");
		}
		ClientScriptUtils.ensureMainThread("holdKey", arguments.getInterpreter(), keyPartial.apply(GLFW.GLFW_PRESS));
		Scheduler.scheduleLoop(1, 5, ticks, keyPartial.apply(GLFW.GLFW_REPEAT));
		Scheduler.schedule(ticks, keyPartial.apply(GLFW.GLFW_RELEASE));
		return null;
	}

	@FunctionDoc(
		name = "editSign",
		desc = {
			"This allows you to edit sign at certain position with given string(lines), max at 4.",
			"This function does not check if sign is in position / sign is editable."
		},
		params = {
			POS, "position", "the position of sign",
			STRING, "string", "the string you want to put"
		},
		examples = "client.editSign(new Pos(0,0,0), '100', '101', 'this is third line', 'last line');"
	)
	private Void editSignFull(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		String string1 = arguments.nextPrimitive(StringDef.class);
		String string2 = arguments.hasNext() ? arguments.nextPrimitive(StringDef.class) : "";
		String string3 = arguments.hasNext() ? arguments.nextPrimitive(StringDef.class) : "";
		String string4 = arguments.hasNext() ? arguments.nextPrimitive(StringDef.class) : "";
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (networkHandler == null) {
			throw new RuntimeError("NetworkHandler was null!");
		}
		client.getNetworkHandler().sendPacket(new UpdateSignC2SPacket(pos.getBlockPos(), string1, string2, string3, string4));
		return null;
	}

	@FunctionDoc(
		name = "clearChat",
		desc = "This will clear the chat hud",
		examples = "client.clearChat();"
	)
	private Object clearChat(Arguments arguments) {
		arguments.nextPrimitive(this).inGameHud.getChatHud().clear(true);
		return null;
	}

	@FunctionDoc(
		name = "getLatestChatMessage",
		desc = "This will return the latest chat message",
		returns = {TEXT, "the latest chat message, null if there is none"},
		examples = "client.getLatestChatMessage();"
	)
	private Text getLatestChatMessage(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);

		//#if MC >= 11901
		ChatHudLine[] chat = ((ChatHudAccessor) client.inGameHud.getChatHud()).getMessages().toArray(ChatHudLine[]::new);
		//#else
		//$$ChatHudLine<Text>[] chat = ((ChatHudAccessor) client.inGameHud.getChatHud()).getMessages().toArray(ChatHudLine[]::new);
		//#endif

		if (chat.length == 0) {
			return null;
		}

		//#if MC >= 11901
		return chat[0].content();
		//#else
		//$$return chat[0].getText().copy();
		//#endif
	}

	@FunctionDoc(
		name = "addCommand",
		desc = "This allows you to register your own client side command in game",
		params = {MAP, "command", "a command map or a command builder"},
		examples = """
			client.addCommand({
				"name": "example",
				"subcommands": { },
				"arguments": { }
			});
			"""
	)
	private Void addCommand(Arguments arguments) {
		arguments.skip();
		CommandNode<ServerCommandSource> commandNode;
		if (arguments.isNext(CommandBuilderDef.class)) {
			commandNode = arguments.nextPrimitive(CommandBuilderDef.class).build();
		} else if (arguments.isNext(MapDef.class)) {
			commandNode = ClientScriptUtils.mapToCommand(arguments.nextPrimitive(MapDef.class), arguments.getInterpreter()).build();
		} else {
			throw new RuntimeError("Expected CommandBuilder or command map");
		}
		if (!(commandNode instanceof LiteralCommandNode<ServerCommandSource> literalCommandNode)) {
			throw new RuntimeError("Expected a literal command builder as root");
		}
		CommandHelper.addComplexCommand(arguments.getInterpreter(), literalCommandNode);
		ClientScriptUtils.ensureMainThread("addCommand", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().networkHandler.onCommandTree(CommandHelper.getCommandPacket());
		});
		return null;
	}

	@FunctionDoc(
		name = "isInSinglePlayer",
		desc = "This will return true if the client is in single player mode",
		returns = {BOOLEAN, "true if the client is in single player mode"},
		examples = "client.isInSinglePlayer();"
	)
	private boolean isInSinglePlayer(Arguments arguments) {
		return arguments.nextPrimitive(this).isInSingleplayer();
	}

	@FunctionDoc(
		name = "playerNameFromUuid",
		desc = "This will return the player name from the given uuid",
		params = {STRING, "uuid", "the uuid as a string"},
		returns = {STRING, "the player name, null if the uuid is not found"},
		examples = "client.playerNameFromUuid('d4fca8c4-e083-4300-9a73-bf438847861c');"
	)
	private Object playerNameFromUuid(Arguments arguments) {
		String uuidAsString = arguments.skip().nextPrimitive(StringDef.class);
		return MojangAPI.getNameFromUuid(UUID.fromString(uuidAsString));
	}

	@FunctionDoc(
		name = "uuidFromPlayerName",
		desc = "This will return the uuid from the given player name",
		params = {STRING, "name", "the player name"},
		returns = {STRING, "the uuid, null if the player name is not found"},
		examples = "client.uuidFromPlayerName('senseiwells');"
	)
	private String uuidFromPlayerName(Arguments arguments) {
		String name = arguments.skip().nextPrimitive(StringDef.class);
		UUID uuid = MojangAPI.getUuidFromName(name);
		return uuid == null ? null : uuid.toString();
	}

	@FunctionDoc(
		name = "getServerIp",
		desc = "This will return the server ip",
		returns = {STRING, "The server ip, null if in single player"},
		examples = "client.getServerIp();"
	)
	private String getServerIp(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		ServerInfo serverInfo = client.getCurrentServerEntry();
		return serverInfo == null ? null : serverInfo.address;
	}

	@FunctionDoc(
		name = "getServerName",
		desc = "This gets the current connected server's name that you have set it to in the multiplayer screen",
		returns = {STRING, "the server name"},
		examples = "client.getServerName();"
	)
	private String getServerName(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		ServerInfo serverInfo = client.getCurrentServerEntry();
		if (serverInfo == null) {
			throw new RuntimeError("Failed to get server name");
		}
		return serverInfo.name;
	}

	@FunctionDoc(
		name = "getPing",
		desc = {
			"This gets the current connected server's ping.",
			"This will throw an error if the client is not connected to a server"
		},
		returns = {NUMBER, "the server ping in milliseconds"},
		examples = "client.getPing();"
	)
	private double getPing(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		ServerInfo serverInfo = client.getCurrentServerEntry();
		if (serverInfo == null) {
			throw new RuntimeError("Failed to get server ping");
		}
		return serverInfo.ping;
	}

	@FunctionDoc(
		name = "getScriptsPath",
		desc = "This gets the script directory path, this is where all scripts are stored",
		returns = {STRING, "the script directory path"},
		examples = "client.getScriptPath();"
	)
	private String getScriptsPath(Arguments arguments) {
		return ClientScript.INSTANCE.getScriptDirectory().toString();
	}

	@FunctionDoc(
		name = "setEssentialClientRule",
		desc = {
			"This sets the given client rule to the given value.",
			"This may throw an error if the name is invalid or the rule cannot be set"
		},
		params = {
			STRING, "ruleName", "the client rule",
			OBJECT, "value", "the new value for the rule"
		},
		examples = "client.setEssentialClientRule('highlightLavaSources', false);"
	)
	private Void setEssentialClientRule(Arguments arguments) {
		String ruleName = arguments.skip().nextPrimitive(StringDef.class);
		String clientRuleValue = arguments.next().toString(arguments.getInterpreter());
		ClientRule<?> clientRule = ClientRules.ruleFromString(ruleName);
		if (clientRule == null) {
			throw new RuntimeError("Invalid ClientRule name");
		}
		ClientScriptUtils.ensureMainThread(
			"setEssentialClientRule", arguments.getInterpreter(), () -> clientRule.setValueFromString(clientRuleValue)
		);
		return null;
	}

	@FunctionDoc(
		name = "resetEssentialClientRule",
		desc = {
			"This resets the given client rule to its default value.",
			"This will throw an error if the rule name is invalid"
		},
		params = {STRING, "ruleName", "the client rule"},
		examples = "client.resetEssentialClientRule('highlightLavaSources');"
	)
	private Void resetEssentialClientRule(Arguments arguments) {
		String clientRuleName = arguments.skip().nextPrimitive(StringDef.class);
		ClientRule<?> clientRule = ClientRules.ruleFromString(clientRuleName);
		if (clientRule == null) {
			throw new RuntimeError("Invalid ClientRule name");
		}
		ClientScriptUtils.ensureMainThread(
			"resetEssentialClientRule", arguments.getInterpreter(), clientRule::resetToDefault
		);
		return null;
	}

	@FunctionDoc(
		name = "getEssentialClientValue",
		desc = {
			"This gets the value of the given client rule.",
			"This will throw an error if the rule name is invalid"
		},
		params = {STRING, "ruleName", "the client rule"},
		returns = {OBJECT, "the value of the client rule"},
		examples = "client.getEssentialClientValue('overrideCreativeWalkSpeed');"
	)
	private Object getEssentialClientRuleValue(Arguments arguments) {
		String ruleName = arguments.skip().nextPrimitive(StringDef.class);
		ClientRule<?> clientRule = ClientRules.ruleFromString(ruleName);
		if (clientRule == null) {
			throw new RuntimeError("Invalid ClientRule name");
		}
		return clientRule.getValue();
	}

	@FunctionDoc(
		name = "getModList",
		desc = "This gets a list of all the mod ids of the mods installed",
		returns = {LIST, "the mod ids"},
		examples = "client.getModList();"
	)
	private ArucasList getModList(Arguments arguments) {
		ArucasList modList = new ArucasList();
		for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
			modList.add(arguments.getInterpreter().create(StringDef.class, modContainer.getMetadata().getId()));
		}
		return modList;
	}

	@FunctionDoc(
		name = "getFps",
		desc = "This gets the current fps",
		returns = {NUMBER, "the fps"},
		examples = "client.getFps();"
	)
	private double getFps(Arguments arguments) {
		return MinecraftClientAccessor.getFps();
	}

	@FunctionDoc(
		name = "parseStringToNbt",
		desc = "This parses a string and turns it into a Nbt compound",
		params = {STRING, "string", "the string to parse"},
		returns = {OBJECT, "the nbt compound as a value"},
		examples = "client.parseStringToNbt('{\"test\":\"test\"}');"
	)
	private ClassInstance parseStringToNbt(Arguments arguments) {
		String nbt = arguments.nextPrimitive(StringDef.class);
		return ClientScriptUtils.nbtToValue(arguments.getInterpreter(), ClientScriptUtils.stringToNbt(nbt), 10);
	}

	@FunctionDoc(
		name = "playSound",
		desc = {
			"This plays the given sound with the given volume and pitch around the player",
			"sound id's can be found [here](https://minecraft.fandom.com/wiki/Sounds.json#Sound_events)"
		},
		params = {
			STRING, "soundId", "the sound id you want to play",
			NUMBER, "volume", "the volume of the sound",
			NUMBER, "pitch", "the pitch of the sound"
		},
		examples = "client.playSound('entity.lightning_bolt.thunder', 1, 1);"
	)
	private Void playSound(Arguments arguments) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		String soundId = arguments.skip().nextPrimitive(StringDef.class);
		Double volume = arguments.nextPrimitive(NumberDef.class);
		Double pitch = arguments.nextPrimitive(NumberDef.class);
		SoundEvent soundEvent = Registry.SOUND_EVENT.get(ClientScriptUtils.stringToIdentifier(soundId));
		player.playSound(soundEvent, SoundCategory.MASTER, volume.floatValue(), pitch.floatValue());
		return null;
	}

	@FunctionDoc(
		name = "renderFloatingItem",
		desc = "This renders an item in front of the player using the totem of undying animation",
		params = {MATERIAL, "material", "the material to render"},
		examples = "client.renderFloatingItem(Material.DIAMOND);"
	)
	private Void renderFloatingItem(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		ScriptMaterial material = arguments.nextPrimitive(MaterialDef.class);
		client.gameRenderer.showFloatingItem(material.asItemStack());
		return null;
	}

	@FunctionDoc(
		name = "stripFormatting",
		desc = "This strips the formatting from the given string",
		params = {STRING, "string", "the string to strip"},
		returns = {STRING, "the stripped string"},
		examples = "client.stripFormatting('§cHello§r');"
	)
	private String stripFormatting(Arguments arguments) {
		String string = arguments.skip().nextPrimitive(StringDef.class);
		return string.replaceAll("§[0-9a-gk-or]", "");
	}

	@FunctionDoc(
		name = "getCursorStack",
		desc = "This returns the item stack that is currently being held by the cursor",
		returns = {ITEM_STACK, "the item stack, will be Air if there is nothing"},
		examples = "client.getCursorStack();"
	)
	private ItemStack getCursorStack(Arguments arguments) {
		return InventoryUtils.getCursorStack();
	}

	@FunctionDoc(
		deprecated = "You should use 'fakeInventoryScreen.setCursorStack(item)' instead",
		name = "setCursorStack",
		desc = {
			"This sets the item stack that is currently being held by the cursor, this does not work",
			"in normal screens only in FakeScreens, this does not actually pick up an item just display like you have"
		},
		params = {ITEM_STACK, "itemStack", "the item stack to set"},
		examples = "client.setCursorStack(Material.DIAMOND.asItemStack());"
	)
	private boolean setCursorStack(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		if (client.currentScreen instanceof FakeInventoryScreen) {
			ScriptItemStack itemStack = arguments.nextPrimitive(ItemStackDef.class);
			return InventoryUtils.setCursorStack(itemStack.stack);
		}
		return false;
	}

	@FunctionDoc(
		name = "getClientRenderDistance",
		desc = "This returns the current render distance set on the client",
		returns = {NUMBER, "the render distance"},
		examples = "client.getClientRenderDistance();"
	)
	private double getClientRenderDistance(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		//#if MC >= 11900
		return client.options.getViewDistance().getValue();
		//#else
		//$$return client.options.viewDistance;
		//#endif
	}

	@FunctionDoc(
		name = "setClientRenderDistance",
		desc = "This sets the render distance on the client",
		params = {NUMBER, "number", "the render distance"},
		examples = "client.setClientRenderDistance(10);"
	)
	private Void setClientRenderDistance(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		int distance = arguments.nextPrimitive(NumberDef.class).intValue();
		//#if MC >= 11900
		client.options.getViewDistance().setValue(distance);
		//#else
		//$$client.options.viewDistance = distance;
		//#endif
		client.worldRenderer.scheduleTerrainUpdate();
		return null;
	}

	@FunctionDoc(
		deprecated = "Use 'client.run(task)' instead",
		name = "runOnMainThread",
		desc = "This runs the given function on the main thread",
		params = {FUNCTION, "function", "the function to run"},
		examples = "client.runOnMainThread(fun() { print('Do something'); });"
	)
	private Void runOnMainThread(Arguments arguments) {
		return this.run(arguments);
	}

	@FunctionDoc(
		name = "run",
		desc = "This runs the given function on the main thread",
		params = {FUNCTION, "function", "the function to run"},
		examples = "client.run(fun() { print('Do something'); });"
	)
	private Void run(Arguments arguments) {
		ArucasFunction function = arguments.skip().nextPrimitive(FunctionDef.class);
		Interpreter branch = arguments.getInterpreter().branch();
		ClientScriptUtils.ensureMainThread("run", arguments.getInterpreter(), () -> {
			function.invoke(branch, List.of());
		});
		return null;
	}

	@FunctionDoc(
		name = "tick",
		desc = "This ticks the client",
		examples = "client.tick();"
	)
	private Void tick(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		client.execute(client::tick);
		return null;
	}

	@FunctionDoc(
		name = "getPlayer",
		desc = "This returns the current player on the client",
		returns = {PLAYER, "the main player"},
		examples = "client.getPlayer();"
	)
	private ClientPlayerEntity getPlayer(Arguments arguments) {
		return EssentialUtils.getPlayer();
	}

	@FunctionDoc(
		name = "getWorld",
		desc = "This returns the world that is currently being played on",
		returns = {WORLD, "the world"},
		examples = "client.getWorld();"
	)
	private ClientWorld getWorld(Arguments arguments) {
		return EssentialUtils.getWorld();
	}

	@FunctionDoc(
		name = "getVersion",
		desc = "This returns the current version of Minecraft you are playing",
		returns = {STRING, "the version for example: '1.17.1'"},
		examples = "client.getVersion();"
	)
	private String getVersion(Arguments arguments) {
		return EssentialUtils.getMinecraftVersion();
	}

	@FunctionDoc(
		name = "canSendScriptPacket",
		desc = "Returns whether the server supports client script packets",
		returns = {BOOLEAN, "Whether the client can send packets to the server"},
		examples = "client.canSendScriptPacket()"
	)
	private boolean canSendScriptPacket(Arguments arguments) {
		return EssentialClient.SCRIPT_NET_HANDLER.isAvailable();
	}

	@FunctionDoc(
		isVarArgs = true,
		name = "sendScriptPacket",
		desc = {
			"This sends a script packet to the server",
			"You can send the follow types of values:",
			"Boolean, Number, String, List (of numbers), Text, ItemStack, Pos, and NbtMaps",
			"You can send byte, int, and long arrays by using the strings 'b', 'i', and 'l' at the start of the list"
		},
		params = {OBJECT, "values...", "the data you want to send to the server"},
		examples = "client.sendScriptPacket('test', false, ['l', 9999, 0, 45]);"
	)
	private Void sendScriptPacket(Arguments arguments) {
		EssentialClient.SCRIPT_NET_HANDLER.sendScriptPacket(arguments.skip());
		return null;
	}
}
