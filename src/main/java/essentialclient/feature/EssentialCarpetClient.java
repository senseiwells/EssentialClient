package essentialclient.feature;

import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import essentialclient.config.clientrule.*;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EssentialCarpetClient {
	public static boolean serverIsCarpet = false;

	public static Map<String, ClientRule<?>> carpetRules = new HashMap<>();

	public static void synchronizeRules(NbtCompound ruleNBT) {
		serverIsCarpet = true;
		String ruleName = ruleNBT.getString("Rule");
		String ruleValue = ruleNBT.getString("Value");
		ParsedRule<?> rule = CarpetServer.settingsManager.getRule(ruleName);
		String ruleDescription = rule != null ? rule.description : "Please install the carpet extension that has %s on the client to see this description".formatted(ruleName);
		try {
			if (carpetRules.containsKey(ruleName)) {
				carpetRules.get(ruleName).setValueFromString(ruleValue);
				return;
			}
		}
		catch (Exception ignored) { }
		if (ruleName.contains("command")) {
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
		if ((isRuleTrue || ruleValue.equals("false"))) {
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
