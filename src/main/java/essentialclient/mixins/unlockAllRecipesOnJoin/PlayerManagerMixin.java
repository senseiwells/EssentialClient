package essentialclient.mixins.unlockAllRecipesOnJoin;

import essentialclient.gui.clientrule.ClientRuleHelper;
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
        if (ClientRuleHelper.getBoolean("unlockAllRecipesOnJoin")) {
            player.unlockRecipes(new ArrayList<>(player.server.getRecipeManager().values()));
        }
    }
}
