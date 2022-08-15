package me.senseiwells.essentialclient.utils.clientscript.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ScriptPos {
	private final Vec3d pos;
	private BlockPos blockPos;

	public ScriptPos(Vec3d pos) {
		this.pos = pos;
	}

	public ScriptPos(BlockPos blockPos) {
		this(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
		this.blockPos = blockPos.toImmutable();
	}

	public ScriptPos(double x, double y, double z) {
		this(new Vec3d(x, y, z));
	}

	public Vec3d getVec3d() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		if (this.blockPos == null) {
			this.blockPos = new BlockPos(this.pos);
		}
		return this.blockPos;
	}

	public double getX() {
		return this.pos.getX();
	}

	public double getY() {
		return this.pos.getY();
	}

	public double getZ() {
		return this.pos.getZ();
	}
}
