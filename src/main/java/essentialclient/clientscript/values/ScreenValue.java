package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.client.gui.screen.Screen;

public class ScreenValue extends Value<Screen> {
	public ScreenValue(Screen screen) {
		super(screen);
	}

	@Override
	public Value<Screen> copy() {
		return this;
	}
}
