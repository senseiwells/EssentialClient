package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class WidgetHelper {
	public static ButtonWidget newButton(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		return ButtonWidget.builder(message, onPress).dimensions(x, y, width, height).build();
	}

	@Deprecated
	public static void setPosition(ClickableWidget widget, int x, int y) {
		widget.setPosition(x, y);
	}
}
