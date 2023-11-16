package me.senseiwells.essentialclient.utils.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class CarpetUtils {
	public static boolean isRuleCommand(CarpetRule<?> rule) {
		return rule.categories().contains("command");
	}

	public static String getRuleSettingsManagerId(CarpetRule<?> rule) {
		return rule.settingsManager().identifier();
	}

	public static String getRuleDescription(CarpetRule<?> rule) {
		return RuleHelper.translatedDescription(rule);
	}

	public static String getRuleExtraInfo(CarpetRule<?> rule) {
		StringBuilder builder = new StringBuilder();
		if (rule.extraInfo() != null) {
			for (Text info : rule.extraInfo()) {
				builder.append(info.getString()).append(" ");
			}
		}
		return builder.toString();
	}

	public static String getRuleValueAsString(CarpetRule<?> rule) {
		return RuleHelper.toRuleString(rule.value());
	}

	public static String getRuleDefaultValueAsString(CarpetRule<?> rule) {
		return RuleHelper.toRuleString(rule.defaultValue());
	}

	@Nullable
	public static CarpetRule<?> getCarpetRule(String name) {
		for (SettingsManager manager : getAllCarpetSettingsManagers()) {
			CarpetRule<?> rule = manager.getCarpetRule(name);
			if (rule != null) {
				return rule;
			}
		}
		return null;
	}

	public static void forEachCarpetRule(BiConsumer<CarpetRule<?>, String> consumer) {
		for (SettingsManager manager : getAllCarpetSettingsManagers()) {
			for (CarpetRule<?> rule : manager.getCarpetRules()) {
				String id = manager.identifier();
				consumer.accept(rule, id);
			}
		}
	}

	private static Collection<SettingsManager> getAllCarpetSettingsManagers() {
		List<SettingsManager> managers = new LinkedList<>();
		managers.add(CarpetServer.settingsManager);
		for (CarpetExtension extension : CarpetServer.extensions) {
			SettingsManager manager = extension.extensionSettingsManager();
			if (manager != null) {
				managers.add(manager);
			}
		}
		return managers;
	}
}
