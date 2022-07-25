package me.senseiwells.essentialclient.mixins.quickLockRecipe;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {
	@Shadow
	@Final
	private RecipeBookResults recipesArea;
	@Shadow
	private TextFieldWidget searchField;

	@Shadow
	protected abstract void refreshResults(boolean resetCurrentPage);

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;getLastClickedRecipe()Lnet/minecraft/recipe/Recipe;"), cancellable = true)
	private void isMiddleClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		Recipe<?> recipe = this.recipesArea.getLastClickedRecipe();
		if (button == 2 && recipe != null) {
			String itemName = recipe.getOutput().getItem().getTranslationKey();
			if (I18n.hasTranslation(itemName)) {
				this.searchField.setText(I18n.translate(itemName));
				this.refreshResults(true);
			}
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), cancellable = true)
	private void isMiddleClickNothing(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (button == 2) {
			this.searchField.setText("");
			this.refreshResults(true);
			cir.setReturnValue(true);
		}
	}
}
