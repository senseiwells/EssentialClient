package me.senseiwells.essentialclient.feature.chunkdebug;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

public class ChunkClientNetworkHandler extends NetworkHandler {
	public static final int VERSION = 1_0_6;

	private final Map<RegistryKey<World>, WorldChunkInfo> chunkInfoMap = new HashMap<>();

	public ChunkClientNetworkHandler() {

	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		ChunkGrid.instance = null;
		this.clearAllChunks();
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

	public Optional<ChunkInfo> getChunk(RegistryKey<World> world, ChunkPos pos) {
		WorldChunkInfo chunks = this.chunkInfoMap.get(world);
		if (chunks == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(chunks.getInfo(pos.toLong()));
	}

	public Iterable<ChunkInfo> getChunks(RegistryKey<World> world) {
		WorldChunkInfo chunks = this.chunkInfoMap.get(world);
		if (chunks == null) {
			return List.of();
		}
		return chunks.getAllInfo();
	}

	public void clearAllChunks() {
		this.chunkInfoMap.forEach((s, chunkData) -> chunkData.clear());
	}

	public Optional<ChunkClusters> getChunkCluster(RegistryKey<World> world) {
		WorldChunkInfo chunks = this.chunkInfoMap.get(world);
		return chunks == null ? Optional.empty() : Optional.of(chunks.getClusters());
	}

	public void updateChunks(RegistryKey<World> world, Iterable<ChunkInfo> infos) {
		WorldChunkInfo chunks = this.chunkInfoMap.computeIfAbsent(world, k -> new WorldChunkInfo());
		ChunkClusters clusters = chunks.getClusters();

		for (ChunkInfo info : infos) {
			long position = info.position();
			chunks.remove(position);
			if (info.level() != ChunkLevel.UNLOADED) {
				clusters.add(position);
				chunks.add(info);
				continue;
			}
			clusters.remove(position);
			if (ClientRules.CHUNK_DEBUG_SHOW_UNLOADED_CHUNKS.getValue()) {
				chunks.add(info);
			}
		}
	}

	public void removeChunkData() {
		this.sendPayload(StopChunkInfoPayload::new);
	}

	public void requestChunkData(RegistryKey<World> world) {
		this.clearAllChunks();
		this.sendPayload(() -> new SetChunkDimensionPayload(world));
	}

	protected void requestServerRefresh() {
		this.clearAllChunks();
		this.sendPayload(ReloadChunkDebugPayload::new);
	}

	@Override
	public void registerCustomPayloads() {
		PayloadTypeRegistry.playC2S().register(ChunkDebugHelloPayload.ID, ChunkDebugHelloPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ReloadChunkDebugPayload.ID, ReloadChunkDebugPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(StopChunkInfoPayload.ID, StopChunkInfoPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetChunkDimensionPayload.ID, SetChunkDimensionPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(ChunkDebugHelloPayload.ID, ChunkDebugHelloPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(BatchedChunkInfoPayload.ID, BatchedChunkInfoPayload.CODEC);

		ClientPlayNetworking.registerGlobalReceiver(ChunkDebugHelloPayload.ID, (payload, context) -> {
			this.onHello(context.player().networkHandler, payload);
		});
		ClientPlayNetworking.registerGlobalReceiver(BatchedChunkInfoPayload.ID, (payload, context) -> {
			this.updateChunks(payload.world, payload.infos);
		});
	}

	@Override
	protected HelloPayload createHelloPayload(String brand, int version) {
		return new ChunkDebugHelloPayload(brand, version);
	}

	private static class ChunkDebugHelloPayload extends HelloPayload {
		public static final Id<ChunkDebugHelloPayload> ID = CustomPayload.id("chunk_debug:hello");
		public static final PacketCodec<PacketByteBuf, ChunkDebugHelloPayload> CODEC = PacketCodec.of(
			ChunkDebugHelloPayload::write,
			ChunkDebugHelloPayload::new
		);

		public ChunkDebugHelloPayload(String brand, int version) {
			super(brand, version);
		}

		public ChunkDebugHelloPayload(PacketByteBuf buf) {
			super(buf.readString(), buf.readInt());
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record BatchedChunkInfoPayload(
		RegistryKey<World> world,
		List<ChunkInfo> infos
	) implements CustomPayload {
		public static final Id<BatchedChunkInfoPayload> ID = CustomPayload.id("chunk_debug:batched_chunk_info");
		public static final PacketCodec<PacketByteBuf, BatchedChunkInfoPayload> CODEC = PacketCodec.of(
			BatchedChunkInfoPayload::write,
			BatchedChunkInfoPayload::read
		);

		public void write(PacketByteBuf buf) {
			int size = this.infos.size();
			long[] chunkPositions = new long[size];
			byte[] levels = new byte[size];
			byte[] statuses = new byte[size];
			byte[] tickets = new byte[size];

			int i = 0;
			for (ChunkInfo info : this.infos) {
				chunkPositions[i] = info.position();
				levels[i] = (byte) info.level().ordinal();
				statuses[i] = (byte) info.status().ordinal();
				tickets[i] = (byte) info.ticket().ordinal();
				i++;
			}

			buf.writeVarInt(size);
			buf.writeLongArray(chunkPositions);
			buf.writeByteArray(levels);
			buf.writeByteArray(statuses);
			buf.writeByteArray(tickets);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}

		public static BatchedChunkInfoPayload read(PacketByteBuf buf) {
			int size = buf.readVarInt();
			long[] chunkPositions = buf.readLongArray(new long[size]);
			byte[] levelTypes = buf.readByteArray(size);
			byte[] statusTypes = buf.readByteArray(size);
			byte[] ticketTypes = buf.readByteArray(size);
			RegistryKey<World> world = buf.readRegistryKey(RegistryKeys.WORLD);

			List<ChunkInfo> infos = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				long position = chunkPositions[i];
				ChunkLevel chunkType = ChunkLevel.decodeChunkType(levelTypes[i]);
				ChunkTicket ticketType = ChunkTicket.decodeTicketType(ticketTypes[i]);
				ChunkStatus status = ChunkStatus.decodeChunkStatus(statusTypes[i]);
				infos.add(new ChunkInfo(position, chunkType, status, ticketType));
			}
			return new BatchedChunkInfoPayload(world, infos);
		}
	}

	private record StopChunkInfoPayload() implements CustomPayload {
		public static final Id<StopChunkInfoPayload> ID = CustomPayload.id("chunk_debug:stop");
		public static final PacketCodec<PacketByteBuf, StopChunkInfoPayload> CODEC = PacketCodec.unit(new StopChunkInfoPayload());

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record SetChunkDimensionPayload(RegistryKey<World> dimension) implements CustomPayload {
		public static final Id<SetChunkDimensionPayload> ID = CustomPayload.id("chunk_debug:set_dimension");
		public static final PacketCodec<PacketByteBuf, SetChunkDimensionPayload> CODEC = PacketCodec.of(
			SetChunkDimensionPayload::write,
			SetChunkDimensionPayload::new
		);

		public SetChunkDimensionPayload(PacketByteBuf buf) {
			this(buf.readRegistryKey(RegistryKeys.WORLD));
		}

		public void write(PacketByteBuf buf) {
			buf.writeRegistryKey(this.dimension);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record ReloadChunkDebugPayload() implements CustomPayload {
		public static final Id<ReloadChunkDebugPayload> ID = CustomPayload.id("chunk_debug:reload");
		public static final PacketCodec<PacketByteBuf, ReloadChunkDebugPayload> CODEC = PacketCodec.unit(new ReloadChunkDebugPayload());

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
