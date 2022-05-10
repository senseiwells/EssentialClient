package me.senseiwells.essentialclient.utils.inventory;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.interfaces.IGhostRecipeBookWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class InventoryUtils {
	public static Slot getItemSlot(ClientPlayerEntity playerEntity, Item item) {
		ScreenHandler containerPlayer = playerEntity.currentScreenHandler;
		Predicate<ItemStack> filterTotemStack = (s) -> s.getItem() == item;
		Slot targetSlot = null;
		for (Slot slot : containerPlayer.slots) {
			ItemStack stack = slot.getStack();
			if (!stack.isEmpty() && filterTotemStack.test(stack)) {
				targetSlot = slot;
				break;
			}
		}
		return targetSlot;
	}

	public static void swapItemToEquipmentSlot(ClientPlayerEntity playerEntity, EquipmentSlot slotType, int sourceSlot) {
		if (sourceSlot != -1 && playerEntity.currentScreenHandler == playerEntity.playerScreenHandler) {
			ScreenHandler container = playerEntity.playerScreenHandler;
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.interactionManager == null) {
				return;
			}
			if (slotType == EquipmentSlot.MAINHAND) {
				int currentHotbarSlot = playerEntity.getInventory().selectedSlot;
				client.interactionManager.clickSlot(container.syncId, sourceSlot, currentHotbarSlot, SlotActionType.SWAP, client.player);
			}
			else if (slotType == EquipmentSlot.OFFHAND) {
				client.interactionManager.clickSlot(container.syncId, sourceSlot, 40, SlotActionType.SWAP, client.player);
			}
		}
	}

	public static void dropAllItemType(ClientPlayerEntity playerEntity, Item item) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.interactionManager == null) {
			return;
		}
		ScreenHandler containerPlayer = playerEntity.currentScreenHandler;
		Predicate<ItemStack> filterItemStack = (s) -> s.getItem() == item;
		for (Slot slot : containerPlayer.slots) {
			ItemStack stack = slot.getStack();
			if (filterItemStack.test(stack)) {
				client.interactionManager.clickSlot(containerPlayer.syncId, slot.id, 1, SlotActionType.THROW, playerEntity);
			}
		}
	}

	// Returns true if failed
	public static boolean tradeAllItems(MinecraftClient client, int index, boolean dropItems) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return true;
		}
		client.execute(() -> {
			Slot tradeSlot = merchantScreen.getScreenHandler().getSlot(2);
			while (true) {
				selectTrade(client, merchantScreen, index);
				if (!tradeSlot.hasStack()) {
					break;
				}
				ItemStack tradeStack = tradeSlot.getStack().copy();
				shiftClickSlot(client, merchantScreen, tradeSlot.id);
				if (dropItems) {
					dropAllItemType(client.player, tradeStack.getItem());
				}
				if (tradeSlot.hasStack()) {
					break;
				}
			}
			clearTradeInputSlot(client, merchantScreen);
		});
		return false;
	}

	public static void selectTrade(MinecraftClient client, MerchantScreen merchantScreen, int index) {
		if (client.getNetworkHandler() == null) {
			return;
		}
		MerchantScreenHandler handler = merchantScreen.getScreenHandler();
		handler.setRecipeIndex(index);
		client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(index));
	}

	public static boolean selectTrade(MinecraftClient client, int index) {
		if (client.getNetworkHandler() == null || !(client.currentScreen instanceof MerchantScreen merchantScreen)) {
			return false;
		}
		MerchantScreenHandler handler = merchantScreen.getScreenHandler();
		client.execute(() -> {
			handler.setRecipeIndex(index);
			client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(index));
		});
		return true;
	}

	// Returns true if failed
	public static boolean tradeSelectedRecipe(MinecraftClient client, boolean drop) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return true;
		}
		MerchantScreenHandler screenHandler = merchantScreen.getScreenHandler();
		Slot tradeSlot = screenHandler.getSlot(2);
		if (tradeSlot.getStack().getCount() == 0) {
			return false;
		}
		client.execute(() -> {
			if (drop) {
				client.interactionManager.clickSlot(merchantScreen.getScreenHandler().syncId, 2, 1, SlotActionType.THROW, client.player);
				return;
			}
			shiftClickSlot(client, merchantScreen, 2);
		});
		return false;
	}

	public static void clearTradeInputSlot(MinecraftClient client, MerchantScreen merchantScreen) {
		if (client.player == null) {
			return;
		}
		Slot slot = merchantScreen.getScreenHandler().getSlot(0);
		if (slot.hasStack()) {
			if (canMergeIntoMain(client.player.getInventory(), slot.getStack())) {
				shiftClickSlot(client, merchantScreen, slot.id);
			}
			else {
				dropStack(client, merchantScreen, slot.id);
			}
		}
		slot = merchantScreen.getScreenHandler().getSlot(1);
		if (!slot.hasStack()) {
			return;
		}
		if (canMergeIntoMain(client.player.getInventory(), slot.getStack())) {
			shiftClickSlot(client, merchantScreen, slot.id);
			return;
		}
		dropStack(client, merchantScreen, slot.id);
	}

	public static int isSlotInHotbar(ScreenHandler screenHandler, int slotNum) {
		int totalSize = screenHandler.slots.size();
		if (screenHandler instanceof PlayerScreenHandler) {
			return slotNum == 45 ? 40 : slotNum >= 36 ? slotNum - 36 : -1;
		}
		return slotNum >= totalSize - 10 ? slotNum - (totalSize - 9) : -1;
	}

	private static boolean canMergeIntoMain(PlayerInventory inventory, ItemStack stack) {
		int count = stack.getCount();
		for (int i = 0; i < inventory.main.size(); i++) {
			ItemStack itemStack = inventory.main.get(i);
			if (itemStack.isEmpty()) {
				return true;
			}
			if (itemStack.isItemEqual(stack)) {
				count -= itemStack.getMaxCount() - itemStack.getCount();
				if (count <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static int checkTradeDisabled(MinecraftClient client, int index) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return -2;
		}
		TradeOfferList tradeOffers = merchantScreen.getScreenHandler().getRecipes();
		if (index > tradeOffers.size() - 1) {
			return -1;
		}
		return tradeOffers.get(index).isDisabled() ? 1 : 0;
	}

	public static int checkPriceForTrade(MinecraftClient client, int index) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return -2;
		}
		TradeOfferList tradeOffers = merchantScreen.getScreenHandler().getRecipes();
		if (index > tradeOffers.size() - 1) {
			return -1;
		}
		return tradeOffers.get(index).getAdjustedFirstBuyItem().getCount();
	}

	public static int checkHasTrade(MinecraftClient client, Item item) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return -2;
		}
		for (TradeOffer offer : merchantScreen.getScreenHandler().getRecipes()) {
			if (offer.getSellItem().getItem() == item) {
				return 1;
			}
		}
		return 0;
	}

	public static boolean clearTrade(MinecraftClient client) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return false;
		}
		client.execute(() -> clearTradeInputSlot(client, merchantScreen));
		return true;
	}

	public static boolean isTradeSelected(MinecraftClient client) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return false;
		}
		Slot tradeSlot = merchantScreen.getScreenHandler().getSlot(2);
		return tradeSlot.getStack().getCount() != 0;
	}

	public static ItemStack getTrade(MinecraftClient client, int index) throws RuntimeException {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			throw new RuntimeException();
		}
		TradeOfferList tradeOffers = merchantScreen.getScreenHandler().getRecipes();
		if (index > tradeOffers.size() - 1) {
			return null;
		}
		return tradeOffers.get(index).getSellItem();
	}

	public static int getIndexOfItemInMerchant(MinecraftClient client, Item item) {
		if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
			return -2;
		}
		int i = 0;
		for (TradeOffer offer : merchantScreen.getScreenHandler().getRecipes()) {
			if (offer.getSellItem().getItem() == item) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static void tryMoveItemsToCraftingGridSlots(MinecraftClient client, ItemStack[] itemStacks, HandledScreen<? extends ScreenHandler> screen) {
		ScreenHandler container = screen.getScreenHandler();
		int numSlots = container.slots.size();
		if (9 < numSlots) {
			clearCraftingGridOfItems(client, itemStacks, screen);

			Slot slotGridFirst = container.getSlot(1);
			Map<Item, List<Integer>> ingredientSlots = getSlotsPerItem(itemStacks);
			for (Map.Entry<Item, List<Integer>> entry : ingredientSlots.entrySet()) {
				ItemStack ingredientReference = entry.getKey().getDefaultStack();
				List<Integer> recipeSlots = entry.getValue();
				List<Integer> targetSlots = new ArrayList<>();
				for (int s : recipeSlots) {
					targetSlots.add(s + 1);
				}
				fillCraftingGrid(client, screen, slotGridFirst, ingredientReference, targetSlots);
			}
		}
	}

	public static void clearCraftingGridOfItems(MinecraftClient client, ItemStack[] recipe, HandledScreen<? extends ScreenHandler> gui) {
		final int invSlots = gui.getScreenHandler().slots.size();
		for (int i = 0, slotNum = 1; i < 9 && slotNum < invSlots; i++, slotNum++) {
			ExceptionUtils.runSafe(() -> Thread.sleep(0, 1));
			Slot slotTmp = gui.getScreenHandler().getSlot(slotNum);
			if (slotTmp != null && slotTmp.hasStack() && (!areStacksEqual(recipe[i], slotTmp.getStack()))) {
				shiftClickSlot(client, gui, slotNum);
				if (slotTmp.hasStack()) {
					dropStack(client, gui, slotNum);
				}
			}
		}
	}

	private static void fillCraftingGrid(MinecraftClient client, HandledScreen<? extends ScreenHandler> gui, Slot slotGridFirst, ItemStack ingredientReference, List<Integer> targetSlots) {
		ScreenHandler container = gui.getScreenHandler();
		PlayerEntity player = client.player;
		if (player == null) {
			return;
		}
		int slotNum;
		int slotReturn = -1;
		int sizeOrig;
		if (ingredientReference.isEmpty()) {
			return;
		}
		while (true) {
			slotNum = matchSlot(container, slotGridFirst, ingredientReference);
			if (slotNum < 0) {
				break;
			}
			if (slotReturn == -1) {
				slotReturn = slotNum;
			}
			leftClickSlot(client, gui, slotNum);
			ItemStack stackCursor = getCursorStack(client);
			if (areStacksEqual(ingredientReference, stackCursor)) {
				sizeOrig = stackCursor.getCount();
				dragSplitItemsIntoSlots(client, gui, targetSlots);
				stackCursor = getCursorStack(client);
				if (!stackCursor.isEmpty()) {
					if (stackCursor.getCount() >= sizeOrig) {
						break;
					}
					leftClickSlot(client, gui, slotReturn);
					if (!getCursorStack(client).isEmpty()) {
						slotReturn = slotNum;
						leftClickSlot(client, gui, slotReturn);
					}
				}
			}
			else {
				break;
			}
			if (!getCursorStack(client).isEmpty()) {
				break;
			}
		}
		if (slotNum >= 0 && !getCursorStack(client).isEmpty()) {
			leftClickSlot(client, gui, slotNum);
		}
	}

	private static void dragSplitItemsIntoSlots(MinecraftClient client, HandledScreen<? extends ScreenHandler> gui, List<Integer> targetSlots) {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			return;
		}
		ItemStack stackInCursor = getCursorStack(client);
		if (stackInCursor.isEmpty()) {
			return;
		}
		if (targetSlots.size() == 1) {
			leftClickSlot(client, gui, targetSlots.get(0));
			return;
		}
		int numSlots = gui.getScreenHandler().slots.size();
		// Start to drag the items
		craftClickSlot(client, gui, -999, 0);
		for (int slotNum : targetSlots) {
			if (slotNum >= numSlots) {
				break;
			}
			// Dragging the items
			craftClickSlot(client, gui, slotNum, 1);
		}
		// Finish dragging items
		craftClickSlot(client, gui, -999, 2);
	}

	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && stack1.isItemEqual(stack2) /*&& ItemStack.areTagsEqual(stack1, stack2)*/;
	}

	private static int matchSlot(ScreenHandler container, Slot slotReference, ItemStack stackReference) {
		int slotNum = -1;
		int largest = 0;
		for (Slot slot : container.slots) {
			if (slot.inventory != slotReference.inventory && slot.hasStack() && areStacksEqual(stackReference, slot.getStack())) {
				int stackSize = slot.getStack().getCount();
				if (stackSize > largest) {
					slotNum = slot.id;
					largest = stackSize;
				}
			}
		}
		return slotNum;
	}

	/**
	 * @param stacks - this is the array of stacks that you want to craft
	 * @return - a map of the itemStack with a list of all the slots it is needed in
	 */
	private static Map<Item, List<Integer>> getSlotsPerItem(ItemStack[] stacks) {
		Map<Item, List<Integer>> mapSlots = new HashMap<>();
		for (int i = 0; i < stacks.length; i++) {
			Item item = stacks[i].getItem();
			if (item != Items.AIR) {
				if (mapSlots.containsKey(item)) {
					mapSlots.get(item).add(i);
				}
				else {
					List<Integer> integerList = new ArrayList<>();
					integerList.add(i);
					mapSlots.put(item, integerList);
				}
			}
		}
		return mapSlots;
	}

	@SuppressWarnings("UnusedReturnValue")
	public static boolean swapSlot(MinecraftClient client, ScreenHandler screenHandler, int index, int secondIndex) {
		if (client.interactionManager != null) {
			client.execute(() -> client.interactionManager.clickSlot(screenHandler.syncId, index, secondIndex, SlotActionType.SWAP, client.player));
			return true;
		}
		return false;
	}

	public static void shiftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int index) {
		if (client.interactionManager != null) {
			client.execute(() -> client.interactionManager.clickSlot(screen.getScreenHandler().syncId, index, 0, SlotActionType.QUICK_MOVE, client.player));
		}
	}

	public static void leftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
		if (client.interactionManager != null) {
			client.execute(() -> client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 0, SlotActionType.PICKUP, client.player));
		}
	}

	public static void craftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum, int mouseButton) {
		if (client.interactionManager != null) {
			client.execute(() -> client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, mouseButton, SlotActionType.QUICK_CRAFT, client.player));
		}
	}

	public static void dropStack(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
		if (client.interactionManager != null) {
			client.execute(() -> client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 1, SlotActionType.THROW, client.player));
		}
	}

	@SuppressWarnings("unused")
	public static void dropSingle(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
		if (client.interactionManager != null) {
			client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 0, SlotActionType.THROW, client.player);
		}
	}

	public static ItemStack getCursorStack(MinecraftClient client) {
		if (client == null || client.player == null) {
			return ItemStack.EMPTY;
		}
		return client.player.currentScreenHandler.getCursorStack();
	}

	public static boolean setCursorStack(MinecraftClient client, ItemStack stack) {
		if (client != null && client.player != null) {
			client.player.currentScreenHandler.setCursorStack(stack);
			return true;
		}
		return false;
	}

	public static ItemStack getStackAtSlot(HandledScreen<?> handledScreen, int slotId) {
		return handledScreen.getScreenHandler().slots.get(slotId).getStack();
	}

	public static boolean areRecipesEqual(Recipe<?> recipe, Recipe<?> other) {
		if (recipe == null || other == null) {
			return false;
		}
		return recipe.getId().equals(other.getId());
	}

	public static boolean isGridEmpty(HandledScreen<?> handledScreen, int gridLength) {
		boolean isEmpty = true;
		List<Slot> slots = handledScreen.getScreenHandler().slots;
		for (int i = 0; i < gridLength; i++) {
			isEmpty &= slots.get(i + 1).hasStack();
		}
		return isEmpty;
	}

	public static int getCraftingSlotLength(HandledScreen<? extends ScreenHandler> gui) {
		ScreenHandler grid = gui.getScreenHandler();
		int counter = 0;
		for (Slot slot : grid.slots) {
			if (slot.inventory instanceof PlayerInventory) {
				break;
			}
			counter += slot.inventory instanceof CraftingInventory ? 1 : 0;
		}
		return counter;
	}

	public static void emptyCraftingGrid(MinecraftClient client, HandledScreen<?> handledScreen, PlayerEntity player, int gridSize) {
		List<Slot> slots = player.currentScreenHandler.slots;
		for (int i = 1; i < gridSize + 1; i++) {
			shiftClickSlot(client, handledScreen, i);
			if (slots.get(i).hasStack()) {
				dropStack(client, handledScreen, i);
			}
			try {
				Thread.sleep(0, 1);
			}
			catch (InterruptedException ignored) {
			}
		}
	}

	public static void dropStackScheduled(MinecraftClient client, HandledScreen<?> handledScreen, boolean craftAll) {
		CraftingSharedConstants.EXECUTOR.schedule(() -> {
			int count = craftAll ? 64 : 1;
			for (int i = 0; i < count; i++) {
				InventoryUtils.dropStack(client, handledScreen, 0);
				try {
					Thread.sleep(2L);
				}
				catch (InterruptedException ignored) {
				}
			}
		}, 40L, TimeUnit.MILLISECONDS);
	}

	@SuppressWarnings("unused")
	public static void doCraftingSlotsFillAction(Recipe<?> recipe, HandledScreen<?> handledScreen, boolean craftAll) {
		doCraftingSlotsFillAction(recipe, null, handledScreen, craftAll);
	}

	public static void doCraftingSlotsFillAction(Recipe<?> recipe, Recipe<?> lastRecipe, HandledScreen<?> handledScreen, boolean craftAll) {
		RecipeBookWidget widget;
		if (handledScreen instanceof InventoryScreen playerScreen) {
			widget = playerScreen.getRecipeBookWidget();
		}
		else if (handledScreen instanceof CraftingScreen craftingScreen) {
			widget = craftingScreen.getRecipeBookWidget();
		}
		else {
			// We not in correct screen. Should never happen really but just for the edge case
			return;
		}

		MinecraftClient mc = EssentialUtils.getClient();
		PlayerEntity player = EssentialUtils.getPlayer();

		int gridLength = InventoryUtils.getCraftingSlotLength(handledScreen);

		// If chosen recipe is different or crafting grid has invalid recipe
		if (!InventoryUtils.areRecipesEqual(recipe, lastRecipe) || (!isGridEmpty(handledScreen, gridLength) && getStackAtSlot(handledScreen, 0).isEmpty())) {
			InventoryUtils.emptyCraftingGrid(mc, handledScreen, player, gridLength);
		}

		RecipeMatcher matcher = new RecipeMatcher();
		// Populating the matcher
		// Starting 1 to skip output slot.
		handledScreen.getScreenHandler().slots.stream().skip(1).map(Slot::getStack).forEachOrdered(matcher::addUnenchantedInput);

		IntList craftInputIds = new IntArrayList();
		int totalCraftOps = matcher.countCrafts(recipe, craftInputIds);

		if (totalCraftOps == 0 && isGridEmpty(handledScreen, gridLength)) {
			widget.showGhostRecipe(recipe, player.currentScreenHandler.slots);
		}
		else {
			((IGhostRecipeBookWidget) widget).clearGhostSlots();
			parseRecipeAndCacheInventory(mc, handledScreen, gridLength, recipe, craftInputIds, craftAll ? totalCraftOps : 1);
		}
		matcher.clear();
	}

	private static void parseRecipeAndCacheInventory(MinecraftClient mc, HandledScreen<?> handledScreen, int gridLength, Recipe<?> recipe, IntList craftInputIds, int craftOps) {
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			int gridSide = (int) Math.sqrt(gridLength);
			int recipeWidth = shapedRecipe.getWidth();
			int recipeHeight = shapedRecipe.getHeight();

			// Filling the recipe with AIR to convert 2x2 into 3x3 if player using 3x3 grid. otherwise, it does nothing
			for (int i = 0; i < recipeHeight - 1; i++) {
				for (int j = recipeWidth; j < gridSide; j++) {
					craftInputIds.add(i * gridSide + j, 0);
				}
			}
		}
		InventoryUtils.cacheInventoryAndFillGrid(mc, craftInputIds, handledScreen, craftOps);
	}

	public static void cacheInventoryAndFillGrid(MinecraftClient client, IntList craftInputIds, HandledScreen<?> handledScreen, int craftOps) {
		Map<Integer, IntList> inventoryCache = new HashMap<>();
		Map<Integer, IntList> craftGridCache = new HashMap<>();
		List<Slot> slots = handledScreen.getScreenHandler().slots;

		for (int i = 0, size = craftInputIds.size(); i < size; i++) {
			int slotId = craftInputIds.getInt(i);
			if (slotId != 0) {
				craftGridCache.computeIfAbsent(slotId, IntArrayList::new).add(i + 1); // +1 to skip output slot
			}
		}

		for (Slot slot : slots) {
			int id = RecipeMatcher.getItemId(slot.getStack());
			if (slot.inventory instanceof PlayerInventory && id != 0) {
				inventoryCache.computeIfAbsent(id, IntArrayList::new).add(slot.id);
			}
		}

		fillCraftingGridWithItems(client, handledScreen, craftGridCache, inventoryCache, craftOps);
	}

	public static void fillCraftingGridWithItems(MinecraftClient client, HandledScreen<?> handledScreen, Map<Integer, IntList> craftInputIds, Map<Integer, IntList> inventoryCache, int craftOps) {
		for (Integer itemId : craftInputIds.keySet()) {
			IntList craftInputSlotIds = craftInputIds.get(itemId);
			int recipeRequiredAmount = craftInputSlotIds.size();
			for (int i = 0; i < craftOps; i++) {
				int startAt = 0;
				int slotCounter = 0;
				IntList slotsPerItem = inventoryCache.get(itemId);
				if (slotsPerItem == null) {
					continue;
				}
				do {
					int slotId = slotsPerItem.getInt(slotCounter);
					int count = getStackAtSlot(handledScreen, slotId).getCount();
					int endAt = Math.min(startAt + count, recipeRequiredAmount);

					leftClickSlot(client, handledScreen, slotId);
					dragSplitItemsIntoGrid(client, handledScreen, craftInputSlotIds, startAt, endAt);
					if (!getCursorStack(client).isEmpty()) {
						leftClickSlot(client, handledScreen, slotId);
					}
					startAt = endAt;
				}
				while (startAt < recipeRequiredAmount && slotCounter++ < slotsPerItem.size());
			}
		}
	}

	public static void dragSplitItemsIntoGrid(MinecraftClient client, HandledScreen<?> handledScreen, IntList craftingSlotIds, int startAt, int endAt) {
		craftClickSlot(client, handledScreen, -999, 4);
		for (int i = startAt; i < craftingSlotIds.size() && i < endAt; i++) {
			int slotId = craftingSlotIds.getInt(i);
			craftClickSlot(client, handledScreen, slotId, 5);
		}
		craftClickSlot(client, handledScreen, -999, 6);
	}
}
