package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.clientscript.Shape;

import java.util.*;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.SPHERE_SHAPE;

@SuppressWarnings("unused")
@ClassDoc(
	name = SPHERE_SHAPE,
	desc = "This class is used to create a sphere shape which can be rendered in the world.",
	importPath = "Minecraft"
)
@ArucasClass(name = SPHERE_SHAPE)
public class SphereShapeWrapper extends Shape.CentreTiltableScalable {
	private static final Map<UUID, Set<SphereShapeWrapper>> NORMAL_SPHERES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<SphereShapeWrapper>> THROUGH_SPHERES = new LinkedHashMap<>(0);

	private float steps;

	public float getSteps() {
		return this.steps;
	}

	@Override
	public void render() {
		super.render();
		addSphereToRender(this);
	}

	@Override
	public void stopRendering() {
		super.stopRendering();
		removeSphereToRender(this);
	}

	@Override
	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		if (this.shouldRenderThroughBlocks() ^ renderThroughBlocks) {
			if (this.isRendering()) {
				removeSphereToRender(this);
				super.setRenderThroughBlocks(renderThroughBlocks);
				addSphereToRender(this);
				return;
			}
			super.setRenderThroughBlocks(renderThroughBlocks);
		}
	}

	@ConstructorDoc(
		desc = "This creates a new sphere shape",
		params = {POS, "pos", "The position of the sphere"},
		example = "new SphereShape(new Pos(0, 10, 0));"
	)
	@ArucasConstructor
	public void constructor(Context context, PosValue pos) {
		this.setCreatedContext(context.createBranch());
		this.setPos(context, pos);
		this.steps = 30;
	}

	@ConstructorDoc(
		desc = "This creates a new sphere shape",
		params = {
			NUMBER, "x", "The x position of the sphere",
			NUMBER, "y", "The y position of the sphere",
			NUMBER, "z", "The z position of the sphere"
		},
		example = "new SphereShape(0, 10, 0);"
	)
	@ArucasConstructor
	public void constructor(Context context, NumberValue x, NumberValue y, NumberValue z) {
		this.constructor(context, new PosValue(x.value, y.value, z.value));
	}

	@FunctionDoc(
		name = "setSteps",
		desc = "This sets the number of steps the sphere will take to render",
		params = {NUMBER, "steps", "The number of steps"},
		example = "sphere.setSteps(30);"
	)
	@ArucasFunction
	public void setSteps(Context context, NumberValue steps) {
		this.steps = steps.value.floatValue();
	}

	// Checkstyle off
	// Checkstyle insists we put this wither overloaded method

	@FunctionDoc(
		name = "getSteps",
		desc = "This gets the number of steps the sphere will take to render",
		returns = {NUMBER, "The number of steps"},
		example = "sphere.getSteps();"
	)
	@ArucasFunction
	public NumberValue getSteps(Context context) {
		return NumberValue.of(this.getSteps());
	}

	// Checkstyle on

	private Map<UUID, Set<SphereShapeWrapper>> getSphereMap() {
		return this.shouldRenderThroughBlocks() ? THROUGH_SPHERES : NORMAL_SPHERES;
	}

	public synchronized static void addSphereToRender(SphereShapeWrapper sphereShapeWrapper) {
		Context context = sphereShapeWrapper.getCreatedContext();
		Map<UUID, Set<SphereShapeWrapper>> boxMap = sphereShapeWrapper.getSphereMap();
		Set<SphereShapeWrapper> sphereWrappers = boxMap.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> boxMap.remove(id));
			return new LinkedHashSet<>();
		});
		sphereWrappers.add(sphereShapeWrapper);
	}

	public synchronized static void removeSphereToRender(SphereShapeWrapper sphereShapeWrapper) {
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
