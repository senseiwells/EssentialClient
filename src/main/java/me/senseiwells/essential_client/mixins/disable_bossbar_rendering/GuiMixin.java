package me.senseiwells.essential_client.mixins.disable_bossbar_rendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
	@WrapWithCondition(
		method = "method_55808",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
		)
	)
	private boolean onRenderBossbar(BossHealthOverlay instance, GuiGraphics guiGraphics) {
		return !EssentialClientConfig.getInstance().getDisableBossbarRendering();
	}
}