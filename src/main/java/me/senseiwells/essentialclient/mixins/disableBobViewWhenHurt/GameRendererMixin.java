package me.senseiwells.essentialclient.mixins.disableBobViewWhenHurt;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
	private void bobViewWhenHurt(MatrixStack matrices, float f, CallbackInfo ci) {
		if (ClientRules.DISABLE_BOB_VIEW_WHEN_HURT.getValue()) {
			ci.cancel();
		}
	}
}
