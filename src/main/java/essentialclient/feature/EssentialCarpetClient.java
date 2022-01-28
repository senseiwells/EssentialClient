package essentialclient.feature;

import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import carpet.settings.RuleCategory;
import essentialclient.config.clientrule.*;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

public class EssentialCarpetClient {
	public static boolean serverIsCarpet = false;

	public static Map<String, ClientRule<?>> carpetRules = new HashMap<>();

	public static void synchronizeRules(NbtCompound ruleNBT) {
		serverIsCarpet = true;
		String ruleName = ruleNBT.getString("Rule");
		String ruleValue = ruleNBT.getString("Value");
		ParsedRule<?> rule = CarpetServer.settingsManager.getRule(ruleName);
		boolean ruleNotNull = rule != null;
		String ruleDescription = ruleNotNull ? rule.description : "Please install the carpet extension that has %s on the client to see this description".formatted(ruleName);
		try {
			if (carpetRules.containsKey(ruleName)) {
				carpetRules.get(ruleName).setValueFromString(ruleValue);
				return;
			}
		}
		catch (Exception ignored) { }
		if ((ruleNotNull && rule.categories.contains(RuleCategory.COMMAND) || (!ruleNotNull && ruleName.contains("command")))) {
			List<String> commandOptions = new ArrayList<>() {{
				this.add("true");
				this.add("false");
				this.add("ops");
			}};
			if (commandOptions.contains(ruleValue)) {
				commandOptions.remove(ruleValue);
				commandOptions.add(0, ruleValue);
				carpetRules.put(ruleName, new CycleClientRule(ruleName, ruleDescription, commandOptions));
				return;
			}
		}
		boolean isRuleTrue = ruleValue.equals("true");
		if ((ruleNotNull && rule.type == boolean.class) || (!ruleNotNull && (isRuleTrue || ruleValue.equals("false")))) {
			carpetRules.put(ruleName, new BooleanClientRule(ruleName, ruleDescription, isRuleTrue, null, false));
			return;
		}
		try {
			int intValue = Integer.parseInt(ruleValue);
			double doubleValue = Double.parseDouble(ruleValue);
			if (intValue == doubleValue) {
				carpetRules.put(ruleName, new IntegerClientRule(ruleName, ruleDescription, intValue, false));
				return;
			}
			carpetRules.put(ruleName, new DoubleClientRule(ruleName, ruleDescription, doubleValue, false));
			return;
		}
		catch (NumberFormatException ignored) { }

		carpetRules.put(ruleName, new StringClientRule(ruleName, ruleDescription, ruleValue, null, false));
	}

	public static Set<ClientRule<?>> getSinglePlayerRules() {
		Set<ClientRule<?>> clientRules = new HashSet<>();
		for (ParsedRule<?> parsedRule : CarpetServer.settingsManager.getRules()) {
			String ruleDescription = parsedRule.description;
			String ruleValue = parsedRule.getAsString();
			String ruleName = parsedRule.name;
			String ruleDefault = parsedRule.defaultAsString;
			if (parsedRule.categories.contains(RuleCategory.COMMAND)) {
				List<String> commandOptions = new ArrayList<>() {{
					this.add("true");
					this.add("false");
					this.add("ops");
				}};
				if (commandOptions.contains(ruleDefault)) {
					commandOptions.remove(ruleDefault);
					commandOptions.add(0, ruleDefault);
					CycleClientRule cycleClientRule = new CycleClientRule(ruleName, ruleDescription, commandOptions);
					cycleClientRule.setValue(ruleValue);
					clientRules.add(cycleClientRule);
					continue;
				}
			}
			if (parsedRule.type == boolean.class) {
				BooleanClientRule booleanClientRule = new BooleanClientRule(ruleName, ruleDescription, ruleDefault.equals("true"), null, false);
				booleanClientRule.setValueFromString(ruleValue);
				clientRules.add(booleanClientRule);
				continue;
			}
			if (parsedRule.type == byte.class || parsedRule.type == short.class || parsedRule.type == int.class || parsedRule.type == long.class) {
				IntegerClientRule integerClientRule = new IntegerClientRule(ruleName, ruleDescription, (int) parsedRule.defaultValue, false);
				integerClientRule.setValue((int) parsedRule.get());
				clientRules.add(integerClientRule);
				continue;
			}
			if (parsedRule.type == float.class || parsedRule.type == double.class) {
				DoubleClientRule doubleClientRule = new DoubleClientRule(ruleName, ruleDescription, (double) parsedRule.defaultValue, false);
				doubleClientRule.setValue((double) parsedRule.get());
				clientRules.add(doubleClientRule);
				continue;
			}
			StringClientRule stringClientRule = new StringClientRule(ruleName, ruleDescription, parsedRule.defaultAsString, null, false);
			stringClientRule.setValue(parsedRule.getAsString());
			clientRules.add(stringClientRule);
		}
		return clientRules;
	}

	public static boolean canChangeRule() {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		return player != null && player.hasPermissionLevel(2);
	}

	// Returns whether it failed
	public static boolean handleRuleChange(String ruleName, String value) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		if (!canChangeRule()) {
			return true;
		}
		player.sendChatMessage("/carpet %s %s".formatted(ruleName, value));
		return false;
	}
}
