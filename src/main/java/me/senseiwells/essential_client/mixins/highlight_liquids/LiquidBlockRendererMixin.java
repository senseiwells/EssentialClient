package me.senseiwells.essential_client.mixins.highlight_liquids;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import me.senseiwells.essential_client.EssentialClientConfig;
import me.senseiwells.essential_client.ducks.TranslucentLiquids;
import me.senseiwells.essential_client.features.HighlightLiquids;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Brightness;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {
	@Inject(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F",
			ordinal = 0
		)
	)
	private void onTesselateFluid(
		BlockAndTintGetter level,
		BlockPos pos,
		VertexConsumer buffer,
		BlockState blockState,
		FluidState fluidState,
		CallbackInfo ci,
		@Share("vertices") LocalRef<FloatList> ref
	) {
		if (fluidState.is(Fluids.LAVA)) {
			if (EssentialClientConfig.getInstance().getHighlightLavaSources()) {
				ref.set(new FloatArrayList());
			}
		} else if (fluidState.is(Fluids.WATER)) {
			if (EssentialClientConfig.getInstance().getHighlightWaterSources()) {
				ref.set(new FloatArrayList());
			}
		}
	}

	@WrapWithCondition(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFI)V"
		)
	)
	private boolean renderTopFace(
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
		@Share("vertices") LocalRef<FloatList> ref
	) {
		FloatList vertices = ref.get();
		if (vertices != null) {
			vertices.add(x);
			vertices.add(y);
			vertices.add(z);
		}
		return true;
	}

	@Inject(
		method = "tesselate",
		at = @At("TAIL")
	)
	private void afterTesselate(
		BlockAndTintGetter level,
		BlockPos pos,
		VertexConsumer buffer,
		BlockState blockState,
		FluidState fluidState,
		CallbackInfo ci,
		@Share("vertices") LocalRef<FloatList> ref
	) {
		FloatList vertices = ref.get();
		if (vertices == null) {
			return;
		}
		VertexConsumer consumer = ((TranslucentLiquids) this).essentialclient$getTranslucentConsumer();
		int light = Brightness.FULL_BRIGHT.pack();
		FloatListIterator iterator = vertices.iterator();
		while (iterator.hasNext()) {
			for (var uv : HighlightLiquids.getSpriteUVs()) {
				consumer.addVertex(iterator.nextFloat(), iterator.nextFloat(), iterator.nextFloat())
					.setColor(0xFFFFFFFF)
					.setUv(uv.u(), uv.v())
					.setLight(light)
					.setNormal(0.0F, 1.0F, 0.0F);
			}
		}
	}
}
