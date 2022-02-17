package essentialclient.feature;

import carpet.CarpetServer;
import carpet.settings.ParsedRule;
import carpet.settings.RuleCategory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import essentialclient.EssentialClient;
import essentialclient.clientrule.entries.*;
import essentialclient.clientrule.entries.ClientRule.Type;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.config.Config;
import essentialclient.utils.misc.NetworkUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;
import java.util.*;

public class CarpetClient implements Config {
	public static final CarpetClient INSTANCE = new CarpetClient();

	private final Map<String, ClientRule<?>> completeRuleMap;
	private final Map<String, ClientRule<?>> currentRuleMap;
	private final String LOADNT;
	private final String DATA_URL;
	private final JsonElement COMMAND;

	private boolean isServerCarpet;

	private CarpetClient() {
		this.completeRuleMap = new HashMap<>();
		this.currentRuleMap = new HashMap<>();
		this.LOADNT = "Could not load rule data";
		this.DATA_URL = "https://raw.githubusercontent.com/Crec0/carpet-rules-database/main/data/parsed_data.json";
		this.COMMAND = new JsonPrimitive("COMMAND");
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
				clientRule = this.completeRuleMap.get(name);
				if (clientRule == null) {
					ParsedRule<?> parsedRule = CarpetServer.settingsManager.getRule(name);
					clientRule = parsedRule != null ? this.parseRuleToClientRule(parsedRule) : new StringClientRule(name, this.LOADNT, value, v -> {
						this.handleChangeRule(name, v);
					});
				}
				else {
					// We must copy otherwise we will be modifying the global CarpetRule
					clientRule = clientRule.copy();
				}
				this.currentRuleMap.put(name, clientRule);
				return;
			}

			if (!clientRule.getValue().toString().equals(value)) {
				clientRule.setValueFromString(value);
			}
		});
	}

	private void handleChangeRule(String ruleName, Object value) {
		if (EssentialUtils.playerHasOp()) {
			EssentialUtils.sendChatMessage("/carpet %s %s".formatted(ruleName, value));
		}
	}

	private Collection<ClientRule<?>> getSinglePlayerRules() {
		List<ClientRule<?>> clientRules = new ArrayList<>();
		for (ParsedRule<?> parsedRule : CarpetServer.settingsManager.getRules()) {
			ClientRule<?> clientRule = this.completeRuleMap.get(parsedRule.name);
			if (clientRule == null) {
				clientRule = this.parseRuleToClientRule(parsedRule);
			}
			else {
				// We must copy otherwise we will be modifying the global CarpetRule
				clientRule = clientRule.copy();
			}
			clientRule.setValueFromString(parsedRule.getAsString());
			clientRules.add(clientRule);
		}
		return clientRules;
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
		JsonElement defaultValue = ruleObject.get("default");
		if (defaultValue == null) {
			defaultValue = ruleObject.get("value");
		}
		JsonElement categories = ruleObject.get("categories");
		if (categories != null && categories.getAsJsonArray().contains(this.COMMAND) && type != Type.BOOLEAN) {
			return new CommandClientRule(name, description, defaultValue.getAsString(), value -> {
				this.handleChangeRule(name, value);
			});
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
				});
			}
		}
		return switch (type) {
			case CYCLE -> {
				JsonArray validCycles = ruleObject.get("cycles").getAsJsonArray();
				List<String> cycles = new ArrayList<>();
				validCycles.forEach(element -> cycles.add(element.getAsString()));
				yield new CycleClientRule(name, description, cycles, defaultValue.getAsString(), value -> {
					this.handleChangeRule(name, value);
				});
			}
			case BOOLEAN -> {
				yield new BooleanClientRule(name, description, defaultValue.getAsBoolean(), value -> {
					this.handleChangeRule(name, value);
				});
			}
			case INTEGER -> {
				yield new IntegerClientRule(name, description, defaultValue.getAsInt(), value -> {
					this.handleChangeRule(name, value);
				});
			}
			case DOUBLE -> {
				yield new DoubleClientRule(name, description, defaultValue.getAsDouble(), value -> {
					this.handleChangeRule(name, value);
				});
			}
			default -> {
				yield new StringClientRule(name, description, defaultValue.getAsString(), value -> {
					this.handleChangeRule(name, value);
				});
			}
		};
	}

	private ClientRule<?> parseRuleToClientRule(ParsedRule<?> parsedRule) {
		String name = parsedRule.name;
		Type type = Type.fromClass(parsedRule.type);
		String description = parsedRule.description;
		String defaultValue = parsedRule.defaultAsString;
		if (parsedRule.categories.contains(RuleCategory.COMMAND)) {
			return new CommandClientRule(name, description, defaultValue, value -> {
				this.handleChangeRule(name, value);
			});
		}
		if (parsedRule.isStrict && type != Type.BOOLEAN) {
			return new CycleClientRule(name, description, parsedRule.options, defaultValue, value -> {
				this.handleChangeRule(name, value);
			});
		}
		ClientRule<?> rule = switch (type) {
			case BOOLEAN -> {
				yield new BooleanClientRule(name, description, defaultValue.equals("true"), value -> {
					this.handleChangeRule(name, value);
				});
			}
			case INTEGER -> {
				Integer intValue = EssentialUtils.catchAsNull(() -> Integer.parseInt(defaultValue));
				yield intValue == null ? null : new IntegerClientRule(name, description, intValue, value -> {
					this.handleChangeRule(name, value);
				});
			}
			case DOUBLE -> {
				Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(defaultValue));
				yield doubleValue == null ? null : new DoubleClientRule(name, description, doubleValue, value -> {
					this.handleChangeRule(name, value);
				});
			}
			default -> null;
		};
		return rule != null ? rule : new StringClientRule(name, description, defaultValue, value -> {
			this.handleChangeRule(name, value);
		});
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
	public Path getConfigPath() {
		return this.getConfigRootPath().resolve("CarpetClient.json");
	}

	@Override
	public JsonArray getSaveData() {
		JsonArray ruleData = new JsonArray();
		this.completeRuleMap.forEach((name, rule) -> {
			JsonObject ruleObject = new JsonObject();
			ruleObject.addProperty("name", name);
			ruleObject.addProperty("type", rule.getType().name());
			ruleObject.addProperty("description", rule.getDescription());
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
	public void readConfig(JsonArray jsonArray) {
		jsonArray = this.getData(jsonArray);
		jsonArray.forEach(jsonElement -> {
			ClientRule<?> rule = this.jsonToClientRule(jsonElement);
			this.completeRuleMap.put(rule.getName(), rule);
		});
	}
}
