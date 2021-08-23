package essentialclient.mixins.unlockAllRecipes;

import essentialclient.gui.clientrule.ClientRule;
import essentialclient.gui.clientrule.ClientRules;
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
        if (ClientRule.getBoolean(ClientRules.unlockAllRecipesOnJoin)) {
            player.unlockRecipes(new ArrayList<>(player.server.getRecipeManager().values()));
        }
    }
}
