package essentialclient.mixins.unlockAllRecipes;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void unlockRecipes(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getBoolean()) {
            player.unlockRecipes(new ArrayList<>(player.server.getRecipeManager().values()));
        }
    }
}
