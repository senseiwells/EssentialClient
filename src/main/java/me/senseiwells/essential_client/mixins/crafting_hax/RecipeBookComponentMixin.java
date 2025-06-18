package me.senseiwells.essential_client.mixins.crafting_hax;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin<T extends RecipeBookMenu<?, ?>> {
    @Shadow protected Minecraft minecraft;

    @Shadow protected T menu;

    @Inject(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePlaceRecipe(ILnet/minecraft/world/item/crafting/RecipeHolder;Z)V",
            shift = At.Shift.AFTER
        )
    )
    private void onHandlePlaceRecipe(
        double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir
    ) {
        if (EssentialClientConfig.getInstance().getCraftingHax() && Screen.hasControlDown()) {
            int containerId = this.menu.containerId;
            boolean craftMax = Screen.hasShiftDown();
            MultiPlayerGameMode mode = this.minecraft.gameMode;
            if (mode != null) {
                mode.handleInventoryMouseClick(
                    containerId, 0, craftMax ? 1 : 0, ClickType.THROW, this.minecraft.player
                );
            }
        }
    }
}
