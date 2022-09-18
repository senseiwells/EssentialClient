package me.senseiwells.essentialclient.rule;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.senseiwells.essentialclient.feature.AFKRules;
import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import me.senseiwells.essentialclient.feature.CustomClientCape;
import me.senseiwells.essentialclient.feature.HighlightLavaSources;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.rule.client.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.config.MappedStringConfig;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.*;

public class ClientRules extends MappedStringConfig<ClientRule<?>> {
	public static final ClientRules INSTANCE = new ClientRules();

	public static final BooleanClientRule
		BETTER_ACCURATE_BLOCK_PLACEMENT = register(new BooleanClientRule("betterAccurateBlockPlacement", "This is the same as accurate block placement for tweakeroo but handled all client side, see controls...")),
		CARPET_ALWAYS_SET_DEFAULT = register(new BooleanClientRule("carpetAlwaysSetDefault", "This makes it so whenever you set a carpet rule it automatically sets it to default")),
		CHUNK_DEBUG_MINIMAP_BACKGROUND = register(new BooleanClientRule("chunkDebugMinimapBackground", "This renders a box showing the bounds of the chunk debug minimap", true)),
		CHUNK_DEBUG_SHOW_UNLOADED_CHUNKS = register(new BooleanClientRule("chunkDebugShowUnloadedChunks", "This shows you unloaded chunks in ChunkDebug")),
		CLIENT_SCRIPT_ANNOUNCEMENTS = register(new BooleanClientRule("clientScriptAnnouncements", "This messages in chat when a script starts and finishes", true)),
		COMMAND_ALTERNATE_DIMENSION = register(new BooleanClientRule("commandAlternateDimension", "This command calculates the coordinates of the alternate dimension", ClientRules::refreshCommand)),
		COMMAND_CLIENT_NICK = register(new BooleanClientRule("commandClientNick", "This allows you to rename player names on the client", ClientRules::refreshCommand)),
		COMMAND_PLAYER_CLIENT = register(new BooleanClientRule("commandPlayerClient", "This command allows you to save /player... commands and execute them", ClientRules::refreshCommand)),
		COMMAND_PLAYER_LIST = register(new BooleanClientRule("commandPlayerList", "This command allows you to execute /player... commands in one command (requires commandPlayerClient)", ClientRules::refreshCommand)),
		COMMAND_REGION = register(new BooleanClientRule("commandRegion", "This command allows you to determine the region you are in or the region at set coords", ClientRules::refreshCommand)),
		COMMAND_SUGGESTOR_IGNORES_SPACES = register(new BooleanClientRule("commandSuggestorIgnoresSpaces", "This makes the command suggestor ignore spaces")),
		CRAFTING_HAX = register(new BooleanClientRule("craftingHax", "This allows you to craft items with the mouse")),
		DISABLE_BOB_VIEW_WHEN_HURT = register(new BooleanClientRule("disableBobViewWhenHurt", "Disables the camera bobbing when you get hurt")),
		DISABLE_BOSS_BAR = register(new BooleanClientRule("disableBossBar", "This will disable the boss bar from rendering")),
		DISABLE_HOTBAR_SCROLLING = register(new BooleanClientRule("disableHotbarScrolling", "This will prevent you from scrolling in your hotbar, learn to use hotkeys :)")),
		DISABLE_JOIN_LEAVE_MESSAGES = register(new BooleanClientRule("disableJoinLeaveMessages", "This will prevent join/leave messages from displaying")),
		DISABLE_MAP_RENDERING = register(new BooleanClientRule("disableMapRendering", "This disables maps rendering in item frames")),
		DISABLE_NAME_TAGS = register(new BooleanClientRule("disableNameTags", "This disables all name tags from rendering")),
		DISABLE_NARRATOR = register(new BooleanClientRule("disableNarrator", "Disables cycling narrator when pressing CTRL + B")),
		DISABLE_NIGHT_VISION_FLASH = register(new BooleanClientRule("disableNightVisionFlash", "Disables the flash that occurs when night vision is about to run out")),
		DISABLE_OP_MESSAGES = register(new BooleanClientRule("disableOpMessages", "This will prevent system messages from displaying")),
		DISABLE_RECIPE_NOTIFICATIONS = register(new BooleanClientRule("disableRecipeNotifications", "Disables the recipe toast from showing")),
		DISABLE_SCREENSHOT_MESSAGE = register(new BooleanClientRule("disableScreenshotMessage", "Disables the message that pops up when you take a screenshot")),
		DISABLE_TUTORIAL_NOTIFICATIONS = register(new BooleanClientRule("disableTutorialNotifications", "Disables the tutorial toast from showing")),
		DISPLAY_TIME_PLAYED = register(new BooleanClientRule("displayTimePlayed", "This will display how long you have had your current client open for in the corner of the pause menu")),
		ESSENTIAL_CLIENT_BUTTON = register(new BooleanClientRule("essentialClientButton", "This renders the Essential Client Menu on the main menu screen, and pause screen", true)),
		TITLE_TEXT_TO_TOP = register(new BooleanClientRule("titleTextToTop", "Forces the Minecraft version and Mojang text to the top of the screen")),
		HIGHLIGHT_LAVA_SOURCES = register(new BooleanClientRule("highlightLavaSources", "Highlights lava sources, credit to plusls for the original code for this", ClientRules::refreshWorld)),
		INCREASE_SPECTATOR_SCROLL_SPEED = register(new BooleanClientRule("increaseSpectatorScrollSpeed", "Increases the limit at which you can scroll to go faster in spectator")),
		OPEN_SCREENSHOT_DIRECTORY = register(new BooleanClientRule("openScreenshotDirectory", "This opens the screenshot directory instead of directly opening the screenshot")),
		PERMANENT_CHAT_HUD = register(new BooleanClientRule("permanentChatHud", "This prevents the chat from being cleared, also applies when chaning worlds/servers")),
		QUICK_LOCK_RECIPE = register(new BooleanClientRule("quickLockRecipe", "If you middle click a recipe it will put the name of the item in the search bar stopping you from craftin the wrong recipe")),
		REMOVE_WARN_RECEIVED_PASSENGERS = register(new BooleanClientRule("removeWarnReceivedPassengers", "This removes the 'Received passengers for unknown entity' warning on the client")),
		STACKABLE_SHULKERS_IN_PLAYER_INVENTORIES = register(new BooleanClientRule("stackableShulkersInPlayerInventories", "This allows for shulkers to stack only in your inventory")),
		STACKABLE_SHULKERS_WITH_ITEMS = register(new BooleanClientRule("stackableShulkersWithItems", "This allows for shulkers with items to stack only in your inventory")),
		START_SELECTED_SCRIPTS_ON_JOIN = register(new BooleanClientRule("startSelectedScriptsOnJoin", "This will enable your selected scripts when you join a world automatically")),
		SURVIVAL_INVENTORY_IN_CREATIVE = register(new BooleanClientRule("survivalInventoryInCreative", "This lets you open the survival inventory in creative mode")),
		TOGGLE_TAB = register(new BooleanClientRule("toggleTab", "This allows you to toggle tab instead of holding to see tab")),
		UNLOCK_ALL_RECIPES_ON_JOIN = register(new BooleanClientRule("unlockAllRecipesOnJoin", "Unlocks every recipe when joining a world"));

