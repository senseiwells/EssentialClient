package me.senseiwells.essentialclient.feature.chunkdebug;

import net.minecraft.util.math.ChunkPos;

public class ChunkInfo {
	private final long position;
	private final ChunkLevel level;
	private final ChunkStatus status;
	private final ChunkTicket ticket;

	public ChunkInfo(long position, ChunkLevel level, ChunkStatus status, ChunkTicket ticket) {
		this.position = position;
		this.level = level;
		this.status = status;
		this.ticket = ticket;
	}

	public long position() {
		return this.position;
	}

	public int getX() {
		return ChunkPos.getPackedX(this.position);
	}

	public int getZ() {
		return ChunkPos.getPackedZ(this.position);
	}

	public ChunkLevel level() {
		return this.level;
	}

	public ChunkStatus status() {
		return this.status;
	}

	public boolean hasTicket() {
		return this.ticket != null;
	}

	public ChunkTicket ticket() {
		return this.ticket;
	}

	public int getColour() {
		return Colourable.getHighestPriority(this.level, this.status, this.ticket).getColour();
	}
}
