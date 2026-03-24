package me.senseiwells.essential_client.mixins.disable_nametag_rendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@WrapWithCondition(
		method = "submit",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
		)
	)
	private boolean onRenderNametag(
        EntityRenderer<?, ?> instance,
        EntityRenderState entityRenderState,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        CameraRenderState cameraRenderState
	) {
		return !EssentialClientConfig.getInstance().getDisableNametagRendering();
	}
}
