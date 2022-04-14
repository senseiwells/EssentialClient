package me.senseiwells.essentialclient.mixins.disableArmourRendering;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
	private <T extends LivingEntity> void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		switch (ClientRules.DISABLE_ARMOUR_RENDERING.getValue()) {
			case "You" -> {
				if (livingEntity instanceof ClientPlayerEntity) {
					ci.cancel();
				}
			}
			case "Players" -> {
				if (livingEntity instanceof PlayerEntity) {
					ci.cancel();
				}
			}
			case "Entities" -> {
				ci.cancel();
			}
		}
	}
}
