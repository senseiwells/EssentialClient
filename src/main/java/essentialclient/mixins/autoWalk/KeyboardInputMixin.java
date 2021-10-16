package essentialclient.mixins.autoWalk;

import essentialclient.feature.clientrule.ClientRules;
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
            shouldAutoHold = ticks++ > ClientRules.AUTO_WALK.getInt();
            if (shouldAutoHold)
                EssentialUtils.sendMessageToActionBar("§aYou are now autowalking");
            return true;
        }
        ticks = 0;
        return shouldAutoHold;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean onIsBackPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            ticks = 0;
            shouldAutoHold = false;
            return true;
        }
        return false;
    }
}
