package essentialclient.mixins.disableTutorialNotifications;

import essentialclient.gui.clientrule.ClientRule;
import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.TutorialToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TutorialToast.class)
public abstract class TutorialToastMixin {

    @Inject(at = @At("HEAD"), method = "draw", cancellable = true)
    private void hideToast(CallbackInfoReturnable<Toast.Visibility> cir) {
        if (ClientRule.getBoolean(ClientRules.disableRecipeNotifications)) {
            cir.setReturnValue(Toast.Visibility.HIDE);
        }
    }
}
