package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class RuleWidget {
	private final String name;
	private final int width;
	private final int height;
	private boolean isToggled;
	private int x;
	private int y;

	public RuleWidget(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.isToggled = false;
	}

	public void toggle() {
		this.isToggled = !this.isToggled;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void drawRule(DrawContext context, TextRenderer font, float fontX, float fontY, int colour) {
		context.drawTextWithShadow(font, Text.literal(getShortName(this.name)), (int) fontX, (int) fontY, colour);
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
	}

	public boolean isToggled() {
		return this.isToggled;
	}

	public static String getShortName(String string) {
		if (string.length() > 34) {
			return string.substring(0, 31) + "...";
		}
		return string;
	}
}
