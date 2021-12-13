package essentialclient.mixins.unlockAllRecipes;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.ducks.IGhostRecipeBookWidget;
import essentialclient.feature.RecipeBookCache;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.inventory.InventoryUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true)
    public void onClickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
        MinecraftClient mc = EssentialUtils.getClient();
        PlayerEntity player = EssentialUtils.getPlayer();

        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && RecipeBookCache.isCached(recipe) && mc.currentScreen instanceof CraftingScreen craftingScreen) {

            int gridLength = InventoryUtils.getCraftingSlotLength(craftingScreen);
            InventoryUtils.clearCraftingGridNEW(mc, craftingScreen, player, gridLength);

            RecipeMatcher matcher = new RecipeMatcher();
            IntList craftInputIds = new IntArrayList();

            player.inventory.populateRecipeFinder(matcher);
            int craftsOps = matcher.countCrafts(recipe, craftInputIds);

            if (craftsOps == 0) {
                craftingScreen.getRecipeBookWidget().showGhostRecipe(recipe, player.currentScreenHandler.slots);
            } else {
                ((IGhostRecipeBookWidget) craftingScreen.getRecipeBookWidget()).clearGhostSlots();
                List<Item> stacks = new ArrayList<>(gridLength);

                for (int id : craftInputIds) {
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
                InventoryUtils.tryMoveItemsToCraftingGridSlotsNEW(mc, stacks, craftingScreen, craftAll ? craftsOps : 1, craftAll);
            }
            matcher.clear();
            ci.cancel();
        }
    }
}
