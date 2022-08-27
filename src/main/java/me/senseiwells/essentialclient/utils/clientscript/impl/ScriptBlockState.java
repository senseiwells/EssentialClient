package me.senseiwells.essentialclient.utils.clientscript.impl;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ScriptBlockState implements ScriptMaterial {
	public final BlockState state;
	public final BlockPos pos;

	public ScriptBlockState(BlockState state, BlockPos pos) {
		this.state = state;
		this.pos = pos == null ? null : pos.toImmutable();
	}

	@Override
	public Identifier getId() {
		return Registry.BLOCK.getId(this.asBlock());
	}

	@Override
	public String getTranslationKey() {
		return this.asBlock().getTranslationKey();
	}

	@Override
	public Block asBlock() {
		return this.state.getBlock();
	}

	@Override
	public BlockState asBlockState() {
		return this.state;
	}

	@Override
	public Object asDefault() {
		return this.state;
	}

	@Override
	public String asString() {
		return this.state.toString();
	}
}
