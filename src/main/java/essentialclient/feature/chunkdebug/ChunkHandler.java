package essentialclient.feature.chunkdebug;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkHandler {

	private static final Map<String, Set<ChunkData>> chunkDataMap = new HashMap<>();

	public synchronized static ChunkData[] getChunks(String world) {
		Set<ChunkData> chunkDataSet = chunkDataMap.get(world);
		if (chunkDataSet == null) {
			return new ChunkData[0];
		}
		return chunkDataSet.toArray(ChunkData[]::new);
	}

	public synchronized static void clearAllChunks() {
		chunkDataMap.forEach((s, chunkData) -> chunkData.clear());
	}

	public synchronized static void deserializeAndProcess(NbtCompound compound) {
		NbtList chunkList = compound.getList("chunks", 10);
		String world = compound.getString("world");
		if (chunkList.isEmpty()) {
			return;
		}
		chunkDataMap.putIfAbsent(world, new HashSet<>());
		Set<ChunkData> chunkDataSet = chunkDataMap.get(world);
		for (NbtElement element : chunkList) {
			NbtCompound chunkCompound = (NbtCompound) element;
			int x = chunkCompound.getInt("x");
			int z = chunkCompound.getInt("z");
			ChunkType chunkType = ChunkType.decodeChunkType(chunkCompound.getInt("t"));
			TicketType ticketType = TicketType.decodeTicketType(chunkCompound.getInt("l"));
			ChunkData chunkData = new ChunkData(x, z, chunkType, ticketType);
			chunkDataSet.remove(chunkData);
			if (chunkType != ChunkType.UNLOADED) {
				chunkDataSet.add(chunkData);
			}
		}
	}

	public static class ChunkData {
		private final ChunkPos chunkPos;
		private final ChunkType chunkType;
		private final TicketType ticketType;

		public ChunkData(int posX, int posZ, ChunkType chunkType, TicketType ticketType) {
			this.chunkPos = new ChunkPos(posX, posZ);
			this.chunkType = chunkType;
			this.ticketType = ticketType;
		}

		public int getPosX() {
			return this.chunkPos.x;
		}

		public int getPosZ() {
			return this.chunkPos.z;
		}

		public ChunkType getChunkType() {
			return this.chunkType;
		}

		public boolean hasTicketType() {
			return this.ticketType != null;
		}

		public TicketType getTicketType() {
			return this.ticketType;
		}

		@Override
		public int hashCode() {
			return this.chunkPos.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof ChunkData otherChunk) {
				return this.chunkPos.equals(otherChunk.chunkPos);
			}
			return false;
		}
	}
}
