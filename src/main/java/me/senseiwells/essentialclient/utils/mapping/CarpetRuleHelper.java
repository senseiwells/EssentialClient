package me.senseiwells.essentialclient.utils.mapping;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.RuleHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

//#if MC >= 11902
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
//#else
//$$import carpet.settings.ParsedRule;
//$$import carpet.settings.SettingsManager;
//#endif

public class CarpetRuleHelper {
	public static Wrapper getCarpetRule(String name) {
		for (SettingsManager manager : getAllCarpetSettingsManagers()) {
			//#if MC >= 11902
			CarpetRule<?> rule = manager.getCarpetRule(name);
			//#else
			//$$ParsedRule<?> rule = manager.getRule(name);
			//#endif
			if (rule != null) {
				return new Wrapper(rule);
			}
		}
		return null;
	}

	public static void forEachCarpetRule(BiConsumer<Wrapper, String> consumer) {
		for (SettingsManager manager : getAllCarpetSettingsManagers()) {
			//#if MC >= 11902
			for (CarpetRule<?> rule : manager.getCarpetRules()) {
				String id = manager.identifier();
				//#else
				//$$for (ParsedRule<?> rule : manager.getRules()) {
				//$$String id = manager.getIdentifier();
				//#endif
				consumer.accept(new Wrapper(rule), id);
			}
		}
	}

	public static void registerCarpetRuleObserver(WrappedRuleObserver observer) {
		//#if MC >= 11902
		SettingsManager.registerGlobalRuleObserver((source, rule, value) -> {
			//#else
			//$$SettingsManager.addGlobalRuleObserver((source, rule, value) -> {
			//#endif
			observer.trigger(source, new Wrapper(rule), value);
		});
	}

	private static Collection<SettingsManager> getAllCarpetSettingsManagers() {
		ArrayList<SettingsManager> managers = new ArrayList<>();
		managers.add(CarpetServer.settingsManager);
		for (CarpetExtension extension : CarpetServer.extensions) {
			//#if MC >= 11902
			SettingsManager manager = extension.extensionSettingsManager();
			//#else
			//$$SettingsManager manager = extension.customSettingsManager();
			//#endif
			managers.add(manager);
		}
		return managers;
	}

	public static class Wrapper {
		//#if MC >= 11902
		private final CarpetRule<?> rule;
		//#else
		//$$private final ParsedRule<?> rule;
		//#endif

		//#if MC >= 11902
		private Wrapper(CarpetRule<?> rule) {
			//#else
			//$$private Wrapper(ParsedRule<?> rule) {
			//#endif
			this.rule = rule;
		}

		public String getName() {
			//#if MC >= 11902
			return this.rule.name();
			//#else
			//$$return this.rule.name;
			//#endif
		}

		public String getDescription() {
			//#if MC >= 11902
			return RuleHelper.translatedDescription(this.rule);
			//#else
			//$$return this.rule.name;
			//#endif
		}

		public Class<?> getType() {
			//#if MC >= 11902
			return this.rule.type();
			//#else
			//$$return this.rule.type;
			//#endif
		}

		public String getExtraInfo() {
			StringBuilder builder = new StringBuilder();
			//#if MC >= 11902
			if (this.rule.extraInfo() != null) {
				for (Text info : this.rule.extraInfo()) {
					builder.append(info.getString()).append(" ");
				}
			}
			//#else
			//$$if (this.rule.extraInfo != null) {
			//$$	for (String info : this.rule.extraInfo) {
			//$$		builder.append(info).append(" ");
			//$$	}
			//$$}
			//#endif
			return builder.toString();
		}

		public Collection<String> getCategories() {
			//#if MC >= 11902
			return this.rule.categories();
			//#else
			//$$return this.rule.categories;
			//#endif
		}

		public Object getValue() {
			//#if MC >= 11902
			return this.rule.value();
			//#else
			//$$return this.rule.get();
			//#endif
		}

		public Object getDefaultValue() {
			//#if MC >= 11902
			return this.rule.defaultValue();
			//#else
			//$$return this.rule.defaultValue;
			//#endif
		}

		public String getValueAsString() {
			//#if MC >= 11902
			return RuleHelper.toRuleString(this.getValue());
			//#else
			//$$return this.rule.getAsString();
			//#endif
		}

		public String getDefaultValueAsString() {
			//#if MC >= 11902
			return RuleHelper.toRuleString(this.getDefaultValue());
			//#else
			//$$return this.rule.defaultAsString;
			//#endif
		}

		public String getSettingsManagerId() {
			//#if MC >= 11902
			return this.rule.settingsManager().identifier();
			//#else
			//$$return this.rule.settingsManager.getIdentifier();
			//#endif
		}

		public boolean isCommand() {
			return this.getCategories().contains("command");
		}
	}

	@FunctionalInterface
	public interface WrappedRuleObserver {
		void trigger(ServerCommandSource source, Wrapper rule, String value);
	}
}
