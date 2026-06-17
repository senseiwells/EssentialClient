package me.senseiwells.essential_client.mixins.disable_bossbar_rendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)
public class HudMixin {
	@WrapWithCondition(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Hud;extractBossOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
		)
	)
	private boolean onRenderBossbar(Hud instance, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		return !EssentialClientConfig.getInstance().getDisableBossbarRendering();
	}
}