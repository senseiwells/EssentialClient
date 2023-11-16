package me.senseiwells.essentialclient.mixins.unlockAllRecipes;

import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.feature.RecipeBookCache;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.RecipeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Unique
	private RecipeEntry<?> essentialclient$lastRecipeCache = null;

	@Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true)
	public void onClickRecipe(int syncId, RecipeEntry<?> recipe, boolean craftAll, CallbackInfo ci) {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.currentScreen instanceof HandledScreen<?> handledScreen && RecipeBookCache.isCached(recipe)) {
			if (CraftingSharedConstants.IS_SCRIPT_CLICK.get()) {
				InventoryUtils.doCraftingSlotsFillAction(recipe, this.essentialclient$lastRecipeCache, handledScreen, craftAll);
				CraftingSharedConstants.IS_SCRIPT_CLICK.set(false);
				ci.cancel();
			}
			if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && CraftingSharedConstants.IS_VANILLA_CLICK.get()) {
				InventoryUtils.doCraftingSlotsFillAction(recipe, this.essentialclient$lastRecipeCache, handledScreen, craftAll);
			}
			this.essentialclient$lastRecipeCache = recipe;
		}
	}
}
