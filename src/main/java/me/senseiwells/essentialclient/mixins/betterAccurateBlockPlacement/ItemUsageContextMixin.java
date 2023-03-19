package me.senseiwells.essentialclient.mixins.betterAccurateBlockPlacement;

import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUsageContext.class)
public class ItemUsageContextMixin {
	@Inject(
		//#if MC >= 11904
		method = "getHorizontalPlayerFacing",
		//#else
		//$$method = "getPlayerFacing",
		//#endif
		at = @At("HEAD"),
		cancellable = true
	)
	private void onGetFacing(CallbackInfoReturnable<Direction> cir) {
		Direction direction = BetterAccurateBlockPlacement.fakeDirection;
		if (direction != null && direction.getAxis() != Direction.Axis.Y) {
			cir.setReturnValue(direction);
		}
	}
}
