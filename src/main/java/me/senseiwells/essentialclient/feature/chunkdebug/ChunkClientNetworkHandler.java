package me.senseiwells.essentialclient.feature.chunkdebug;

import io.netty.buffer.Unpooled;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.feature.MultiConnectSupport;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ChunkClientNetworkHandler {
	public static boolean chunkDebugAvailable = false;
	public static Identifier ESSENTIAL_CHANNEL = new Identifier("essentialclient", "chunkdebug");
	public static final int
		HELLO = 0,
		RELOAD = 15,
		DATA = 16,
		VERSION = 1_0_1;

	private ClientPlayNetworkHandler networkHandler;

	public ChunkClientNetworkHandler() { }

	public void onHello(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (packetByteBuf.readableBytes() == 0 || packetByteBuf.readVarInt() < VERSION) {
			EssentialClient.LOGGER.info("Server has out of date Chunk Debug!");
			return;
		}
		EssentialClient.LOGGER.info("Chunk Debug is available");
		this.networkHandler = networkHandler;
		chunkDebugAvailable = true;
		this.respondHello();
	}

	private void respondHello() {
		if (this.networkHandler != null) {
			MultiConnectSupport.sendCustomPacket(
				this.networkHandler, ESSENTIAL_CHANNEL,
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(HELLO).writeString(EssentialClient.VERSION).writeVarInt(VERSION)
			);
		}
	}

	public void handlePacket(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (packetByteBuf != null) {
			switch (packetByteBuf.readVarInt()) {
				case HELLO -> this.onHello(packetByteBuf, networkHandler);
				case DATA -> this.processPacket(packetByteBuf);
			}
		}
	}

	private void processPacket(PacketByteBuf packetByteBuf) {
		int size = packetByteBuf.readVarInt();
		long[] chunkPositions = packetByteBuf.readLongArray(new long[size]);
		byte[] levelTypes = packetByteBuf.readByteArray(size);
		byte[] ticketTypes = packetByteBuf.readByteArray(size);
		String world = packetByteBuf.readString();

		ChunkHandler.deserializeAndProcess(world, chunkPositions, levelTypes, ticketTypes);
	}

	public void requestChunkData() {
		this.requestChunkData(new Identifier("minecraft:dummy"));
	}

	public void requestChunkData(String worldName) {
		this.requestChunkData(new Identifier(worldName));
	}

	private void requestChunkData(Identifier worldIdentifier) {
		if (this.networkHandler != null) {
			MultiConnectSupport.sendCustomPacket(
				this.networkHandler, ESSENTIAL_CHANNEL,
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(DATA).writeIdentifier(worldIdentifier)
			);
		}
	}

	protected void requestServerRefresh() {
		if (this.networkHandler != null) {
			MultiConnectSupport.sendCustomPacket(
				this.networkHandler, ESSENTIAL_CHANNEL,
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(RELOAD)
			);
		}
	}
}
