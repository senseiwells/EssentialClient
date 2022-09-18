package me.senseiwells.essentialclient.mixins.stackableShulkers;

import carpet.helpers.InventoryHelper;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	// Copied from carpet
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
	private void removeEmptyShulkerBoxTags(World worldIn, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
		if (EssentialUtils.isStackableShulkers(stack.getItem())) {
			if (InventoryHelper.cleanUpShulkerBoxTag(stack)) {
				((ItemEntity) (Object) this).setStack(stack);
			}
		}
	}
}
