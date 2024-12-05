package me.senseiwells.essential_client.mixins.sneak_to_not_waterlog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlockContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@WrapOperation(
		method = "use",
		constant = @Constant(classValue = LiquidBlockContainer.class)
	)
	private boolean playerCheckBypass(
		Object object,
		Operation<Boolean> original,
		@Local(argsOnly = true) Player player
	) {
		if (EssentialClientConfig.getInstance().getSneakToNotWaterlog()) {
			return original.call(object) && !player.isSecondaryUseActive();
		}
		return original.call(object);
	}
}
