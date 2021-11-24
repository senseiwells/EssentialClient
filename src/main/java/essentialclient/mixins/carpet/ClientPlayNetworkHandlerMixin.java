package essentialclient.mixins.carpet;

import essentialclient.network.ClientMessageHandler;
import essentialclient.utils.carpet.Reference;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
    @Inject(method = "onCustomPayload", at = @At(value = "CONSTANT", args = "stringValue=Unknown custom packed identifier: {}"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void onCustomPayloadNotFound(CustomPayloadS2CPacket packet, CallbackInfo ci, Identifier id, PacketByteBuf buf) {
        if (Reference.CARPET_CHANNEL_NAME.equals(id)) {
            if (buf.refCnt() > 0) {
                buf.release();
            }
            ci.cancel();
        }
    }
    
    @Inject(method = "onCustomPayload", at = @At("TAIL"))
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        Identifier channel = packet.getChannel();
        PacketByteBuf buf = packet.getData();
        ClientMessageHandler.receivedPacket(channel, buf);
    }
}
