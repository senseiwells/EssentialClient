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
import net.minecraft.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScreenRemapper {
	private static final Map<Class<? extends Screen>, String> SCREEN_REMAPPER = Util.make(new LinkedHashMap<>(), map -> {
		map.put(AccessibilityOptionsScreen.class, "AccessibilityOptions");
		map.put(AdvancementsScreen.class, "Advancement");
		map.put(AnvilScreen.class, "Anvil");
		map.put(BeaconScreen.class, "Beacon");
		map.put(BlastFurnaceScreen.class, "BlastFurnace");
		map.put(BookEditScreen.class, "BookEdit");
		map.put(BookScreen.class, "BookScreen");
		map.put(BrewingStandScreen.class, "BrewingStand");
		map.put(CartographyTableScreen.class, "CartographyTable");
		map.put(ChatOptionsScreen.class, "ChatOptions");
		map.put(ChatScreen.class, "Chat");
		map.put(CommandBlockScreen.class, "CommandBlock");
		//#if MC >= 11901
		//$$map.put(ConfirmLinkScreen.class, "ConfirmChatLink");
		// #endif
		map.put(ConfirmScreen.class, "Confirm");
		map.put(ConnectScreen.class, "Connect");
		map.put(ControlsOptionsScreen.class, "ControlOptions");
		map.put(CraftingScreen.class, "Crafting");
		map.put(CreativeInventoryScreen.class, "CreativeInventory");
		map.put(CreditsScreen.class, "Credits");
		map.put(DeathScreen.class, "Death");
		map.put(DialogScreen.class, "Dialog");
		map.put(DisconnectedRealmsScreen.class, "DisconnectedRealms");
		map.put(DisconnectedScreen.class, "Disconnected");
		map.put(EditGameRulesScreen.class, "EditGameRules");
		map.put(EditWorldScreen.class, "EditWorld");
		map.put(EnchantmentScreen.class, "Enchantment");
		map.put(ForgingScreen.class, "Forging");
		map.put(FurnaceScreen.class, "Furnace");
		map.put(GameMenuScreen.class, "GameMenu");
		map.put(GameModeSelectionScreen.class, "GameModeSelection");
		map.put(GameOptionsScreen.class, "GameOptions");
		map.put(Generic3x3ContainerScreen.class, "Generic3x3Container");
		map.put(GenericContainerScreen.class, "GenericContainer");
		map.put(GrindstoneScreen.class, "Grindstone");
		map.put(HopperScreen.class, "Hopper");
		map.put(HorseScreen.class, "Horse");
		map.put(InventoryScreen.class, "Inventory");
		map.put(JigsawBlockScreen.class, "JigsawBlock");
		map.put(LanguageOptionsScreen.class, "LanguageOptions");
		map.put(LecternScreen.class, "Lectern");
		map.put(LevelLoadingScreen.class, "LevelLoading");
		map.put(LoomScreen.class, "LoomScreen");
		map.put(MerchantScreen.class, "Merchant");
		map.put(MinecartCommandBlockScreen.class, "MinecrartCommandBlock");
		map.put(MouseOptionsScreen.class, "MouseOptions");
		map.put(SimpleOptionsScreen.class, "SimpleOptions");
		map.put(NoticeScreen.class, "Notice");
		map.put(OpenToLanScreen.class, "OpenToLan");
		map.put(OptionsScreen.class, "Options");
		map.put(OutOfMemoryScreen.class, "OutOfMemory");
		map.put(PackScreen.class, "Pack");
		map.put(PresetsScreen.class, "Presets");
		map.put(ProgressScreen.class, "Progress");
		map.put(MessageScreen.class, "SaveLevel");
		map.put(ShulkerBoxScreen.class, "ShulkerBox");
		map.put(SignEditScreen.class, "SignEdit");
		map.put(SkinOptionsScreen.class, "SkinOptions");
		map.put(SleepingChatScreen.class, "SleepingChat");
		map.put(SmithingScreen.class, "Smithing");
		map.put(SmokerScreen.class, "Smoker");
		map.put(SocialInteractionsScreen.class, "SocialInteractions");
		map.put(SoundOptionsScreen.class, "SoundOptions");
		map.put(StatsScreen.class, "Stats");
		map.put(StonecutterScreen.class, "Stonecutter");
		map.put(StructureBlockScreen.class, "StructureBlock");
		map.put(VideoOptionsScreen.class, "VideoOptions");
	});

	public static String getScreenName(Class<? extends Screen> screen) {
		String screenName = SCREEN_REMAPPER.get(screen);
		return screenName != null ? screenName : screen.getSimpleName().replaceFirst("Screen$", "");
	}
}
