package essentialclient.mixins.hotbarFade;

import net.minecraft.client.render.OverlayVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OverlayVertexConsumer.class)
public class OverlayVertexConsumerMixin {
	/* TODO
	@ModifyConstant(method = "next", constant = @Constant(floatValue = 1.0F, ordinal = 4))
	private float onGetAlpha(float original) {
		return RenderHelper.isRenderingHotBarGui ? 0.4F : original;
	}
	 */
}
