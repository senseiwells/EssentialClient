package me.senseiwells.essentialclient.mixins.betterAccurateBlockPlacement;

import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPlacementContext.class)
public class ItemPlacementContextMixin {
	@Inject(method = "getPlayerLookDirection", at = @At("HEAD"), cancellable = true)
	private void onGetDirection(CallbackInfoReturnable<Direction> cir) {
		if (BetterAccurateBlockPlacement.fakeDirection != null) {
			cir.setReturnValue(BetterAccurateBlockPlacement.fakeDirection);
		}
	}
}
