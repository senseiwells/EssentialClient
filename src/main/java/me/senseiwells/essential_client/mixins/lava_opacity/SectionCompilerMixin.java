package me.senseiwells.essential_client.mixins.lava_opacity;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.*;
import me.senseiwells.essential_client.ducks.TranslucentLiquids;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {
	@Shadow protected abstract BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> bufferLayers, SectionBufferBuilderPack sectionBufferBuilderPack, RenderType renderType);

	@Shadow @Final private BlockRenderDispatcher blockRenderer;

	@Inject(
		method = "compile",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/RandomSource;create()Lnet/minecraft/util/RandomSource;"
		)
	)
	private void onRenderFluid(
		SectionPos sectionPos,
		RenderChunkRegion region,
		VertexSorting vertexSorting,
		SectionBufferBuilderPack sectionBufferBuilderPack,
		CallbackInfoReturnable<SectionCompiler.Results> cir,
		@Local Map<RenderType, BufferBuilder> cache
	) {
		BufferBuilder translucent = this.getOrBeginLayer(cache, sectionBufferBuilderPack, RenderType.translucent());
		((TranslucentLiquids) this.blockRenderer).essentialclient$setTranslucentConsumer(translucent);
	}

	@Inject(
		method = "compile",
		at = @At("RETURN")
	)
	private void afterRenderFluids(CallbackInfoReturnable<SectionCompiler.Results> cir) {
		((TranslucentLiquids) this.blockRenderer).essentialclient$setTranslucentConsumer(null);
	}
}
