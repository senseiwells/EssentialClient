package me.senseiwells.essentialclient.mixins.clientScript;

import com.llamalad7.mixinextras.sugar.Local;
import me.senseiwells.essentialclient.utils.render.RenderHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;clearFog()V", shift = At.Shift.BEFORE))
	private void postRender(
		RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local MatrixStack matrices
	) {
		RenderHelper.renderAllShapes(matrices);
		// Rendering blocks needs to come after rendering
		// shapes because it clears the depth buffer
		RenderHelper.renderBlocks(matrices);
	}
}
