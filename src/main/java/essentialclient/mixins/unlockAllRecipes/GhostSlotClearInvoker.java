package essentialclient.mixins.unlockAllRecipes;

import essentialclient.utils.interfaces.IGhostRecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookGhostSlots;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeBookWidget.class)
public abstract class GhostSlotClearInvoker implements IGhostRecipeBookWidget {

	@Shadow
	@Final
	protected RecipeBookGhostSlots ghostSlots;
	
	@Override
	public void clearGhostSlots() {
		this.ghostSlots.reset();
	}
}
