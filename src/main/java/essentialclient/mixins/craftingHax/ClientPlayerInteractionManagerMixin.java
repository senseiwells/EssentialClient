package essentialclient.mixins.craftingHax;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.CraftingSharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "clickRecipe", at = @At("RETURN"))
    public void onRecipeClick(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
        if (ClientRules.CRAFTING_HAX.getValue() && Screen.hasControlDown()) {
            int count = craftAll ? recipe.getOutput().getMaxCount() : 1;
            CraftingSharedConstants.THROW_AMOUNT.set(count);
        }
    }
}
