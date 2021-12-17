package essentialclient.mixins.removeWarnReceivedPassengers;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("mapping")
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"))
	private void onWarn(Logger logger, String message) {
		if (ClientRules.REMOVE_WARN_RECEIVED_PASSENGERS.getValue()) {
			return;
		}
		logger.warn(message);
	}
}
