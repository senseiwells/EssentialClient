package me.senseiwells.essential_client.mixins.better_accurate_block_placement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.senseiwells.essential_client.features.BetterAccurateBlockPlacement;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockPlaceContext.class)
public class BlockPlaceContextMixin {
	@ModifyReturnValue(
		method = "getNearestLookingDirection",
		at = @At("RETURN")
	)
	private Direction onGetNearestLookingDirection(Direction original) {
		Direction fake = BetterAccurateBlockPlacement.getFakeDirection();
		return fake == null ? original : fake;
	}
}
