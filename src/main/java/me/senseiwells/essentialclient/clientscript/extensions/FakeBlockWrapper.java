package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.clientscript.values.BlockValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@ArucasClass(name = "FakeBlock")
public class FakeBlockWrapper implements IArucasWrappedClass, Shape.Tiltable, Shape.Directional {
	private static final Map<UUID, Set<FakeBlockWrapper>> BLOCKS_TO_RENDER = new LinkedHashMap<>(0);

	public BlockPos blockPos;
	public BlockState blockState;
	public BlockEntity blockEntity;
	public Direction direction;

	private float xTilt;
	private float yTilt;
	private float zTilt;

	private void setBlockState(BlockState state) {
		this.blockState = state;
		if (this.blockPos != null && state instanceof BlockEntityProvider provider) {
			this.blockEntity = provider.createBlockEntity(this.blockPos, this.blockState);
		}
	}

	private void setBlockPos(BlockPos pos) {
		this.blockPos = pos;
		if (pos != null && this.blockState instanceof BlockEntityProvider provider) {
			this.blockEntity = provider.createBlockEntity(pos, this.blockState);
		}
	}

	@Override
	public float getXTilt() {
		return this.xTilt;
	}

	@Override
	public float getYTilt() {
		return this.yTilt;
	}

	@Override
	public float getZTilt() {
		return this.zTilt;
	}

	@Override
	public void setXTilt(float xTilt) {
		this.xTilt = xTilt;
	}

	@Override
	public void setYTilt(float yTilt) {
		this.yTilt = yTilt;
	}

	@Override
	public void setZTilt(float zTilt) {
		this.zTilt = zTilt;
	}

	@Override
	public Direction getDirection() {
		return this.direction;
	}

	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@ArucasConstructor
	public void construct(Context context, BlockValue blockValue, PosValue posValue) {
		this.setBlockState(blockValue.value);
		this.setBlockPos(posValue.toBlockPos());
	}

	@ArucasFunction
	public void setBlock(Context context, BlockValue blockValue) {
		this.setBlockState(blockValue.value);
	}

	@ArucasFunction
	public void setPos(Context context, PosValue posValue) {
		this.setBlockPos(posValue.toBlockPos());
	}

	@ArucasFunction
	public Value<?> getBlock(Context context) {
		return new BlockValue(this.blockState, this.blockPos);
	}

	@ArucasFunction
	public Value<?> getPos(Context context) {
		return new PosValue(this.blockPos);
	}

	@ArucasFunction
	public void render(Context context) {
		addFakeBlock(context, this);
	}

	@ArucasFunction
	public void stopRendering(Context context) {
		removeFakeBlock(context, this);
	}

	public synchronized static void addFakeBlock(Context context, FakeBlockWrapper blockWrapper) {
		Set<FakeBlockWrapper> blockWrappers = BLOCKS_TO_RENDER.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> BLOCKS_TO_RENDER.remove(id));
			return new LinkedHashSet<>();
		});
		blockWrappers.add(blockWrapper);
	}

	public synchronized static void removeFakeBlock(Context context, FakeBlockWrapper blockWrapper) {
		Set<FakeBlockWrapper> fakeBlocks = BLOCKS_TO_RENDER.get(context.getContextId());
		if (fakeBlocks != null) {
			fakeBlocks.remove(blockWrapper);
		}
	}

	public synchronized static Stream<FakeBlockWrapper> getAllBlocksToRender() {
		return BLOCKS_TO_RENDER.values().stream().flatMap(Collection::stream).filter(fake -> fake.blockState != null && fake.blockPos != null);
	}
}
