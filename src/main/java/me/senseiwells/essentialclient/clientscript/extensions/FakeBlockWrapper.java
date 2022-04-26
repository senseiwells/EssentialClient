package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.essentialclient.clientscript.values.BlockValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@ArucasClass(name = "FakeBlock")
public class FakeBlockWrapper implements IArucasWrappedClass {
	private static final Map<UUID, Set<FakeBlockWrapper>> BLOCKS_TO_RENDER = new LinkedHashMap<>(0);

	public BlockPos blockPos;
	public BlockState blockState;
	public BlockEntity blockEntity;
	public Direction direction;

	public float xTilt;
	public float yTilt;
	public float zTilt;

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
	public void setDirection(Context context, StringValue stringValue) {
		this.direction =  Direction.byName(stringValue.value.toLowerCase());
	}

	@ArucasFunction
	public void setTilt(Context context, NumberValue xTilt, NumberValue yTilt, NumberValue zTilt) {
		this.xTilt = xTilt.value.floatValue();
		this.yTilt = yTilt.value.floatValue();
		this.zTilt = zTilt.value.floatValue();
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
	public Value<?> getDirection(Context context) {
		return  this.direction == null ? NullValue.NULL : StringValue.of(this.direction.getName());
	}

	@ArucasFunction
	public Value<?> getTiltX(Context context) {
		return NumberValue.of(this.xTilt);
	}

	@ArucasFunction
	public Value<?> getTiltY(Context context) {
		return NumberValue.of(this.yTilt);
	}

	@ArucasFunction
	public Value<?> getTiltZ(Context context) {
		return NumberValue.of(this.zTilt);
	}

	@ArucasFunction
	public void render(Context context) {
		addFakeBlock(context, this);
	}

	@ArucasFunction
	public BooleanValue stopRendering(Context context) {
		return BooleanValue.of(removeFakeBlock(context, this));
	}

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

	@SuppressWarnings("UnusedReturnValue")
	public synchronized static boolean addFakeBlock(Context context, FakeBlockWrapper blockWrapper) {
		Set<FakeBlockWrapper> blockWrappers = BLOCKS_TO_RENDER.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> BLOCKS_TO_RENDER.remove(id));
			return new LinkedHashSet<>();
		});
		return blockWrappers.add(blockWrapper);
	}

	public synchronized static boolean removeFakeBlock(Context context, FakeBlockWrapper blockWrapper) {
		Set<FakeBlockWrapper> fakeBlocks = BLOCKS_TO_RENDER.get(context.getContextId());
		return fakeBlocks != null && fakeBlocks.remove(blockWrapper);
	}

	public synchronized static Stream<FakeBlockWrapper> getAllBlocksToRender() {
		return BLOCKS_TO_RENDER.values().stream().flatMap(Collection::stream).filter(fake -> fake.blockState != null && fake.blockPos != null);
	}
}
