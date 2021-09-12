package essentialclient.utils.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
}
