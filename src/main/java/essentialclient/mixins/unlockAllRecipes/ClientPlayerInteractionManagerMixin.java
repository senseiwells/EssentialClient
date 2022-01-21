package essentialclient.mixins.unlockAllRecipes;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.RecipeBookCache;
import essentialclient.feature.RecipeBookTracker;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Unique
	private Recipe<?> lastRecipeCache = null;

	@Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true)
	public void onClickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.currentScreen instanceof HandledScreen<?> handledScreen && RecipeBookCache.isCached(recipe)) {
			if (RecipeBookTracker.IS_SCRIPT_CLICK.get()) {
				InventoryUtils.doCraftingSlotsFillAction(recipe, this.lastRecipeCache, handledScreen, craftAll);
				RecipeBookTracker.IS_SCRIPT_CLICK.set(false);
				this.lastRecipeCache = recipe;
				ci.cancel();
			}
			if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && RecipeBookTracker.IS_VANILLA_CLICK.get()) {
				InventoryUtils.doCraftingSlotsFillAction(recipe, this.lastRecipeCache, handledScreen, craftAll);
				RecipeBookTracker.IS_VANILLA_CLICK.set(false);
				this.lastRecipeCache = recipe;
				ci.cancel();
			}
		}
	}
}
