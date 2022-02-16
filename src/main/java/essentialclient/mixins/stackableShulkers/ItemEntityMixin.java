package essentialclient.mixins.stackableShulkers;

import carpet.helpers.InventoryHelper;
import essentialclient.clientrule.ClientRules;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	//copied from carpet
	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
	private void removeEmptyShulkerBoxTags(World worldIn, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
		if (ClientRules.STACKABLE_SHULKERS_IN_PLAYER_INVENTORIES.getValue() && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
			if (InventoryHelper.cleanUpShulkerBoxTag(stack)) {
				((ItemEntity) (Object) this).setStack(stack);
			}
		}
	}
}
