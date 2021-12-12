package essentialclient.mixins.missingTools;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Set;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin extends MiningToolItem {

    //Client implementation of missingTools
    protected PickaxeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, Set<Block> tag, Item.Settings settings) {
        super(attackDamage, attackSpeed, material, tag, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        if (ClientRules.MISSING_TOOLS.getValue() && material == Material.GLASS) {
            return miningSpeed;
        }
        return super.getMiningSpeedMultiplier(stack, state);
    }
}
