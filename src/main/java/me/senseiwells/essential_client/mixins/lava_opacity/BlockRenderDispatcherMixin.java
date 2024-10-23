package me.senseiwells.essential_client.mixins.lava_opacity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.senseiwells.essential_client.ducks.TranslucentLiquids;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin implements TranslucentLiquids {
	@Shadow @Final private LiquidBlockRenderer liquidBlockRenderer;

	@Override
	public void essentialclient$setTranslucentConsumer(VertexConsumer consumer) {
		((TranslucentLiquids) this.liquidBlockRenderer).essentialclient$setTranslucentConsumer(consumer);
	}
}
