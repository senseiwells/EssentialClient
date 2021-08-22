package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import essentialclient.gui.clientruleformat.BooleanClientRule;
import essentialclient.gui.clientruleformat.BooleanClientRuleHelper;
import essentialclient.gui.rulesscreen.ClientRulesScreen;
import essentialclient.gui.ConfigListWidget;
import essentialclient.gui.rulesscreen.ServerRulesScreen;
import com.google.common.collect.ImmutableList;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import essentialclient.utils.render.ITooltipEntry;
import essentialclient.utils.render.RenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BooleanListEntry extends ConfigListWidget.Entry implements ITooltipEntry {
    private final ParsedRule<?> settings;
    private final BooleanClientRule clientSettings;
    private final String rule;
    private final ServerRulesScreen gui;
    private final ClientRulesScreen clientGui;
    private final ButtonWidget infoButton;
    private final ButtonWidget editButton;
    private final ButtonWidget resetButton;
    private final MinecraftClient client;


    public BooleanListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        this.settings = settings;
        this.clientSettings = null;
        this.client = client;
        this.gui = gui;
        this.clientGui = null;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(settings.getAsString()), (buttonWidget) -> {
            String invertedBoolean = String.valueOf(!Boolean.parseBoolean(buttonWidget.getMessage().getString()));
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, invertedBoolean, client);
            buttonWidget.setMessage(new LiteralText(invertedBoolean));
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, settings.defaultAsString, client);
            this.editButton.setMessage(new LiteralText(settings.defaultAsString));
        });
    }

    public BooleanListEntry(final BooleanClientRule settings, MinecraftClient client, ClientRulesScreen gui) {
        this.settings = null;
        this.clientSettings = settings;
        this.client = client;
        this.gui = null;
        this.clientGui = gui;
        this.rule = settings.name;
        this.infoButton = new ButtonWidget(0, 0, 14, 20, new LiteralText("i"), (button -> button.active = false));
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(String.valueOf(settings.value)), (buttonWidget) -> {
            settings.invertBoolean();
            buttonWidget.setMessage(new LiteralText(String.valueOf(settings.value)));
            BooleanClientRuleHelper.writeSaveFile(BooleanClientRule.clientBooleanRulesMap);
            if (settings.isCommand && client.getNetworkHandler() != null) {
                client.inGameHud.getChatHud().addMessage(new LiteralText("§cRelog for client command changes to take full effect"));
                /* The game still suggests the command but it registers it as invalid, idk how to fix :P
                 * MinecraftClient.getInstance().getNetworkHandler().onCommandTree(new CommandTreeS2CPacket((RootCommandNode<CommandSource>) (Object) ClientCommandManager.DISPATCHER.getRoot()));
                 * MinecraftClient.getInstance().getNetworkHandler().onCommandSuggestions(new CommandSuggestionsS2CPacket());
                 */
            }
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            settings.value = settings.defaultValue;
            BooleanClientRuleHelper.writeSaveFile(BooleanClientRule.clientBooleanRulesMap);
            this.editButton.setMessage(new LiteralText(settings.defaultAsString));
        });
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        TextRenderer font = client.textRenderer;
        float fontX = (float)(x + 90 - ConfigListWidget.length);
        float fontY = (float)(y + height / 2 - 9 / 2);
        font.draw(matrices, this.rule, fontX, fontY, 16777215);
        
        this.resetButton.x = x + 290;
        this.resetButton.y = y;
        if (this.settings != null)
            this.resetButton.active = !this.settings.getAsString().equals(this.settings.defaultAsString);
        else
            this.resetButton.active = this.clientSettings.value != this.clientSettings.defaultValue;
        
        this.editButton.x = x + 180;
        this.editButton.y = y;
        
        this.infoButton.x = x + 156;
        this.infoButton.y = y;
        
        this.infoButton.render(matrices, mouseX, mouseY, delta);
        this.editButton.render(matrices, mouseX, mouseY, delta);
        this.resetButton.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.infoButton ,this.editButton, this.resetButton);
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
}
