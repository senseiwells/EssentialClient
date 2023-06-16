package me.senseiwells.essentialclient.mixins.chunkDebug;

import me.senseiwells.essentialclient.feature.chunkdebug.ChunkGrid;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
import net.minecraft.client.gui.DrawContext;
//#else
//$$import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	private int scaledWidth;
	@Shadow
	private int scaledHeight;

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			//#if MC >= 12000
			target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V",
			//#else
			//$$target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V",
			//#endif
			shift = At.Shift.AFTER
		)
	)
	private void afterCrossHairRender(
		//#if MC >= 12000
		DrawContext context,
		//#else
		//$$MatrixStack matrices,
		//#endif
		float tickDelta,
		CallbackInfo ci
	) {
		if (ChunkGrid.instance != null) {
			ChunkGrid.instance.renderMinimap(this.scaledWidth, this.scaledHeight);
		}
	}
}
