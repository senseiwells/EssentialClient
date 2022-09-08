package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.utils.impl.ArucasIterator;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.function.Function;

public class PosIterator implements ArucasIterator {
	final Iterator<BlockPos> blockPosIterator;
	final Function<Object, ClassInstance> converter;

	public PosIterator(Iterator<BlockPos> posIterator, Function<Object, ClassInstance> converter) {
		this.blockPosIterator = posIterator;
		this.converter = converter;
	}

	@Override
	public boolean hasNext() {
		return this.blockPosIterator.hasNext();
	}

	@Override
	public ClassInstance next() {
		return this.converter.apply(this.blockPosIterator.next());
	}

	public static class Block extends PosIterator {
		private final World world;

		public Block(World world, Iterator<BlockPos> posIterator, Function<Object, ClassInstance> converter) {
			super(posIterator, converter);
			this.world = world;
		}

		@Override
		public ClassInstance next() {
			BlockPos pos = this.blockPosIterator.next();
			BlockState state = this.world.getBlockState(pos);
			return this.converter.apply(new ScriptBlockState(state, pos));
		}
	}
}
