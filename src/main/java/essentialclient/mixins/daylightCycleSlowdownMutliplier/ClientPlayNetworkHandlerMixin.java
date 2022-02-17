package essentialclient.mixins.daylightCycleSlowdownMutliplier;

import essentialclient.clientrule.ClientRules;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onWorldTimeUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"), cancellable = true)
	private void onTimeOfDayUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
		if (ClientRules.PERMANENT_TIME.getValue() >= 0) {
			ci.cancel();
		}
	}
}
