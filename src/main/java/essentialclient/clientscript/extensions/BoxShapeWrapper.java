package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.PosValue;
import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ArucasWrapper(name = "BoxShape")
public class BoxShapeWrapper implements IArucasWrappedClass {
	private static final Set<BoxShapeWrapper> boxesToRender = new LinkedHashSet<>(0);

	@ArucasMember(assignable = false)
	public PosValue pos1;
	@ArucasMember(assignable = false)
	public PosValue pos2;
	public int red;
	public int green;
	public int blue;
	public int opacity;
	public int outlineRed;
	public int outlineGreen;
	public int outlineBlue;
	public int outlineWidth;
	public boolean renderThroughBlocks;

	@ArucasConstructor
	public void constructor(Context context, PosValue pos1, PosValue pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.red = this.green = this.blue = this.opacity = 255;
		this.outlineRed = this.outlineGreen = this.outlineBlue = 0;
		this.outlineWidth = 1;
		this.renderThroughBlocks = false;
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

	@ArucasFunction
	public NullValue setPos1(Context context, PosValue posValue) {
		this.pos1 = posValue;
		return NullValue.NULL;
	}

	@ArucasFunction
	public NullValue setPos2(Context context, PosValue posValue) {
		this.pos2 = posValue;
		return NullValue.NULL;
	}

	@ArucasFunction
	public NullValue setColour(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		int red = redValue.value.intValue();
		int green = greenValue.value.intValue();
		int blue = blueValue.value.intValue();
		this.throwIfColoursInvalid(red, green, blue);
		this.red = red;
		this.green = green;
		this.blue = blue;
		return NullValue.NULL;
	}

	@ArucasFunction
	public NullValue setColur(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		return this.setColour(context, redValue, greenValue, blueValue);
	}


	@ArucasFunction
	public NullValue setOpacity(Context context, NumberValue opacityValue) {
		int opacity = opacityValue.value.intValue();
		this.throwIfColourInvalid(opacity);
		this.opacity = opacity;
		return NullValue.NULL;
	}

	@ArucasFunction
	public NullValue setOutlineColour(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		int red = redValue.value.intValue();
		int green = greenValue.value.intValue();
		int blue = blueValue.value.intValue();
		this.throwIfColoursInvalid(red, green, blue);
		this.outlineRed = red;
		this.outlineGreen = green;
		this.outlineBlue = blue;
		return NullValue.NULL;
	}

	@ArucasFunction
	public NullValue setOutlineColor(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		return this.setOutlineColour(context, redValue, greenValue, blueValue);
	}

	@ArucasFunction
	public NullValue setOutlinePixelWidth(Context context, NumberValue numberValue) {
		this.outlineWidth = numberValue.value.intValue();
		return NullValue.NULL;
	}

	@ArucasFunction
	public NullValue setRenderThroughBlocks(Context context, BooleanValue booleanValue) {
		this.renderThroughBlocks = booleanValue.value;
		return NullValue.NULL;
	}

	@ArucasFunction
	public NumberValue getRed(Context context) {
		return NumberValue.of(this.red);
	}

	@ArucasFunction
	public NumberValue getGreen(Context context) {
		return NumberValue.of(this.green);
	}

	@ArucasFunction
	public NumberValue getBlue(Context context) {
		return NumberValue.of(this.blue);
	}

	@ArucasFunction
	public NumberValue getOpacity(Context context) {
		return NumberValue.of(this.opacity);
	}

	@ArucasFunction
	public NumberValue getOutlineRed(Context context) {
		return NumberValue.of(this.outlineRed);
	}

	@ArucasFunction
	public NumberValue getOutlineGreen(Context context) {
		return NumberValue.of(this.outlineGreen);
	}

	@ArucasFunction
	public NumberValue getOutlineBlue(Context context) {
		return NumberValue.of(this.outlineBlue);
	}

	@ArucasFunction
	public NumberValue getOutlinePixelWidth(Context context) {
		return NumberValue.of(this.outlineWidth);
	}

	@ArucasFunction
	public BooleanValue shouldRenderThroughBlocks(Context context) {
		return BooleanValue.of(this.renderThroughBlocks);
	}

	@ArucasFunction
	public NullValue render(Context context) {
		addBoxToRender(this);
		return NullValue.NULL;
	}

	@ArucasFunction
	public BooleanValue stopRendering(Context context) {
		return BooleanValue.of(removeBoxToRender(this));
	}

	private void throwIfColoursInvalid(int red, int green, int blue) {
		this.throwIfColourInvalid(red);
		this.throwIfColourInvalid(green);
		this.throwIfColourInvalid(blue);
	}

	private void throwIfColourInvalid(int colour) {
		if (colour > 255 || colour < 0) {
			throw new RuntimeException("Colours must be between 0 and 255");
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	public synchronized static boolean addBoxToRender(BoxShapeWrapper boxShapeWrapper) {
		return boxesToRender.add(boxShapeWrapper);
	}

	public synchronized static boolean removeBoxToRender(BoxShapeWrapper boxShapeWrapper) {
		return boxesToRender.remove(boxShapeWrapper);
	}

	public synchronized static void clearBoxesToRender() {
		boxesToRender.clear();
	}

	public synchronized static List<BoxShapeWrapper> getBoxesToRender() {
		return List.copyOf(boxesToRender);
	}
}
