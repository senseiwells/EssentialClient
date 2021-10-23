package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import com.google.common.collect.ImmutableList;
import essentialclient.gui.ConfigListWidget;
import essentialclient.feature.clientrule.ClientRuleHelper;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.utils.render.RuleWidget;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
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

public class StringListEntry extends BaseListEntry {
    private final TextFieldWidget textField;
    private boolean invalid;
    
    public StringListEntry(final ParsedRule<?> settings, final MinecraftClient client, final ServerRulesScreen gui) {
        super(settings, client, gui);
        TextFieldWidget stringField = new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, new LiteralText("Type a string value"));
        stringField.setText(settings.getAsString());
        stringField.setChangedListener(s -> this.checkForInvalid(stringField));
        this.textField = stringField;
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, settings.defaultAsString, client);
            stringField.setText(settings.defaultAsString);
        });
        gui.getStringFieldList().add(this.textField);
    }

    public StringListEntry(final ClientRules settings, final MinecraftClient client, final ClientRulesScreen gui) {
        super(settings, client, gui);
        TextFieldWidget stringField = new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, new LiteralText("Type a string value"));
        stringField.setText(settings.getString());
        stringField.setChangedListener(s -> this.checkForInvalid(stringField));
        this.textField = stringField;
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            settings.setValue(settings.defaultValue);
            stringField.setText(settings.defaultValue);
            ClientRuleHelper.writeSaveFile();
            ClientRuleHelper.executeOnChange(client, settings, gui);
        });
        gui.getStringFieldList().add(this.textField);
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.resetButton, this.textField);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of(this.resetButton, this.textField);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        return this.textField.charTyped(chr, keyCode);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ENTER KEY -> 257
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.textField.setText(this.textField.getText());
            this.textField.changeFocus(false);
            if (!this.invalid && this.serverSettings != null)
                CarpetSettingsServerNetworkHandler.ruleChange(this.serverSettings.name, this.textField.getText(), this.client);
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || this.textField.keyPressed(keyCode, scanCode, modifiers);
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

        this.textField.x = x + 182;
        this.textField.y = y + 3;
        this.textField.setEditableColor(this.invalid ? 16733525 : 16777215);
        if (invalid) {
            DiffuseLighting.enableGuiDepthLighting();
            client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.BARRIER), this.textField.x + this.textField.getWidth() - 18, this.textField.y- 1);
            DiffuseLighting.disableGuiDepthLighting();
        }

        this.textField.render(new MatrixStack(), mouseX, mouseY, delta);
        this.resetButton.render(new MatrixStack(), mouseX, mouseY, delta);
    }
    
    private void setInvalid(boolean invalid) {
        this.invalid = invalid;
        if (this.serverGui != null)
            this.serverGui.setInvalid(invalid);
        else
            this.clientGui.setInvalid(invalid);
    }
    
    private void checkForInvalid(TextFieldWidget widget) {
        boolean empty = widget.getText().isEmpty();
        if (empty) {
            if (this.serverGui != null)
                this.serverGui.setEmpty(true);
            else
                this.clientGui.setEmpty(true);
            this.setInvalid(true);
        }
        else {
            this.setInvalid(false);
            if (this.serverSettings != null)
                return;
            this.clientSettings.setValue(widget.getText());
            ClientRuleHelper.writeSaveFile();
        }
    }
}
