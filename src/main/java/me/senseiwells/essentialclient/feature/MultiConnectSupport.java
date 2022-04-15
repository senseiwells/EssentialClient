package me.senseiwells.essentialclient.feature;

import carpet.network.CarpetClient;
import carpet.network.ClientNetworkHandler;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class MultiConnectSupport {
	static {
		MultiConnectAPI.instance().addClientboundIdentifierCustomPayloadListener(event -> {
			if (ChunkClientNetworkHandler.ESSENTIAL_CHANNEL.equals(event.getChannel())) {
				EssentialClient.CHUNK_NET_HANDLER.handlePacket(event.getData(), EssentialUtils.getNetworkHandler());
			}
			if (CarpetClient.CARPET_CHANNEL.equals(event.getChannel())) {
				ClientNetworkHandler.handleData(event.getData(), EssentialUtils.getPlayer());
			}
		});
	}

	public static void load() { }

	public static void sendCustomPacket(ClientPlayNetworkHandler networkHandler, Identifier identifier, PacketByteBuf byteBuf) {
		MultiConnectAPI.instance().forceSendCustomPayload(networkHandler, identifier, byteBuf);
	}
}
