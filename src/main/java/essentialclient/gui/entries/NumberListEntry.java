package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import essentialclient.gui.clientrule.ClientRuleHelper;
import essentialclient.gui.clientrule.ClientRules;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.ConfigListWidget;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import essentialclient.utils.render.ITooltipEntry;
import essentialclient.utils.render.RenderHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class NumberListEntry extends ConfigListWidget.Entry implements ITooltipEntry {
    private final ParsedRule<?> settings;
    private final ClientRules clientSettings;
    private final String rule;
    private final ButtonWidget infoButton;
    private final TextFieldWidget numberField;
    private final ButtonWidget resetButton;
    private final MinecraftClient client;
    private final ServerRulesScreen gui;
    private final ClientRulesScreen clientGui;
    private boolean invalid;
    
    public NumberListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        this.settings = settings;
        this.clientSettings = null;
        this.client = client;
        this.gui = gui;
        this.clientGui = null;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
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
        this.settings = null;
        this.clientSettings = settings;
        this.client = client;
        this.gui = null;
        this.clientGui = gui;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
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
        // ENTER KEY -> 257
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.numberField.setText(this.numberField.getText());
            this.numberField.changeFocus(false);
            if (!this.invalid && settings != null)
                CarpetSettingsServerNetworkHandler.ruleChange(settings.name, this.numberField.getText(), client);
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || this.numberField.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        TextRenderer font = client.textRenderer;
        float fontX = (float)(x + 90 - ConfigListWidget.length);
        float fontY = (float)(y + height / 2 - 9 / 2);
        font.draw(new MatrixStack(), this.rule, fontX, fontY, 16777215);

        this.resetButton.x = x + 290;
        this.resetButton.y = y;
        if (this.settings != null)
            this.resetButton.active = !this.settings.getAsString().equals(this.settings.defaultAsString) || this.invalid;
        else
            this.resetButton.active = !this.clientSettings.getString().equals(this.clientSettings.defaultValue) || this.invalid;
        
        this.numberField.x = x + 182;
        this.numberField.y = y + 3;
        this.numberField.setEditableColor(this.invalid ? 16733525 : 16777215);
        if (invalid) {
            DiffuseLighting.enableGuiDepthLighting();
            client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.BARRIER), this.numberField.x + this.numberField.getWidth() - 18, this.numberField.y- 1);
            DiffuseLighting.disableGuiDepthLighting();
        }
        
        this.infoButton.x = x + 156;
        this.infoButton.y = y;
        
        this.infoButton.render(new MatrixStack(), mouseX, mouseY, delta);
        this.numberField.render(new MatrixStack(), mouseX, mouseY, delta);
        this.resetButton.render(new MatrixStack(), mouseX, mouseY, delta);
    }
    
    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.infoButton , this.numberField, this.resetButton);
    }
    
    @Override
    public void drawTooltip(int slotIndex, int x, int y, int mouseX, int mouseY, int listWidth, int listHeight, int slotWidth, int slotHeight, float partialTicks) {
        if (this.infoButton.isHovered() && !this.infoButton.active) {
            String description;
            if (this.settings != null)
                description = this.settings.description;
            else
                description = this.clientSettings.description;
            RenderHelper.drawGuiInfoBox(client.textRenderer, description, mouseY + 5, listWidth, slotWidth, listHeight, 48);
        }
    }
    
    private void setInvalid(boolean invalid) {
        this.invalid = invalid;
        if (this.gui != null)
            this.gui.setInvalid(invalid);
        else
            this.clientGui.setInvalid(invalid);
    }
    
    private void checkForInvalid(String newValue, TextFieldWidget widget, ParsedRule<?> settings) {
        this.gui.setEmpty(widget.getText().isEmpty());
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

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of(this.infoButton , this.numberField, this.resetButton);
    }
}
