package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.utils.render.RenderHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11903
import org.joml.Matrix4f;
//#else
//$$import net.minecraft.util.math.Matrix4f;
//#endif

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	//#if MC >= 11800
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;clearFog()V", shift = At.Shift.BEFORE))
	//#else
	//$$@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;method_23792()V", shift = At.Shift.BEFORE))
	//#endif
	private void postRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		RenderHelper.renderAllShapes(matrices);
		// Rendering blocks needs to come after rendering
		// shapes because it clears the depth buffer
		RenderHelper.renderBlocks(matrices);
	}
}
