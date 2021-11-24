package essentialclient.utils.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                int currentHotbarSlot = playerEntity.inventory.selectedSlot;
                client.interactionManager.clickSlot(container.syncId, sourceSlot, currentHotbarSlot, SlotActionType.SWAP, client.player);
            }
            else if (slotType == EquipmentSlot.OFFHAND) {
                int tempSlot = (playerEntity.inventory.selectedSlot + 1) % 9;
                client.interactionManager.clickSlot(container.syncId, sourceSlot, tempSlot, SlotActionType.SWAP, client.player);
                client.interactionManager.clickSlot(container.syncId, 45, tempSlot, SlotActionType.SWAP, client.player);
                client.interactionManager.clickSlot(container.syncId, sourceSlot, tempSlot, SlotActionType.SWAP, client.player);
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

    public static boolean tradeAllItems(MinecraftClient client, int index, boolean dropItems) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
            return false;
        }
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
        return true;
    }

    public static void selectTrade(MinecraftClient client, MerchantScreen merchantScreen, int index) {
        if (client.getNetworkHandler() == null) {
            return;
        }
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        handler.setRecipeIndex(index);
        client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(index));
    }

    public static void clearTradeInputSlot(MinecraftClient client, MerchantScreen merchantScreen) {
        Slot slot = merchantScreen.getScreenHandler().getSlot(0);
        if (slot.hasStack()) {
            shiftClickSlot(client, merchantScreen, slot.id);
        }
        slot = merchantScreen.getScreenHandler().getSlot(1);
        if (slot.hasStack()) {
            shiftClickSlot(client, merchantScreen, slot.id);
        }
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

    public static ItemStack getTrade(MinecraftClient client, int index) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null) {
            return null;
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
            if (!clearCraftingGridOfItems(client, itemStacks, screen)) {
                return;
            }
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

    private static boolean clearCraftingGridOfItems(MinecraftClient client, ItemStack[] recipe, HandledScreen<? extends ScreenHandler> gui) {
        final int invSlots = gui.getScreenHandler().slots.size();
        for (int i = 0, slotNum = 1; i < 9 && slotNum < invSlots; i++, slotNum++) {
            try { Thread.sleep(0, 1); }
            catch (InterruptedException ignored) { }
            Slot slotTmp = gui.getScreenHandler().getSlot(slotNum);
            if (slotTmp != null && slotTmp.hasStack() && (!areStacksEqual(recipe[i], slotTmp.getStack()))) {
                shiftClickSlot(client, gui, slotNum);
                if (slotTmp.hasStack()) {
                    dropStack(client, gui, slotNum);
                }
            }
        }
        return true;
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
            slotNum = getSlotNumberOfLargestMatchingStackFromDifferentInventory(container, slotGridFirst, ingredientReference);
            if (slotNum < 0) {
                break;
            }
            if (slotReturn == -1) {
                slotReturn = slotNum;
            }
            leftClickSlot(client, gui, slotNum);
            ItemStack stackCursor = player.inventory.getCursorStack();
            if (areStacksEqual(ingredientReference, stackCursor)) {
                sizeOrig = stackCursor.getCount();
                dragSplitItemsIntoSlots(client, gui, targetSlots);
                stackCursor = player.inventory.getCursorStack();
                if (!stackCursor.isEmpty()) {
                    if (stackCursor.getCount() >= sizeOrig) {
                        break;
                    }
                    leftClickSlot(client, gui, slotReturn);
                    if (!player.inventory.getCursorStack().isEmpty()) {
                        slotReturn = slotNum;
                        leftClickSlot(client, gui, slotReturn);
                    }
                }
            }
            else {
                break;
            }
            if (!player.inventory.getCursorStack().isEmpty()) {
                break;
            }
        }
        if (slotNum >= 0 && !player.inventory.getCursorStack().isEmpty()) {
            leftClickSlot(client, gui, slotNum);
        }
    }

    private static void dragSplitItemsIntoSlots(MinecraftClient client, HandledScreen<? extends ScreenHandler> gui, List<Integer> targetSlots) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        ItemStack stackInCursor = player.inventory.getCursorStack();
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
        // Finish dragging itemsoak
        craftClickSlot(client, gui, -999, 2);
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
        return !stack1.isEmpty() && stack1.isItemEqual(stack2) && ItemStack.areTagsEqual(stack1, stack2);
    }

    private static int getSlotNumberOfLargestMatchingStackFromDifferentInventory(ScreenHandler container, Slot slotReference, ItemStack stackReference) {
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

    public static void shiftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int index) {
        if (client.interactionManager == null) {
            return;
        }
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, index, 0, SlotActionType.QUICK_MOVE, client.player);
    }

    public static void leftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
        if (client.interactionManager == null) {
            return;
        }
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 0, SlotActionType.PICKUP, client.player);
    }

    public static void craftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum, int mouseButton) {
        if (client.interactionManager == null) {
            return;
        }
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, mouseButton, SlotActionType.QUICK_CRAFT, client.player);
    }

    public static void dropStack(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
        if (client.interactionManager == null) {
            return;
        }
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 1, SlotActionType.THROW, client.player);
    }
}
