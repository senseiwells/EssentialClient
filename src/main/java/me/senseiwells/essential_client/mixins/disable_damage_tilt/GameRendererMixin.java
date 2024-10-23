package me.senseiwells.essential_client.mixins.disable_damage_tilt;

import com.mojang.blaze3d.vertex.PoseStack;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(
		method = "bobHurt",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onDamageTilt(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
		if (EssentialClientConfig.getInstance().getDisableDamageTilt()) {
			ci.cancel();
		}
	}
}
