package me.senseiwells.essentialclient.utils.inventory;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunction;
import me.senseiwells.essentialclient.clientscript.definitions.ItemStackDef;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.interfaces.IGhostRecipeBookWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.*;
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

import static me.senseiwells.essentialclient.utils.EssentialUtils.*;

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
			} else if (slotType == EquipmentSlot.OFFHAND) {
				client.interactionManager.clickSlot(container.syncId, sourceSlot, 40, SlotActionType.SWAP, client.player);
			}
		}
	}

	public static void dropAllItemType(Item item) {
		PlayerEntity player = getPlayer();
		ClientPlayerInteractionManager interactionManager = getInteractionManager();
		ScreenHandler containerPlayer = player.currentScreenHandler;
		for (Slot slot : containerPlayer.slots) {
			ItemStack stack = slot.getStack();
			if (stack.getItem() == item) {
				interactionManager.clickSlot(containerPlayer.syncId, slot.id, 1, SlotActionType.THROW, player);
			}
		}
	}

	public static void dropAllItemExact(ItemStack test) {
		PlayerEntity player = getPlayer();
		ClientPlayerInteractionManager interactionManager = getInteractionManager();
		ScreenHandler containerPlayer = player.currentScreenHandler;
		for (Slot slot : containerPlayer.slots) {
			ItemStack stack = slot.getStack();
			if (stack.isItemEqual(test) && ItemStack.areNbtEqual(stack, test)) {
				interactionManager.clickSlot(containerPlayer.syncId, slot.id, 1, SlotActionType.THROW, player);
			}
		}
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

	public static void tryMoveItemsToCraftingGridSlots(ItemStack[] itemStacks, ScreenHandler screenHandler) {
		int numSlots = screenHandler.slots.size();
		if (9 < numSlots) {
			clearCraftingGridOfItems(itemStacks, screenHandler);

			Slot slotGridFirst = screenHandler.getSlot(1);
			Map<Item, List<Integer>> ingredientSlots = getSlotsPerItem(itemStacks);
			for (Map.Entry<Item, List<Integer>> entry : ingredientSlots.entrySet()) {
				ItemStack ingredientReference = entry.getKey().getDefaultStack();
				List<Integer> recipeSlots = entry.getValue();
				List<Integer> targetSlots = new ArrayList<>();
				for (int s : recipeSlots) {
					targetSlots.add(s + 1);
				}
				fillCraftingGrid(screenHandler, slotGridFirst, ingredientReference, targetSlots);
			}
		}
	}

	public static void clearCraftingGridOfItems(ItemStack[] recipe, ScreenHandler handler) {
		final int invSlots = handler.slots.size();
		for (int i = 0, slotNum = 1; i < 9 && slotNum < invSlots; i++, slotNum++) {
			EssentialUtils.throwAsRuntime(() -> Thread.sleep(0, 1));
			Slot slotTmp = handler.getSlot(slotNum);
			if (slotTmp != null && slotTmp.hasStack() && (!areStacksEqual(recipe[i], slotTmp.getStack()))) {
				shiftClickSlot(handler, slotNum);
				if (slotTmp.hasStack()) {
					dropStack(handler, slotNum);
				}
			}
		}
	}

	private static void fillCraftingGrid(ScreenHandler screenHandler, Slot slotGridFirst, ItemStack ingredientReference, List<Integer> targetSlots) {
		PlayerEntity player = EssentialUtils.getPlayer();
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
			slotNum = matchSlot(screenHandler, slotGridFirst, ingredientReference);
			if (slotNum < 0) {
				break;
			}
			if (slotReturn == -1) {
				slotReturn = slotNum;
			}
			leftClickSlot(screenHandler, slotNum);
			ItemStack stackCursor = getCursorStack();
			if (areStacksEqual(ingredientReference, stackCursor)) {
				sizeOrig = stackCursor.getCount();
				dragSplitItemsIntoSlots(screenHandler, targetSlots);
				stackCursor = getCursorStack();
				if (!stackCursor.isEmpty()) {
					if (stackCursor.getCount() >= sizeOrig) {
						break;
					}
					leftClickSlot(screenHandler, slotReturn);
					if (!getCursorStack().isEmpty()) {
						slotReturn = slotNum;
						leftClickSlot(screenHandler, slotReturn);
					}
				}
			} else {
				break;
			}
			if (!getCursorStack().isEmpty()) {
				break;
			}
		}
		if (slotNum >= 0 && !getCursorStack().isEmpty()) {
			leftClickSlot(screenHandler, slotNum);
		}
	}

	private static void dragSplitItemsIntoSlots(ScreenHandler handler, List<Integer> targetSlots) {
		ClientPlayerEntity player = getPlayer();
		if (player == null) {
			return;
		}
		ItemStack stackInCursor = getCursorStack();
		if (stackInCursor.isEmpty()) {
			return;
		}
		if (targetSlots.size() == 1) {
			leftClickSlot(handler, targetSlots.get(0));
			return;
		}
		int numSlots = handler.slots.size();
		// Start to drag the items
		craftClickSlot(handler, -999, 0);
		for (int slotNum : targetSlots) {
			if (slotNum >= numSlots) {
				break;
			}
			// Dragging the items
			craftClickSlot(handler, slotNum, 1);
		}
		// Finish dragging items
		craftClickSlot(handler, -999, 2);
	}

	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && stack1.isItemEqual(stack2);
	}

	public static boolean areStacksTotallyEqual(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && stack1.isItemEqual(stack2) && ItemStack.areNbtEqual(stack1, stack2);
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
				} else {
					List<Integer> integerList = new ArrayList<>();
					integerList.add(i);
					mapSlots.put(item, integerList);
				}
			}
		}
		return mapSlots;
	}

	public static void swapSlot(ScreenHandler screenHandler, int index, int secondIndex) {
		getInteractionManager().clickSlot(screenHandler.syncId, index, secondIndex, SlotActionType.SWAP, getPlayer());
	}

	public static void shiftClickSlot(ScreenHandler screen, int index) {
		getInteractionManager().clickSlot(screen.syncId, index, 0, SlotActionType.QUICK_MOVE, getPlayer());
	}

	public static void leftClickSlot(ScreenHandler screen, int slotNum) {
		getInteractionManager().clickSlot(screen.syncId, slotNum, 0, SlotActionType.PICKUP, getPlayer());
	}

	public static void craftClickSlot(ScreenHandler screen, int slotNum, int mouseButton) {
		getInteractionManager().clickSlot(screen.syncId, slotNum, mouseButton, SlotActionType.QUICK_CRAFT, getPlayer());
	}

	public static void dropStack(ScreenHandler screen, int slotNum) {
		getInteractionManager().clickSlot(screen.syncId, slotNum, 0, SlotActionType.THROW, getPlayer());
	}

	public static ItemStack getCursorStack() {
		return getPlayer().currentScreenHandler.getCursorStack();
	}

	public static boolean setCursorStack(ItemStack stack) {
		getPlayer().currentScreenHandler.setCursorStack(stack);
		return true;
	}

	public static ItemStack getStackAtSlot(ScreenHandler screenHandler, int slotId) {
		return screenHandler.slots.get(slotId).getStack();
	}

	public static boolean areRecipesEqual(Recipe<?> recipe, Recipe<?> other) {
		if (recipe == null || other == null) {
			return false;
		}
		return recipe.getId().equals(other.getId());
	}

	public static boolean areTradesEqual(TradeOffer trade, TradeOffer other) {
		return areStacksTotallyEqual(trade.getOriginalFirstBuyItem(), other.getOriginalFirstBuyItem())
			&& areStacksTotallyEqual(trade.getSecondBuyItem(), other.getSecondBuyItem())
			&& areStacksTotallyEqual(trade.getSellItem(), other.getSellItem());
	}

	public static boolean isGridEmpty(ScreenHandler screenHandler, int gridLength) {
		boolean isEmpty = true;
		List<Slot> slots = screenHandler.slots;
		for (int i = 0; i < gridLength; i++) {
			isEmpty &= !slots.get(i + 1).hasStack();
		}
		return isEmpty;
	}

	public static int getCraftingSlotLength(ScreenHandler handler) {
		if (handler instanceof AbstractRecipeScreenHandler<?> recipeScreen) {
			return recipeScreen.getCraftingHeight() * recipeScreen.getCraftingWidth();
		}
		return 0;
	}

	public static void emptyCraftingGrid(ScreenHandler screenHandler, PlayerEntity player, int gridSize) {
		List<Slot> slots = player.currentScreenHandler.slots;
		for (int i = 1; i < gridSize + 1; i++) {
			shiftClickSlot(screenHandler, i);
			if (slots.get(i).hasStack()) {
				dropStack(screenHandler, i);
			}
			throwAsRuntime(() -> Thread.sleep(0L, 1));
		}
	}

	public static void dropStackScheduled(ScreenHandler screenHandler, boolean craftAll) {
		CraftingSharedConstants.EXECUTOR.schedule(() -> {
			int count = craftAll ? 64 : 1;
			for (int i = 0; i < count; i++) {
				getClient().execute(() -> dropStack(screenHandler, 0));
				throwAsRuntime(() -> Thread.sleep(2L));
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
		} else if (handledScreen instanceof CraftingScreen craftingScreen) {
			widget = craftingScreen.getRecipeBookWidget();
		} else {
			// We not in correct screen. Should never happen really but just for the edge case
			return;
		}

		ScreenHandler handler = handledScreen.getScreenHandler();

		PlayerEntity player = getPlayer();

		int gridLength = getCraftingSlotLength(handler);

		// If chosen recipe is different or crafting grid has invalid recipe
		if (!areRecipesEqual(recipe, lastRecipe) || (!isGridEmpty(handler, gridLength) && getStackAtSlot(handler, 0).isEmpty())) {
			emptyCraftingGrid(handler, player, gridLength);
		}

		RecipeMatcher matcher = new RecipeMatcher();
		// Populating the matcher
		// Starting 1 to skip output slot.
		handler.slots.stream().skip(1).map(Slot::getStack).forEachOrdered(matcher::addUnenchantedInput);

		IntList craftInputIds = new IntArrayList();
		int totalCraftOps = matcher.countCrafts(recipe, craftInputIds);

		if (totalCraftOps == 0 && isGridEmpty(handler, gridLength)) {
			widget.showGhostRecipe(recipe, player.currentScreenHandler.slots);
		} else {
			((IGhostRecipeBookWidget) widget).clearGhostSlots();
			parseRecipeAndCacheInventory(handler, gridLength, recipe, craftInputIds, craftAll ? totalCraftOps : 1);
		}
		matcher.clear();
	}

	private static void parseRecipeAndCacheInventory(ScreenHandler screenHandler, int gridLength, Recipe<?> recipe, IntList craftInputIds, int craftOps) {
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
		cacheInventoryAndFillGrid(craftInputIds, screenHandler, craftOps);
	}

	public static void cacheInventoryAndFillGrid(IntList craftInputIds, ScreenHandler screenHandler, int craftOps) {
		Map<Integer, IntList> inventoryCache = new HashMap<>();
		Map<Integer, IntList> craftGridCache = new HashMap<>();
		List<Slot> slots = screenHandler.slots;

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

		fillCraftingGridWithItems(screenHandler, craftGridCache, inventoryCache, craftOps);
	}

	public static void fillCraftingGridWithItems(ScreenHandler screenHandler, Map<Integer, IntList> craftInputIds, Map<Integer, IntList> inventoryCache, int craftOps) {
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
					int count = getStackAtSlot(screenHandler, slotId).getCount();
					int endAt = Math.min(startAt + count, recipeRequiredAmount);

					leftClickSlot(screenHandler, slotId);
					dragSplitItemsIntoGrid(screenHandler, craftInputSlotIds, startAt, endAt);
					if (!getCursorStack().isEmpty()) {
						leftClickSlot(screenHandler, slotId);
					}
					startAt = endAt;
				}
				while (startAt < recipeRequiredAmount && slotCounter++ < slotsPerItem.size());
			}
		}
	}

	public static void dragSplitItemsIntoGrid(ScreenHandler screenHandler, IntList craftingSlotIds, int startAt, int endAt) {
		craftClickSlot(screenHandler, -999, 4);
		for (int i = startAt; i < craftingSlotIds.size() && i < endAt; i++) {
			int slotId = craftingSlotIds.getInt(i);
			craftClickSlot(screenHandler, slotId, 5);
		}
		craftClickSlot(screenHandler, -999, 6);
	}

	// ClientScript stuff

	public static void craftRecipe(Recipe<?> recipe, boolean shouldDrop) {
		ScreenHandler handler = clickRecipe(recipe, true);
		if (shouldDrop) {
			InventoryUtils.dropStackScheduled(handler, true);
		} else {
			InventoryUtils.shiftClickSlot(handler, 0);
		}
	}

	public static ScreenHandler clickRecipe(Recipe<?> recipe, boolean craftAll) {
		if (!(EssentialUtils.getClient().currentScreen instanceof HandledScreen<?> handledScreen)) {
			throw new RuntimeError("Must be in a crafting GUI");
		}
		ScreenHandler handler = handledScreen.getScreenHandler();
		CraftingSharedConstants.IS_SCRIPT_CLICK.set(true);
		EssentialUtils.getInteractionManager().clickRecipe(handler.syncId, recipe, craftAll);
		return handler;
	}

	public static Object anvil(Interpreter interpreter, ArucasFunction predicate1, ArucasFunction predicate2, boolean takeItems) {
		ClientPlayerEntity player = getPlayer();
		if (!(player.currentScreenHandler instanceof AnvilScreenHandler anvilHandler)) {
			throw new RuntimeError("Not in anvil gui");
		}
		interpreter = interpreter.branch();
		ClientPlayerInteractionManager interactionManager = getInteractionManager();
		boolean firstValid = false;
		boolean secondValid = false;
		for (Slot slot : anvilHandler.slots) {
			if (firstValid && secondValid) {
				break;
			}
			List<ClassInstance> arguments = List.of(interpreter.create(ItemStackDef.class, new ScriptItemStack(slot.getStack())));
			if (!firstValid) {
				Boolean returnValue = predicate1.invoke(interpreter.branch(), arguments).getPrimitive(BooleanDef.class);
				if (returnValue != null && returnValue) {
					firstValid = true;
					interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
					interactionManager.clickSlot(anvilHandler.syncId, 0, 0, SlotActionType.PICKUP, player);
					continue;
				}
			}
			if (!secondValid) {
				Boolean returnValue = predicate2.invoke(interpreter.branch(), arguments).getPrimitive(BooleanDef.class);
				if (returnValue != null && returnValue) {
					secondValid = true;
					interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
					interactionManager.clickSlot(anvilHandler.syncId, 1, 0, SlotActionType.PICKUP, player);
				}
			}
		}
		anvilHandler.updateResult();
		if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
			return anvilHandler.getLevelCost();
		}
		boolean successful = firstValid && secondValid && anvilHandler.getSlot(2).hasStack();
		if (takeItems) {
			shiftClickSlot(anvilHandler, 2);
		}
		return successful;
	}

	public static Object anvilRename(Interpreter interpreter, String newName, ArucasFunction predicate) {
		ClientPlayerEntity player = getPlayer();
		if (!(player.currentScreenHandler instanceof AnvilScreenHandler anvilHandler)) {
			throw new RuntimeError("Not in anvil gui");
		}
		interpreter = interpreter.branch();
		for (Slot slot : anvilHandler.slots) {
			ItemStack itemStack = slot.getStack();
			if (!itemStack.getName().getString().equals(newName)) {
				List<ClassInstance> args = List.of(interpreter.create(ItemStackDef.class, new ScriptItemStack(itemStack)));
				Boolean returnValue = predicate.invoke(interpreter.branch(), args).getPrimitive(BooleanDef.class);
				if (returnValue != null && returnValue) {
					shiftClickSlot(anvilHandler, slot.id);
					break;
				}
			}
		}
		anvilHandler.updateResult();
		anvilHandler.setNewItemName(newName);
		getNetworkHandler().sendPacket(new RenameItemC2SPacket(newName));
		if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
			return anvilHandler.getLevelCost();
		}
		if (anvilHandler.getSlot(2).hasStack()) {
			shiftClickSlot(anvilHandler, 2);
			return true;
		}
		return false;
	}

	public static boolean stonecutter(Item input, Item output) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		if (!(player.currentScreenHandler instanceof StonecutterScreenHandler cutterHandler)) {
			throw new RuntimeError("Not in stonecutter gui");
		}
		boolean valid = false;
		for (Slot slot : cutterHandler.slots) {
			if (slot.getStack().getItem() == input) {
				shiftClickSlot(cutterHandler, slot.id);
				valid = true;
				break;
			}
		}
		if (!valid) {
			return false;
		}
		List<StonecuttingRecipe> stonecuttingRecipes = cutterHandler.getAvailableRecipes();
		for (int i = 0; i < stonecuttingRecipes.size(); i++) {
			if (stonecuttingRecipes.get(i).getOutput().getItem() == output) {
				cutterHandler.onButtonClick(getPlayer(), i);
				getInteractionManager().clickButton(cutterHandler.syncId, i);
				if (cutterHandler.getSlot(1).hasStack()) {
					shiftClickSlot(cutterHandler, 1);
					return true;
				}
			}
		}
		return false;
	}

	public static void tradeAllItems(int index, boolean dropItems) {
		MerchantScreenHandler handler = checkScreen();
		Slot tradeSlot = handler.getSlot(2);
		while (true) {
			selectTrade(handler, index);
			if (!tradeSlot.hasStack()) {
				break;
			}
			ItemStack tradeStack = tradeSlot.getStack().copy();
			shiftClickSlot(handler, tradeSlot.id);
			if (dropItems) {
				dropAllItemType(tradeStack.getItem());
			}
			if (tradeSlot.hasStack()) {
				break;
			}
		}
		clearTradeInputSlot(handler);
	}

	private static void selectTrade(MerchantScreenHandler handler, int index) {
		handler.setRecipeIndex(index);
		handler.switchTo(index);
		EssentialUtils.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(index));
	}

	public static void selectTrade(int index) {
		MerchantScreenHandler handler = checkScreen();
		handler.setRecipeIndex(index);
		handler.switchTo(index);
		EssentialUtils.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(index));
	}

	public static void tradeSelectedRecipe(boolean drop) {
		MerchantScreenHandler screenHandler = checkScreen();
		Slot tradeSlot = screenHandler.getSlot(2);
		if (tradeSlot.getStack().getCount() == 0) {
			return;
		}
		if (drop) {
			getInteractionManager().clickSlot(screenHandler.syncId, 2, 1, SlotActionType.THROW, getPlayer());
			return;
		}
		shiftClickSlot(screenHandler, 2);
	}

	public static void clearTradeInputSlot(MerchantScreenHandler handler) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		Slot slot = handler.getSlot(0);
		if (slot.hasStack()) {
			if (canMergeIntoMain(player.getInventory(), slot.getStack())) {
				shiftClickSlot(handler, slot.id);
			} else {
				dropStack(handler, slot.id);
			}
		}
		slot = handler.getSlot(1);
		if (!slot.hasStack()) {
			return;
		}
		if (canMergeIntoMain(player.getInventory(), slot.getStack())) {
			shiftClickSlot(handler, slot.id);
			return;
		}
		dropStack(handler, slot.id);
	}

	public static boolean checkTradeDisabled(int index) {
		TradeOfferList tradeOffers = checkScreen().getRecipes();
		if (index > tradeOffers.size() - 1) {
			throwOutOfBounds();
		}
		return tradeOffers.get(index).isDisabled();
	}

	public static int checkPriceForTrade(int index) {
		TradeOfferList tradeOffers = checkScreen().getRecipes();
		if (index > tradeOffers.size() - 1) {
			throwOutOfBounds();
		}
		return tradeOffers.get(index).getAdjustedFirstBuyItem().getCount();
	}

	public static boolean checkHasTrade(Item item) {
		for (TradeOffer offer : checkScreen().getRecipes()) {
			if (offer.getSellItem().getItem() == item) {
				return true;
			}
		}
		return false;
	}

	public static void clearTrade() {
		clearTradeInputSlot(checkScreen());
	}

	public static boolean isTradeSelected() {
		Slot tradeSlot = checkScreen().getSlot(2);
		return tradeSlot.getStack().getCount() != 0;
	}

	public static ItemStack getTrade(int index) {
		TradeOfferList tradeOffers = checkScreen().getRecipes();
		if (index > tradeOffers.size() - 1) {
			throwOutOfBounds();
		}
		return tradeOffers.get(index).getSellItem();
	}

	public static int getIndexOfItemInMerchant(Item item) {
		int i = 0;
		for (TradeOffer offer : checkScreen().getRecipes()) {
			if (offer.getSellItem().getItem() == item) {
				return i;
			}
			i++;
		}
		throwOutOfBounds();
		return -1;
	}

	private static MerchantScreenHandler checkScreen() {
		if (EssentialUtils.getClient().currentScreen instanceof MerchantScreen merchantScreen) {
			return merchantScreen.getScreenHandler();
		}
		throw new RuntimeError("Not in merchant screen");
	}

	private static void throwOutOfBounds() {
		throw new RuntimeError("That trade is out of bounds");
	}
}
