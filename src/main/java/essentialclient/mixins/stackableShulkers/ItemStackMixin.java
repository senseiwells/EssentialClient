package essentialclient.mixins.stackableShulkers;

import carpet.helpers.InventoryHelper;
import essentialclient.config.clientrule.ClientRules;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//priority is to be able to be applied before tweakeroo (for compatibility)
@Mixin(value = ItemStack.class, priority = 900)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "getMaxCount", at=@At(value = "HEAD"), cancellable = true)
    public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
        if (ClientRules.STACKABLE_SHULKERS_IN_PLAYER_INVENTORIES.getValue() && this.getItem() instanceof BlockItem && ((BlockItem) this.getItem()).getBlock() instanceof ShulkerBoxBlock) {
            ItemStack stack = (ItemStack) (Object) this;
            if (!InventoryHelper.shulkerBoxHasItems(stack)) {
                stack.removeSubTag("BlockEntityTag");
                cir.setReturnValue(64);
            }
            else if (ClientRules.STACKABLE_SHULKERS_WITH_ITEMS.getValue())
                cir.setReturnValue(64);
        }
    }
}