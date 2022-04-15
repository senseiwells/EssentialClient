package me.senseiwells.essentialclient.feature;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import carpet.settings.RuleCategory;
import carpet.settings.SettingsManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.senseiwells.arucas.utils.NetworkUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.carpet.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.misc.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class CarpetClient implements Config.CList {
	public static final CarpetClient INSTANCE = new CarpetClient();

	private final Map<String, CarpetClientRule<?>> ALL_RULES;
	private final Map<String, CarpetClientRule<?>> CURRENT_RULES;
	private final Set<String> MANAGERS;
	private final String LOADNT;
	private final String DATA_URL;
	private final JsonElement COMMAND;
	private final AtomicBoolean HANDLING_DATA;

	private boolean isServerCarpet;

	static {
		Events.ON_DISCONNECT.register(v -> {
			INSTANCE.onDisconnect();
		});
	}

	private CarpetClient() {
		this.ALL_RULES = new HashMap<>();
		this.CURRENT_RULES = new HashMap<>();
		this.MANAGERS = new HashSet<>();
		this.LOADNT = "Could not load rule data";
		this.DATA_URL = "https://raw.githubusercontent.com/Crec0/carpet-rules-database/main/data/parsed_data.json";
		this.COMMAND = new JsonPrimitive("COMMAND");
		this.HANDLING_DATA = new AtomicBoolean(false);
		this.isServerCarpet = false;
	}

	public boolean isServerCarpet() {
		return this.isServerCarpet;
	}

	public boolean isCarpetManager(String name) {
		return this.MANAGERS.contains(name);
	}

	public void onDisconnect() {
		this.isServerCarpet = false;
		this.CURRENT_RULES.clear();
		this.MANAGERS.clear();
	}

	public Collection<CarpetClientRule<?>> getCurrentCarpetRules() {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.isInSingleplayer()) {
			return this.getSinglePlayerRules();
		}
		if (client.getCurrentServerEntry() != null && this.isServerCarpet) {
			return this.CURRENT_RULES.values();
		}
		return this.ALL_RULES.values();
	}

	public void syncCarpetRule(NbtCompound compound) {
		this.isServerCarpet = true;
		// We run on main thread to prevent concurrent modification exceptions
		// This method will be called from the network thread
		EssentialUtils.getClient().execute(() -> {
			String name = compound.getString("Rule");
			String value = compound.getString("Value");
			String manager = compound.getString("Manager");
			CarpetClientRule<?> rule = this.CURRENT_RULES.get(name);
			if (rule == null) {
				// We prioritise ParsedRule, but ClientRule's description
				ParsedRule<?> parsedRule = CarpetServer.settingsManager.getRule(name);
				rule = this.ALL_RULES.get(name);
				if (parsedRule != null) {
					rule = rule == null ? this.parseRuleToClientRule(parsedRule, manager) : this.parseRuleToClientRule(parsedRule, rule.getDescription(), rule.getOptionalInfo(), manager);
				}
				else {
					// We must copy otherwise we will be modifying the global CarpetRule
					rule = rule != null ? rule.shallowCopy() : new StringCarpetRule(name, this.LOADNT, value);
				}
				this.CURRENT_RULES.put(name, rule);
				return;
			}

			this.HANDLING_DATA.set(true);
			rule.setValueFromString(value);
			this.HANDLING_DATA.set(false);
		});
	}

	private Collection<CarpetClientRule<?>> getSinglePlayerRules() {
		if (this.CURRENT_RULES.isEmpty()) {
			this.HANDLING_DATA.set(true);

			Consumer<SettingsManager> managerProcessor = settings -> {
				for (ParsedRule<?> parsedRule : settings.getRules()) {
					CarpetClientRule<?> clientRule = this.ALL_RULES.get(parsedRule.name);
					clientRule = clientRule == null ? this.parseRuleToClientRule(parsedRule, settings.getIdentifier()) :
						this.parseRuleToClientRule(parsedRule, clientRule.getDescription(), clientRule.getOptionalInfo(), settings.getIdentifier());
					clientRule.setValueFromString(parsedRule.getAsString());
					this.CURRENT_RULES.put(clientRule.getName(), clientRule);
				}
			};

			managerProcessor.accept(CarpetServer.settingsManager);
			for (CarpetExtension extension : CarpetServer.extensions) {
				SettingsManager manager = extension.customSettingsManager();
				if (manager != null) {
					managerProcessor.accept(manager);
				}
			}

			this.HANDLING_DATA.set(false);
		}
		return this.CURRENT_RULES.values();
	}

	private CarpetClientRule<?> jsonToClientRule(JsonElement jsonElement) {
		JsonObject ruleObject = jsonElement.getAsJsonObject();
		String name = ruleObject.get("name").getAsString();
		Rule.Type type = Rule.Type.fromString(ruleObject.get("type").getAsString());
		String description = ruleObject.get("description").getAsString();
		JsonElement repo = ruleObject.get("repo");
		if (repo != null) {
			String repoString = repo.getAsString();
			description += "\n§3From: " + repoString;
		}
		String optionalInfo = null;
		JsonElement extraInfo = ruleObject.get("extras");
		if (extraInfo != null) {
			if (extraInfo.isJsonArray()) {
				StringBuilder builder = new StringBuilder();
				extraInfo.getAsJsonArray().forEach(element -> {
					if (element.isJsonPrimitive()) {
						builder.append(element.getAsString()).append(" ");
					}
				});
				optionalInfo = builder.toString();
			}
			if (extraInfo.isJsonPrimitive()) {
				optionalInfo = extraInfo.getAsString();
			}
		}
		JsonElement defaultValue = ruleObject.get("default");
		if (defaultValue == null) {
			defaultValue = ruleObject.get("value");
		}
		JsonElement categories = ruleObject.get("categories");
		if (categories != null && categories.getAsJsonArray().contains(this.COMMAND) && type != Rule.Type.BOOLEAN) {
			CycleCarpetRule rule = CycleCarpetRule.commandOf(name, description, defaultValue.getAsString());
			rule.setOptionalInfo(optionalInfo);
			return rule;
		}
		JsonElement strictElement = ruleObject.get("strict");
		if (strictElement != null && strictElement.getAsBoolean() && type != Rule.Type.BOOLEAN) {
			List<String> validValues = new ArrayList<>();
			JsonElement options = ruleObject.get("options");
			if (options.isJsonArray()) {
				JsonArray values = options.getAsJsonArray();
				values.forEach(element -> validValues.add(element.getAsString()));
				CycleCarpetRule rule = new CycleCarpetRule(name, description, validValues, defaultValue.getAsString());
				rule.setOptionalInfo(optionalInfo);
				return rule;
			}
		}
		CarpetClientRule<?> rule = switch (type) {
			case CYCLE -> {
				JsonArray validCycles = ruleObject.get("cycles").getAsJsonArray();
				List<String> cycles = new ArrayList<>();
				validCycles.forEach(element -> cycles.add(element.getAsString()));
				yield new CycleCarpetRule(name, description, cycles, defaultValue.getAsString());
			}
			case BOOLEAN -> {
				yield new BooleanCarpetRule(name, description, defaultValue.getAsBoolean());
			}
			case INTEGER -> {
				yield new IntegerCarpetRule(name, description, defaultValue.getAsInt());
			}
			case DOUBLE -> {
				yield new DoubleCarpetRule(name, description, defaultValue.getAsDouble());
			}
			default -> {
				yield new StringCarpetRule(name, description, defaultValue.getAsString());
			}
		};
		rule.setOptionalInfo(optionalInfo);
		return rule;
	}

	private CarpetClientRule<?> parseRuleToClientRule(ParsedRule<?> parsedRule, String manager) {
		return this.parseRuleToClientRule(parsedRule, null, null, manager);
	}

	private CarpetClientRule<?> parseRuleToClientRule(ParsedRule<?> parsedRule, String description, String optionalInfo, String manager) {
		String name = parsedRule.name;
		Rule.Type type = Rule.Type.fromClass(parsedRule.type);
		String defaultValue = parsedRule.defaultAsString;
		if (description == null) {
			description = parsedRule.description;
		}
		if (optionalInfo == null && !parsedRule.extraInfo.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (String info : parsedRule.extraInfo) {
				builder.append(info).append(" ");
			}
			optionalInfo = builder.toString();
		}
		if (parsedRule.categories.contains(RuleCategory.COMMAND)) {
			CycleCarpetRule rule = CycleCarpetRule.commandOf(name, description, defaultValue);
			rule.setOptionalInfo(optionalInfo);
			return rule;
		}
		if (parsedRule.isStrict && type != Rule.Type.BOOLEAN) {
			CycleCarpetRule rule = new CycleCarpetRule(name, description, parsedRule.options, defaultValue);
			rule.setOptionalInfo(optionalInfo);
			return rule;
		}

		CarpetClientRule<?> rule = switch (type) {
			case BOOLEAN -> {
				yield new BooleanCarpetRule(name, description, defaultValue.equals("true"));
			}
			case INTEGER -> {
				Integer intValue = EssentialUtils.catchAsNull(() -> Integer.parseInt(defaultValue));
				yield intValue == null ? null : new IntegerCarpetRule(name, description, intValue);
			}
			case DOUBLE -> {
				Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(defaultValue));
				yield doubleValue == null ? null : new DoubleCarpetRule(name, description, doubleValue);
			}
			default -> null;
		};

		if (rule == null) {
			rule = new StringCarpetRule(name, description, defaultValue);
		}

		this.MANAGERS.add(manager);
		rule.setCustomManager(manager);
		rule.setOptionalInfo(optionalInfo);
		return rule;
	}

	private JsonArray getData(JsonArray fallback) {
		String content = NetworkUtils.getStringFromUrl(this.DATA_URL);
		if (content == null) {
			EssentialClient.LOGGER.error("Failed to fetch rule data");
			return fallback;
		}
		return this.GSON.fromJson(content, JsonArray.class);
	}

	@Override
	public String getConfigName() {
		return "CarpetClient";
	}

	@Override
	public JsonElement getSaveData() {
		JsonArray ruleData = new JsonArray();
		this.ALL_RULES.forEach((name, rule) -> {
			JsonObject ruleObject = new JsonObject();
			ruleObject.addProperty("name", name);
			ruleObject.addProperty("type", rule.getType().name());
			ruleObject.addProperty("description", rule.getDescription());
			ruleObject.addProperty("extras", rule.getOptionalInfo());
			ruleObject.add("default", rule.getDefaultValueAsJson());
			if (rule instanceof CycleCarpetRule cycleRule) {
				JsonArray validCycles = new JsonArray();
				cycleRule.getCycleValues().forEach(validCycles::add);
				ruleObject.add("cycles", validCycles);
			}
			ruleData.add(ruleObject);
		});
		return ruleData;
	}

	@Override
	public void readConfig(JsonArray jsonElement) {
		JsonArray jsonArray = this.getData(jsonElement.getAsJsonArray());
		jsonArray.forEach(element -> {
			CarpetClientRule<?> rule = this.jsonToClientRule(element);
			this.ALL_RULES.put(rule.getName(), rule);
		});
	}
}
