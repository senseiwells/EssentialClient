package me.senseiwells.essentialclient.feature;

import carpet.network.CarpetClient;
import carpet.network.ClientNetworkHandler;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class MultiConnectSupport {
	static {
		MultiConnectAPI.instance().addClientboundIdentifierCustomPayloadListener(event -> {
			// Add support for incoming carpet packets too
			if (CarpetClient.CARPET_CHANNEL.equals(event.getChannel())) {
				ClientNetworkHandler.handleData(event.getData(), EssentialUtils.getPlayer());
			}
			ClientPlayNetworkHandler clientNetworkHandler = EssentialUtils.getNetworkHandler();
			for (NetworkHandler networkHandler : EssentialClient.NETWORK_HANDLERS) {
				if (networkHandler.getNetworkChannel().equals(event.getChannel())) {
					networkHandler.handlePacket(event.getData(), clientNetworkHandler);
				}
			}
		});
	}

	public static void load() { }

	public static void sendCustomPacket(ClientPlayNetworkHandler networkHandler, Identifier identifier, PacketByteBuf byteBuf) {
		MultiConnectAPI.instance().forceSendCustomPayload(networkHandler, identifier, byteBuf);
	}
}
