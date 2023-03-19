package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class WidgetHelper {
	public static ButtonWidget newButton(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		//#if MC >= 11903
		return ButtonWidget.builder(message, onPress).dimensions(x, y, width, height).build();
		//#else
		//$$return new ButtonWidget(x, y, width, height, message, onPress);
		//#endif
	}

	public static void setPosition(ClickableWidget widget, int x, int y) {
		//#if MC >= 11904
		widget.setPosition(x, y);
		//#if MC >= 11903
		//$$widget.setPos(x, y);
		//#else
		//$$widget.x = x;
		//$$widget.y = y;
		//#endif
	}

	public static int getX(ClickableWidget widget) {
		//#if MC >= 11903
		return widget.getX();
		//#else
		//$$return widget.x;
		//#endif
	}
}
