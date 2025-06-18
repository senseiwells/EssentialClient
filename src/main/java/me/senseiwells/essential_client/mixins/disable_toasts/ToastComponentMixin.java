package me.senseiwells.essential_client.mixins.disable_toasts;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.gui.components.toasts.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class)
public class ToastComponentMixin {
	@Inject(
		method = "addToast",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onAddToast(Toast toast, CallbackInfo ci) {
		boolean disabled = switch (toast) {
			case AdvancementToast ignored -> EssentialClientConfig.getInstance().getDisableAdvancementToasts();
			case RecipeToast ignored -> EssentialClientConfig.getInstance().getDisableRecipeToasts();
			case TutorialToast ignored -> EssentialClientConfig.getInstance().getDisableTutorialToasts();
			default -> false;
		};
		if (disabled) {
			ci.cancel();
		}
	}
}
