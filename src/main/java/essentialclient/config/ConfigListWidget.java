package essentialclient.config;

import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import essentialclient.config.clientrule.*;
import essentialclient.config.entries.BooleanListEntry;
import essentialclient.config.entries.CycleListEntry;
import essentialclient.config.entries.NumberListEntry;
import essentialclient.config.entries.StringListEntry;
import essentialclient.config.rulescreen.ClientRulesScreen;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.config.rulescreen.ServerRulesScreen;
import essentialclient.utils.render.ITooltipEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class ConfigListWidget extends ElementListWidget<ConfigListWidget.Entry> {
    public static int length;
    private final List<ConfigListWidget.Entry> entries = new ArrayList<>();
    
    public ConfigListWidget(RulesScreen gui, MinecraftClient client, String filter) {
        super(client, gui.width + 45, gui.height, 43, gui.height - 32, 20);
        if (gui instanceof ServerRulesScreen) {
            Collection<ParsedRule<?>> rules = CarpetServer.settingsManager.getRules();
            rules.forEach(r -> {
                if (filter == null || r.name.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) {
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
                }
            });
        }
        if (gui instanceof ClientRulesScreen) {
            Collection<ClientRule<?>> clientRules = switch (ClientRules.DISPLAY_RULE_TYPE.getValue()) {
                case "Rule Type" -> ClientRules.getMapInType();
                case "Alphabetical" -> ClientRules.getMapAlphabetically();
                default -> ClientRules.getMapAlphabetically();
            };
            clientRules.forEach(rule -> {
                if (filter == null || rule.getName().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) {
                    int i = client.textRenderer.getWidth(rule.getName()) - 55;
                    if (i > length) {
                        length = i;
                    }
                    switch (rule.getType()) {
                        case BOOLEAN -> {
                            BooleanListEntry booleanList = new BooleanListEntry((BooleanClientRule) rule, client, gui);
                            this.addEntry(booleanList);
                            this.entries.add(booleanList);
                        }
                        case INTEGER, DOUBLE -> {
                            NumberListEntry numberList = new NumberListEntry((NumberClientRule<?>) rule, client, gui);
                            this.addEntry(numberList);
                            this.entries.add(numberList);
                        }
                        case STRING -> {
                            StringListEntry stringList = new StringListEntry((StringClientRule) rule, client, gui);
                            this.addEntry(stringList);
                            this.entries.add(stringList);
                        }
                        case CYCLE -> {
                            CycleListEntry cycleList = new CycleListEntry((CycleClientRule) rule, client, gui);
                            this.addEntry(cycleList);
                            this.entries.add(cycleList);
                        }
                    }
                }
            });
        }
    }

    public void clear() {
        this.clearEntries();
        this.entries.clear();
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
