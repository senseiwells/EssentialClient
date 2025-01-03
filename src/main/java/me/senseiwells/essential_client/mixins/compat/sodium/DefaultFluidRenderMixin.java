package me.senseiwells.essential_client.mixins.compat.sodium;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.senseiwells.essential_client.EssentialClientConfig;
import me.senseiwells.essential_client.features.HighlightLiquids;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuad;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadViewMutable;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Debug(export = true)
@Mixin(value = DefaultFluidRenderer.class, remap = false)
public abstract class DefaultFluidRenderMixin {
	@Unique private final ModelQuadViewMutable highlightQuad = new ModelQuad();

	@Shadow protected abstract boolean isSideExposed(BlockAndTintGetter world, int x, int y, int z, Direction dir, float height);

	@Shadow protected abstract void writeQuad(ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip);

	@Shadow @Final private float[] brightness;

	@Shadow @Final private int[] quadColors;

	@Inject(
		method = "render",
		at = @At("HEAD")
	)
	private void onRenderFluid(
		LevelSlice level,
		BlockState blockState,
		FluidState fluidState,
		BlockPos blockPos,
		BlockPos offset,
		TranslucentGeometryCollector collector,
		ChunkModelBuilder meshBuilder,
		Material material,
		ColorProvider<FluidState> colorProvider,
		TextureAtlasSprite[] sprites,
		CallbackInfo ci,
		@Share("shouldRenderLiquid") LocalBooleanRef shouldRenderLiquid,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		shouldRenderLiquid.set(true);
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
		if (shouldRenderHighlight.get()) {
			this.highlightQuad.setFlags(0);
			this.highlightQuad.setSprite(HighlightLiquids.sprite);
			List<UVPair> uvs = HighlightLiquids.getSpriteUVs();
			for (int i = 0; i < 4; i++) {
				UVPair uv = uvs.get(i);
				this.highlightQuad.setTexU(i, uv.u());
				this.highlightQuad.setTexV(i, uv.v());
				// this.highlightQuad.setNormal(i, ModelQuadFacing.OPPOSING_Y);
			}
		}
	}

	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;isFullBlockFluidOccluded(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z"
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;isSideExposed(Lnet/minecraft/world/level/BlockAndTintGetter;IIILnet/minecraft/core/Direction;F)Z"
			)
		)
	)
	private boolean shouldCullFace(
		DefaultFluidRenderer instance,
		BlockAndTintGetter world,
		BlockPos pos,
		Direction dir,
		BlockState blockState,
		FluidState fluid,
		Operation<Boolean> original,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight
	) {
		if (!shouldRenderHighlight.get()) {
			return original.call(instance, world, pos, dir, blockState, fluid);
		}
		return !this.isSideExposed(world, pos.getX(), pos.getY(), pos.getZ(), dir, 1);
	}

	@WrapWithCondition(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;setVertex(Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadViewMutable;IFFFFF)V"
		)
	)
	private boolean onVertex(
		ModelQuadViewMutable quad,
		int i,
		float x,
		float y,
		float z,
		float u,
		float v
	) {
		this.highlightQuad.setX(i, x);
		this.highlightQuad.setY(i, y);
		this.highlightQuad.setZ(i, z);
		return true;
	}

	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;writeQuad(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;Z)V"
		)
	)
	private void onWriteQuad(
		DefaultFluidRenderer instance,
		ChunkModelBuilder meshBuilder,
		TranslucentGeometryCollector collector,
		Material material,
		BlockPos blockPos,
		ModelQuadView quad,
		ModelQuadFacing facing,
		boolean flip,
		Operation<Void> original,
		@Local(argsOnly = true) FluidState fluidState,
		@Share("shouldRenderLiquid") LocalBooleanRef shouldRenderLiquid,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		if (shouldRenderLiquid.get()) {
			if (fluidState.is(FluidTags.LAVA)) {
				float opacity = EssentialClientConfig.getInstance().getLavaOpacity();
				for (int i = 0; i < 4; i++) {
					this.quadColors[i] = ColorABGR.withAlpha(this.quadColors[i], opacity);
				}
			}
			original.call(instance, meshBuilder, collector, material, blockPos, quad, facing, flip);
		}
		if (shouldRenderHighlightFace.get()) {
			Arrays.fill(this.brightness, 1.0F);
			Arrays.fill(this.quadColors, 0xFFFFFFFF);
			this.writeQuad(
				meshBuilder, collector, material, blockPos, this.highlightQuad, facing, flip
			);
		}
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Direction;getAxis()Lnet/minecraft/core/Direction$Axis;"
		)
	)
	private void onAxis(
		LevelSlice level,
		BlockState blockState,
		FluidState fluidState,
		BlockPos blockPos,
		BlockPos offset,
		TranslucentGeometryCollector collector,
		ChunkModelBuilder meshBuilder,
		Material material,
		ColorProvider<FluidState> colorProvider,
		TextureAtlasSprite[] sprites,
		CallbackInfo ci,
		@Local Direction direction,
		@Share("shouldRenderLiquid") LocalBooleanRef shouldRenderLiquid,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		FluidState neighbor = level.getFluidState(blockPos.relative(direction));
		boolean isNeighborSameFluid = neighbor.getType().isSame(fluidState.getType());
		shouldRenderLiquid.set(!isNeighborSameFluid);
		shouldRenderHighlightFace.set(
			shouldRenderHighlight.get() && (isNeighborSameFluid || neighbor.is(Fluids.EMPTY)) && !neighbor.isSource()
		);
	}
}
