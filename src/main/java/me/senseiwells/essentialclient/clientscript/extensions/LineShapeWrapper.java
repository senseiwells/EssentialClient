package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;

import java.util.*;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.LINE_SHAPE;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

@SuppressWarnings("unused")
@ClassDoc(
	name = LINE_SHAPE,
	desc = "This class allows you to create a line shape which can be used to draw lines in the world.",
	importPath = "Minecraft"
)
@ArucasClass(name = LINE_SHAPE)
public class LineShapeWrapper extends Shape.PositionTiltableScalable {
	private static final Map<UUID, Set<LineShapeWrapper>> NORMAL_LINES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<LineShapeWrapper>> THROUGH_LINES = new LinkedHashMap<>(0);

	@Override
	public void render() {
		super.render();
		addLineToRender(this);
	}

	@Override
	public void stopRendering() {
		super.stopRendering();
		removeLineToRender(this);
	}

	@ConstructorDoc(
		desc = "Creates a new line shape",
		params = {
			POS, "pos1", "The starting position of the line",
			POS, "pos2", "The ending position of the line"
		},
		examples = "new LineShape(new Pos(0, 0, 0), new Pos(1, 1, 1));"
	)
	@ArucasConstructor
	public void constructor(Context context, PosValue pos1, PosValue pos2) {
		this.setCreatedContext(context.createBranch());
		this.setPos1(context, pos1);
		this.setPos2(context, pos2);
		this.setOutlineWidth(5);
	}

	@ConstructorDoc(
		desc = "Creates a new line shape",
		params = {
			NUMBER, "x1", "The x position of the starting position of the line",
			NUMBER, "y1", "The y position of the starting position of the line",
			NUMBER, "z1", "The z position of the starting position of the line",
			NUMBER, "x2", "The x position of the ending position of the line",
			NUMBER, "y2", "The y position of the ending position of the line",
			NUMBER, "z2", "The z position of the ending position of the line"
		},
		examples = "new LineShape(0, 0, 0, 1, 1, 1);"
	)
	@ArucasConstructor
	public void constructor(Context context, NumberValue x1, NumberValue y1, NumberValue z1, NumberValue x2, NumberValue y2, NumberValue z2) {
		this.constructor(context, new PosValue(x1.value, y1.value, z1.value), new PosValue(x2.value, y2.value, z2.value));
	}

	@Override
	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		if (this.shouldRenderThroughBlocks() ^ renderThroughBlocks) {
			if (this.isRendering()) {
				removeLineToRender(this);
				super.setRenderThroughBlocks(renderThroughBlocks);
				addLineToRender(this);
				return;
			}
			super.setRenderThroughBlocks(renderThroughBlocks);
		}
	}

	private Map<UUID, Set<LineShapeWrapper>> getLineMap() {
		return this.shouldRenderThroughBlocks() ? THROUGH_LINES : NORMAL_LINES;
	}

	public synchronized static void addLineToRender(LineShapeWrapper lineWrapper) {
		Context context = lineWrapper.getCreatedContext();
		Map<UUID, Set<LineShapeWrapper>> boxMap = lineWrapper.getLineMap();
		Set<LineShapeWrapper> boxShapeWrapperSet = boxMap.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> boxMap.remove(id));
			return new LinkedHashSet<>();
		});
		boxShapeWrapperSet.add(lineWrapper);
	}

	public synchronized static void removeLineToRender(LineShapeWrapper lineWrapper) {
		Context context = lineWrapper.getCreatedContext();
		Map<UUID, Set<LineShapeWrapper>> boxMap = lineWrapper.getLineMap();
		Set<LineShapeWrapper> boxShapeWrapperSet = boxMap.get(context.getContextId());
		if (boxShapeWrapperSet != null) {
			boxShapeWrapperSet.remove(lineWrapper);
		}
	}

	public synchronized static List<LineShapeWrapper> getNormalLines() {
		return NORMAL_LINES.values().stream().flatMap(Collection::stream).toList();
	}

	public synchronized static List<LineShapeWrapper> getThroughLines() {
		return THROUGH_LINES.values().stream().flatMap(Collection::stream).toList();
	}
}
