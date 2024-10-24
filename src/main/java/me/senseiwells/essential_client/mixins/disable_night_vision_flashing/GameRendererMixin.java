package me.senseiwells.essential_client.mixins.disable_night_vision_flashing;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyExpressionValue(
		method = "getNightVisionScale",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/effect/MobEffectInstance;endsWithin(I)Z"
		)
	)
	private static boolean isNightVisionEnding(boolean original) {
		if (EssentialClientConfig.getInstance().getDisableNightVisionFlashing()) {
			return false;
		}
		return original;
	}
}
