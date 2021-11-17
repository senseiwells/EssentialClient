package essentialclient.mixins.betterAccurateBlockPlacement;

import essentialclient.feature.BetterAccurateBlockPlacement;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUsageContext.class)
public class ItemUsageContextMixin {
	@Inject(method = "getPlayerFacing", at = @At("HEAD"), cancellable = true)
	private void onGetFacing(CallbackInfoReturnable<Direction> cir) {
		Direction direction = BetterAccurateBlockPlacement.fakeDirection;
		if (direction != null) {
			switch (direction) {
				case WEST, EAST, NORTH, SOUTH -> cir.setReturnValue(direction);
			}
		}
	}
}
