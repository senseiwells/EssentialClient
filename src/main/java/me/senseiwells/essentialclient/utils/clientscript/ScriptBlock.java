package me.senseiwells.essentialclient.utils.clientscript;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public record ScriptBlock(BlockState state, BlockPos pos) { }
