package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import essentialclient.feature.clientrule.ClientRuleHelper;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.ConfigListWidget;
import essentialclient.utils.render.RuleWidget;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class NumberListEntry extends BaseListEntry {
    private final TextFieldWidget numberField;
    private boolean invalid;
    
    public NumberListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        super(settings, client, gui);
        TextFieldWidget numField = new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, new LiteralText("Type an number value"));
        numField.setText(settings.getAsString());
        numField.setChangedListener(s -> this.checkForInvalid(s, numField, settings));
        this.numberField = numField;
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, settings.defaultAsString, client);
            numField.setText(settings.defaultAsString);
        });
        gui.getNumberFieldList().add(this.numberField);
    }

    public NumberListEntry(final ClientRules settings, MinecraftClient client, ClientRulesScreen gui) {
        super(settings, client, gui);
        TextFieldWidget numField = new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, new LiteralText("Type a number value"));
        numField.setText(settings.getString());
        numField.setChangedListener(s -> this.checkForInvalid(s, numField, settings));
        this.numberField = numField;
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            settings.setValue(settings.defaultValue);
            numField.setText(settings.defaultValue);
            ClientRuleHelper.writeSaveFile();
            ClientRuleHelper.executeOnChange(client, settings, gui);
        });
        gui.getNumberFieldList().add(this.numberField);
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
            if (!this.invalid && this.serverSettings != null)
                CarpetSettingsServerNetworkHandler.ruleChange(this.serverSettings.name, this.numberField.getText(), this.client);
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || this.numberField.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        TextRenderer font = client.textRenderer;
        float fontX = (float)(x + 90 - ConfigListWidget.length);
        float fontY = (float)(y + height / 2 - 9 / 2);

        this.ruleWidget = new RuleWidget(this.rule, x - 50, y + 2, 200, 15);
        this.ruleWidget.drawRule(font, fontX, fontY, 16777215);

        this.resetButton.x = x + 290;
        this.resetButton.y = y;

        this.resetButton.active = this.serverSettings == null ? !this.clientSettings.getString().equals(this.clientSettings.defaultValue) || this.invalid : !this.serverSettings.getAsString().equals(this.serverSettings.defaultAsString) || this.invalid;
        
        this.numberField.x = x + 182;
        this.numberField.y = y + 3;
        this.numberField.setEditableColor(this.invalid ? 16733525 : 16777215);
        if (invalid) {
            DiffuseLighting.enableGuiDepthLighting();
            client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.BARRIER), this.numberField.x + this.numberField.getWidth() - 18, this.numberField.y- 1);
            DiffuseLighting.disableGuiDepthLighting();
        }

        this.numberField.render(new MatrixStack(), mouseX, mouseY, delta);
        this.resetButton.render(new MatrixStack(), mouseX, mouseY, delta);
    }
    
    private void setInvalid(boolean invalid) {
        this.invalid = invalid;
        if (this.serverGui != null)
            this.serverGui.setInvalid(invalid);
        else
            this.clientGui.setInvalid(invalid);
    }
    
    private void checkForInvalid(String newValue, TextFieldWidget widget, ParsedRule<?> settings) {
        this.serverGui.setEmpty(widget.getText().isEmpty());
        boolean isNumber;
        try {
            if (settings.type == int.class)
                Integer.parseInt(newValue);
            else if (settings.type == double.class)
                Double.parseDouble(newValue);
            isNumber = true;
        }
        catch (NumberFormatException e) {
            isNumber = false;
        }
        this.setInvalid(!isNumber);
    }

    private void checkForInvalid(String newValue, TextFieldWidget widget, ClientRules clientSettings) {
        this.clientGui.setEmpty(widget.getText().isEmpty());
        boolean isNumber;
        try {
            if (clientSettings.type == ClientRules.Type.INTEGER)
                this.clientSettings.setValue(String.valueOf(Integer.parseInt(newValue)));
            else
                this.clientSettings.setValue(String.valueOf(Double.parseDouble(newValue)));
            ClientRuleHelper.writeSaveFile();
            isNumber = true;
        }
        catch (NumberFormatException e) {
            isNumber = false;
        }
        this.setInvalid(!isNumber);
    }
}
