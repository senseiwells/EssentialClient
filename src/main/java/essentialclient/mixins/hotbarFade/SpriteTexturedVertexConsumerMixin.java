package essentialclient.mixins.hotbarFade;

import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpriteTexturedVertexConsumer.class)
public class SpriteTexturedVertexConsumerMixin {
	/* TODO
	@ModifyVariable(method = "vertex(DDD)Lnet/minecraft/client/render/VertexConsumer;", at = @At("HEAD"), ordinal = 6)
	private float onGetAlphaVertex(float original) {
		return RenderHelper.isRenderingHotBarGui ? 0.4F : original;
	}
	*/
}
