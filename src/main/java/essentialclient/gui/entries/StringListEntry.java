package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import essentialclient.gui.ConfigListWidget;
import essentialclient.gui.clientrule.ClientRule;
import essentialclient.gui.clientrule.ClientRuleHelper;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import essentialclient.utils.render.ITooltipEntry;
import essentialclient.utils.render.RenderHelper;
import com.google.common.collect.ImmutableList;
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

public class StringListEntry extends ConfigListWidget.Entry implements ITooltipEntry
{
    private final ParsedRule<?> settings;
    private final ClientRule clientSettings;
    private final String rule;
    private final ButtonWidget infoButton;
    private final TextFieldWidget textField;
    private final ButtonWidget resetButton;
    private final MinecraftClient client;
    private final ServerRulesScreen gui;
    private final ClientRulesScreen clientGui;
    private boolean invalid;
    
    public StringListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        this.settings = settings;
        this.clientSettings = null;
        this.client = client;
        this.gui = gui;
        this.clientGui = null;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
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

    public StringListEntry(final ClientRule settings, MinecraftClient client, ClientRulesScreen gui) {
        this.settings = null;
        this.clientSettings = settings;
        this.client = client;
        this.gui = null;
        this.clientGui = gui;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
        TextFieldWidget stringField = new TextFieldWidget(client.textRenderer, 0, 0, 96, 14, new LiteralText("Type a string value"));
        stringField.setText(settings.value);
        stringField.setChangedListener(s -> this.checkForInvalid(stringField));
        this.textField = stringField;
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            settings.value = settings.defaultValue;
            stringField.setText(settings.defaultValue);
            ClientRuleHelper.writeSaveFile();
        });
        gui.getStringFieldList().add(this.textField);
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
            if (!this.invalid)
                CarpetSettingsServerNetworkHandler.ruleChange(settings.name, this.textField.getText(), client);
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || this.textField.keyPressed(keyCode, scanCode, modifiers);
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
            this.resetButton.active = !this.settings.getAsString().equals(this.settings.defaultAsString);
        else
            this.resetButton.active = !this.clientSettings.value.equals(this.clientSettings.defaultValue);
        
        this.textField.x = x + 182;
        this.textField.y = y + 3;
        if (this.textField.getText().isEmpty()) {
            DiffuseLighting.enable();
            client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.BARRIER), this.textField.x + this.textField.getWidth() - 18, this.textField.y- 1);
            DiffuseLighting.disable();
        }
        
        this.infoButton.x = x + 156;
        this.infoButton.y = y;
        
        this.infoButton.render(new MatrixStack(), mouseX, mouseY, delta);
        this.textField.render(new MatrixStack(), mouseX, mouseY, delta);
        this.resetButton.render(new MatrixStack(), mouseX, mouseY, delta);
    }
    
    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.infoButton ,this.textField, this.resetButton);
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
    
    private void checkForInvalid(TextFieldWidget widget) {
        boolean empty = widget.getText().isEmpty();
        if (empty) {
            if (this.gui != null)
                this.gui.setEmpty(true);
            else
                this.clientGui.setEmpty(true);
            this.setInvalid(true);
        }
        else {
            this.setInvalid(false);
            if (this.settings != null)
                return;
            this.clientSettings.value = widget.getText();
            ClientRuleHelper.writeSaveFile();
        }
    }
}
