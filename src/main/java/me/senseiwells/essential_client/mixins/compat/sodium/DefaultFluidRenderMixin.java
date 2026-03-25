package me.senseiwells.essential_client.mixins.compat.sodium;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
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
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(DefaultFluidRenderer.class)
public abstract class DefaultFluidRenderMixin {
	@Unique private final ModelQuadViewMutable highlightQuad = new ModelQuad();

	@Shadow(remap = false) @Final private float[] brightness;

	@Shadow(remap = false) @Final private int[] quadColors;

	@Shadow protected abstract void writeQuad(ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip);

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
		FluidModel sprites,
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
		if (shouldRenderHighlight.get()) {
			this.highlightQuad.setFlags(0);
			this.highlightQuad.setSprite(HighlightLiquids.sprite);
			UVPair[] uvs = HighlightLiquids.getSpriteUVs();
			for (int i = 0; i < 4; i++) {
				UVPair uv = uvs[i];
				this.highlightQuad.setTexU(i, uv.u());
				this.highlightQuad.setTexV(i, uv.v());
				// this.highlightQuad.setNormal(i, ModelQuadFacing.OPPOSING_Y);
			}
		}
	}

	@Definition(id = "isFullBlockFluidSideVisible", method = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;isFullBlockFluidSideVisible(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)Z")
	@Expression("this.isFullBlockFluidSideVisible(?, ?, ?, ?)")
	@WrapOperation(
		method = "render",
		at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private boolean bypassFaceCheckIfHighlighting(
		DefaultFluidRenderer instance, 
		BlockGetter view, 
		BlockPos selfPos,
		Direction facing, 
		FluidState fluid, 
		Operation<Boolean> original,
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight
	) {
		if (!shouldRenderHighlight.get()) {
			return original.call(instance, view, selfPos, facing, fluid);
		}
		return true;
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
		@Share("shouldRenderFluid") LocalBooleanRef shouldRenderFluid,
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		if (shouldRenderFluid.get()) {
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
			target = "Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;fromDirection(Lnet/minecraft/core/Direction;)Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;"
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
		FluidModel sprites,
		CallbackInfo ci,
		@Local(name = "dir") Direction direction, 
		@Share("shouldRenderFluid") LocalBooleanRef shouldRenderFluid, 
		@Share("shouldRenderHighlight") LocalBooleanRef shouldRenderHighlight, 
		@Share("shouldRenderHighlightFace") LocalBooleanRef shouldRenderHighlightFace
	) {
		FluidState neighbor = level.getFluidState(blockPos.relative(direction));
		boolean isNeighborSameFluid = neighbor.getType().isSame(fluidState.getType());
		shouldRenderFluid.set(!isNeighborSameFluid);
		shouldRenderHighlightFace.set(
			shouldRenderHighlight.get() && (isNeighborSameFluid || neighbor.is(Fluids.EMPTY)) && !neighbor.isSource()
		);
	}
}
