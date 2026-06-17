package me.senseiwells.essential_client.mixins.highlight_liquids;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.senseiwells.essential_client.EssentialClientConfig;
import me.senseiwells.essential_client.features.HighlightLiquids;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Brightness;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Apply after the Fabric api mixin
@Mixin(value = FluidRenderer.class, priority = 1100)
public abstract class FluidRendererMixin {
	@Shadow
	private static boolean isNeighborSameFluid(FluidState fluidState, FluidState neighborFluidState) {
		return false;
	}

	@Shadow
	private static boolean isFaceOccludedBySelf(BlockState state, Direction direction) {
		return false;
	}

	@Shadow
	protected abstract void vertex(VertexConsumer builder, float x, float y, float z, int color, float u, float v, int lightCoords);

	@Inject(
		method = "tesselate",
		at = @At("HEAD")
	)
	private void initializeHighlightFlags(
		BlockAndTintGetter level,
		BlockPos pos,
		FluidRenderer.Output output,
		BlockState blockState,
		FluidState fluidState,
		CallbackInfo ci,
		@Share("shouldRenderFluid") LocalBooleanRef shouldRenderFluid,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		shouldRenderFluid.set(true);
		shouldRenderHighlight.set(false);
		if (fluidState.is(Fluids.LAVA)) {
			if (EssentialClientConfig.getInstance().getHighlightLavaSources()) {
				shouldRenderHighlight.set(true);
			}
		} else if (fluidState.is(Fluids.WATER)) {
			if (EssentialClientConfig.getInstance().getHighlightWaterSources()) {
				shouldRenderHighlight.set(true);
			}
		}

		shouldRenderHighlightFace.set(shouldRenderHighlight.get());
	}

	@WrapOperation(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/FluidRenderer;shouldRenderFace(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)Z"
		)
	)
	private boolean bypassFaceCheckIfHighlighting(
		FluidState fluidState,
		BlockState blockState,
		Direction direction,
		FluidState neighborFluidState,
		Operation<Boolean> original,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight
	) {
		if (direction.getAxis().isVertical() || !shouldRenderHighlight.get()) {
			return original.call(fluidState, blockState, direction, neighborFluidState);
		}
		return !isFaceOccludedBySelf(blockState, direction);
	}

	@WrapOperation(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/FluidRenderer;addFace(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFFFFFFFFFFFFFIIZ)V"
		)
	)
	private void renderFluidAndHighlight(
		FluidRenderer instance,
		VertexConsumer builder,
		float x0, float y0, float z0,
		float u0, float v0,
		float x1, float y1, float z1,
		float u1, float v1,
		float x2, float y2, float z2,
		float u2, float v2,
		float x3, float y3, float z3,
		float u3, float v3,
		int color,
		int lightCoords,
		boolean addBackFace,
		Operation<Void> original,
		@Share("shouldRenderFluid") LocalBooleanRef shouldRenderFluid,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		if (shouldRenderFluid.get()) {
			original.call(
				instance,
				builder,
				x0, y0, z0,
				u0, v0,
				x1, y1, z1,
				u1, v1,
				x2, y2, z2,
				u2, v2,
				x3, y3, z3,
				u3, v3,
				color,
				lightCoords,
				addBackFace
			);
		}

		if (shouldRenderHighlightFace.get()) {
			int light = Brightness.FULL_BRIGHT.pack();
			UVPair[] uvs = HighlightLiquids.getSpriteUVs();
			int highlight = 0xFFFFFFFF;
			this.vertex(builder, x0, y0, z0, highlight, uvs[0].u(), uvs[0].v(), light);
			this.vertex(builder, x1, y1, z1, highlight, uvs[1].u(), uvs[1].v(), light);
			this.vertex(builder, x2, y2, z2, highlight, uvs[2].u(), uvs[2].v(), light);
			this.vertex(builder, x3, y3, z3, highlight, uvs[3].u(), uvs[3].v(), light);
			if (addBackFace) {
				this.vertex(builder, x3, y3, z3, highlight, uvs[3].u(), uvs[3].v(), light);
				this.vertex(builder, x2, y2, z2, highlight, uvs[2].u(), uvs[2].v(), light);
				this.vertex(builder, x1, y1, z1, highlight, uvs[1].u(), uvs[1].v(), light);
				this.vertex(builder, x0, y0, z0, highlight, uvs[0].u(), uvs[0].v(), light);
			}
		}
	}

	@Inject(
		method = "tesselate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Direction;getAxis()Lnet/minecraft/core/Direction$Axis;"
		)
	)
	private void updateHighlightFlags(
		BlockAndTintGetter level,
		BlockPos pos,
		FluidRenderer.Output output,
		BlockState blockState,
		FluidState fluidState,
		CallbackInfo ci,
		@Local(name = "faceDir") Direction direction,
		@Share("shouldRenderFluid") LocalBooleanRef shouldRenderFluid,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		FluidState neighbor = level.getFluidState(pos.relative(direction));
		boolean isNeighborSameFluid = isNeighborSameFluid(fluidState, neighbor);
		shouldRenderFluid.set(!isNeighborSameFluid);
		shouldRenderHighlightFace.set(
			shouldRenderHighlight.get() && (isNeighborSameFluid || neighbor.is(Fluids.EMPTY)) && !neighbor.isSource()
		);
	}
}
