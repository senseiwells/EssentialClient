package essentialclient.mixins.disableMapRendering;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntityRenderer.class)
public class ItemFrameEntityRendererMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(ItemFrameEntity itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (itemStack.isOf(Items.FILLED_MAP) && ClientRules.DISABLE_MAP_RENDERING.getBoolean())
			ci.cancel();
	}
}
