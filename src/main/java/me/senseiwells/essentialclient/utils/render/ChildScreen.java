package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ChildScreen extends Screen {
	protected final Screen parent;

	protected ChildScreen(Text title, Screen parent) {
		super(title);
		this.parent = parent;
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}
}
