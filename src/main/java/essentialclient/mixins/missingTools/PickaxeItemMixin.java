package essentialclient.mixins.missingTools;

import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.block.Material;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {
    //Client implementation of missingTools
    @ModifyVariable(method = "getMiningSpeedMultiplier", at = @At(value = "STORE", ordinal = 0))
    private Material modifyMaterial(Material originalMaterial) {
        if (originalMaterial == Material.GLASS && ClientRules.MISSINGTOOLS.getBoolean())
            return Material.STONE;
        return originalMaterial;
    }
}