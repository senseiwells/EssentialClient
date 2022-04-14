package me.senseiwells.essentialclient.mixins.craftingHax;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "clickRecipe", at = @At("RETURN"))
	public void onRecipeClick(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
		if (ClientRules.CRAFTING_HAX.getValue()
			&& CraftingSharedConstants.IS_VANILLA_CLICK.get()
			&& Screen.hasControlDown()
			&& this.client.currentScreen instanceof HandledScreen<?> handledScreen
		) {
			InventoryUtils.dropStackScheduled(this.client, handledScreen, craftAll);
			CraftingSharedConstants.IS_VANILLA_CLICK.set(false);
		}
	}
}
