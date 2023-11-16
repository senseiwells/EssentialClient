package me.senseiwells.essentialclient.utils.network;

import me.senseiwells.essentialclient.EssentialClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class HandlerPayload implements CustomPayload {
	private final NetworkHandler handler;
	private final Identifier id;
	private final PacketByteBuf buffer;

	public HandlerPayload(NetworkHandler handler, Identifier id, PacketByteBuf buf) {
		this.handler = handler;
		this.id = id;
		// TODO: copy buffer
		this.buffer = buf;
	}

	public void handle(ClientPlayNetworkHandler handler) {
		try {
			this.handler.handlePacket(this.buffer, handler);
			this.buffer.release();
		} catch (Exception e) {
			EssentialClient.LOGGER.error("Failed to handle incoming packet ({})!", this.id, e);
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		throw new UnsupportedOperationException("Cannot write a read only payload");
	}

	@Override
	public Identifier id() {
		return this.id;
	}
}
