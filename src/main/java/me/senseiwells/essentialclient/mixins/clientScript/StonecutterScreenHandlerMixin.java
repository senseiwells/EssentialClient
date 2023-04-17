package me.senseiwells.essentialclient.mixins.clientScript;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents.ON_CLICK_STONECUTTER_RECIPE;

@Mixin(net.minecraft.screen.StonecutterScreenHandler.class)
public abstract class StonecutterScreenHandlerMixin {

	@Shadow
	@Final
	private Property selectedRecipe;

	@Shadow
	private List<StonecuttingRecipe> availableRecipes;

	@Inject(method = "onButtonClick", at = @At(target = "Lnet/minecraft/screen/StonecutterScreenHandler;populateResult()V", value = "INVOKE"))
	private void onButtonClick(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
		int i = this.selectedRecipe.get();
		if (i >= 0 && i < this.availableRecipes.size()) {
			ON_CLICK_STONECUTTER_RECIPE.run(this.availableRecipes.get(i));
		}
	}
}
