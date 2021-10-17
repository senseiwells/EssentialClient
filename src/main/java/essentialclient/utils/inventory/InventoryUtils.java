package essentialclient.utils.inventory;

import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;

import java.util.*;
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
            if (client.interactionManager == null)
                return;
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

    public static void dropAllItemType(ClientPlayerEntity playerEntity, String itemIdentifier) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null)
            return;
        Item item = Registry.ITEM.get(new Identifier(itemIdentifier));
        ScreenHandler containerPlayer = playerEntity.currentScreenHandler;
        Predicate<ItemStack> filterItemStack = (s) -> s.getItem() == item;
        for (Slot slot : containerPlayer.slots) {
            ItemStack stack = slot.getStack();
            if (filterItemStack.test(stack)) {
                client.interactionManager.clickSlot(containerPlayer.syncId, slot.id, 1, SlotActionType.THROW, playerEntity);
            }
        }
    }

    public static void tradeAllItems(MinecraftClient client, int index, boolean dropItems) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null)
            return;
        Slot tradeSlot = merchantScreen.getScreenHandler().getSlot(2);
        while (true) {
            selectTrade(client, merchantScreen, index);
            if (!tradeSlot.hasStack())
                break;
            ItemStack tradeStack = tradeSlot.getStack().copy();
            shiftClickSlot(client, merchantScreen, tradeSlot.id);
            if (dropItems)
                dropAllItemType(client.player, Registry.ITEM.getId(tradeStack.getItem()).getPath());
            if (tradeSlot.hasStack())
                break;
        }
        clearTradeInputSlot(client, merchantScreen);
    }

    public static void selectTrade(MinecraftClient client, MerchantScreen merchantScreen, int index) {
        if (client.getNetworkHandler() == null)
            return;
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        handler.setRecipeIndex(index);
        client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(index));
    }

    public static void clearTradeInputSlot(MinecraftClient client, MerchantScreen merchantScreen) {
        Slot slot = merchantScreen.getScreenHandler().getSlot(0);
        if (slot.hasStack())
            shiftClickSlot(client, merchantScreen, slot.id);
        slot = merchantScreen.getScreenHandler().getSlot(1);
        if (slot.hasStack())
            shiftClickSlot(client, merchantScreen, slot.id);
    }

    public static boolean checkTradeDisabled(MinecraftClient client, int index) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null)
            return false;
        return merchantScreen.getScreenHandler().getRecipes().get(index).isDisabled();
    }

    public static boolean checkTradeDisabled(MinecraftClient client, Item item) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null)
            return false;
        for (TradeOffer offer : merchantScreen.getScreenHandler().getRecipes()) {
            if (offer.getSellItem().getItem() == item)
                return offer.isDisabled();
        }
        return false;
    }

    public static List<Value<?>> checkEnchantmentForTrade(MinecraftClient client, int index) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null)
            return null;
        NbtList nbtList = merchantScreen.getScreenHandler().getRecipes().get(index).getSellItem().getEnchantments();
        List<Value<?>> enchantmentList = new ArrayList<>();
        EnchantmentHelper.fromNbt(nbtList).forEach((enchantment, integer) -> enchantmentList.add(new ListValue(List.of(
                new StringValue(Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).getPath()),
                new NumberValue(integer)
                ))));
        return enchantmentList;
    }

    public static List<Value<?>> checkEnchantment(MinecraftClient client, int index) {
        assert client.player != null;
        ScreenHandler playerContainer = client.player.currentScreenHandler;
        if (index > playerContainer.slots.size() || index < 0)
            return new ArrayList<>();
        NbtList nbtList = playerContainer.getSlot(index).getStack().getEnchantments();
        List<Value<?>> enchantmentList = new ArrayList<>();
        EnchantmentHelper.fromNbt(nbtList).forEach((enchantment, integer) -> enchantmentList.add(new ListValue(List.of(
                new StringValue(Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).getPath()),
                new NumberValue(integer)
        ))));
        return enchantmentList;
    }

    public static int getDurability(MinecraftClient client, int index) {
        assert client.player != null;
        ScreenHandler playerContainer = client.player.currentScreenHandler;
        if (index > playerContainer.slots.size() || index < 0)
            return -1;
        ItemStack stack = playerContainer.getSlot(index).getStack();
        return stack.getMaxDamage() - stack.getDamage();
    }

    public static boolean checkHasTrade(MinecraftClient client, Item item) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null)
            return false;
        for (TradeOffer offer : merchantScreen.getScreenHandler().getRecipes()) {
            if (offer.getSellItem().getItem() == item)
                return true;
        }
        return false;
    }

    public static int getIndexOfItemInMerchant(MinecraftClient client, Item item) {
        if (!(client.currentScreen instanceof MerchantScreen merchantScreen) || client.interactionManager == null)
            return -1;
        int i = 0;
        for (TradeOffer offer : merchantScreen.getScreenHandler().getRecipes()) {
            if (offer.getSellItem().getItem() == item)
                return i;
            i++;
        }
        return -1;
    }

    public static void tryMoveItemsToCraftingGridSlots(MinecraftClient client, ItemStack[] itemStacks, HandledScreen<? extends ScreenHandler> screen) {
        ScreenHandler container = screen.getScreenHandler();
        int numSlots = container.slots.size();
        if (9 < numSlots) {
            if (!clearCraftingGridOfItems(client, itemStacks, screen))
                return;
            Slot slotGridFirst = container.getSlot(1);
            Map<ItemStack, List<Integer>> ingredientSlots = getSlotsPerItem(itemStacks);
            for (Map.Entry<ItemStack, List<Integer>> entry : ingredientSlots.entrySet()) {
                ItemStack ingredientReference = entry.getKey();
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
            catch (InterruptedException ignored) {}
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
        assert player != null;
        int slotNum;
        int slotReturn = -1;
        int sizeOrig;
        if (ingredientReference.isEmpty())
            return;
        while (true) {
            slotNum = getSlotNumberOfLargestMatchingStackFromDifferentInventory(container, slotGridFirst, ingredientReference);
            if (slotNum < 0)
                break;
            if (slotReturn == -1)
                slotReturn = slotNum;
            leftClickSlot(client, gui, slotNum);
            ItemStack stackCursor = player.inventory.getCursorStack();
            if (areStacksEqual(ingredientReference, stackCursor)) {
                sizeOrig = stackCursor.getCount();
                dragSplitItemsIntoSlots(client, gui, targetSlots);
                stackCursor = player.inventory.getCursorStack();
                if (!stackCursor.isEmpty()) {
                    if (stackCursor.getCount() >= sizeOrig)
                        break;
                    leftClickSlot(client, gui, slotReturn);
                    if (!player.inventory.getCursorStack().isEmpty()) {
                        slotReturn = slotNum;
                        leftClickSlot(client, gui, slotReturn);
                    }
                }
            }
            else
                break;
            if (!player.inventory.getCursorStack().isEmpty())
                break;
        }
        if (slotNum >= 0 && !player.inventory.getCursorStack().isEmpty())
            leftClickSlot(client, gui, slotNum);
    }

    private static void dragSplitItemsIntoSlots(MinecraftClient client, HandledScreen<? extends ScreenHandler> gui, List<Integer> targetSlots) {
        assert client.player != null;
        ItemStack stackInCursor = client.player.inventory.getCursorStack();
        if (stackInCursor.isEmpty())
            return;
        if (targetSlots.size() == 1) {
            leftClickSlot(client, gui, targetSlots.get(0));
            return;
        }
        int numSlots = gui.getScreenHandler().slots.size();
        craftClickSlot(client, gui, -999, 0);
        for (int slotNum : targetSlots) {
            if (slotNum >= numSlots) {
                break;
            }
            craftClickSlot(client, gui, slotNum, 1);
        }
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

    private static Map<ItemStack, List<Integer>> getSlotsPerItem(ItemStack[] stacks) {
        Map<ItemStack, List<Integer>> mapSlots = new HashMap<>();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (!stack.isEmpty()) {
                List<Integer> slots = mapSlots.computeIfAbsent(stack, k -> new ArrayList<>());
                slots.add(i);
            }
        }
        return mapSlots;
    }

    public static void shiftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int index) {
        if (client.interactionManager == null)
            return;
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, index, 0, SlotActionType.QUICK_MOVE, client.player);
    }

    public static void leftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
        if (client.interactionManager == null)
            return;
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 0, SlotActionType.PICKUP, client.player);
    }

    public static void craftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum, int mouseButton) {
        if (client.interactionManager == null)
            return;
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, mouseButton, SlotActionType.QUICK_CRAFT, client.player);
    }

    public static void dropStack(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int slotNum) {
        if (client.interactionManager == null)
            return;
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotNum, 1, SlotActionType.THROW, client.player);
    }
}
