package essentialclient.mixins.functions;

import essentialclient.feature.clientscript.MinecraftEventFunction;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onHealthUpdate", at = @At("HEAD"))
    private void onHealthUpdate(CallbackInfo ci) {
        MinecraftEventFunction.ON_HEALTH_UPDATE.tryRunFunction();
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(CallbackInfo ci) {
        MinecraftEventFunction.ON_RECEIVE_MESSAGE.tryRunFunction();
    }

    @Inject(method = "onHeldItemChange", at = @At("HEAD"))
    private void onHeldItemChange(CallbackInfo ci) {
        MinecraftEventFunction.ON_ITEM_CHANGE.tryRunFunction();
    }

    @Inject(method = "onEntityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"))
    private void onTotem(CallbackInfo ci) {
        MinecraftEventFunction.ON_TOTEM.tryRunFunction();
    }
}
