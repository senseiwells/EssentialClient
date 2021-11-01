package essentialclient.mixins.unlockAllRecipes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {

    @Shadow
    protected MinecraftClient client;

    @Shadow
    public abstract void showGhostRecipe(Recipe<?> recipe, List<Slot> slots);

    @Unique
    RecipeMatcher matcher = new RecipeMatcher();

    @Inject(method = "mouseClicked", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "net/minecraft/client/network/ClientPlayerInteractionManager.clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V"))
    private void postClickRecipe(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, Recipe<?> recipe, RecipeResultCollection recipeResultCollection) {
        PlayerEntity player = this.client.player;
        if (player != null) {
            player.inventory.populateRecipeFinder(this.matcher);
            if (!this.matcher.match(recipe, null)) {
                this.showGhostRecipe(recipe, this.client.player.currentScreenHandler.slots);
            }
            matcher.clear();
        }
    }
}
