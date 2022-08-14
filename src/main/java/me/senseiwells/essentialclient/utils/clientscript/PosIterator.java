package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.clientscript.values.BlockValue;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

public class PosIterator implements Iterator<Value> {
	Iterator<BlockPos> posIterable;

	public PosIterator(Iterator<BlockPos> posIterator) {
		this.posIterable = posIterator;
	}

	@Override
	public boolean hasNext() {
		return this.posIterable.hasNext();
	}

	@Override
	public Value next() {
		return new PosValue(this.posIterable.next());
	}

	public static class Block extends PosIterator {
		private final ClientWorld world;

		public Block(ClientWorld world, Iterator<BlockPos> posIterator) {
			super(posIterator);
			this.world = world;
		}

		@Override
		public Value next() {
			BlockPos pos = this.posIterable.next();
			BlockState state = this.world.getBlockState(pos);
			return new BlockValue(state, pos);
		}
	}
}
