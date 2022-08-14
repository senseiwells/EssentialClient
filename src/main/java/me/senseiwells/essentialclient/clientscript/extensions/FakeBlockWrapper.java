package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.throwables.BuiltInException;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.clientscript.values.BlockValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.Supplier;
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
	private static final Supplier<BuiltInException> UNSUPPORTED = () -> new BuiltInException("FakeBlock does not support this function");

	public BlockPos blockPos;
	public BlockState blockState;
	public Direction direction;
	public boolean cull;

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
	public int getRed() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getGreen() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getBlue() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getAlpha() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getOutlineRed() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getOutlineGreen() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getOutlineBlue() {
		throw UNSUPPORTED.get();
	}

	@Override
	public int getOutlineWidth() {
		throw UNSUPPORTED.get();
	}

	@Override
	public boolean hasOutline() {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setRed(int red) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setGreen(int green) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setBlue(int blue) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setOutlineRed(int outlineRed) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setOutlineGreen(int outlineGreen) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setOutlineBlue(int outlineBlue) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setOutlineWidth(int outline) {
		throw UNSUPPORTED.get();
	}

	@Override
	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		throw UNSUPPORTED.get();
	}

	@ConstructorDoc(
		desc = "Creates a fake block with the given block and position",
		params = {
			BLOCK, "block", "The block to use",
			POS, "pos", "The position of the block"
		},
		example = "new FakeBlock(Material.BEDROCK.asBlock(), new Pos(0, 0, 0));"
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
		example = "fakeBlock.setBlock(Material.BEDROCK.asBlock());"
	)
	@ArucasFunction
	public void setBlock(Context context, BlockValue blockValue) {
		this.setBlockState(blockValue.value);
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the fake block",
		params = {POS, "pos", "The position of the block"},
		example = "fakeBlock.setPos(new Pos(0, 0, 0));"
	)
	@ArucasFunction
	public void setPos(Context context, PosValue posValue) {
		this.setBlockPos(posValue.toBlockPos());
	}

	@FunctionDoc(
		name = "getBlock",
		desc = "Gets the current block type of the fake block",
		returns = {BLOCK, "The block type of the fake block"},
		example = "fakeBlock.getBlock();"
	)
	@ArucasFunction
	public Value getBlock(Context context) {
		return new BlockValue(this.blockState, this.blockPos);
	}

	@FunctionDoc(
		name = "getPos",
		desc = "Gets the current position of the fake block",
		returns = {POS, "The position of the fake block"},
		example = "fakeBlock.getPos();"
	)
	@ArucasFunction
	public Value getPos(Context context) {
		return new PosValue(this.blockPos);
	}

	@FunctionDoc(
		name = "setCull",
		params = {BOOLEAN, "shouldCull", "whether the block should be culled"},
		desc = "Sets whether the block should be culled",
		example = "fakeBlock.setCull(true);"
	)
	@ArucasFunction
	public void setCull(Context context, BooleanValue shouldCull) {
		this.cull = shouldCull.value;
	}

	@FunctionDoc(
		name = "shouldCull",
		desc = "Returns whether the block is set to cull or not",
		returns = {BOOLEAN, "whether the block should cull"},
		example = "fakeBlock.shouldCull();"
	)
	@ArucasFunction
	public Value shouldCull(Context context) {
		return BooleanValue.of(this.cull);
	}

	public synchronized static void addFakeBlock(FakeBlockWrapper blockWrapper) {
		Context context = blockWrapper.getCreatedContext();
		Set<FakeBlockWrapper> fakeBlockWrapperSet = NORMAL_BLOCKS.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> NORMAL_BLOCKS.remove(id));
			return new LinkedHashSet<>();
		});
		fakeBlockWrapperSet.add(blockWrapper);
	}

	public synchronized static void removeFakeBlock(FakeBlockWrapper blockWrapper) {
		Context context = blockWrapper.getCreatedContext();
		Set<FakeBlockWrapper> fakeBlocks = NORMAL_BLOCKS.get(context.getContextId());
		if (fakeBlocks != null) {
			fakeBlocks.remove(blockWrapper);
		}
	}

	public synchronized static Stream<FakeBlockWrapper> getBlocks() {
		return NORMAL_BLOCKS.values().stream().flatMap(Collection::stream);
	}
}
