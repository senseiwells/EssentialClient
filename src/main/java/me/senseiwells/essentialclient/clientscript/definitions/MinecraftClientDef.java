package me.senseiwells.essentialclient.clientscript.definitions;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.ArucasFunction;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.MINECRAFT_CLIENT;

@ClassDoc(
	name = MINECRAFT_CLIENT,
	desc = "This allows for many core interactions with the MinecraftClient",
	language = Language.Java
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

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
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
		returns = @ReturnDoc(type = MinecraftClientDef.class, desc = "the MinecraftClient instance"),
		examples = "MinecraftClient.getClient();"
	)
	private ClassInstance getClientStatic(Arguments arguments) {
		return this.instance;
	}

	@FunctionDoc(
		isStatic = true,
		name = "get",
		desc = "Returns the MinecraftClient instance",
		returns = @ReturnDoc(type = MinecraftClientDef.class, desc = "the MinecraftClient instance"),
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
			MemberFunction.of("completionsForCommand", 1, this::completionsForCommand),
			MemberFunction.of("isInSinglePlayer", this::isInSinglePlayer),
			MemberFunction.of("getOnlinePlayerNames", this::getOnlinePlayerNames),
			MemberFunction.of("getOnlinePlayerUuids", this::getOnlinePlayerUuids),
			MemberFunction.of("getOnlinePlayerNamesAndUuids", this::getOnlinePlayerNamesAndUuids),
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
			MemberFunction.of("run", 1, this::run),
			MemberFunction.of("tick", this::tick),

			MemberFunction.of("canSendScriptPacket", this::canSendScriptPacket),
			MemberFunction.arb("sendScriptPacket", this::sendScriptPacket)
		);
	}

	@FunctionDoc(
		deprecated = "This function is unstable, it should not be used",
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
		returns = @ReturnDoc(type = FileDef.class, desc = "the Minecraft run directory"),
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
		params = {@ParameterDoc(type = StringDef.class, name = "name", desc = "the name of the file")},
		examples = "client.screenshot('screenshot.png');"
	)
	private Void screenshotNamed(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		String name = arguments.nextPrimitive(StringDef.class);
		ScreenshotRecorder.saveScreenshot(
			client.runDirectory,
			name.endsWith(".png") ? name : name + ".png",
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
		params = {@ParameterDoc(type = StringDef.class, name = "key", desc = "the key to press")},
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
		params = {@ParameterDoc(type = StringDef.class, name = "key", desc = "the key to release")},
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
			@ParameterDoc(type = StringDef.class, name = "key", desc = "the key to hold"),
			@ParameterDoc(type = NumberDef.class, name = "milliseconds", desc = "the number of milliseconds you want it held for")
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
			"This allows you to edit sign at certain position with given string(lines), up to 4 lines.",
			"This function does not check if sign is editable / is in position."
		},
		params = {
			@ParameterDoc(type = PosDef.class, name = "position", desc = "the position of sign"),
			@ParameterDoc(type = StringDef.class, name = "strings", desc = "the lines for the sign, requires 1 string and up to 4 strings", isVarargs = true)
		},
		examples = "client.editSign(new Pos(0,0,0), '100', '101', 'this is third line', 'last line');"
	)
	private Void editSignFull(Arguments arguments) {
		ScriptPos pos = arguments.skip().nextPrimitive(PosDef.class);
		String first = arguments.nextPrimitive(StringDef.class);
		String second = arguments.hasNext() ? arguments.nextPrimitive(StringDef.class) : "";
		String third = arguments.hasNext() ? arguments.nextPrimitive(StringDef.class) : "";
		String fourth = arguments.hasNext() ? arguments.nextPrimitive(StringDef.class) : "";
		EssentialUtils.getNetworkHandler().sendPacket(new UpdateSignC2SPacket(
			pos.getBlockPos(),
			true,
			first,
			second,
			third,
			fourth
		));
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
		returns = @ReturnDoc(type = TextDef.class, desc = "the latest chat message, null if there is none"),
		examples = "client.getLatestChatMessage();"
	)
	private Text getLatestChatMessage(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);

		ChatHudLine[] chat = ((ChatHudAccessor) client.inGameHud.getChatHud()).essentialclient$getMessages().toArray(ChatHudLine[]::new);

		if (chat.length == 0) {
			return null;
		}

		return chat[0].content();
	}

	@FunctionDoc(
		name = "addCommand",
		desc = "This allows you to register your own client side command in game",
		params = {@ParameterDoc(type = MapDef.class, name = "command", desc = "a command map or a command builder")},
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
		name = "completionsForCommand",
		desc = "This gets a list of completions for a given command",
		params = @ParameterDoc(type = StringDef.class, name = "command", desc = "The command to get suggestions for."),
		returns = @ReturnDoc(type = ListDef.class, desc = "A list of all the suggestions as strings."),
		examples = "client.completionsForCommand('/gamemode '); // ['adventure', 'creative', 'spectator', 'survival']"
	)
	private ArucasList completionsForCommand(Arguments arguments) {
		arguments.skip();
		String command = arguments.nextPrimitive(StringDef.class);
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		CommandDispatcher<CommandSource> dispatcher = EssentialUtils.getNetworkHandler().getCommandDispatcher();
		CommandSource source = EssentialUtils.getNetworkHandler().getCommandSource();
		ParseResults<CommandSource> result = dispatcher.parse(command, source);
		Suggestions suggestions = arguments.getInterpreter().canInterrupt((Supplier<Suggestions>) () ->
			EssentialUtils.throwAsUnchecked(() -> dispatcher.getCompletionSuggestions(result).get())
		);
		ArucasList list = new ArucasList();
		for (Suggestion suggestion : suggestions.getList()) {
			list.add(arguments.getInterpreter().convertValue(suggestion.getText()));
		}
		return list;
	}

	@FunctionDoc(
		name = "isInSinglePlayer",
		desc = "This will return true if the client is in single player mode",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the client is in single player mode"),
		examples = "client.isInSinglePlayer();"
	)
	private boolean isInSinglePlayer(Arguments arguments) {
		return arguments.nextPrimitive(this).isInSingleplayer();
	}

	@FunctionDoc(
		name = "getOnlinePlayerNames",
		desc = "This will get a list of all the online player's names.",
		returns = @ReturnDoc(type = ListDef.class, desc = "The list of online player names."),
		examples = "client.getOnlinePlayerNames();"
	)
	private List<String> getOnlinePlayerNames(Arguments arguments) {
		return EssentialUtils.getNetworkHandler().getPlayerList()
			.stream()
			.map(e -> e.getProfile().getName())
			.toList();
	}

	@FunctionDoc(
		name = "getOnlinePlayerNamesAndUuids",
		desc = "This will get a map of all the online player's names to their uuids.",
		returns = @ReturnDoc(type = MapDef.class, desc = "The map of online player names to uuids."),
		examples = "client.getOnlinePlayerNamesAndUuids();"
	)
	private Object getOnlinePlayerNamesAndUuids(Arguments arguments) {
		return EssentialUtils.getNetworkHandler().getPlayerList()
			.stream()
			.map(PlayerListEntry::getProfile)
			.collect(Collectors.toMap(GameProfile::getName, GameProfile::getId));
	}

	@FunctionDoc(
		name = "getOnlinePlayerUuids",
		desc = "This will get a list of all the online player's uuids.",
		returns = @ReturnDoc(type = ListDef.class, desc = "The list of online player uuids."),
		examples = "client.getOnlinePlayerUuids();"
	)
	private List<String> getOnlinePlayerUuids(Arguments arguments) {
		return EssentialUtils.getNetworkHandler().getPlayerList()
			.stream()
			.map(e -> e.getProfile().getId().toString())
			.toList();
	}

	@FunctionDoc(
		name = "playerNameFromUuid",
		desc = {
			"This will return the player name from the given uuid.",
			"The player name is fetched from the Mojang API, this is",
			"intended for use to get a player's name who is not online"
		},
		params = {@ParameterDoc(type = StringDef.class, name = "uuid", desc = "the uuid as a string")},
		returns = @ReturnDoc(type = StringDef.class, desc = "the player name, null if the uuid is not found"),
		examples = "client.playerNameFromUuid('d4fca8c4-e083-4300-9a73-bf438847861c');"
	)
	private Object playerNameFromUuid(Arguments arguments) {
		String uuidAsString = arguments.skip().nextPrimitive(StringDef.class);
		return MojangAPI.getNameFromUuid(UUID.fromString(uuidAsString));
	}

	@FunctionDoc(
		name = "uuidFromPlayerName",
		desc = {
			"This will return the uuid from the given player name.",
			"The player uuid is fetched from the Mojang API, this is",
			"intended for use to get a player's uuid who is not online"
		},
		params = {@ParameterDoc(type = StringDef.class, name = "name", desc = "the player name")},
		returns = @ReturnDoc(type = StringDef.class, desc = "the uuid, null if the player name is not found"),
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
		returns = @ReturnDoc(type = StringDef.class, desc = "The server ip, null if in single player"),
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
		returns = @ReturnDoc(type = StringDef.class, desc = "the server name"),
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
		returns = @ReturnDoc(type = NumberDef.class, desc = "the server ping in milliseconds"),
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
		returns = @ReturnDoc(type = StringDef.class, desc = "the script directory path"),
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
			@ParameterDoc(type = StringDef.class, name = "ruleName", desc = "the client rule"),
			@ParameterDoc(type = ObjectDef.class, name = "value", desc = "the new value for the rule")
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
		params = {@ParameterDoc(type = StringDef.class, name = "ruleName", desc = "the client rule")},
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
		params = {@ParameterDoc(type = StringDef.class, name = "ruleName", desc = "the client rule")},
		returns = @ReturnDoc(type = ObjectDef.class, desc = "the value of the client rule"),
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
		returns = @ReturnDoc(type = ListDef.class, desc = "the mod ids"),
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
		returns = @ReturnDoc(type = NumberDef.class, desc = "the fps"),
		examples = "client.getFps();"
	)
	private double getFps(Arguments arguments) {
		return MinecraftClientAccessor.getFps();
	}

	@FunctionDoc(
		name = "parseStringToNbt",
		desc = "This parses a string and turns it into a Nbt compound",
		params = {@ParameterDoc(type = StringDef.class, name = "string", desc = "the string to parse")},
		returns = @ReturnDoc(type = ObjectDef.class, desc = "the nbt compound as a value"),
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
			@ParameterDoc(type = StringDef.class, name = "soundId", desc = "the sound id you want to play"),
			@ParameterDoc(type = NumberDef.class, name = "volume", desc = "the volume of the sound"),
			@ParameterDoc(type = NumberDef.class, name = "pitch", desc = "the pitch of the sound")
		},
		examples = "client.playSound('entity.lightning_bolt.thunder', 1, 1);"
	)
	private Void playSound(Arguments arguments) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		String soundId = arguments.skip().nextPrimitive(StringDef.class);
		Double volume = arguments.nextPrimitive(NumberDef.class);
		Double pitch = arguments.nextPrimitive(NumberDef.class);
		SoundEvent soundEvent = Registries.SOUND_EVENT.get(ClientScriptUtils.stringToIdentifier(soundId));
		ClientScriptUtils.ensureMainThread("playSound", arguments.getInterpreter(), () -> {
			player.playSoundToPlayer(soundEvent, SoundCategory.MASTER, volume.floatValue(), pitch.floatValue());
		});
		return null;
	}

	@FunctionDoc(
		name = "renderFloatingItem",
		desc = "This renders an item in front of the player using the totem of undying animation",
		params = {@ParameterDoc(type = MaterialDef.class, name = "material", desc = "the material to render")},
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
		params = {@ParameterDoc(type = StringDef.class, name = "string", desc = "the string to strip")},
		returns = @ReturnDoc(type = StringDef.class, desc = "the stripped string"),
		examples = "client.stripFormatting('§cHello§r');"
	)
	private String stripFormatting(Arguments arguments) {
		String string = arguments.skip().nextPrimitive(StringDef.class);
		return string.replaceAll("§[0-9a-gk-or]", "");
	}

	@FunctionDoc(
		name = "getCursorStack",
		desc = "This returns the item stack that is currently being held by the cursor",
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the item stack, will be Air if there is nothing"),
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
		params = @ParameterDoc(type = ItemStackDef.class, name = "itemStack", desc = "the item stack to set"),
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
		returns = @ReturnDoc(type = NumberDef.class, desc = "the render distance"),
		examples = "client.getClientRenderDistance();"
	)
	private double getClientRenderDistance(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		return client.options.getViewDistance().getValue();
	}

	@FunctionDoc(
		name = "setClientRenderDistance",
		desc = "This sets the render distance on the client",
		params = {@ParameterDoc(type = NumberDef.class, name = "number", desc = "the render distance")},
		examples = "client.setClientRenderDistance(10);"
	)
	private Void setClientRenderDistance(Arguments arguments) {
		MinecraftClient client = arguments.nextPrimitive(this);
		int distance = arguments.nextPrimitive(NumberDef.class).intValue();
		client.options.getViewDistance().setValue(distance);
		client.worldRenderer.scheduleTerrainUpdate();
		return null;
	}

	@FunctionDoc(
		deprecated = "Use 'client.run(task)' instead",
		name = "runOnMainThread",
		desc = "This runs the given function on the main thread",
		params = {@ParameterDoc(type = FunctionDef.class, name = "function", desc = "the function to run")},
		examples = "client.runOnMainThread(fun() { print('Do something'); });"
	)
	private Void runOnMainThread(Arguments arguments) {
		return this.run(arguments);
	}

	@FunctionDoc(
		name = "run",
		desc = "This runs the given function on the main thread",
		params = {@ParameterDoc(type = FunctionDef.class, name = "function", desc = "the function to run")},
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
		returns = @ReturnDoc(type = PlayerDef.class, desc = "the main player"),
		examples = "client.getPlayer();"
	)
	private ClientPlayerEntity getPlayer(Arguments arguments) {
		return EssentialUtils.getPlayer();
	}

	@FunctionDoc(
		name = "getWorld",
		desc = "This returns the world that is currently being played on",
		returns = @ReturnDoc(type = WorldDef.class, desc = "the world"),
		examples = "client.getWorld();"
	)
	private ClientWorld getWorld(Arguments arguments) {
		return EssentialUtils.getWorld();
	}

	@FunctionDoc(
		name = "getVersion",
		desc = "This returns the current version of Minecraft you are playing",
		returns = @ReturnDoc(type = StringDef.class, desc = "the version for example: '1.17.1'"),
		examples = "client.getVersion();"
	)
	private String getVersion(Arguments arguments) {
		return EssentialUtils.getMinecraftVersion();
	}

	@FunctionDoc(
		deprecated = "This features is no longer supported",
		name = "canSendScriptPacket",
		desc = "Returns whether the server supports client script packets",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "Whether the client can send packets to the server"),
		examples = "client.canSendScriptPacket()"
	)
	private boolean canSendScriptPacket(Arguments arguments) {
		return false;
	}

	@FunctionDoc(
		deprecated = "This features is no longer supported",
		name = "sendScriptPacket",
		desc = {
			"This sends a script packet to the server",
			"You can send the follow types of values:",
			"Boolean, Number, String, List (of numbers), Text, ItemStack, Pos, and NbtMaps",
			"You can send byte, int, and long arrays by using the strings 'b', 'i', and 'l' at the start of the list"
		},
		params = @ParameterDoc(type = ObjectDef.class, name = "values...", desc = "the data you want to send to the server", isVarargs = true),
		examples = "client.sendScriptPacket('test', false, ['l', 9999, 0, 45]);"
	)
	private Void sendScriptPacket(Arguments arguments) {
		return null;
	}
}
