package me.senseiwells.essentialclient.mixins.chunkDebug;

import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	private int scaledWidth;
	@Shadow
	private int scaledHeight;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
	private void afterCrossHairRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		if (ChunkDebugScreen.chunkGrid != null) {
			ChunkDebugScreen.chunkGrid.renderMinimap(this.scaledWidth, this.scaledHeight);
		}
	}
}
