package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.core.Interpreter;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ScriptLine extends ScriptShape.Cornered {
	private static final Map<UUID, Set<ScriptShape>> REGULAR_LINES = new LinkedHashMap<>(0);
	private static final Map<UUID, Set<ScriptShape>> IGNORE_DEPTH_LINES = new LinkedHashMap<>(0);

	public ScriptLine(Interpreter interpreter, Vec3d cornerA, Vec3d cornerB) {
		super(interpreter, cornerA, cornerB);
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getRegularDepthMap() {
		return REGULAR_LINES;
	}

	@Override
	protected Map<UUID, Set<ScriptShape>> getIgnoreDepthMap() {
		return IGNORE_DEPTH_LINES;
	}

	public static boolean hasRegular() {
		return REGULAR_LINES.size() > 0;
	}

	public static boolean hasIgnoreDepth() {
		return IGNORE_DEPTH_LINES.size() > 0;
	}

	public static void forEachRegular(Consumer<ScriptLine> consumer) {
		synchronized (REGULAR_LINES) {
			REGULAR_LINES.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptLine) s)));
		}
	}

	public static void forEachIgnoreDepth(Consumer<ScriptLine> consumer) {
		synchronized (IGNORE_DEPTH_LINES) {
			IGNORE_DEPTH_LINES.values().forEach(set -> set.forEach(s -> consumer.accept((ScriptLine) s)));
		}
	}
}
