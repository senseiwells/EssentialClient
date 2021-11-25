package essentialclient.mixins.autoWalk;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    private int ticks = 0;
    private boolean shouldAutoHold = false;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    private boolean onIsForwardPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()){
            int autoWalk = ClientRules.AUTO_WALK.getValue();
            this.shouldAutoHold = autoWalk > 0 && this.ticks++ > autoWalk;
            if (this.shouldAutoHold) {
                EssentialUtils.sendMessageToActionBar("§aYou are now autowalking");
            }
            return true;
        }
        this.ticks = 0;
        return this.shouldAutoHold;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean onIsBackPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            this.ticks = 0;
            this.shouldAutoHold = false;
            return true;
        }
        return false;
    }
}
