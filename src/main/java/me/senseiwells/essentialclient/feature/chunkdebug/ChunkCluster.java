package me.senseiwells.essentialclient.feature.chunkdebug;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.ArrayList;
import java.util.List;

public class ChunkCluster {
	private final List<LongSet> groups;
	private final LongSet chunks;

	public ChunkCluster() {
		this.groups = new ArrayList<>();
		this.chunks = new LongOpenHashSet();
	}

	public void addChunk(long position) {
		if (!this.chunks.add(position)) {
			return;
		}

		long[] directions = getOffsets(position);
		long up = directions[0];
		long down = directions[1];
		long right = directions[2];
		long left = directions[3];

		List<LongSet> nearby = new ArrayList<>(4);
		for (LongSet positions : this.groups) {
			if (positions.contains(up) || positions.contains(down) || positions.contains(right) || positions.contains(left)) {
				nearby.add(positions);
			}
		}

		if (nearby.isEmpty()) {
			LongSet set = new LongOpenHashSet();
			this.groups.add(set);
			set.add(position);
			return;
		}
		if (nearby.size() == 1) {
			nearby.get(0).add(position);
			return;
		}

		// Our chunk was between two borders - find the largest
		LongSet largest = nearby.get(0);
		for (int i = 1; i < nearby.size(); i++) {
			LongSet next = nearby.get(i);
			if (largest.size() < next.size()) {
				largest = next;
			}
		}
		largest.add(position);

		// Merge others into the largest
		for (LongSet set : nearby) {
			if (set != largest) {
				largest.addAll(set);
				this.groups.remove(set);
			}
		}
	}

	public void removeChunk(long position) {
		if (!this.chunks.remove(position)) {
			return;
		}

		for (LongSet group : this.groups) {
			if (group.remove(position)) {
				// When we remove a group it may split that group up
				this.groups.remove(group);
				if (group.isEmpty()) {
					return;
				}
				// We just completely recalculate the group
				this.groups.addAll(search(position, group));
				return;
			}
		}
	}

	public void clear() {
		this.chunks.clear();
		this.groups.clear();
	}

	public int count() {
		return this.groups.size();
	}

	public List<LongSet> getGroups() {
		return this.groups;
	}

	// We search around a position splitting into groups
	private static List<LongSet> search(long origin, LongSet originGroup) {
		long[] directions = getOffsets(origin);
		List<LongSet> groups = new ArrayList<>(4);

		for (long direction : directions) {
			if (!originGroup.contains(direction)) {
				continue;
			}
			boolean grouped = false;
			for (LongSet group : groups) {
				if (group.contains(direction)) {
					grouped = true;
					break;
				}
			}
			if (!grouped) {
				LongSet found = new LongOpenHashSet();
				searchFrom(direction, originGroup, new LongOpenHashSet(), found);
				if (!found.isEmpty()) {
					groups.add(found);
				}
			}
		}
		return groups;
	}

	private static void searchFrom(long position, LongSet group, LongSet checked, LongSet found) {
		long[] directions = getOffsets(position);

		for (long direction : directions) {
			if (checked.add(direction) && group.contains(direction)) {
				found.add(direction);
				searchFrom(direction, group, checked, found);
			}
		}
	}

	private static long[] getOffsets(long position) {
		int x = (int) position;
		int z = (int) (position >> 32);
		return new long[]{pack(x + 1, z), pack(x - 1, z), pack(x, z + 1), pack(x, z - 1)};
	}

	private static long pack(int x, int z) {
		return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
	}
}
