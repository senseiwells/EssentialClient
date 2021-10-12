package essentialclient.mixins.functions;

import essentialclient.feature.clientscript.MinecraftEventFunction;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onHealthUpdate", at = @At("HEAD"))
    private void onHealthUpdate(CallbackInfo ci) {
        MinecraftEventFunction.ON_HEALTH_UPDATE.tryRunFunction();
    }

    @Inject(method = "onEntityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"))
    private void onTotem(CallbackInfo ci) {
        MinecraftEventFunction.ON_TOTEM.tryRunFunction();
    }

    @Inject(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setGameMode(Lnet/minecraft/world/GameMode;)V"))
    private void onGamemodeChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        MinecraftEventFunction.ON_GAMEMODE_CHANGE.tryRunFunction(List.of(new StringValue(GameMode.byId((int) packet.getValue()).getName())));
    }
}
