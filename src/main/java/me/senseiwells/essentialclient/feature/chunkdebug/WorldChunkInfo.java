package me.senseiwells.essentialclient.feature.chunkdebug;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;

public class WorldChunkInfo {
	private final Long2ObjectMap<ChunkInfo> chunks;
	private final ChunkClusters clusters;

	public WorldChunkInfo() {
		this.chunks = new Long2ObjectOpenHashMap<>();
		this.clusters = new ChunkClusters();
	}

	public ChunkInfo getInfo(ChunkPos pos) {
		return this.chunks.get(pos.toLong());
	}

	public ChunkInfo getInfo(long position) {
		return this.chunks.get(position);
	}

	public Iterable<ChunkInfo> getAllInfo() {
		return this.chunks.values();
	}

	public ChunkClusters getClusters() {
		return this.clusters;
	}

	public void clear() {
		this.chunks.clear();
		this.clusters.clear();
	}

	void remove(long position) {
		this.chunks.remove(position);
	}

	void add(ChunkInfo info) {
		this.chunks.put(info.position(), info);
	}
}
