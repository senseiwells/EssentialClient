package essentialclient.mixins.unlockAllRecipes;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getBoolean() && client.player != null) {
            packet.getRecipes().forEach(recipe -> client.player.getRecipeBook().add(recipe));
        }
    }
}
