package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

//#if MC >= 12000
import net.minecraft.client.gui.DrawContext;
//#else
//$$import net.minecraft.client.util.math.MatrixStack;
//#endif

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

	@Override
	public final void render(
		//#if MC >= 1200
		DrawContext context,
		//#else
		//$$MatrixStack context,
		//#endif
		int mouseX,
		int mouseY,
		float delta
	) {
		this.render(new RenderContextWrapper(context), mouseX, mouseY, delta);
	}

	public void render(RenderContextWrapper wrapper, int mouseX, int mouseY, float delta) {
		super.render(wrapper.getContext(), mouseX, mouseY, delta);
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
