package me.senseiwells.essentialclient.mixins.unlockAllRecipes;

import me.senseiwells.essentialclient.feature.RecipeBookCache;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
	@Shadow
	@Final
	private RecipeManager recipeManager;

	protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		super(client, connection, connectionState);
	}

	@Inject(method = "onSynchronizeRecipes", at = @At("HEAD"))
	private void onSyncRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
		if (this.client.player != null) {
			RecipeBookCache.setRecipeCache(packet.getRecipes());
			if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue()) {
				ClientRecipeBook recipeBook = this.client.player.getRecipeBook();
				packet.getRecipes().forEach(recipeBook::add);
			}
		}
	}

	@Inject(method = "onUnlockRecipes", at = @At("HEAD"))
	private void onUnlock(UnlockRecipesS2CPacket packet, CallbackInfo ci) {
		switch (packet.getAction()) {
			case ADD, INIT -> {
				for (Identifier identifier : packet.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(RecipeBookCache::removeRecipeFromCache);
				}
			}
			case REMOVE -> {
				for (Identifier identifier : packet.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(RecipeBookCache::addRecipeToCache);
				}
			}
		}
	}
}
