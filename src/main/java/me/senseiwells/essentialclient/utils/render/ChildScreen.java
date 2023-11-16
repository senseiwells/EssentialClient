package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ChildScreen extends Screen {
	private final Screen parent;

	protected ChildScreen(Text title, Screen parent) {
		super(title);
		this.parent = parent;
	}

	public Screen getParent() {
		return this.parent;
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	public static class Typed<T extends Screen> extends ChildScreen {
		protected Typed(Text title, T parent) {
			super(title, parent);
		}

		@SuppressWarnings("unchecked")
		public T getParent() {
			return (T) super.getParent();
		}

		@Override
		public void close() {
			if (this.client != null) {
				this.client.setScreen(this.getParent());
			}
		}
	}
}
