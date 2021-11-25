package essentialclient.mixins.unlockAllRecipes;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "onSynchronizeRecipes", at = @At("HEAD"))
    private void onSyncRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && client.player != null) {
            ClientRecipeBook recipeBook = client.player.getRecipeBook();
            packet.getRecipes().forEach(recipeBook::add);
        }
    }
}
