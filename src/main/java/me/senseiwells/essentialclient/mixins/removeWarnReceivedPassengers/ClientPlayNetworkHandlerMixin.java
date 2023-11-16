package me.senseiwells.essentialclient.mixins.removeWarnReceivedPassengers;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", remap = false))
	private void onWarn(Logger instance, String s) {
		if (!ClientRules.REMOVE_WARN_RECEIVED_PASSENGERS.getValue()) {
			instance.warn(s);
		}
	}
}
