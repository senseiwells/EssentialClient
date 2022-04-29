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
	public void onClose() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	public static class Typed<T extends Screen> extends Screen {
		private final T parent;

		protected Typed(Text title, T parent) {
			super(title);
			this.parent = parent;
		}

		public T getParent() {
			return this.parent;
		}

		@Override
		public void onClose() {
			if (this.client != null) {
				this.client.setScreen(this.parent);
			}
		}
	}

}
