package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.clientscript.values.BlockValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
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
public class FakeBlockWrapper implements IArucasWrappedClass, Shape.Tiltable, Shape.Directional, Shape.Scalable {
	private static final Map<UUID, Set<FakeBlockWrapper>> BLOCKS_TO_RENDER = new LinkedHashMap<>(0);

	public BlockPos blockPos;
	public BlockState blockState;
	public Direction direction;

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
		name = "render",
		desc = "This sets the shape to be rendered indefinitely, the shape will only stop rendering when the script ends or when you call the stopRendering() method",
		example = "shape.render();"
	)
	@ArucasFunction
	public void render(Context context) {
		addFakeBlock(context, this);
	}

	@FunctionDoc(
		name = "stopRendering",
		desc = "This stops the shape from rendering",
		example = "shape.stopRendering();"
	)
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
		return BLOCKS_TO_RENDER.values().stream().flatMap(Collection::stream);
	}
}
