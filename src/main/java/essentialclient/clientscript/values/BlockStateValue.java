package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.block.BlockState;

public class BlockStateValue extends Value<BlockState> {
	public BlockStateValue(BlockState block) {
		super(block);
	}

	@Override
	public Value<?> copy() {
		return new BlockStateValue(this.value);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof BlockStateValue otherValue)) {
			return false;
		}
		return this.value.getBlock().equals(otherValue.value.getBlock());
	}
}
