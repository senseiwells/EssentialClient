package essentialclient.gui;

import essentialclient.clientrule.ClientRules;
import essentialclient.clientrule.entries.BooleanClientRule;
import essentialclient.clientrule.entries.ClientRule;
import essentialclient.clientrule.entries.CycleClientRule;
import essentialclient.feature.CarpetClient;
import essentialclient.gui.entries.*;
import essentialclient.gui.rulescreen.RulesScreen;
import essentialclient.utils.render.RuleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.Collection;
import java.util.Locale;

public class ConfigListWidget extends ElementListWidget<ConfigListWidget.Entry> {
	public static int length = 0;

	public ConfigListWidget(RulesScreen rulesScreen, MinecraftClient client, String filter) {
		super(client, rulesScreen.width + 45, rulesScreen.height, 43, rulesScreen.height - 32, 20);
		this.reloadEntries(rulesScreen, filter);
	}

	public void reloadEntries(RulesScreen rulesScreen, String filter) {
		this.clearEntries();
		Collection<ClientRule<?>> clientRules = rulesScreen.isServerScreen() ?
			ClientRules.sortRulesAlphabetically(CarpetClient.INSTANCE.getCurrentCarpetRules()) : ClientRules.getCurrentClientRules();

		clientRules.forEach(rule -> {
			String ruleName = rule.getName();
			if (filter != null && !ruleName.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) {
				return;
			}
			int i = this.client.textRenderer.getWidth(RuleWidget.getShortName(ruleName)) - 55;
			if (i > length) {
				length = i;
			}
			BaseListEntry<?> entry = switch (rule.getType()) {
				case BOOLEAN -> new BooleanListEntry((BooleanClientRule) rule, this.client, rulesScreen);
				case INTEGER, DOUBLE -> new NumberListEntry(rule, this.client, rulesScreen);
				case STRING -> new StringListEntry(rule, this.client, rulesScreen);
				case CYCLE -> new CycleListEntry((CycleClientRule) rule, this.client, rulesScreen);
				default -> throw new IllegalStateException("Unexpected value: " + rule.getType());
			};
			this.addEntry(entry);
		});
	}

	public void updateAllEntriesOnClose() {
		this.children().forEach(Entry::updateEntryOnClose);
	}

	@Override
	protected int getScrollbarPositionX() {
		return this.width / 2 + this.getRowWidth() / 2 + 4;
	}

	@Override
	public int getRowWidth() {
		return 360;
	}

	public abstract static class Entry extends ElementListWidget.Entry<ConfigListWidget.Entry> {
		public abstract void updateEntryOnClose();
	}
}
