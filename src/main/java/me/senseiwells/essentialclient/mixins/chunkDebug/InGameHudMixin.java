package me.senseiwells.essentialclient.mixins.chunkDebug;

import me.senseiwells.essentialclient.feature.chunkdebug.ChunkGrid;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(
		method = "renderCrosshair",
		at = @At(value = "TAIL")
	)
	private void afterCrossHairRender(
		DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci
	) {
		if (ChunkGrid.instance != null) {
			ChunkGrid.instance.renderMinimap(context.getScaledWindowWidth(), context.getScaledWindowWidth());
		}
	}
}
