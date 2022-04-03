package me.senseiwells.essentialclient.feature;

import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import carpet.settings.RuleCategory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientrule.entries.*;
import me.senseiwells.essentialclient.clientrule.entries.ClientRule.Type;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.misc.NetworkUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class CarpetClient implements Config.CList {
	public static final CarpetClient INSTANCE = new CarpetClient();

	private final Map<String, ClientRule<?>> completeRuleMap;
	private final Map<String, ClientRule<?>> currentRuleMap;
	private final String LOADNT;
	private final String DATA_URL;
	private final JsonElement COMMAND;
	private final AtomicBoolean HANDLING_DATA;

	private boolean isServerCarpet;

	private CarpetClient() {
		this.completeRuleMap = new HashMap<>();
		this.currentRuleMap = new HashMap<>();
		this.LOADNT = "Could not load rule data";
		this.DATA_URL = "https://raw.githubusercontent.com/Crec0/carpet-rules-database/main/data/parsed_data.json";
		this.COMMAND = new JsonPrimitive("COMMAND");
		this.HANDLING_DATA = new AtomicBoolean(false);
		this.isServerCarpet = false;
	}

	public boolean isServerCarpet() {
		return this.isServerCarpet;
	}

	public void onDisconnect() {
		this.isServerCarpet = false;
		this.currentRuleMap.clear();
	}

	public Collection<ClientRule<?>> getCurrentCarpetRules() {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.isInSingleplayer()) {
			return this.getSinglePlayerRules();
		}
		if (client.getCurrentServerEntry() != null && this.isServerCarpet) {
			return this.currentRuleMap.values();
		}
		return this.completeRuleMap.values();
	}

	public void syncCarpetRule(NbtCompound compound) {
		this.isServerCarpet = true;
		// We run on main thread to prevent concurrent modification exceptions
		// This method will be called from the network thread
		EssentialUtils.getClient().execute(() -> {
			String name = compound.getString("Rule");
			String value = compound.getString("Value");
			ClientRule<?> clientRule = this.currentRuleMap.get(name);
			if (clientRule == null) {
				// We prioritise ParsedRule, but ClientRule's description
				ParsedRule<?> parsedRule = CarpetServer.settingsManager.getRule(name);
				clientRule = this.completeRuleMap.get(name);
				if (parsedRule != null) {
					clientRule = clientRule == null ? this.parseRuleToClientRule(parsedRule) : this.parseRuleToClientRule(parsedRule, clientRule.getDescription(), clientRule.getOptionalInfo());
				}
				else {
					// We must copy otherwise we will be modifying the global CarpetRule
					clientRule = clientRule != null ? clientRule.copy() : new StringClientRule(name, this.LOADNT, value, this.handleRuleChange(name));
				}
				this.currentRuleMap.put(name, clientRule);
				return;
			}

			this.HANDLING_DATA.set(true);
			clientRule.setValueFromString(value);
			this.HANDLING_DATA.set(false);
		});
	}

	private void handleChangeRule(String ruleName, Object value) {
		if (EssentialUtils.playerHasOp()) {
			EssentialUtils.sendChatMessage("/carpet %s %s".formatted(ruleName, value));
		}
	}

	private Collection<ClientRule<?>> getSinglePlayerRules() {
		if (this.currentRuleMap.isEmpty()) {
			this.HANDLING_DATA.set(true);
			for (ParsedRule<?> parsedRule : CarpetServer.settingsManager.getRules()) {
				ClientRule<?> clientRule = this.completeRuleMap.get(parsedRule.name);
				clientRule = clientRule == null ? this.parseRuleToClientRule(parsedRule) : this.parseRuleToClientRule(parsedRule, clientRule.getDescription(), clientRule.getOptionalInfo());
				clientRule.setValueFromString(parsedRule.getAsString());
				this.currentRuleMap.put(clientRule.getName(), clientRule);
			}
			this.HANDLING_DATA.set(false);
		}
		return this.currentRuleMap.values();
	}

	private ClientRule<?> jsonToClientRule(JsonElement jsonElement) {
		JsonObject ruleObject = jsonElement.getAsJsonObject();
		String name = ruleObject.get("name").getAsString();
		Type type = Type.fromString(ruleObject.get("type").getAsString());
		String description = ruleObject.get("description").getAsString();
		JsonElement repo = ruleObject.get("repo");
		if (repo != null) {
			description += "\n§3From: " + repo.getAsString();
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
		if (categories != null && categories.getAsJsonArray().contains(this.COMMAND) && type != Type.BOOLEAN) {
			return new CommandClientRule(name, description, defaultValue.getAsString(), value -> {
				this.handleChangeRule(name, value);
			}).setOptionalInfo(optionalInfo);
		}
		JsonElement strictElement = ruleObject.get("strict");
		if (strictElement != null && strictElement.getAsBoolean() && type != Type.BOOLEAN) {
			List<String> validValues = new ArrayList<>();
			JsonElement options = ruleObject.get("options");
			if (options.isJsonArray()) {
				JsonArray values = options.getAsJsonArray();
				values.forEach(element -> validValues.add(element.getAsString()));
				return new CycleClientRule(name, description, validValues, defaultValue.getAsString(), value -> {
					this.handleChangeRule(name, value);
				}).setOptionalInfo(optionalInfo);
			}
		}
		ClientRule<?> clientRule = switch (type) {
			case CYCLE -> {
				JsonArray validCycles = ruleObject.get("cycles").getAsJsonArray();
				List<String> cycles = new ArrayList<>();
				validCycles.forEach(element -> cycles.add(element.getAsString()));
				yield new CycleClientRule(name, description, cycles, defaultValue.getAsString(), this.handleRuleChange(name));
			}
			case BOOLEAN -> {
				yield new BooleanClientRule(name, description, defaultValue.getAsBoolean(), this.handleRuleChange(name));
			}
			case INTEGER -> {
				yield new IntegerClientRule(name, description, defaultValue.getAsInt(), this.handleRuleChange(name));
			}
			case DOUBLE -> {
				yield new DoubleClientRule(name, description, defaultValue.getAsDouble(), this.handleRuleChange(name));
			}
			default -> {
				yield new StringClientRule(name, description, defaultValue.getAsString(), this.handleRuleChange(name));
			}
		};
		return clientRule.setOptionalInfo(optionalInfo);
	}

	private ClientRule<?> parseRuleToClientRule(ParsedRule<?> parsedRule) {
		return this.parseRuleToClientRule(parsedRule, null, null);
	}

	private ClientRule<?> parseRuleToClientRule(ParsedRule<?> parsedRule, String description, String optionalInfo) {
		String name = parsedRule.name;
		Type type = Type.fromClass(parsedRule.type);
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
			return new CommandClientRule(name, description, defaultValue, this.handleRuleChange(name)).setOptionalInfo(optionalInfo);
		}
		if (parsedRule.isStrict && type != Type.BOOLEAN) {
			return new CycleClientRule(name, description, parsedRule.options, defaultValue, this.handleRuleChange(name)).setOptionalInfo(optionalInfo);
		}
		ClientRule<?> rule = switch (type) {
			case BOOLEAN -> {
				yield new BooleanClientRule(name, description, defaultValue.equals("true"), this.handleRuleChange(name));
			}
			case INTEGER -> {
				Integer intValue = EssentialUtils.catchAsNull(() -> Integer.parseInt(defaultValue));
				yield intValue == null ? null : new IntegerClientRule(name, description, intValue, this.handleRuleChange(name));
			}
			case DOUBLE -> {
				Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(defaultValue));
				yield doubleValue == null ? null : new DoubleClientRule(name, description, doubleValue, this.handleRuleChange(name));
			}
			default -> null;
		};
		return (rule != null ? rule : new StringClientRule(name, description, defaultValue, this.handleRuleChange(name))).setOptionalInfo(optionalInfo);
	}

	private <T> Consumer<T> handleRuleChange(String name) {
		return o -> {
			if (!this.HANDLING_DATA.get()) {
				this.handleChangeRule(name, o);
			}
		};
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
		this.completeRuleMap.forEach((name, rule) -> {
			JsonObject ruleObject = new JsonObject();
			ruleObject.addProperty("name", name);
			ruleObject.addProperty("type", rule.getType().name());
			ruleObject.addProperty("description", rule.getDescription());
			ruleObject.addProperty("extras", rule.getOptionalInfo());
			ruleObject.add("default", rule.getDefaultAsJson());
			if (rule instanceof CycleClientRule cycleRule) {
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
			ClientRule<?> rule = this.jsonToClientRule(element);
			this.completeRuleMap.put(rule.getName(), rule);
		});
	}
}
