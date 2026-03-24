package me.senseiwells.essential_client.mixins.crafting_hax;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin<T extends RecipeBookMenu> {
    @Shadow protected Minecraft minecraft;

    @Shadow @Final protected T menu;

    @Inject(
        method = "tryPlaceRecipe",
        at = @At("RETURN")
    )
    private void onHandlePlaceRecipe(
        RecipeCollection recipes,
        RecipeDisplayId recipe,
        boolean useMaxItems,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (EssentialClientConfig.getInstance().getCraftingHax() && this.minecraft.hasControlDown()) {
            int containerId = this.menu.containerId;
            MultiPlayerGameMode mode = this.minecraft.gameMode;
            LocalPlayer player = this.minecraft.player;
            if (mode != null && player != null) {
                mode.handleContainerInput(containerId, 0, useMaxItems ? 1 : 0, ContainerInput.THROW, player);
            }
        }
    }
}
