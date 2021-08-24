package essentialclient.mixins.disableNarrator;

import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @ModifyVariable(method = "onKey", at = @At("STORE"),ordinal = 0)
    private boolean modifiedBoolean(boolean old) {
        if (ClientRules.DISABLENARRATOR.getBoolean())
            return false;
        return old;
    }
}
