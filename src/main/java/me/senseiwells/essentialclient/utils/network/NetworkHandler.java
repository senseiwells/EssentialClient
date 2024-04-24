package me.senseiwells.essentialclient.utils.network;

import me.senseiwells.essentialclient.EssentialClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static me.senseiwells.essentialclient.utils.network.NetworkUtils.DATA;
import static me.senseiwells.essentialclient.utils.network.NetworkUtils.HELLO;

public abstract class NetworkHandler {
	private final Identifier channel;
	private boolean available;
	private ClientPlayNetworkHandler networkHandler;

	public NetworkHandler(Identifier channel) {
		this.channel = channel;
		ClientPlayNetworking.registerGlobalReceiver(
			channel,
			(c, handler, buf, r) -> c.execute(() -> this.handlePacket(buf, handler))
		);
	}

	public abstract int getVersion();

	public final Identifier getNetworkChannel() {
		return this.channel;
	}

	public final void handlePacket(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
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

	protected void sendDataPacket(PacketWriter writer) {
		this.sendPacket(buf -> {
			buf.writeVarInt(DATA);
			writer.write(buf);
		});
	}

	protected void sendPacket(PacketWriter writer) {
		if (this.networkHandler != null) {
			PacketByteBuf buf = PacketByteBufs.create();
			writer.write(buf);
			this.networkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(this.getNetworkChannel(), buf));
		}
	}

	private void respondHello() {
		this.sendPacket(buf -> buf.writeVarInt(HELLO).writeString(EssentialClient.VERSION).writeVarInt(this.getVersion()));
	}

	protected abstract void processData(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler);

	protected void customData(int varInt, PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) { }

	public void onDisconnect() {
		this.available = false;
	}
}
