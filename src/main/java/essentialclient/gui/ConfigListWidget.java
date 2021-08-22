package essentialclient.gui;

import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import essentialclient.gui.clientrule.ClientRule;
import essentialclient.gui.entries.BooleanListEntry;
import essentialclient.gui.entries.NumberListEntry;
import essentialclient.gui.entries.StringListEntry;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.rulescreen.GameRulesScreen;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.render.ITooltipEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigListWidget extends ElementListWidget<ConfigListWidget.Entry> {
    public static int length;
    private final List<ConfigListWidget.Entry> entries = new ArrayList<>();
    
    public ConfigListWidget(ServerRulesScreen gui, MinecraftClient client) {
        super(client, gui.width + 45, gui.height, 43, gui.height - 32, 20);
        Collection<ParsedRule<?>> rules = CarpetServer.settingsManager.getRules();
        rules.forEach(r -> {
            int i = client.textRenderer.getWidth(r.name) - 50;
            if (i > length) {
                length = i;
            }
            if (r.type == boolean.class) {
                BooleanListEntry booleanList = new BooleanListEntry(r, client, gui);
                this.addEntry(booleanList);
                this.entries.add(booleanList);
            }
            else if (r.type == int.class || r.type == double.class) {
                NumberListEntry numberList = new NumberListEntry(r, client, gui);
                this.addEntry(numberList);
                this.entries.add(numberList);
            }
            else {
                StringListEntry stringList = new StringListEntry(r, client, gui);
                this.addEntry(stringList);
                this.entries.add(stringList);
            }
        });
    }

    public ConfigListWidget(ClientRulesScreen gui, MinecraftClient client) {
        super(client, gui.width + 45, gui.height, 43, gui.height - 32, 20);
        Collection<ClientRule> clientRuleCollection = ClientRule.getRules();
        clientRuleCollection.forEach(clientRules -> {
            int i = client.textRenderer.getWidth(clientRules.name) - 55;
            if (i > length) {
                length = i;
            }
            if (clientRules.type.equalsIgnoreCase("boolean")) {
                BooleanListEntry booleanList = new BooleanListEntry(clientRules, client, gui);
                this.addEntry(booleanList);
                this.entries.add(booleanList);
            }
            else if (clientRules.type.equalsIgnoreCase("number")) {
                NumberListEntry numberList = new NumberListEntry(clientRules, client, gui);
                this.addEntry(numberList);
                this.entries.add(numberList);
            }
            else if (clientRules.type.equalsIgnoreCase("string")) {
                StringListEntry stringList = new StringListEntry(clientRules, client, gui);
                this.addEntry(stringList);
                this.entries.add(stringList);
            }
        });
    }

    public ConfigListWidget(GameRulesScreen gui, MinecraftClient client) {
        super(client, gui.width + 45, gui.height, 43, gui.height - 32, 20);
        //GameRules serverRules = CarpetServer.minecraft_server.getGameRules();
        //todo
    }


    @Override
    protected int getScrollbarPositionX() {
        return this.width / 2 + getRowWidth() / 2 + 4;
    }

    @Override
    public int getRowWidth() {
        return 180 * 2;
    }
    
    private int getSize() {
        return this.entries.size();
    }
    
    private ParentElement getListEntry(int i) {
        return this.entries.get(i);
    }
    
    public void drawTooltip(int mouseX, int mouseY, float delta) {
        int insideLeft = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
        int insideTop = this.top + 4 - (int) this.getScrollAmount();
        int l = this.itemHeight - 4;
        
        for (int i = 0; i < this.getSize(); i++) {
            int k = insideTop + i * this.itemHeight + this.headerHeight;
            
            ParentElement entry = getListEntry(i);
            if (entry instanceof ITooltipEntry) {
                ((ITooltipEntry) entry).drawTooltip(i, insideLeft, k, mouseX, mouseY, this.getRowWidth(), this.height, this.width, l, delta);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.Entry<ConfigListWidget.Entry> {
    
    }
}
