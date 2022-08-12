package me.senseiwells.essentialclient.mixins.betterAccurateBlockPlacement;

import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPlacementContext.class)
public class ItemPlacementContextMixin {
	@Inject(method = "getPlayerLookDirection", at = @At("HEAD"), cancellable = true)
	private void onGetDirection(CallbackInfoReturnable<Direction> cir) {
		if (BetterAccurateBlockPlacement.fakeDirection != null) {
			cir.setReturnValue(BetterAccurateBlockPlacement.fakeDirection);
		}
	}

	@Inject(method = "getVerticalPlayerLookDirection", at = @At("HEAD"), cancellable = true, require = 0)
	private void onGetVerticalDirection(CallbackInfoReturnable<Direction> cir) {
		if (BetterAccurateBlockPlacement.fakeDirection != null && BetterAccurateBlockPlacement.fakeDirection.getAxis() == Direction.Axis.Y) {
			cir.setReturnValue(BetterAccurateBlockPlacement.fakeDirection);
		}
	}

	@Redirect(method = "getPlacementDirections", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;getEntityFacingOrder(Lnet/minecraft/entity/Entity;)[Lnet/minecraft/util/math/Direction;"))
	private Direction[] onGetArrayDirections(Entity entity) {
		if (BetterAccurateBlockPlacement.fakeDirection != null) {
			return BetterAccurateBlockPlacement.getFacingOrder();
		}
		return Direction.getEntityFacingOrder(entity);
	}
}
