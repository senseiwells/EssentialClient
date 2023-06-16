package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.RenderContextWrapper;
import me.senseiwells.essentialclient.utils.render.Texts;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class StringListEntry extends BaseListEntry<TextFieldWidget> {
	private boolean invalid;
	private boolean changed;

	public StringListEntry(Rule<?> rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, Texts.EMPTY);
		});
		this.setResetButton(buttonWidget -> {
			this.rule.resetToDefault();
			this.editButton.setText(this.rule.getDefaultValue().toString());
		});
		this.invalid = false;
		this.changed = false;
		if (rule instanceof Rule.Str strRule) {
			this.editButton.setMaxLength(strRule.getMaxLength());
		}
		this.editButton.setText(rule.getValue().toString());
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
			this.editButton.setFocused(false);
			if (!this.invalid) {
				this.changed = false;
				this.rule.setValueFromString(this.editButton.getText());
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers) || this.editButton.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderEditButton(RenderContextWrapper wrapper, int x, int y, int mouseX, int mouseY, float delta) {
		int xi = x + 182;
		int yi = y + 3;
		WidgetHelper.setPosition(this.editButton, xi, yi);
		this.editButton.setEditableColor(this.invalid ? 16733525 : 16777215);
		if (this.invalid) {
			DiffuseLighting.enableGuiDepthLighting();
			//#if MC >= 12000
			wrapper.getContext().drawItem(
				Items.BARRIER.getDefaultStack(),
				xi + this.editButton.getWidth() - 18,
				yi - 1
			);
			//#else
			//$$this.client.getItemRenderer().renderGuiItemIcon(
			//$$	//#if MC >= 11904
			//$$	wrapper.getMatrices(),
			//$$	//#endif
			//$$	Items.BARRIER.getDefaultStack(),
			//$$	xi + this.editButton.getWidth() - 18,
			//$$	yi - 1
			//$$);
			//#endif
			DiffuseLighting.disableGuiDepthLighting();
		}
		this.editButton.render(wrapper.getContext(), mouseX, mouseY, delta);
	}

	@Override
	protected void checkDisabled() {
		if (this.cannotEdit() || !this.rule.isAvailable() || !this.rule.changeable()) {
			this.children().forEach(child -> {
				child.active = false;
			});
			this.editButton.setEditable(false);
			this.editButton.setFocusUnlocked(false);
		}
	}

	@Override
	public void unFocus() {
		this.editButton.setFocused(false);
	}

	@Override
	public void updateEntryOnClose() {
		if (this.invalid) {
			this.rule.resetToDefault();
			return;
		}
		if (this.changed) {
			this.rule.setValueFromString(this.editButton.getText());
		}
	}
}
