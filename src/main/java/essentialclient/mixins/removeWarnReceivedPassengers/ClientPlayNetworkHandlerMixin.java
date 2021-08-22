package essentialclient.mixins.removeWarnReceivedPassengers;

import essentialclient.gui.clientruleformat.ClientRuleHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"))
    private void onWarn(Logger logger, String message) {
        if (ClientRuleHelper.getBoolean("removeWarnReceivedPassengers"))
            return;
        logger.warn(message);
    }
}
