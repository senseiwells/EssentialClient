package essentialclient.config.entries;

import com.google.common.collect.ImmutableList;
import essentialclient.config.ConfigListWidget;
import essentialclient.config.clientrule.*;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.config.rulescreen.ServerRulesScreen;
import essentialclient.feature.EssentialCarpetClient;
import essentialclient.utils.render.RuleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class NumberListEntry extends BaseListEntry {
	private final TextFieldWidget numberField;
	private boolean invalid;
	private boolean changed = false;

	public NumberListEntry(final NumberClientRule<?> clientRule, final MinecraftClient client, final RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen, rulesScreen instanceof ServerRulesScreen);
		TextFieldWidget numField = new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, new LiteralText("Type a number value"));
		numField.setText(clientRule.getValue().toString());
		numField.setChangedListener(s -> this.checkForInvalid(s, numField, clientRule));
		this.numberField = numField;
		this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
			if (this.isServerScreen && EssentialCarpetClient.handleRuleChange(clientRule.getName(), clientRule.getDefaultValue().toString())) {
				return;
			}
			clientRule.resetToDefault();
			numField.setText(clientRule.getDefaultValue().toString());
			ClientRuleHelper.writeSaveFile();
			clientRule.run();
		});
		rulesScreen.getNumberFieldList().add(this.numberField);
	}

	@Override
	public List<? extends Element> children() {
		return ImmutableList.of(this.resetButton, this.numberField);
	}
	
	@Override
	public boolean charTyped(char chr, int keyCode)
	{
		return this.numberField.charTyped(chr, keyCode);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			this.numberField.setText(this.numberField.getText());
			this.numberField.changeFocus(false);
			if (!this.invalid && this.isServerScreen && EssentialCarpetClient.canChangeRule()) {
				EssentialCarpetClient.handleRuleChange(this.ruleName, this.clientRule.getValue().toString());
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers) || this.numberField.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
		TextRenderer font = this.client.textRenderer;
		float fontX = (float)(x + 90 - ConfigListWidget.length);
		float fontY = (float)(y + height / 2 - 9 / 2);

		this.ruleWidget = new RuleWidget(this.ruleName, x - 50, y + 2, 200, 15);
		this.ruleWidget.drawRule(matrices, font, fontX, fontY, 16777215);

		this.resetButton.x = x + 290;
		this.resetButton.y = y;

		this.resetButton.active = this.clientRule.isNotDefault() || this.invalid;
		
		this.numberField.x = x + 182;
		this.numberField.y = y + 3;
		this.numberField.setEditableColor(this.invalid ? 16733525 : 16777215);
		if (this.invalid) {
			DiffuseLighting.enable();
			client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.BARRIER), this.numberField.x + this.numberField.getWidth() - 18, this.numberField.y- 1);
			DiffuseLighting.disable();
		}

		this.numberField.render(matrices, mouseX, mouseY, delta);
		this.resetButton.render(matrices, mouseX, mouseY, delta);
	}
	
	private void setInvalid(boolean invalid) {
		this.invalid = invalid;
		this.rulesScreen.setInvalid(invalid);
	}

	private void checkForInvalid(String newValue, TextFieldWidget widget, NumberClientRule<?> clientRule) {
		this.changed = true;
		this.rulesScreen.setEmpty(widget.getText().isEmpty());
		boolean isNumber;
		try {
			if (clientRule.getType() == ClientRule.Type.INTEGER) {
				((IntegerClientRule) clientRule).setValue(Integer.parseInt(newValue));
			}
			else if (clientRule.getType() == ClientRule.Type.DOUBLE){
				((DoubleClientRule) clientRule).setValue(Double.parseDouble(newValue));
			}
			else {
				throw new NumberFormatException();
			}
			if (!this.isServerScreen) {
				ClientRuleHelper.writeSaveFile();
			}
			isNumber = true;
		}
		catch (NumberFormatException e) {
			isNumber = false;
		}
		this.setInvalid(!isNumber);
	}

	@Override
	public void updateEntryOnClose() {
		if (this.invalid) {
			this.clientRule.resetToDefault();
		}
		if (this.changed && EssentialCarpetClient.canChangeRule() && this.isServerScreen) {
			EssentialCarpetClient.handleRuleChange(this.ruleName, this.clientRule.getValue().toString());
		}
	}
}
