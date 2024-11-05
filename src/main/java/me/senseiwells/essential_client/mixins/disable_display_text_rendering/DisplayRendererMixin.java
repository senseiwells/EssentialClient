package me.senseiwells.essential_client.mixins.disable_display_text_rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.MultiBufferSource;
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
		method = "render(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onRender(
		DisplayEntityRenderState state,
		PoseStack poseStack,
		MultiBufferSource multiBufferSource,
		int i,
		CallbackInfo ci
	) {
		if (state instanceof TextDisplayEntityRenderState && EssentialClientConfig.getInstance().getDisableDisplayTextRendering()) {
			ci.cancel();
		}
	}
}
