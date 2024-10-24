package me.senseiwells.essential_client.mixins.highlight_liquids;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
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
import net.minecraft.core.Direction;
import net.minecraft.util.Brightness;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Apply after the Fabric api mixin
@Mixin(value = LiquidBlockRenderer.class, priority = 1100)
public abstract class LiquidBlockRendererMixin {
	@Shadow
	private static boolean isNeighborSameFluid(FluidState firstState, FluidState secondState) {
		return false;
	}

	@Shadow
	private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Inject(
		method = "tesselate",
		at = @At("HEAD")
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

	@WrapOperation(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;shouldRenderFace(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)Z"
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;isFaceOccludedByNeighbor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;FLnet/minecraft/world/level/block/state/BlockState;)Z"
			)
		)
	)
	private boolean shouldRenderFace(
		BlockAndTintGetter level,
		BlockPos pos,
		FluidState fluidState,
		BlockState blockState,
		Direction side,
		FluidState neighborFluid,
		Operation<Boolean> original,
		@Share("vertices") LocalRef<FloatList> ref
	) {
		if (ref.get() == null) {
			return original.call(level, pos, fluidState, blockState, side, neighborFluid);
		}
		return !isFaceOccludedBySelf(level, pos, blockState, side);
	}

	@WrapWithCondition(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFI)V"
		),
		slice = @Slice(
			to = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/core/Direction;getAxis()Lnet/minecraft/core/Direction$Axis;"
			)
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
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Direction;getAxis()Lnet/minecraft/core/Direction$Axis;"
		)
	)
	private void onDirection(
		BlockAndTintGetter level,
		BlockPos pos,
		VertexConsumer buffer,
		BlockState blockState,
		FluidState fluidState,
		CallbackInfo ci,
		@Local Direction direction,
		@Share("shouldRenderLiquid") LocalBooleanRef shouldRenderLiquid,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight
	) {
		FluidState neighbor = level.getFluidState(pos.relative(direction));
		boolean isNeighborSameFluid = isNeighborSameFluid(fluidState, neighbor);
		shouldRenderLiquid.set(!isNeighborSameFluid);
		shouldRenderHighlight.set((isNeighborSameFluid || neighbor.is(Fluids.EMPTY)) && !neighbor.isSource());
	}

	@WrapWithCondition(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFI)V"
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/core/Direction;getAxis()Lnet/minecraft/core/Direction$Axis;"
			)
		)
	)
	private boolean onSideVertex(
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
		@Share("vertices") LocalRef<FloatList> ref,
		@Share("shouldRenderLiquid") LocalBooleanRef shouldRenderLiquid,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight
	) {
		FloatList vertices = ref.get();
		if (shouldRenderHighlight.get() && vertices != null) {
			vertices.add(x);
			vertices.add(y);
			vertices.add(z);
		}
		return shouldRenderLiquid.get();
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
