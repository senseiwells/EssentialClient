package essentialclient.mixins.unlockAllRecipes;

import essentialclient.feature.VanillaRecipeBookClickTracker;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V"))
	public void onClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		VanillaRecipeBookClickTracker.IS_VANILLA_CLICK.set(true);
	}
}
