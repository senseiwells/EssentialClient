package me.senseiwells.essentialclient.mixins.unlockAllRecipes;

import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V"))
	public void onClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		CraftingSharedConstants.IS_VANILLA_CLICK.set(true);
	}
}
