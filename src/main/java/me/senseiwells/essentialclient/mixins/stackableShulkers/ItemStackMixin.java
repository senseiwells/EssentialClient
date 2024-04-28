package me.senseiwells.essentialclient.mixins.stackableShulkers;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Priority is to be able to be applied before tweakeroo (for compatibility)
@Mixin(value = ItemStack.class, priority = 900)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(method = "getMaxCount", at = @At(value = "HEAD"), cancellable = true)
	public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
		if (EssentialUtils.isStackableShulkers(this.getItem())) {
			@SuppressWarnings("ConstantConditions")
			ItemStack stack = (ItemStack) (Object) this;
			ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
			if (container == null || container.streamNonEmpty().findAny().isEmpty()) {
				cir.setReturnValue(64);
			} else if (EssentialUtils.isStackableShulkerWithItems()) {
				cir.setReturnValue(64);
			}
		}
	}
}
