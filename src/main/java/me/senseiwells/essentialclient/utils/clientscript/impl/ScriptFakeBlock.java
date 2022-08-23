package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.core.Interpreter;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ScriptFakeBlock extends ScriptShape {
	private static final Map<UUID, Set<ScriptShape>> REGULAR_BLOCKS = new ConcurrentHashMap<>();
	private static final Map<UUID, Set<ScriptShape>> IGNORE_DEPTH_BLOCKS = new ConcurrentHashMap<>();

	private Vec3d position;
	private BlockState state;
	private Direction direction;
	private boolean cull;

	public ScriptFakeBlock(Interpreter interpreter, Vec3d position, BlockState state) {
		super(interpreter);
		this.position = position;
		this.state = state;
		this.direction = Direction.NORTH;
		this.cull = true;
	}

	public void setPosition(Vec3d position) {
		this.position = position;
	}

	public void setState(BlockState state) {
		this.state = state;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setCull(boolean cull) {
		this.cull = cull;
	}

	public Vec3d getPosition() {
		return this.position;
	}

	public BlockState getState() {
		return this.state;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public boolean shouldCull() {
		return this.cull;
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getRegularDepthMap() {
		return REGULAR_BLOCKS;
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getIgnoreDepthMap() {
		return IGNORE_DEPTH_BLOCKS;
	}

	public static void forEachRegular(Consumer<ScriptFakeBlock> consumer) {
		REGULAR_BLOCKS.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptFakeBlock) s)));
	}

	public static void forEachIgnoreDepth(Consumer<ScriptFakeBlock> consumer) {
		IGNORE_DEPTH_BLOCKS.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptFakeBlock) s)));
	}
}
