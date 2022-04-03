package me.senseiwells.essentialclient.mixins.quickLockRecipe;

import me.senseiwells.essentialclient.clientrule.ClientRules;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimatedResultButton.class)
public class AnimatedResultButtonMixin {
	@Inject(method = "isValidClickButton", at = @At("RETURN"), cancellable = true)
	private void isValid(int button, CallbackInfoReturnable<Boolean> cir) {
		if (ClientRules.QUICK_LOCK_RECIPE.getValue()) {
			cir.setReturnValue(cir.getReturnValue() || button == 2);
		}
	}
}
