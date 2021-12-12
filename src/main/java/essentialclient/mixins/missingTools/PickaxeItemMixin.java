package essentialclient.mixins.missingTools;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {
    //Client implementation of missingTools
    @Redirect(method = "getMiningSpeedMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getMaterial()Lnet/minecraft/block/Material;"))
    private Material modifyMaterial(BlockState state) {
        Material originalMaterial = state.getMaterial();
        if (originalMaterial == Material.GLASS && ClientRules.MISSING_TOOLS.getValue()) {
            return Material.STONE;
        }
        return originalMaterial;
    }
}
