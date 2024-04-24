package me.senseiwells.essentialclient.feature.chunkdebug;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import me.senseiwells.essentialclient.utils.network.NetworkUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ChunkClientNetworkHandler extends NetworkHandler {
	public static final int VERSION = 1_0_4;

	public ChunkClientNetworkHandler() {
		super(new Identifier("essentialclient", "chunkdebug"));
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
		RegistryKey<World> world = packetByteBuf.readRegistryKey(RegistryKeys.WORLD);

		ChunkHandler.deserializeAndProcess(world, chunkPositions, levelTypes, statusTypes, ticketTypes);
	}

	public void removeChunkData() {
		this.sendPacket(buf -> buf.writeVarInt(NetworkUtils.STOP));
	}

	public void requestChunkData(RegistryKey<World> world) {
		this.sendDataPacket(buf -> buf.writeRegistryKey(world));
	}

	protected void requestServerRefresh() {
		this.sendPacket(buf -> buf.writeVarInt(NetworkUtils.RELOAD));
	}
}
