package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;

import java.util.*;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.LINE_SHAPE;

@SuppressWarnings("unused")
@ArucasClass(name = LINE_SHAPE)
public class LineShapeWrapper extends Shape.Positioned implements Shape.Tiltable {
	private static final Map<UUID, Set<LineShapeWrapper>> NORMAL_LINES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<LineShapeWrapper>> THROUGH_LINES = new LinkedHashMap<>(0);

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
		addLineToRender(this);
	}

	@Override
	public void stopRendering() {
		super.stopRendering();
		removeLineToRender(this);
	}

	@ArucasConstructor
	public void constructor(Context context, PosValue pos1, PosValue pos2) {
		this.setCreatedContext(context.createBranch());
		this.setPos1(context, pos1);
		this.setPos2(context, pos2);
		this.setOutlineWidth(5);
	}

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
