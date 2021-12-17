package essentialclient.mixins.missingTools;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin extends MiningToolItemMixin {
	//Client implementation of missingTools
	@Override
	public void miningSpeedHandler(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
		if (ClientRules.MISSING_TOOLS.getValue() && state.getMaterial() == Material.GLASS) {
			cir.setReturnValue(this.miningSpeed);
		}
	}
}
