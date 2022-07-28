package me.senseiwells.essentialclient.feature.chunkdebug;

import io.netty.buffer.Unpooled;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import static me.senseiwells.essentialclient.utils.network.NetworkUtils.DATA;
import static me.senseiwells.essentialclient.utils.network.NetworkUtils.RELOAD;

public class ChunkClientNetworkHandler extends NetworkHandler {
	public static final Identifier CHUNK_DEBUG_CHANNEL = new Identifier("essentialclient", "chunkdebug");
	public static final int VERSION = 1_0_3;

	public ChunkClientNetworkHandler() { }

	@Override
	public Identifier getNetworkChannel() {
		return CHUNK_DEBUG_CHANNEL;
	}

	@Override
	public int getVersion() {
		return VERSION;
	}

	@Override
	protected void onHelloSuccess() {
		EssentialClient.LOGGER.info("Chunk Debug is available");
	}

	@Override
	protected void onHelloFail() {
		EssentialClient.LOGGER.info("Server has out of date Chunk Debug!");
	}

	@Override
	protected void processData(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		int size = packetByteBuf.readVarInt();
		long[] chunkPositions = packetByteBuf.readLongArray(new long[size]);
		byte[] levelTypes = packetByteBuf.readByteArray(size);
		byte[] statusTypes = packetByteBuf.readByteArray(size);
		byte[] ticketTypes = packetByteBuf.readByteArray(size);
		String world = packetByteBuf.readString();

		ChunkHandler.deserializeAndProcess(world, chunkPositions, levelTypes, statusTypes, ticketTypes);
	}

	public void requestChunkData() {
		this.requestChunkData(new Identifier("minecraft:dummy"));
	}

	public void requestChunkData(String worldName) {
		this.requestChunkData(new Identifier(worldName));
	}

	private void requestChunkData(Identifier worldIdentifier) {
		if (this.getNetworkHandler() != null) {
			this.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(
				CHUNK_DEBUG_CHANNEL,
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(DATA).writeIdentifier(worldIdentifier)
			));
		}
	}

	protected void requestServerRefresh() {
		if (this.getNetworkHandler() != null) {
			this.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(
				CHUNK_DEBUG_CHANNEL,
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(RELOAD)
			));
		}
	}
}
