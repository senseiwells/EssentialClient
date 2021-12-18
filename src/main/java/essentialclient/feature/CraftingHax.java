package essentialclient.feature;

import essentialclient.utils.inventory.InventoryUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class CraftingHax {
    public static void registerTickEventListener() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            int count = CraftingSharedConstants.THROW_AMOUNT.get();
            if (count > 0 && client.currentScreen instanceof HandledScreen<?> handledScreen) {
                for (int i = 0; i < count; i++) {
                    InventoryUtils.dropSingle(client, handledScreen, 0);
                }
                CraftingSharedConstants.THROW_AMOUNT.set(0);
            }
        });
    }
}
