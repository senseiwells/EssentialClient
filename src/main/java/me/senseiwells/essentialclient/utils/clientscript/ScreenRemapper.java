package me.senseiwells.essentialclient.utils.clientscript;

import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;

import java.util.Map;

public class ScreenRemapper {
	private static final Map<Class<? extends Screen>, String> SCREEN_REMAPPER = Map.ofEntries(
		Map.entry(AccessibilityOptionsScreen.class, "AccessibilityOptions"),
		Map.entry(AdvancementsScreen.class, "Advancement"),
		Map.entry(AnvilScreen.class, "Anvil"),
		Map.entry(BeaconScreen.class, "Beacon"),
		Map.entry(BlastFurnaceScreen.class, "BlastFurnace"),
		Map.entry(BookEditScreen.class, "BookEdit"),
		Map.entry(BookScreen.class, "BookScreen"),
		Map.entry(BrewingStandScreen.class, "BrewingStand"),
		Map.entry(CartographyTableScreen.class, "CartographyTable"),
		Map.entry(ChatOptionsScreen.class, "ChatOptions"),
		Map.entry(ChatScreen.class, "Chat"),
		Map.entry(CommandBlockScreen.class, "CommandBlock"),
		Map.entry(ConfirmLinkScreen.class, "ConfirmChatLink"),
		Map.entry(ConfirmScreen.class, "Confirm"),
		Map.entry(ConnectScreen.class, "Connect"),
		Map.entry(ControlsOptionsScreen.class, "ControlOptions"),
		Map.entry(CraftingScreen.class, "Crafting"),
		Map.entry(CreativeInventoryScreen.class, "CreativeInventory"),
		Map.entry(CreditsScreen.class, "Credits"),
		Map.entry(DeathScreen.class, "Death"),
		Map.entry(DialogScreen.class, "Dialog"),
		Map.entry(DisconnectedRealmsScreen.class, "DisconnectedRealms"),
		Map.entry(DisconnectedScreen.class, "Disconnected"),
		Map.entry(EditGameRulesScreen.class, "EditGameRules"),
		Map.entry(EditWorldScreen.class, "EditWorld"),
		Map.entry(EnchantmentScreen.class, "Enchantment"),
		Map.entry(ForgingScreen.class, "Forging"),
		Map.entry(FurnaceScreen.class, "Furnace"),
		Map.entry(GameMenuScreen.class, "GameMenu"),
		Map.entry(GameModeSelectionScreen.class, "GameModeSelection"),
		Map.entry(GameOptionsScreen.class, "GameOptions"),
		Map.entry(Generic3x3ContainerScreen.class, "Generic3x3Container"),
		Map.entry(GenericContainerScreen.class, "GenericContainer"),
		Map.entry(GrindstoneScreen.class, "Grindstone"),
		Map.entry(HopperScreen.class, "Hopper"),
		Map.entry(HorseScreen.class, "Horse"),
		Map.entry(InventoryScreen.class, "Inventory"),
		Map.entry(JigsawBlockScreen.class, "JigsawBlock"),
		Map.entry(LanguageOptionsScreen.class, "LanguageOptions"),
		Map.entry(LecternScreen.class, "Lectern"),
		Map.entry(LevelLoadingScreen.class, "LevelLoading"),
		Map.entry(LoomScreen.class, "LoomScreen"),
		Map.entry(MerchantScreen.class, "Merchant"),
		Map.entry(MinecartCommandBlockScreen.class, "MinecrartCommandBlock"),
		Map.entry(MouseOptionsScreen.class, "MouseOptions"),
		Map.entry(SimpleOptionsScreen.class, "SimpleOptions"),
		Map.entry(NoticeScreen.class, "Notice"),
		Map.entry(OpenToLanScreen.class, "OpenToLan"),
		Map.entry(OptionsScreen.class, "Options"),
		Map.entry(OutOfMemoryScreen.class, "OutOfMemory"),
		Map.entry(PackScreen.class, "Pack"),
		Map.entry(PresetsScreen.class, "Presets"),
		Map.entry(ProgressScreen.class, "Progress"),
		Map.entry(MessageScreen.class, "SaveLevel"),
		Map.entry(ShulkerBoxScreen.class, "ShulkerBox"),
		Map.entry(SignEditScreen.class, "SignEdit"),
		Map.entry(SkinOptionsScreen.class, "SkinOptions"),
		Map.entry(SleepingChatScreen.class, "SleepingChat"),
		Map.entry(SmithingScreen.class, "Smithing"),
		Map.entry(SmokerScreen.class, "Smoker"),
		Map.entry(SocialInteractionsScreen.class, "SocialInteractions"),
		Map.entry(SoundOptionsScreen.class, "SoundOptions"),
		Map.entry(StatsScreen.class, "Stats"),
		Map.entry(StonecutterScreen.class, "Stonecutter"),
		Map.entry(StructureBlockScreen.class, "StructureBlock"),
		Map.entry(VideoOptionsScreen.class, "VideoOptions")
	);

	public static String getScreenName(Class<? extends Screen> screen) {
		String screenName = SCREEN_REMAPPER.get(screen);
		return screenName != null ? screenName : screen.getSimpleName().replaceFirst("Screen$", "");
	}
}
