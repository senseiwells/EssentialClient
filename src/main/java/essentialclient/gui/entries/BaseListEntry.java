package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import com.google.common.collect.ImmutableList;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.gui.ConfigListWidget;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.render.ITooltipEntry;
import essentialclient.utils.render.RenderHelper;
import essentialclient.utils.render.RuleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public abstract class BaseListEntry extends ConfigListWidget.Entry implements ITooltipEntry {
    protected final ParsedRule<?> serverSettings;
    protected final ClientRules clientSettings;
    protected final String rule;
    protected final ServerRulesScreen serverGui;
    protected final ClientRulesScreen clientGui;
    protected final MinecraftClient client;
    protected RuleWidget ruleWidget;
    protected ButtonWidget editButton;
    protected ButtonWidget resetButton;


    public BaseListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        this.serverSettings = settings;
        this.client = client;
        this.serverGui = gui;
        this.rule = settings.name;
        this.clientGui = null;
        this.clientSettings = null;
    }

    public BaseListEntry(final ClientRules settings, MinecraftClient client, ClientRulesScreen gui) {
        this.clientSettings = settings;
        this.client = client;
        this.clientGui = gui;
        this.rule = settings.name;
        this.serverGui = null;
        this.serverSettings = null;
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

        assert this.clientSettings != null;
        this.resetButton.active = this.serverSettings == null ? !this.clientSettings.getString().equals(this.clientSettings.defaultValue) : !this.serverSettings.getAsString().equals(this.serverSettings.defaultAsString);

        this.editButton.x = x + 180;
        this.editButton.y = y;

        this.editButton.render(matrices, mouseX, mouseY, delta);
        this.resetButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void drawTooltip(int slotIndex, int x, int y, int mouseX, int mouseY, int listWidth, int listHeight, int slotWidth, int slotHeight, float partialTicks) {
        if (this.ruleWidget != null && this.ruleWidget.isHovered(mouseX, mouseY)) {
            assert this.clientSettings != null;
            String description = this.serverSettings == null ? this.clientSettings.description : this.serverSettings.description;
            RenderHelper.drawGuiInfoBox(client.textRenderer, description, mouseX, mouseY);
        }
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.editButton, this.resetButton);
    }


    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of(this.editButton, this.resetButton);
    }
}
