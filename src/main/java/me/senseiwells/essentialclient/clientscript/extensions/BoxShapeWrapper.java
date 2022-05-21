package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;

import java.util.*;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BOX_SHAPE;

@SuppressWarnings("unused")
@ArucasClass(name = BOX_SHAPE)
public class BoxShapeWrapper extends Shape.Positioned implements Shape.Tiltable {
	private static final Map<UUID, Set<BoxShapeWrapper>> NORMAL_BOXES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<BoxShapeWrapper>> THROUGH_BOXES = new LinkedHashMap<>(0);

	private float xTilt;
	private float yTilt;
	private float zTilt;

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

	@ArucasConstructor
	public void constructor(Context context, PosValue pos1, PosValue pos2) {
		this.setCreatedContext(context.createBranch());
		this.setPos1(context, pos1);
		this.setPos2(context, pos2);
	}

	@ArucasConstructor
	public void constructor(Context context, NumberValue x1, NumberValue y1, NumberValue z1, NumberValue x2, NumberValue y2, NumberValue z2) {
		this.constructor(context, new PosValue(x1.value, y1.value, z1.value), new PosValue(x2.value, y2.value, z2.value));
	}

	@ArucasConstructor
	public void constructor(Context context, PosValue origin) {
		this.constructor(context, origin, origin);
	}

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
