package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.core.Interpreter;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ScriptSphere extends ScriptShape.Centred {
	private static final Map<UUID, Set<ScriptShape>> REGULAR_SPHERES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<ScriptShape>> IGNORE_DEPTH_SPHERES = new LinkedHashMap<>(0);

	private float steps;

	public ScriptSphere(Interpreter interpreter, Vec3d position) {
		super(interpreter, position);
	}

	public void setSteps(float steps) {
		this.steps = steps;
	}

	public float getSteps() {
		return this.steps;
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getRegularDepthMap() {
		return REGULAR_SPHERES;
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getIgnoreDepthMap() {
		return IGNORE_DEPTH_SPHERES;
	}

	public static boolean hasRegular() {
		return REGULAR_SPHERES.size() > 0;
	}

	public static boolean hasIgnoreDepth() {
		return IGNORE_DEPTH_SPHERES.size() > 0;
	}

	public static void forEachRegular(Consumer<ScriptSphere> consumer) {
		synchronized (REGULAR_SPHERES) {
			REGULAR_SPHERES.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptSphere) s)));
		}
	}

	public static void forEachIgnoreDepth(Consumer<ScriptSphere> consumer) {
		synchronized (IGNORE_DEPTH_SPHERES) {
			IGNORE_DEPTH_SPHERES.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptSphere) s)));
		}
	}
}
