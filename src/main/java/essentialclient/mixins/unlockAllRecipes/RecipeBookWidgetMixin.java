package essentialclient.mixins.unlockAllRecipes;

import essentialclient.utils.inventory.InventoryUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookGhostSlots;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {

    @Shadow
    protected MinecraftClient client;

    @Shadow
    public abstract void showGhostRecipe(Recipe<?> recipe, List<Slot> slots);

    @Unique
    RecipeMatcher matcher = new RecipeMatcher();

    @Shadow @Final protected RecipeBookGhostSlots ghostSlots;

    @Inject(
            method = "mouseClicked",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "net/minecraft/client/network/ClientPlayerInteractionManager.clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V"
            )
    )
    private void postClickRecipe(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, Recipe<?> recipe, RecipeResultCollection recipeResultCollection) {
        PlayerEntity player = this.client.player;
        if (player != null) {

            player.inventory.populateRecipeFinder(this.matcher);
            IntList craftInputIds = new IntArrayList();
            int craftsOps = matcher.countCrafts(recipe, craftInputIds);

            if (craftsOps == 0) {
                this.showGhostRecipe(recipe, this.client.player.currentScreenHandler.slots);
            } else {
                this.ghostSlots.reset();
                if (this.client.currentScreen != null && this.client.currentScreen instanceof HandledScreen<?> handledScreen) {
                    List<ItemStack> stacks = craftInputIds.stream()
                                                          .map(Item::byRawId)
                                                          .map(Item::getDefaultStack)
                                                          .collect(Collectors.toList());
                    for (int i = stacks.size(); i < 9; i++) {
                        stacks.add(Items.AIR.getDefaultStack());
                    }
                    InventoryUtils.tryMoveItemsToCraftingGridSlots(this.client, stacks.toArray(ItemStack[]::new), handledScreen);
                }
            }
        }
    }
}
