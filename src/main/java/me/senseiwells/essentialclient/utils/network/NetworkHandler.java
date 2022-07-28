package me.senseiwells.essentialclient.utils.network;

import io.netty.buffer.Unpooled;
import me.senseiwells.essentialclient.EssentialClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import static me.senseiwells.essentialclient.utils.network.NetworkUtils.DATA;
import static me.senseiwells.essentialclient.utils.network.NetworkUtils.HELLO;

public abstract class NetworkHandler {
	private boolean available;
	private ClientPlayNetworkHandler networkHandler;

	public abstract Identifier getNetworkChannel();

	public abstract int getVersion();

	public void handlePacket(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (packetByteBuf != null) {
			int varInt = packetByteBuf.readVarInt();
			switch (varInt) {
				case HELLO -> this.onHello(packetByteBuf, networkHandler);
				case DATA -> this.processData(packetByteBuf, networkHandler);
				default -> this.customData(varInt, packetByteBuf, networkHandler);
			}
		}
	}

	public final ClientPlayNetworkHandler getNetworkHandler() {
		return this.networkHandler;
	}

	public final boolean isAvailable() {
		return this.available;
	}

	public final void onHello(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (packetByteBuf.readableBytes() == 0 || packetByteBuf.readVarInt() < this.getVersion()) {
			this.onHelloFail();
			return;
		}
		this.onHelloSuccess();
		this.available = true;
		this.networkHandler = networkHandler;
		this.respondHello();
	}

	public final void onHelloSinglePlayer() {
		this.onHelloSuccess();
		this.available = true;
	}

	protected void onHelloSuccess() { }

	protected void onHelloFail() { }

	private void respondHello() {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(new CustomPayloadC2SPacket(
				this.getNetworkChannel(),
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(HELLO).writeString(EssentialClient.VERSION).writeVarInt(this.getVersion())
			));
		}
	}

	protected abstract void processData(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler);

	protected void customData(int varInt, PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) { }

	public void onDisconnect() {
		this.available = false;
	}
}
