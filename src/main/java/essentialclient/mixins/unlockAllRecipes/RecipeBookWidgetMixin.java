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
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
            // adding intentional delay ensuring server's packet is received before we populate the matcher
            try { Thread.sleep(0, 1); }
            catch (InterruptedException ignored) {}

            // populate the matcher and get crafting ops and matching stacks
            IntList craftInputIds = new IntArrayList();
            player.inventory.populateRecipeFinder(this.matcher);
            int craftsOps = matcher.countCrafts(recipe, craftInputIds);

            // if crafting ops, is 0, then we show ghost recipe
            if (craftsOps == 0) {
                this.showGhostRecipe(recipe, this.client.player.currentScreenHandler.slots);
            } else {
                // otherwise, we fill the slots.
                // this is the fix for special event locked recipes like chest, boat, bowl, etc.
                this.ghostSlots.reset();
                List<ItemStack> stacks = craftInputIds.stream()
                                                      .map(RecipeMatcher::getStackFromId)
                                                      .collect(Collectors.toList());
                for (int i = stacks.size(); i < 9; i++) {
                    stacks.add(ItemStack.EMPTY);
                }
                InventoryUtils.tryMoveItemsToCraftingGridSlots(this.client, stacks.toArray(ItemStack[]::new), handledScreen);
            }
            matcher.clear();
        }
    }
}
