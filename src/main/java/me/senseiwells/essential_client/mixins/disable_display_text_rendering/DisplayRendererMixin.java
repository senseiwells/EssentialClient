package me.senseiwells.essential_client.mixins.disable_display_text_rendering;

import com.llamalad7.mixinextras.sugar.Local;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayRenderer.class)
public class DisplayRendererMixin {
	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/Display;Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;F)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onRender(CallbackInfo ci, @Local(argsOnly = true, name = "state") DisplayEntityRenderState state) {
		if (state instanceof TextDisplayEntityRenderState && EssentialClientConfig.getInstance().getDisableDisplayTextRendering()) {
			ci.cancel();
		}
	}
}
