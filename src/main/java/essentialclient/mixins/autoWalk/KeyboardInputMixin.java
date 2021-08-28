package essentialclient.mixins.autoWalk;

import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    private int ticks = 0;
    private int cooldown = 0;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    private boolean onIsForwardPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            if (cooldown > 100)
                ticks = 0;
            ticks++;
            return true;
        }
        else if (ClientRules.AUTOWALK.getInt() != 0 && ticks >= ClientRules.AUTOWALK.getInt()) {
            cooldown = cooldown > 100 ? cooldown : cooldown + 1;
            return true;
        }
        ticks = 0;
        cooldown = 0;
        return false;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean onIsBackPressed(KeyBinding keyBinding) {
        if (keyBinding.isPressed()) {
            ticks = 0;
            cooldown = 0;
            return true;
        }
        return false;
    }
}
