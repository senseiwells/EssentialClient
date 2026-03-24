package me.senseiwells.essential_client.mixins.display_time_played;

import me.senseiwells.essential_client.features.DisplayStartTime;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
	protected PauseScreenMixin(Component title) {
		super(title);
	}

	@Inject(
		method = "extractRenderState",
		at = @At("TAIL")
	)
	private void extractPlayTimeState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		DisplayStartTime.onExtractRenderState(guiGraphics, this.font);
	}
}
