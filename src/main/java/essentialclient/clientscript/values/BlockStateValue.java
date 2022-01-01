package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockStateValue extends Value<BlockState> {
	public final BlockPos blockPos;

	public BlockStateValue(BlockState block, BlockPos pos) {
		super(block);
		this.blockPos = pos;
	}

	public BlockStateValue(BlockState block) {
		this(block, null);
	}

	public Value<?> getBlockX() {
		return this.blockPos == null ? NullValue.NULL : new NumberValue(this.blockPos.getX());
	}

	public Value<?> getBlockY() {
		return this.blockPos == null ? NullValue.NULL : new NumberValue(this.blockPos.getY());
	}

	public Value<?> getBlockZ() {
		return this.blockPos == null ? NullValue.NULL : new NumberValue(this.blockPos.getZ());
	}

	@Override
	public Value<BlockState> copy() {
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof BlockStateValue otherValue)) {
			return false;
		}
		return this.value.getBlock().equals(otherValue.value.getBlock());
	}

	@Override
	public String toString() {
		return "Block{id=%s}".formatted(Registry.BLOCK.getId(this.value.getBlock()).getPath());
	}
}
