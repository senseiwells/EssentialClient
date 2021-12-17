package essentialclient.mixins.sodium;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.HighlightLavaSources;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer", remap = false)
public class FluidRendererMixin {
	@Shadow
	@Final
	private Sprite[] lavaSprites;

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "render", at = @At("HEAD"), require = 0)
	public void modifyLavaSprites(BlockRenderView world, FluidState fluidState, BlockPos pos, @Coerce Object buffers, CallbackInfoReturnable<Boolean> info) {
		if (ClientRules.HIGHLIGHT_LAVA_SOURCES.getValue() && fluidState.isIn(FluidTags.LAVA) &&
				world.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
			this.lavaSprites[0] = HighlightLavaSources.lavaSourceStillSprite;
			this.lavaSprites[1] = HighlightLavaSources.lavaSourceFlowSprite;
		}
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "render", at = @At("RETURN"), require = 0)
	public void restoreLavaSprites(BlockRenderView world, FluidState fluidState, BlockPos pos, @Coerce Object buffers, CallbackInfoReturnable<Boolean> info) {
		this.lavaSprites[0] = HighlightLavaSources.defaultLavaSourceStillSprite;
		this.lavaSprites[1] = HighlightLavaSources.defaultLavaSourceFlowSprite;
	}
}
