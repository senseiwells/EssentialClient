package me.senseiwells.essential_client.mixins.lava_opacity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.senseiwells.essential_client.EssentialClientConfig;
import me.senseiwells.essential_client.ducks.TranslucentLiquids;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin implements TranslucentLiquids {
	@Unique
	private final ThreadLocal<VertexConsumer> translucentConsumer = new ThreadLocal<>();

	@WrapWithCondition(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFI)V"
		)
	)
	private boolean onVertex(
		LiquidBlockRenderer instance,
		VertexConsumer buffer,
		float x,
		float y,
		float z,
		float red,
		float green,
		float blue,
		float u,
		float v,
		int packedLight,
		@Local(ordinal = 0) boolean isLava
	) {
		if (!isLava) {
			return true;
		}
		VertexConsumer consumer = this.translucentConsumer.get();
		consumer.addVertex(x, y, z)
			.setColor(red, green, blue, EssentialClientConfig.getInstance().getLavaOpacity())
			.setUv(u, v)
			.setLight(packedLight)
			.setNormal(0.0F, 1.0F, 0.0F);
		return false;
	}

	@Override
	public void essentialclient$setTranslucentConsumer(VertexConsumer consumer) {
		this.translucentConsumer.set(consumer);
	}

	@Override
	public VertexConsumer essentialclient$getTranslucentConsumer() {
		return this.translucentConsumer.get();
	}
}
