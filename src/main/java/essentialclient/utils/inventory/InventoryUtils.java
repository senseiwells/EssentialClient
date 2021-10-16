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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                int currentHotbarSlot = playerEntity.getInventory().selectedSlot;
                client.interactionManager.clickSlot(container.syncId, sourceSlot, currentHotbarSlot, SlotActionType.SWAP, client.player);
            }
            else if (slotType == EquipmentSlot.OFFHAND) {
                int tempSlot = (playerEntity.getInventory().selectedSlot + 1) % 9;
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

    public static void shiftClickSlot(MinecraftClient client, HandledScreen<? extends ScreenHandler> screen, int index) {
        if (client.interactionManager == null)
            return;
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, index, 0, SlotActionType.QUICK_MOVE, client.player);
    }
}
