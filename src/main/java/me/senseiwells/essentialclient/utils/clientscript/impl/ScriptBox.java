package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.core.Interpreter;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ScriptBox extends ScriptShape.Cornered {
	private static final Map<UUID, Set<ScriptShape>> REGULAR_BOXES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<ScriptShape>> IGNORE_DEPTH_BOXES = new LinkedHashMap<>(0);

	public ScriptBox(Interpreter interpreter, Vec3d cornerA, Vec3d cornerB) {
		super(interpreter, cornerA, cornerB);
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getRegularDepthMap() {
		return REGULAR_BOXES;
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getIgnoreDepthMap() {
		return IGNORE_DEPTH_BOXES;
	}

	public static boolean hasRegular() {
		return REGULAR_BOXES.size() > 0;
	}

	public static boolean hasIgnoreDepth() {
		return IGNORE_DEPTH_BOXES.size() > 0;
	}

	public static void forEachRegular(Consumer<ScriptBox> consumer) {
		synchronized (REGULAR_BOXES) {
			REGULAR_BOXES.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptBox) s)));
		}
	}

	public static void forEachIgnoreDepth(Consumer<ScriptBox> consumer) {
		synchronized (IGNORE_DEPTH_BOXES) {
			IGNORE_DEPTH_BOXES.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptBox) s)));
		}
	}
}
