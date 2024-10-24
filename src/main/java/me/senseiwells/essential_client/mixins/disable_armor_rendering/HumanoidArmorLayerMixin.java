package me.senseiwells.essential_client.mixins.disable_armor_rendering;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
	@Inject(
		method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onRenderArmor(CallbackInfo ci) {
		if (EssentialClientConfig.getInstance().getDisableArmorRendering()) {
			ci.cancel();
		}
	}
}
