package me.senseiwells.essentialclient.mixins.removeWarnReceivedPassengers;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.network.ClientPlayNetworkHandler;

//#if MC >= 11800
import org.slf4j.Logger;
//#else
//$$import org.apache.logging.log4j.Logger;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	//#if MC >= 11800
	@Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", remap = false))
	//#else
	//$$@Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V", remap = false))
	//#endif
	private void onWarn(Logger instance, String s) {
		if (!ClientRules.REMOVE_WARN_RECEIVED_PASSENGERS.getValue()) {
			instance.warn(s);
		}
	}
}
