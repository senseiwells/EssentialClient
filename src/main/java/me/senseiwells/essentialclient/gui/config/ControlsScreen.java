package me.senseiwells.essentialclient.gui.config;

import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ControlsScreen extends ChildScreen {
	private ClientKeyBind focusedKeyBinding;
	private ControlsListWidget controlWidget;

	public ControlsScreen(Screen parent) {
		super(Texts.CONTROLS_SCREEN, parent);
	}

	public void setFocusedKeyBinding(ClientKeyBind keyBind) {
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
			this.focusedKeyBinding.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
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
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.focusedKeyBinding != null) {
			InputUtil.Key key = keyCode == GLFW.GLFW_KEY_ESCAPE ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode);
			this.focusedKeyBinding.setBoundKey(key);
			this.focusedKeyBinding = null;
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
