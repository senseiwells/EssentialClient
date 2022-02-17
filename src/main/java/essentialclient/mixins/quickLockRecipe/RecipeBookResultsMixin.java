package essentialclient.mixins.quickLockRecipe;

import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookResults.class)
public class RecipeBookResultsMixin {
	@Shadow
	@Final
	private List<AnimatedResultButton> resultButtons;
	@Shadow
	private Recipe<?> lastClickedRecipe;
	@Shadow
	private RecipeResultCollection resultCollection;

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE), cancellable = true)
	private void checkMiddleMouse(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir) {
		for (AnimatedResultButton resultButton : this.resultButtons) {
			if (resultButton.mouseClicked(mouseX, mouseY, button) && button == 2) {
				this.lastClickedRecipe = resultButton.currentRecipe();
				this.resultCollection = resultButton.getResultCollection();
				cir.setReturnValue(true);
				break;
			}
		}
	}
}
