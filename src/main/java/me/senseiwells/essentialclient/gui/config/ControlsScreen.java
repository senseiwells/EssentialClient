package me.senseiwells.essentialclient.gui.config;

import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ControlsScreen extends ChildScreen {
	private ClientKeyBind hoveredKeyBinding;
	private ClientKeyBind focusedKeyBinding;
	private ControlsListWidget controlWidget;
	private boolean firstKey;

	public ControlsScreen(Screen parent) {
		super(Texts.CONTROLS_SCREEN, parent);
	}

	public void setHoveredKeyBinding(ClientKeyBind keyBind) {
		this.hoveredKeyBinding = keyBind;
	}

	public void setFocusedKeyBinding(ClientKeyBind keyBind) {
		this.firstKey = true;
		this.focusedKeyBinding = keyBind;
	}

	public boolean isBindingFocused(ClientKeyBind keyBind) {
		return keyBind == this.focusedKeyBinding;
	}

	@Override
	protected void init() {
		this.controlWidget = new ControlsListWidget(this.client, this);
		this.addSelectableChild(this.controlWidget);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, Texts.DONE, buttonWidget -> this.close()));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.focusedKeyBinding != null) {
			this.focusedKeyBinding = null;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.controlWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

		if (this.hoveredKeyBinding != null) {
			String display = this.hoveredKeyBinding.getDisplay();
			List<Text> textList = List.of(
				Texts.literal(this.hoveredKeyBinding.getName()).formatted(Formatting.GOLD),
				display.isEmpty() ? Texts.NO_KEYBINDING : Texts.literal(display)
			);
			this.renderTooltip(matrices, textList, mouseX, mouseY);
			this.hoveredKeyBinding = null;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.focusedKeyBinding != null) {
			if (this.focusedKeyBinding.isSingleKey()) {
				InputUtil.Key key = keyCode == GLFW.GLFW_KEY_ESCAPE ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode);
				this.focusedKeyBinding.addKey(key);
				this.focusedKeyBinding = null;
				return true;
			}
			if (this.firstKey) {
				this.firstKey = false;
				this.focusedKeyBinding.clearKey();
			}
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				this.focusedKeyBinding = null;
				return true;
			}
			this.focusedKeyBinding.addKey(InputUtil.fromKeyCode(keyCode, scanCode));
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
