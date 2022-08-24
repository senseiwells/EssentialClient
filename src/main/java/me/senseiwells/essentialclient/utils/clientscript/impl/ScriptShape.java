package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ScriptShape {
	private final Interpreter interpreter;

	private int red;
	private int green;
	private int blue;
	private int alpha;

	private float xScale;
	private float yScale;
	private float zScale;

	private float xTilt;
	private float yTilt;
	private float zTilt;

	private boolean render;
	private boolean ignoreDepth;

	ScriptShape(Interpreter interpreter) {
		this.interpreter = interpreter;
		this.red = 255;
		this.green = 255;
		this.blue = 255;
		this.alpha = 255;

		this.xScale = 1;
		this.yScale = 1;
		this.zScale = 1;

		this.xTilt = 0;
		this.yTilt = 0;
		this.zTilt = 0;

		this.render = false;
		this.ignoreDepth = false;
	}

	public final void setColour(int colour) {
		this.red = (colour >> 16) & 0xFF;
		this.green = (colour >> 8) & 0xFF;
		this.blue = colour & 0xFF;
	}

	public final void setRed(int red) {
		this.red = this.checkInBounds(red);
	}

	public final void setGreen(int green) {
		this.green = this.checkInBounds(green);
	}

	public final void setBlue(int blue) {
		this.blue = this.checkInBounds(blue);
	}

	public final void setAlpha(int alpha) {
		this.alpha = this.checkInBounds(alpha);
	}

	public final int getColour() {
		return (this.red << 16) | (this.green << 8) | this.blue;
	}

	public final int getRed() {
		return this.red;
	}

	public final int getGreen() {
		return this.green;
	}

	public final int getBlue() {
		return this.blue;
	}

	public final int getAlpha() {
		return this.alpha;
	}

	public final void setXScale(float xScale) {
		this.xScale = xScale;
	}

	public final void setYScale(float yScale) {
		this.yScale = yScale;
	}

	public final void setZScale(float zScale) {
		this.zScale = zScale;
	}

	public final float getXScale() {
		return this.xScale;
	}

	public final float getYScale() {
		return this.yScale;
	}

	public final float getZScale() {
		return this.zScale;
	}

	public final void setXTilt(float xTilt) {
		this.xTilt = xTilt;
	}

	public final void setYTilt(float yTilt) {
		this.yTilt = yTilt;
	}

	public final void setZTilt(float zTilt) {
		this.zTilt = zTilt;
	}

	public final float getXTilt() {
		return this.xTilt;
	}

	public final float getYTilt() {
		return this.yTilt;
	}

	public final float getZTilt() {
		return this.zTilt;
	}

	public final void render(boolean render) {
		this.render = render;

		if (render) {
			this.addShape();
			return;
		}
		this.removeShape();
	}

	public final boolean getRender() {
		return this.render;
	}

	public final void setIgnoreDepth(boolean ignoreDepth) {
		if (this.shouldIgnoreDepth() ^ ignoreDepth) {
			if (this.getRender()) {
				this.removeShape();
				this.ignoreDepth = ignoreDepth;
				this.addShape();
				return;
			}
			this.ignoreDepth = ignoreDepth;
		}
	}

	public final boolean shouldIgnoreDepth() {
		return this.ignoreDepth;
	}

	protected final int compactRgb(int red, int green, int blue) {
		return (red << 16) | (green << 8) | blue;
	}

	protected final int checkInBounds(int colour) {
		if (colour < 0 || colour > 255) {
			throw new RuntimeError("Colour must be between 0 and 255");
		}
		return colour;
	}

	protected final int checkNonNegative(int value) {
		if (value < 0) {
			throw new RuntimeError("Value must be non-negative");
		}
		return value;
	}

	protected final float checkNonNegative(float value) {
		if (value < 0) {
			throw new RuntimeError("Value must be non-negative");
		}
		return value;
	}

	protected abstract Map<UUID, Set<ScriptShape>> getRegularDepthMap();

	protected abstract Map<UUID, Set<ScriptShape>> getIgnoreDepthMap();

	private Map<UUID, Set<ScriptShape>> getMap() {
		return this.shouldIgnoreDepth() ? this.getIgnoreDepthMap() : this.getRegularDepthMap();
	}

	private void addShape() {
		Map<UUID, Set<ScriptShape>> map = this.getMap();
		if (this.interpreter.getThreadHandler().getRunning()) {
			Set<ScriptShape> shapes = map.computeIfAbsent(this.interpreter.getProperties().getId(), id -> {
				this.interpreter.getThreadHandler().addShutdownEvent(() -> map.remove(id));
				return ConcurrentHashMap.newKeySet();
			});
			shapes.add(this);
		}
	}

	private void removeShape() {
		Map<UUID, Set<ScriptShape>> map = this.getMap();
		synchronized (map) {
			Set<ScriptShape> shapes = map.get(this.interpreter.getProperties().getId());
			if (shapes != null) {
				shapes.remove(this);
			}
		}
	}

	public static abstract class Outlined extends ScriptShape {
		private int outlineRed;
		private int outlineGreen;
		private int outlineBlue;
		private int outlineWidth;

		Outlined(Interpreter interpreter) {
			super(interpreter);
		}

		public void setOutlineColour(int colour) {
			this.outlineRed = (colour >> 16) & 0xFF;
			this.outlineGreen = (colour >> 8) & 0xFF;
			this.outlineBlue = colour & 0xFF;
		}

		public void setOutlineRed(int red) {
			this.outlineRed = this.checkInBounds(red);
		}

		public void setOutlineGreen(int green) {
			this.outlineGreen = this.checkInBounds(green);
		}

		public void setOutlineBlue(int blue) {
			this.outlineBlue = this.checkInBounds(blue);
		}

		public void setOutlineWidth(int width) {
			this.outlineWidth = this.checkNonNegative(width);
		}

		public int getOutlineColour() {
			return this.compactRgb(this.outlineRed, this.outlineGreen, this.outlineBlue);
		}

		public int getOutlineRed() {
			return this.outlineRed;
		}

		public int getOutlineGreen() {
			return this.outlineGreen;
		}

		public int getOutlineBlue() {
			return this.outlineBlue;
		}

		public int getOutlineWidth() {
			return this.outlineWidth;
		}
	}

	public static abstract class Centred extends Outlined {
		private Vec3d position;
		private float width;

		Centred(Interpreter interpreter, Vec3d position) {
			super(interpreter);
			this.position = position;
			this.width = 5;
		}

		public void setPosition(Vec3d position) {
			this.position = position;
		}

		public Vec3d getPosition() {
			return this.position;
		}

		public void setWidth(float width) {
			this.width = this.checkNonNegative(width);
		}

		public float getWidth() {
			return this.width;
		}
	}

	public static abstract class Cornered extends Outlined {
		private Vec3d cornerA;
		private Vec3d cornerB;

		Cornered(Interpreter interpreter, Vec3d cornerA, Vec3d cornerB) {
			super(interpreter);
			this.cornerA = cornerA;
			this.cornerB = cornerB;
		}

		public void setCornerA(Vec3d cornerA) {
			this.cornerA = cornerA;
		}

		public void setCornerB(Vec3d cornerB) {
			this.cornerB = cornerB;
		}

		public Vec3d getCornerA() {
			return this.cornerA;
		}

		public Vec3d getCornerB() {
			return this.cornerB;
		}
	}
}
