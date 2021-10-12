package essentialclient.mixins.functions;

import essentialclient.feature.clientscript.MinecraftEventFunction;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftEventFunction.ON_CLIENT_TICK.tryRunFunction();
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void onAttack(CallbackInfo ci) {
        MinecraftEventFunction.ON_ATTACK.tryRunFunction();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void onUse(CallbackInfo ci) {
        MinecraftEventFunction.ON_USE.tryRunFunction();
    }

    @Inject(method = "doItemPick", at = @At("HEAD"))
    private void onPickBlock(CallbackInfo ci) {
        MinecraftEventFunction.ON_PICK_BLOCK.tryRunFunction();
    }

    @Inject(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void onBlockBroken(boolean bl, CallbackInfo ci) {
        MinecraftEventFunction.ON_BLOCK_BROKEN.tryRunFunction();
    }
}
