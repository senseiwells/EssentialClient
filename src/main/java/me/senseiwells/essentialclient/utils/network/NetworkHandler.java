package me.senseiwells.essentialclient.utils.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

import java.util.function.Supplier;

public abstract class NetworkHandler {
	private boolean available;
	private ClientPlayNetworkHandler networkHandler;

	public NetworkHandler() {

	}

	public final ClientPlayNetworkHandler getNetworkHandler() {
		return this.networkHandler;
	}

	public final void sendPayload(Supplier<CustomPayload> supplier) {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(supplier.get()));
		}
	}

	public final boolean isAvailable() {
		return this.available;
	}

	public final void onHello(ClientPlayNetworkHandler handler, HelloPayload payload) {
		if (payload.version < this.getVersion()) {
			this.onHelloFail();
			return;
		}
		this.onHelloSuccess();
		this.available = true;
		this.networkHandler = handler;
		this.sendPayload(() -> this.createHelloPayload("essential_client", this.getVersion()));
	}

	public final void onHelloSinglePlayer() {
		this.onHelloSuccess();
		this.available = true;
	}

	public void onDisconnect() {
		this.available = false;
	}

	public abstract int getVersion();

	public abstract void registerCustomPayloads();

	protected abstract CustomPayload createHelloPayload(String brand, int version);

	protected void onHelloSuccess() { }

	protected void onHelloFail() { }

	public static abstract class HelloPayload implements CustomPayload {
		public final String brand;
		public final int version;

		public HelloPayload(String brand, int version) {
			this.brand = brand;
			this.version = version;
		}

		public HelloPayload(PacketByteBuf buf) {
			this(buf.readString(), buf.readInt());
		}

		public void write(PacketByteBuf buf) {
			buf.writeString(this.brand);
			buf.writeInt(this.version);
		}
	}
}