	public static final IntegerClientRule
		ANNOUNCE_AFK = register(new IntegerClientRule("announceAFK", "This announces when you become afk after a set amount of time (ticks)")),
		AFK_LOGOUT = register(new IntegerClientRule("afkLogout", "Number of ticks until client will disconnect you from world (must be >= 200 to be active)")),
		AUTO_WALK = register(new IntegerClientRule("autoWalk", "This will auto walk after you have held your key for set amount of ticks")),
		INCREASE_SPECTATOR_SCROLL_SENSITIVITY = register(new IntegerClientRule("increaseSpectatorScrollSensitivity", "Increases the sensitivity at which you can scroll to go faster in spectator")),
		SWITCH_TO_TOTEM = register(new IntegerClientRule("switchToTotem", "This will switch to a totem (if you have one), under a set amount of health"));

	public static final IntegerSliderClientRule
		PERMANENT_TIME = register(new IntegerSliderClientRule("permanentTime", "This forces your client to set a time of day", -1, -1, 23999));

	public static final DoubleClientRule
		OVERRIDE_CREATIVE_WALK_SPEED = register(new DoubleClientRule("overrideCreativeWalkSpeed", "This allows you to override the vanilla walk speed in creative mode", 0.0D));

	public static final DoubleSliderClientRule
		SOUL_SPEED_FOV_MULTIPLIER = register(new DoubleSliderClientRule("soulSpeedFovMultiplier", "Determines the percentage of Fov scaling when walking on soil soul or soul sand", 0, 0, 1)),
		WATER_FOV_MULTIPLIER = register(new DoubleSliderClientRule("waterFovMultiplier", "Determines the percentage of Fov scaling when fully submerged in water", 0, 0, 1));

