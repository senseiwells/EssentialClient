package essentialclient.mixins.unlockAllRecipes;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract RecipeManager getRecipeManager();

    @Shadow
    public abstract void onUnlockRecipes(UnlockRecipesS2CPacket packet);

    @Inject(method = "onUnlockRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/UnlockRecipesS2CPacket;getRecipeIdsToChange()Ljava/util/List;", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    private void onInitRecipes(UnlockRecipesS2CPacket packet, CallbackInfo ci) {
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getBoolean() && client.player != null) {
            List<Identifier> recipes = this.getRecipeManager().keys().collect(Collectors.toList());
            this.onUnlockRecipes(new UnlockRecipesS2CPacket(UnlockRecipesS2CPacket.Action.ADD, recipes, Collections.emptyList(), client.player.getRecipeBook().getOptions()));
            ci.cancel();
        }
    }
}
