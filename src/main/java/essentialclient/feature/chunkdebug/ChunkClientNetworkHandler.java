package essentialclient.feature.chunkdebug;

import essentialclient.EssentialClient;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ChunkClientNetworkHandler {
	public static boolean chunkDebugAvailable = false;
	public static Identifier ESSENTIAL_CHANNEL = new Identifier("essentialclient", "chunkdebug");
	public static final int
		HELLO = 0,
		DATA = 16;

	private ClientPlayNetworkHandler networkHandler;

	public ChunkClientNetworkHandler() { }

	public void onHello(ClientPlayNetworkHandler networkHandler) {
		EssentialClient.LOGGER.info("Chunk Debug is available");
		this.networkHandler = networkHandler;
		chunkDebugAvailable = true;
		this.respondHello();
	}

	private void respondHello() {
		this.networkHandler.sendPacket(new CustomPayloadC2SPacket(
			ESSENTIAL_CHANNEL,
			new PacketByteBuf(Unpooled.buffer()).writeVarInt(HELLO).writeString(EssentialClient.VERSION)
		));
	}

	public void handlePacket(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (packetByteBuf != null) {
			switch (packetByteBuf.readVarInt()) {
				case HELLO -> this.onHello(networkHandler);
				case DATA -> this.processPacket(packetByteBuf);
			}
		}
	}

	private void processPacket(PacketByteBuf packetByteBuf) {
		NbtCompound compound = packetByteBuf.readNbt();
		if (compound != null) {
			ChunkHandler.deserializeAndProcess(compound);
		}
	}

	public void requestChunkData() {
		this.requestChunkData(new Identifier("minecraft:dummy"));
	}

	public void requestChunkData(World world) {
		if (world == null) {
			this.requestChunkData();
			return;
		}
		this.requestChunkData(world.getRegistryKey().getValue());
	}

	public void requestChunkData(String worldName) {
		this.requestChunkData(new Identifier(worldName));
	}

	private void requestChunkData(Identifier worldIdentifier) {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(new CustomPayloadC2SPacket(
				ESSENTIAL_CHANNEL,
				new PacketByteBuf(Unpooled.buffer()).writeVarInt(DATA).writeIdentifier(worldIdentifier)
			));
		}
	}
}
