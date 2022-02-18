package essentialclient.gui.entries;

import essentialclient.clientrule.entries.ClientRule;
import essentialclient.gui.rulescreen.RulesScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class StringListEntry extends BaseListEntry<TextFieldWidget> {
	private boolean invalid;
	private boolean changed;

	public StringListEntry(ClientRule<?> clientRule, MinecraftClient client, RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen, () -> {
			return new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, LiteralText.EMPTY);
		});
		this.setResetButton(buttonWidget -> {
			this.clientRule.resetToDefault();
			this.editButton.setText(this.clientRule.getDefaultValue().toString());
		});
		this.invalid = false;
		this.changed = false;
		this.editButton.setText(clientRule.getValue().toString());
		this.editButton.setChangedListener(this::checkForInvalid);
		rulesScreen.addTextField(this.editButton);
	}

	protected void checkForInvalid(String newString) {
		this.changed = true;
		boolean isEmpty = newString.isEmpty();
		this.rulesScreen.setEmpty(isEmpty);
		this.setInvalid(isEmpty);
	}

	protected void setInvalid(boolean invalid) {
		this.invalid = invalid;
		this.rulesScreen.setInvalid(invalid);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return this.editButton.charTyped(chr, keyCode);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			this.editButton.setText(this.editButton.getText());
			this.editButton.changeFocus(false);
			if (!this.invalid) {
				this.changed = false;
				this.clientRule.setValueFromString(this.editButton.getText());
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers) || this.editButton.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderEditButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		int xi = this.editButton.x = x + 182;
		int yi = this.editButton.y = y + 3;
		this.editButton.setEditableColor(this.invalid ? 16733525 : 16777215);
		if (this.invalid) {
			DiffuseLighting.enableGuiDepthLighting();
			this.client.getItemRenderer().renderGuiItemIcon(Items.BARRIER.getDefaultStack(), xi + this.editButton.getWidth() - 18, yi - 1);
			DiffuseLighting.disableGuiDepthLighting();
		}
		this.editButton.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected void checkDisabled() {
		if (this.cannotEdit()) {
			this.children().forEach(child -> {
				child.active = false;
			});
			this.editButton.setEditable(false);
			this.editButton.setFocusUnlocked(false);
		}
	}

	@Override
	public void updateEntryOnClose() {
		if (this.invalid) {
			this.clientRule.resetToDefault();
			return;
		}
		if (this.changed) {
			this.clientRule.setValueFromString(this.editButton.getText());
		}
	}
}
