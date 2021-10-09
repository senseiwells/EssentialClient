package essentialclient.mixins.mouseScrollRules;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {
    @ModifyConstant(method = "onMouseScroll", constant = @Constant(floatValue = 0.2F))
    private float newScrollLimit(float originalFloat) {
        return ClientRules.INCREASE_SPECTATOR_SCROLL_SPEED.getBoolean() ? 10.0F : originalFloat;
    }
    @ModifyConstant(method = "onMouseScroll", constant = @Constant(floatValue = 0.005F))
    private float newSensitivtyLimit(float originalFloat) {
        int newSensitivity = ClientRules.INCREASE_SPECTATOR_SCROLL_SENSITIVITY.getInt();
        return newSensitivity > 0 ? originalFloat * newSensitivity : originalFloat;
    }
    @Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
    private void onScrollHotbar(PlayerInventory playerInventory, double scrollAmount) {
        if (ClientRules.DISABLE_HOTBAR_SCROLLING.getBoolean())
            return;
        playerInventory.scrollInHotbar(scrollAmount);
    }
}
