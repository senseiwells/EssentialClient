package me.senseiwells.essentialclient.feature;

import carpet.CarpetExtension;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.senseiwells.arucas.utils.NetworkUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.rule.carpet.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.carpet.CarpetUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.misc.Scheduler;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CarpetClient implements CarpetExtension, Config.CList {
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
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> INSTANCE.onDisconnect());

		SettingsManager.registerGlobalRuleObserver((source, rule, value) -> {
			INSTANCE.loadSinglePlayerRules();
			CarpetClientRule<?> clientRule = INSTANCE.CURRENT_RULES.get(rule.name());
			if (clientRule != null) {
				clientRule.setFromServer(value);
			}
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

	@Override
	public void onGameStarted() {
		EssentialClient.CONFIG_SET.add(CarpetClient.INSTANCE);
		Scheduler.schedule(0, () -> this.readConfig());
	}

	public boolean isServerCarpet() {
		return this.isServerCarpet || EssentialUtils.getClient().isInSingleplayer();
	}

	public boolean isCarpetManager(String name) {
		return this.MANAGERS.contains(name);
	}

	public CarpetClientRule<?> getRule(String name) {
		if (this.CURRENT_RULES.isEmpty() && EssentialUtils.getClient().isInSingleplayer()) {
			this.loadSinglePlayerRules();
		}
		return this.CURRENT_RULES.get(name);
	}

	public void onDisconnect() {
		this.isServerCarpet = false;
		this.CURRENT_RULES.clear();
		this.MANAGERS.clear();
	}

	public Collection<CarpetClientRule<?>> getCurrentCarpetRules() {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.isInSingleplayer()) {
			this.loadSinglePlayerRules();
			return this.CURRENT_RULES.values();
		}
		if (client.getCurrentServerEntry() != null && this.isServerCarpet) {
			return this.CURRENT_RULES.values();
		}
		return this.ALL_RULES.values();
	}

	public void syncCarpetRule(NbtCompound compound) {
		this.isServerCarpet = true;
		// We run on the main thread to prevent concurrent modification exceptions
		// This method will be called from the network thread
		EssentialUtils.getClient().execute(() -> {
			String name = compound.getString("Rule");
			String value = compound.getString("Value");
			String manager = compound.getString("Manager");
			CarpetClientRule<?> wrapped = this.CURRENT_RULES.get(name);
			if (wrapped == null) {
				CarpetRule<?> rule = CarpetUtils.getCarpetRule(name);
				wrapped = this.ALL_RULES.get(name);
				if (rule != null) {
					if (wrapped == null) {
						wrapped = this.parseRuleToClientRule(rule, manager);
					} else {
						wrapped = this.parseRuleToClientRule(rule, wrapped.getDescription(), wrapped.getOptionalInfo(), manager);
					}
				} else {
					// We must copy otherwise we will be modifying the global CarpetRule
					wrapped = wrapped != null ? wrapped.shallowCopy() : new StringCarpetRule(name, this.LOADNT, value);
				}
				this.CURRENT_RULES.put(name, wrapped);
			}

			this.HANDLING_DATA.set(true);
			wrapped.setFromServer(value);
			this.HANDLING_DATA.set(false);

			if (
				EssentialUtils.getClient().currentScreen instanceof RulesScreen rulesScreen &&
				rulesScreen.getTitle() == Texts.SERVER_SCREEN
			) {
				rulesScreen.refreshRules(rulesScreen.getSearchBoxText());
			}
		});
	}

	private void loadSinglePlayerRules() {
		if (this.CURRENT_RULES.isEmpty() && EssentialUtils.getClient().isInSingleplayer()) {
			this.HANDLING_DATA.set(true);

			CarpetUtils.forEachCarpetRule((rule, managerId) -> {
				CarpetClientRule<?> clientRule = this.ALL_RULES.get(rule.name());
				clientRule = clientRule == null ? this.parseRuleToClientRule(rule, managerId) :
					this.parseRuleToClientRule(rule, clientRule.getDescription(), clientRule.getOptionalInfo(), managerId);
				clientRule.setFromServer(CarpetUtils.getRuleValueAsString(rule));
				this.CURRENT_RULES.put(clientRule.getName(), clientRule);
			});

			this.HANDLING_DATA.set(false);
		}
	}

	private CarpetClientRule<?> jsonToClientRule(JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			EssentialClient.LOGGER.error("Failed to load Carpet Rule (Not JSON Object!):\n{}", jsonElement);
			return null;
		}
		JsonObject ruleObject = jsonElement.getAsJsonObject();
		JsonElement nameElement = ruleObject.get("name");
		if (nameElement == null || !nameElement.isJsonPrimitive()) {
			EssentialClient.LOGGER.error("Failed to load Carpet Rule (No Valid Name!):\n{}", ruleObject);
			return null;
		}
		String name = nameElement.getAsString();

		CarpetRule<?> rule = CarpetUtils.getCarpetRule(name);
		try {
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
			if (rule != null) {
				return this.parseRuleToClientRule(rule, description, optionalInfo, rule.settingsManager().identifier());
			}
			Rule.Type type = Rule.Type.fromString(ruleObject.get("type").getAsString());
			JsonElement defaultValue = ruleObject.get("default");
			if (defaultValue == null) {
				defaultValue = ruleObject.get("value");
			}
			JsonElement categories = ruleObject.get("categories");
			if (categories != null && categories.getAsJsonArray().contains(this.COMMAND) && type != Rule.Type.BOOLEAN) {
				CycleCarpetRule wrapped = CycleCarpetRule.commandOf(name, description, defaultValue.getAsString());
				wrapped.setOptionalInfo(optionalInfo);
				return wrapped;
			}
			JsonElement isStrict = ruleObject.get("strict");
			if (isStrict != null && isStrict.getAsBoolean() && type != Rule.Type.BOOLEAN) {
				List<String> validValues = new ArrayList<>();
				JsonElement options = ruleObject.get("options");
				if (options.isJsonArray()) {
					JsonArray values = options.getAsJsonArray();
					values.forEach(element -> validValues.add(element.getAsString()));
					CycleCarpetRule wrapped = new CycleCarpetRule(name, description, validValues, defaultValue.getAsString());
					wrapped.setOptionalInfo(optionalInfo);
					return wrapped;
				}
			}
			CarpetClientRule<?> wrapped = switch (type) {
				case CYCLE -> {
					JsonArray validCycles = ruleObject.get("cycles").getAsJsonArray();
					List<String> cycles = new ArrayList<>();
					validCycles.forEach(element -> cycles.add(element.getAsString()));
					yield new CycleCarpetRule(name, description, cycles, defaultValue.getAsString());
				}
				case BOOLEAN -> new BooleanCarpetRule(name, description, defaultValue.getAsBoolean());
				case INTEGER -> new IntegerCarpetRule(name, description, defaultValue.getAsInt());
				case DOUBLE -> new DoubleCarpetRule(name, description, defaultValue.getAsDouble());
				default -> new StringCarpetRule(name, description, defaultValue.getAsString());
			};
			wrapped.setOptionalInfo(optionalInfo);
			return wrapped;
		} catch (Exception e) {
			if (rule != null) {
				return this.parseRuleToClientRule(rule, CarpetUtils.getRuleSettingsManagerId(rule));
			}
			// EssentialClient.LOGGER.error("Failed to load Carpet Rule '{}'", name, e);
			return null;
		}
	}

	private CarpetClientRule<?> parseRuleToClientRule(CarpetRule<?> rule, String manager) {
		return this.parseRuleToClientRule(rule, null, null, manager);
	}

	private CarpetClientRule<?> parseRuleToClientRule(CarpetRule<?> rule, String description, String optionalInfo, String manager) {
		String name = rule.name();
		Rule.Type type = Rule.Type.fromClass(rule.type());
		String defaultValue = CarpetUtils.getRuleDefaultValueAsString(rule);
		if (description == null) {
			description = CarpetUtils.getRuleDescription(rule);
		}
		if (optionalInfo == null) {
			optionalInfo = CarpetUtils.getRuleExtraInfo(rule);
			if (optionalInfo.isBlank()) {
				optionalInfo = null;
			}
		}
		if (CarpetUtils.isRuleCommand(rule)) {
			CycleCarpetRule wrapped = CycleCarpetRule.commandOf(name, description, defaultValue);
			wrapped.setOptionalInfo(optionalInfo);
			return wrapped;
		}
		if (rule.strict() && type != Rule.Type.BOOLEAN) {
			CycleCarpetRule wrapped = new CycleCarpetRule(name, description, new ArrayList<>(rule.suggestions()), defaultValue);
			wrapped.setOptionalInfo(optionalInfo);
			return wrapped;
		}
		CarpetClientRule<?> wrapped = switch (type) {
			case BOOLEAN -> new BooleanCarpetRule(name, description, "true".equals(defaultValue));
			case INTEGER -> {
				Integer integer;
				try {
					integer = Integer.parseInt(defaultValue);
				} catch (NumberFormatException e) {
					integer = null;
				}
				yield integer == null ? null : new IntegerCarpetRule(name, description, integer);
			}
			case DOUBLE -> {
				Double doubleValue;
				try {
					doubleValue = Double.parseDouble(defaultValue);
				} catch (NumberFormatException e) {
					doubleValue = null;
				}
				yield doubleValue == null ? null : new DoubleCarpetRule(name, description, doubleValue);
			}
			default -> null;
		};

		if (wrapped == null) {
			wrapped = new StringCarpetRule(name, description, defaultValue);
		}

		this.MANAGERS.add(manager);
		wrapped.setCustomManager(manager);
		wrapped.setOptionalInfo(optionalInfo);
		return wrapped;
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
			ruleObject.addProperty("type", rule.getType().toString());
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
		JsonArray jsonArray = this.getData(jsonElement);
		jsonArray.forEach(element -> {
			CarpetClientRule<?> rule = this.jsonToClientRule(element);
			if (rule != null) {
				this.ALL_RULES.put(rule.getName(), rule);
			}
		});
	}
}
