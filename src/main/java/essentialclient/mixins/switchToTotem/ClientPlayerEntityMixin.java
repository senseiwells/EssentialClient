package essentialclient.mixins.switchToTotem;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "updateHealth", at = @At("HEAD"))
    private void checkHealth(float health, CallbackInfo ci) {
        int switchToTotemHealth = ClientRules.SWITCH_TO_TOTEM.getValue();
        if (switchToTotemHealth > 0 && switchToTotemHealth > health) {
            ClientPlayerEntity playerEntity = (ClientPlayerEntity) (Object) this;
            Slot totemSlot = InventoryUtils.getItemSlot(playerEntity, Items.TOTEM_OF_UNDYING);
            if (totemSlot != null && totemSlot.id != 45) {
                InventoryUtils.swapItemToEquipmentSlot(playerEntity, EquipmentSlot.OFFHAND, totemSlot.id);
            }
        }
    }
}
