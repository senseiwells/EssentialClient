package essentialclient.mixins.unlockAllRecipes;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.RecipeBookCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
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
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    private MinecraftClient client;

    @Shadow @Final private RecipeManager recipeManager;

    @Inject(method = "onSynchronizeRecipes", at = @At("HEAD"))
    private void onSyncRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && client.player != null) {
            ClientRecipeBook recipeBook = client.player.getRecipeBook();
            packet.getRecipes().forEach(recipeBook::add);
            RecipeBookCache.setRecipeCache(packet.getRecipes());
        }
    }

    @Inject(method = "onUnlockRecipes", at = @At("HEAD"))
    private void onUnlock(UnlockRecipesS2CPacket packet, CallbackInfo ci) {
        if (!ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue()) return;
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
