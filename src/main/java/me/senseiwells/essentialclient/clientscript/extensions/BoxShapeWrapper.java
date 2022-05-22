package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;

import java.util.*;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BOX_SHAPE;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

@SuppressWarnings("unused")
@ClassDoc(
	name = BOX_SHAPE,
	desc = "This class allows you to create box shapes that can be rendered in the world.",
	importPath = "Minecraft"
)
@ArucasClass(name = BOX_SHAPE)
public class BoxShapeWrapper extends Shape.PositionTiltableScalable {
	private static final Map<UUID, Set<BoxShapeWrapper>> NORMAL_BOXES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<BoxShapeWrapper>> THROUGH_BOXES = new LinkedHashMap<>(0);

	@Override
	public void render() {
		super.render();
		addBoxToRender(this);
	}

	@Override
	public void stopRendering() {
		super.stopRendering();
		removeBoxToRender(this);
	}

	@Override
	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		if (this.shouldRenderThroughBlocks() ^ renderThroughBlocks) {
			if (this.isRendering()) {
				removeBoxToRender(this);
				super.setRenderThroughBlocks(renderThroughBlocks);
				addBoxToRender(this);
				return;
			}
			super.setRenderThroughBlocks(renderThroughBlocks);
		}
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {
			POS, "pos1", "The position of the first corner of the box",
			POS, "pos2", "The position of the second corner of the box"
		},
		example = "new BoxShape(new Pos(0, 0, 0), new Pos(10, 10, 10));"
	)
	@ArucasConstructor
	public void constructor(Context context, PosValue pos1, PosValue pos2) {
		this.setCreatedContext(context.createBranch());
		this.setPos1(context, pos1);
		this.setPos2(context, pos2);
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {
			NUMBER, "x1", "The x position of the first corner of the box",
			NUMBER, "y1", "The y position of the first corner of the box",
			NUMBER, "z1", "The z position of the first corner of the box",
			NUMBER, "x2", "The x position of the second corner of the box",
			NUMBER, "y2", "The y position of the second corner of the box",
			NUMBER, "z2", "The z position of the second corner of the box"
		},
		example = "new BoxShape(0, 0, 0, 10, 10, 10);"
	)
	@ArucasConstructor
	public void constructor(Context context, NumberValue x1, NumberValue y1, NumberValue z1, NumberValue x2, NumberValue y2, NumberValue z2) {
		this.constructor(context, new PosValue(x1.value, y1.value, z1.value), new PosValue(x2.value, y2.value, z2.value));
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {POS, "pos", "The position which will be used for the first and second corner of the box"},
		example = "new BoxShape(new Pos(0, 0, 0));"
	)
	@ArucasConstructor
	public void constructor(Context context, PosValue origin) {
		this.constructor(context, origin, origin);
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {
			NUMBER, "x", "The x position which will be used for the first and second corner of the box",
			NUMBER, "y", "The y position which will be used for the first and second corner of the box",
			NUMBER, "z", "The z position which will be used for the first and second corner of the box"
		},
		example = "new BoxShape(0, 0, 0);"
	)
	@ArucasConstructor
	public void constructor(Context context, NumberValue x, NumberValue y, NumberValue z) {
		this.constructor(context, new PosValue(x.value, y.value, z.value));
	}

	private Map<UUID, Set<BoxShapeWrapper>> getBoxMap() {
		return this.shouldRenderThroughBlocks() ? THROUGH_BOXES : NORMAL_BOXES;
	}

	public synchronized static void addBoxToRender(BoxShapeWrapper boxShapeWrapper) {
		Context context = boxShapeWrapper.getCreatedContext();
		Map<UUID, Set<BoxShapeWrapper>> boxMap = boxShapeWrapper.getBoxMap();
		Set<BoxShapeWrapper> boxShapeWrapperSet = boxMap.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> boxMap.remove(id));
			return new LinkedHashSet<>();
		});
		boxShapeWrapperSet.add(boxShapeWrapper);
	}

	public synchronized static void removeBoxToRender(BoxShapeWrapper boxShapeWrapper) {
		Context context = boxShapeWrapper.getCreatedContext();
		Map<UUID, Set<BoxShapeWrapper>> boxMap = boxShapeWrapper.getBoxMap();
		Set<BoxShapeWrapper> boxShapeWrapperSet = boxMap.get(context.getContextId());
		if (boxShapeWrapperSet != null) {
			boxShapeWrapperSet.remove(boxShapeWrapper);
		}
	}

	public synchronized static List<BoxShapeWrapper> getNormalBoxes() {
		return NORMAL_BOXES.values().stream().flatMap(Collection::stream).toList();
	}

	public synchronized static List<BoxShapeWrapper> getThroughBoxes() {
		return THROUGH_BOXES.values().stream().flatMap(Collection::stream).toList();
	}
}
