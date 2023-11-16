package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.network.HandlerPayload;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomPayloadS2CPacket.class)
public class CustomPayloadS2CPacketMixin {
	@Inject(
		method = "readPayload",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void onReadPayload(
		Identifier id,
		PacketByteBuf buf,
		CallbackInfoReturnable<CustomPayload> cir
	) {
		for (NetworkHandler handler : EssentialClient.NETWORK_HANDLERS) {
			if (id.equals(handler.getNetworkChannel())) {
				cir.setReturnValue(new HandlerPayload(handler, id, buf));
			}
		}
	}
}
