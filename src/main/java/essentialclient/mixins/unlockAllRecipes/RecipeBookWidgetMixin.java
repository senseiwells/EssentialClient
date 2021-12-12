package essentialclient.mixins.unlockAllRecipes;

import essentialclient.utils.inventory.InventoryUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookGhostSlots;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {

    @Shadow
    protected MinecraftClient client;

    @Shadow
    public abstract void showGhostRecipe(Recipe<?> recipe, List<Slot> slots);

    @Unique
    RecipeMatcher matcher = new RecipeMatcher();

    @Shadow
    @Final
    protected RecipeBookGhostSlots ghostSlots;

    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/network/ClientPlayerInteractionManager.clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V"
            )
    )
    private void redirectClickSlotCall(ClientPlayerInteractionManager instance, int syncId, Recipe<?> recipe, boolean craftAll) {
        PlayerEntity player = this.client.player;
        if (player != null && this.client.currentScreen instanceof HandledScreen<?> handledScreen) {
            int gridLength = InventoryUtils.getCraftingSlotLength(handledScreen);
            InventoryUtils.clearCraftingGridNEW(this.client, handledScreen, player, gridLength);

            IntList craftInputIds = new IntArrayList();
            player.inventory.populateRecipeFinder(this.matcher);
            int craftsOps = matcher.countCrafts(recipe, craftInputIds);

            if (craftsOps == 0) {
                this.showGhostRecipe(recipe, this.client.player.currentScreenHandler.slots);
            } else {
                // if crafting possible, we fill the slots.
                // this is the fix for special event locked recipes like chest, boat, bowl, etc.
                this.ghostSlots.reset();

                List<Item> stacks = new ArrayList<>(gridLength);

                for (int id: craftInputIds) {
                    stacks.add(RecipeMatcher.getStackFromId(id).getItem());
                }

                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    int gridSide = (int) Math.sqrt(gridLength);
                    int recipeWidth = shapedRecipe.getWidth();
                    int recipeHeight = shapedRecipe.getHeight();

                    for (int i = 0; i < recipeHeight - 1; i++) {
                        for (int j = recipeWidth; j < gridSide; j++) {
                            stacks.add(i * gridSide + j, ItemStack.EMPTY.getItem());
                        }
                    }
                }
                InventoryUtils.tryMoveItemsToCraftingGridSlotsNEW(this.client, stacks, handledScreen, craftsOps, !craftAll);
            }
            matcher.clear();
        }
    }
}
