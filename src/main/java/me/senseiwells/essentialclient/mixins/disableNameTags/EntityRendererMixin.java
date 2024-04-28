package me.senseiwells.essentialclient.mixins.disableNameTags;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	private void onRender(
		Entity entity,
		Text text,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		float tickDelta,
		CallbackInfo ci
	) {
		if (ClientRules.DISABLE_NAME_TAGS.getValue()) {
			ci.cancel();
		}
	}
}
