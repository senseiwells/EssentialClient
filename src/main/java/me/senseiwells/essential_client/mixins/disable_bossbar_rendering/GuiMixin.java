package me.senseiwells.essential_client.mixins.disable_bossbar_rendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
	@WrapWithCondition(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;extractBossOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
		)
	)
	private boolean onRenderBossbar(Gui instance, GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
		return !EssentialClientConfig.getInstance().getDisableBossbarRendering();
	}
}