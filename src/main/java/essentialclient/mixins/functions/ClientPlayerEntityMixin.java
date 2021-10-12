package essentialclient.mixins.functions;

import essentialclient.feature.clientscript.MinecraftEventFunction;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"))
    public void onChatMessage(String message, CallbackInfo ci) {
        MinecraftEventFunction.ON_CHAT_MESSAGE.tryRunFunction(List.of(new StringValue(message)));
    }
}
