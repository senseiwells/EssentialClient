package essentialclient.mixins.unlockAllRecipes;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.RecipeBookCache;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true)
    public void onClickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
        MinecraftClient mc = EssentialUtils.getClient();
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && RecipeBookCache.isCached(recipe) && mc.currentScreen instanceof HandledScreen<?> handledScreen) {
            InventoryUtils.doCraftingSlotsFillAction(recipe, handledScreen, craftAll);
            ci.cancel();
        }
    }
}
