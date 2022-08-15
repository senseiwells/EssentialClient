package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.utils.clientscript.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Stream;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@SuppressWarnings("unused")
@ClassDoc(
	name = FAKE_BLOCK,
	desc = "This class can be used to create fake blocks which can be rendered in the world.",
	importPath = "Minecraft"
)
@ArucasClass(name = FAKE_BLOCK)
public class FakeBlockWrapper extends Shape implements Shape.Tiltable, Shape.Directional, Shape.Scalable {
	private static final Map<UUID, Set<FakeBlockWrapper>> NORMAL_BLOCKS = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<FakeBlockWrapper>> THROUGH_BLOCKS = new LinkedHashMap<>(0);

	public BlockPos blockPos;
	public BlockState blockState;
	public Direction direction = Direction.NORTH;
	public boolean cull = true;

	private float xTilt;
	private float yTilt;
	private float zTilt;

	private float xScale;
	private float yScale;
	private float zScale;

	private void setBlockState(BlockState state) {
		this.blockState = state;
	}

	private void setBlockPos(BlockPos pos) {
		this.blockPos = pos;
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
	public float getXScale() {
		return this.xScale;
	}

	@Override
	public float getYScale() {
		return this.yScale;
	}

	@Override
	public float getZScale() {
		return this.zScale;
	}

	@Override
	public void setXScale(float xScale) {
		this.xScale = xScale;
	}

	@Override
	public void setYScale(float yScale) {
		this.yScale = yScale;
	}

	@Override
	public void setZScale(float zScale) {
		this.zScale = zScale;
	}

	@Override
	public Direction getDirection() {
		return this.direction;
	}

	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void render() {
		super.render();
		addFakeBlock(this);
	}

	@Override
	public void stopRendering() {
		super.stopRendering();
		removeFakeBlock(this);
	}

	@Override
	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		if (this.shouldRenderThroughBlocks() ^ renderThroughBlocks) {
			if (this.isRendering()) {
				removeFakeBlock(this);
				super.setRenderThroughBlocks(renderThroughBlocks);
				addFakeBlock(this);
				return;
			}
			super.setRenderThroughBlocks(renderThroughBlocks);
		}
	}

	@ConstructorDoc(
		desc = "Creates a fake block with the given block and position",
		params = {
			BLOCK, "block", "The block to use",
			POS, "pos", "The position of the block"
		},
		examples = "new FakeBlock(Material.BEDROCK.asBlock(), new Pos(0, 0, 0));"
	)
	@ArucasConstructor
	public void construct(Context context, BlockValue blockValue, PosValue posValue) {
		this.setCreatedContext(context.createBranch());
		this.setBlockState(blockValue.value);
		this.setBlockPos(posValue.toBlockPos());
		this.setDefaultScale();
	}

	@FunctionDoc(
		name = "setBlock",
		desc = "Sets the block type to render of the fake block",
		params = {BLOCK, "block", "The block to render"},
		examples = "fakeBlock.setBlock(Material.BEDROCK.asBlock());"
	)
	@ArucasFunction
	public void setBlock(Context context, BlockValue blockValue) {
		this.setBlockState(blockValue.value);
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the fake block",
		params = {POS, "pos", "The position of the block"},
		examples = "fakeBlock.setPos(new Pos(0, 0, 0));"
	)
	@ArucasFunction
	public void setPos(Context context, PosValue posValue) {
		this.setBlockPos(posValue.toBlockPos());
	}

	@FunctionDoc(
		name = "getBlock",
		desc = "Gets the current block type of the fake block",
		returns = {BLOCK, "The block type of the fake block"},
		examples = "fakeBlock.getBlock();"
	)
	@ArucasFunction
	public Value getBlock(Context context) {
		return new BlockValue(this.blockState, this.blockPos);
	}

	@FunctionDoc(
		name = "getPos",
		desc = "Gets the current position of the fake block",
		returns = {POS, "The position of the fake block"},
		examples = "fakeBlock.getPos();"
	)
	@ArucasFunction
	public Value getPos(Context context) {
		return new PosValue(this.blockPos);
	}

	@FunctionDoc(
		name = "setCull",
		params = {BOOLEAN, "shouldCull", "whether the block should be culled"},
		desc = "Sets whether the block should be culled",
		examples = "fakeBlock.setCull(true);"
	)
	@ArucasFunction
	public void setCull(Context context, BooleanValue shouldCull) {
		this.cull = shouldCull.value;
	}

	@FunctionDoc(
		name = "shouldCull",
		desc = "Returns whether the block is set to cull or not",
		returns = {BOOLEAN, "whether the block should cull"},
		examples = "fakeBlock.shouldCull();"
	)
	@ArucasFunction
	public Value shouldCull(Context context) {
		return BooleanValue.of(this.cull);
	}

	private Map<UUID, Set<FakeBlockWrapper>> getBlockMap() {
		return this.shouldRenderThroughBlocks() ? THROUGH_BLOCKS : NORMAL_BLOCKS;
	}

	public synchronized static void addFakeBlock(FakeBlockWrapper blockWrapper) {
		Context context = blockWrapper.getCreatedContext();
		Map<UUID, Set<FakeBlockWrapper>> map = blockWrapper.getBlockMap();
		Set<FakeBlockWrapper> fakeBlockWrapperSet = map.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> map.remove(id));
			return new LinkedHashSet<>();
		});
		fakeBlockWrapperSet.add(blockWrapper);
	}

	public synchronized static void removeFakeBlock(FakeBlockWrapper blockWrapper) {
		Context context = blockWrapper.getCreatedContext();
		Set<FakeBlockWrapper> fakeBlocks = blockWrapper.getBlockMap().get(context.getContextId());
		if (fakeBlocks != null) {
			fakeBlocks.remove(blockWrapper);
		}
	}

	public synchronized static Stream<FakeBlockWrapper> getBlocks() {
		return NORMAL_BLOCKS.values().stream().flatMap(Collection::stream);
	}

	public synchronized static Stream<FakeBlockWrapper> getThroughBlocks() {
		return THROUGH_BLOCKS.values().stream().flatMap(Collection::stream);
	}
}
