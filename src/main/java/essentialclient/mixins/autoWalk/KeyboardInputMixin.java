package essentialclient.mixins.autoWalk;

import essentialclient.gui.clientrule.ClientRules;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    private int ticks = 0;
    private boolean letgo;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    private boolean onIsForwardPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            if (letgo)
                ticks = 0;
            ticks++;
            return true;
        }
        else if (ClientRules.AUTO_WALK.getInt() != 0 && ticks >= ClientRules.AUTO_WALK.getInt()) {
            if (!letgo) {
                EssentialUtils.sendMessageToActionBar("§aYou are now autowalking");
                letgo = true;
            }
            return true;
        }
        ticks = 0;
        letgo = false;
        return false;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean onIsBackPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            ticks = 0;
            letgo = false;
            return true;
        }
        return false;
    }
}
