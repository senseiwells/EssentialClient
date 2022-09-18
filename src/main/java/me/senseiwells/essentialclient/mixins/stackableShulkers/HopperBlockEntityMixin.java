package me.senseiwells.essentialclient.mixins.stackableShulkers;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@Inject(method = "canMergeItems", at = @At("HEAD"), cancellable = true)
	private static void canMergeItems(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> cir) {
		if (EssentialUtils.isStackableShulkers(first.getItem())) {
			cir.setReturnValue(false);
		}
	}
}
