package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;

import java.util.*;

@SuppressWarnings("unused")
@ArucasClass(name = "SphereShape")
public class SphereShapeWrapper extends Shape.CentrePositioned implements Shape.Tiltable {
	private static final Map<UUID, Set<SphereShapeWrapper>> NORMAL_SPHERES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<SphereShapeWrapper>> THROUGH_SPHERES = new LinkedHashMap<>(0);

	private float steps;
	private float xTilt;
	private float yTilt;
	private float zTilt;

	public float getSteps() {
		return this.steps;
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
	public void constructor(Context context, PosValue pos) {
		this.setCreatedContext(context.createBranch());
		this.setPos(context, pos);
		this.steps = 50;
	}

	@ArucasConstructor
	public void constructor(Context context, NumberValue x, NumberValue y, NumberValue z) {
		this.constructor(context, new PosValue(x.value, y.value, z.value));
	}

	@Override
	public void render(Context context) {
		super.render(context);
		addBoxToRender(this);
	}

	@Override
	public void stopRendering(Context context) {
		super.stopRendering(context);
		removeBoxToRender(this);
	}

	@ArucasFunction
	public void setSteps(Context context, NumberValue steps) {
		this.steps = steps.value.floatValue();
	}

	@ArucasFunction
	public NumberValue getSteps(Context context) {
		return NumberValue.of(this.getSteps());
	}

	private Map<UUID, Set<SphereShapeWrapper>> getSphereMap() {
		return this.shouldRenderThroughBlocks() ? THROUGH_SPHERES : NORMAL_SPHERES;
	}

	public synchronized static void addBoxToRender(SphereShapeWrapper sphereShapeWrapper) {
		Context context = sphereShapeWrapper.getCreatedContext();
		Map<UUID, Set<SphereShapeWrapper>> boxMap = sphereShapeWrapper.getSphereMap();
		Set<SphereShapeWrapper> sphereWrappers = boxMap.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> boxMap.remove(id));
			return new LinkedHashSet<>();
		});
		sphereWrappers.add(sphereShapeWrapper);
	}

	public synchronized static void removeBoxToRender(SphereShapeWrapper sphereShapeWrapper) {
		Context context = sphereShapeWrapper.getCreatedContext();
		Map<UUID, Set<SphereShapeWrapper>> boxMap = sphereShapeWrapper.getSphereMap();
		Set<SphereShapeWrapper> sphereShapeWrappers = boxMap.get(context.getContextId());
		if (sphereShapeWrappers != null) {
			sphereShapeWrappers.remove(sphereShapeWrapper);
		}
	}

	public synchronized static List<SphereShapeWrapper> getNormalSpheres() {
		return NORMAL_SPHERES.values().stream().flatMap(Collection::stream).toList();
	}

	public synchronized static List<SphereShapeWrapper> getThroughSpheres() {
		return THROUGH_SPHERES.values().stream().flatMap(Collection::stream).toList();
	}
}