	public static final StringClientRule
		ANNOUNCE_AFK_MESSAGE = register(new StringClientRule("announceAFKMessage", "This is the message you announce after you are afk", "I am now AFK")),
		ANNOUNCE_BACK_MESSAGE = register(new StringClientRule("announceBackMessage", "This is the message you announce after you are back from being afk", ""));

	@SuppressWarnings("unused")
	public static final CycleClientRule
		CUSTOM_CLIENT_CAPE = register(new CycleClientRule("customClientCape", "This allows you to select a Minecraft cape to wear, this only appears client side", CustomClientCape.getCapeNames(), ClientRules::refreshCape)),
		DISABLE_ARMOUR_RENDERING = register(new CycleClientRule("disableArmourRendering", "This allows you to disable armour rendering for entities", List.of("None", "You", "Players", "Entities"))),
		DISPLAY_RULE_TYPE = register(new CycleClientRule("displayRuleType", "This allows you to choose the order you want rules to be displayed", List.of("Alphabetical", "Rule Type"), ClientRules::refreshScreen));

	static {
		AFKRules.load();
		BetterAccurateBlockPlacement.load();

		if (EssentialUtils.isModInstalled("fabric-resource-loader-v0")) {
			HighlightLavaSources.load();
		}
	}

	private ClientRules() { }

	public static void load() { }

	public static void addRule(String name, ClientRule<?> rule) {
		INSTANCE.set(name, rule);
	}

	public static ClientRule<?> ruleFromString(String name) {
		return INSTANCE.get(name);
	}

	public static Collection<ClientRule<?>> getClientRules() {
		return INSTANCE.map.values();
	}

	public static Collection<ClientRule<?>> getCurrentClientRules() {
		if (!ClientRules.DISPLAY_RULE_TYPE.getValue().equals("Rule Type")) {
			return Rule.sortRulesAlphabetically(getClientRules());
		}
		SortedMap<String, ClientRule<?>> sortedMap = new TreeMap<>();
		for (ClientRule<?> rule : getClientRules()) {
			sortedMap.put(rule.getType().toString() + rule.getName(), rule);
		}
		return sortedMap.values();
	}

	private static void refreshCommand(Rule<Boolean> value) {
		ClientPlayerEntity playerEntity = EssentialUtils.getPlayer();
		if (playerEntity != null) {
			playerEntity.networkHandler.onCommandTree(CommandHelper.getCommandPacket());
		}
	}

	private static void refreshCape(Rule<String> value) {
		CustomClientCape.setCurrentCape(value.getValue());
	}

	private static void refreshScreen(Rule<String> value) {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.currentScreen instanceof RulesScreen rulesScreen) {
			rulesScreen.refreshRules(rulesScreen.getSearchBoxText());
		}
	}

	private static void refreshWorld(Rule<Boolean> value) {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.worldRenderer != null) {
			client.worldRenderer.reload();
		}
	}

	@Override
	public String getConfigName() {
		return "ClientRules";
	}

	@Override
	protected JsonElement valueToJson(ClientRule<?> value) {
		JsonObject clientRuleObject = new JsonObject();
		clientRuleObject.add("value", value.getValueAsJson());
		clientRuleObject.addProperty("extras", value.getOptionalInfo());
		return clientRuleObject;
	}

	@Override
	protected ClientRule<?> jsonToValue(String key, JsonElement valueElement) {
		JsonObject clientRuleObject = valueElement.getAsJsonObject();
		JsonElement value = clientRuleObject.get("value");
		JsonElement info = clientRuleObject.get("extras");
		ClientRule<?> clientRule = this.get(key);
		if (clientRule != null) {
			clientRule.setValueFromJson(value);
			if (info instanceof JsonPrimitive) {
				clientRule.setOptionalInfo(info.getAsString());
			}
		}
		return clientRule;
	}

	private static <T extends ClientRule<?>> T register(T clientRule) {
		addRule(clientRule.getName(), clientRule);
		clientRule.onValueChange();
		clientRule.addListener(rule -> INSTANCE.saveConfig());
		return clientRule;
	}
}
