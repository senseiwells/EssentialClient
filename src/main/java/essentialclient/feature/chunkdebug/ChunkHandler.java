package essentialclient.feature.chunkdebug;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

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
		chunkDataSet.clear();
		for (NbtElement element : chunkList) {
			NbtCompound chunkCompound = (NbtCompound) element;
			int x = chunkCompound.getInt("x");
			int z = chunkCompound.getInt("z");
			ChunkType chunkType = ChunkType.decodeChunkType(chunkCompound.getInt("type"));
			ChunkData chunkData = new ChunkData(x, z, chunkType);
			if (chunkType != ChunkType.UNLOADED) {
				chunkDataSet.add(chunkData);
			}
		}
	}

	public static class ChunkData {
		private final int posX;
		private final int posZ;
		private final ChunkType chunkType;

		public ChunkData(int posX, int posZ, ChunkType chunkType) {
			this.posX = posX;
			this.posZ = posZ;
			this.chunkType = chunkType;
		}

		public int getPosX() {
			return posX;
		}

		public int getPosZ() {
			return posZ;
		}

		public ChunkType getChunkType() {
			return chunkType;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ChunkData otherChunk)) {
				return false;
			}
			return otherChunk.posZ == this.posZ && otherChunk.posX == this.posX;
		}
	}
}
