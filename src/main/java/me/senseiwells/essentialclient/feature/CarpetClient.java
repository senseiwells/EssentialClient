package me.senseiwells.essentialclient.feature;

import carpet.CarpetExtension;
import carpet.CarpetServer;

//#if MC >= 11902
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleCategory;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import net.minecraft.text.Text;
//#else
//$$import carpet.settings.ParsedRule;
//$$import carpet.settings.RuleCategory;
//$$import carpet.settings.SettingsManager;
//#endif

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.rule.carpet.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.misc.Events;
import me.senseiwells.essentialclient.utils.render.Texts;
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

		//#if MC >= 11902
		SettingsManager.registerGlobalRuleObserver((c, r, s) -> {
			INSTANCE.loadSinglePlayerRules();
			CarpetClientRule<?> rule = INSTANCE.CURRENT_RULES.get(r.name());
			//#else
			//$$SettingsManager.addGlobalRuleObserver((c, r, s) -> {
			//$$	INSTANCE.loadSinglePlayerRules();
			//$$	CarpetClientRule<?> rule = INSTANCE.CURRENT_RULES.get(r.name);
			//#endif
			if (rule != null) {
				rule.setFromServer(s);
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

	public boolean isServerCarpet() {
		return this.isServerCarpet | EssentialUtils.getClient().isInSingleplayer();
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
		// We run on main thread to prevent concurrent modification exceptions
		// This method will be called from the network thread
		EssentialUtils.getClient().execute(() -> {
			String name = compound.getString("Rule");
			String value = compound.getString("Value");
			String manager = compound.getString("Manager");
			CarpetClientRule<?> rule = this.CURRENT_RULES.get(name);
			if (rule == null) {
				// We prioritise ParsedRule, but ClientRule's description
				//#if MC >= 11902
				CarpetRule<?> carpetRule = CarpetServer.settingsManager.getCarpetRule(name);
				//#else
				//$$ParsedRule<?> carpetRule = CarpetServer.settingsManager.getRule(name);
				//#endif
				rule = this.ALL_RULES.get(name);
				if (carpetRule != null) {
					rule = rule == null ? this.parseRuleToClientRule(carpetRule, manager) : this.parseRuleToClientRule(carpetRule, rule.getDescription(), rule.getOptionalInfo(), manager);
				} else {
					// We must copy otherwise we will be modifying the global CarpetRule
					rule = rule != null ? rule.shallowCopy() : new StringCarpetRule(name, this.LOADNT, value);
				}
				this.CURRENT_RULES.put(name, rule);
			}

			this.HANDLING_DATA.set(true);
			rule.setFromServer(value);
			this.HANDLING_DATA.set(false);
			if (EssentialUtils.getClient().currentScreen instanceof RulesScreen rulesScreen && rulesScreen.getTitle() == Texts.SERVER_SCREEN) {
				rulesScreen.refreshRules(rulesScreen.getSearchBoxText());
			}
		});
	}

	private void loadSinglePlayerRules() {
		if (this.CURRENT_RULES.isEmpty() && EssentialUtils.getClient().isInSingleplayer()) {
			this.HANDLING_DATA.set(true);

			Consumer<SettingsManager> managerProcessor = settings -> {
				//#if MC > 11902
				for (CarpetRule<?> carpetRule : settings.getCarpetRules()) {
					CarpetClientRule<?> clientRule = this.ALL_RULES.get(carpetRule.name());
					clientRule = clientRule == null ? this.parseRuleToClientRule(carpetRule, settings.identifier()) :
						this.parseRuleToClientRule(carpetRule, clientRule.getDescription(), clientRule.getOptionalInfo(), settings.identifier());
					clientRule.setFromServer(RuleHelper.toRuleString(carpetRule.value()));
					this.CURRENT_RULES.put(clientRule.getName(), clientRule);
				}
				//#else
				//$$for (ParsedRule<?> parsedRule : settings.getRules()) {
				//$$	CarpetClientRule<?> clientRule = this.ALL_RULES.get(parsedRule.name);
				//$$	clientRule = clientRule == null ? this.parseRuleToClientRule(parsedRule, settings.getIdentifier()) :
				//$$		this.parseRuleToClientRule(parsedRule, clientRule.getDescription(), clientRule.getOptionalInfo(), settings.getIdentifier());
				//$$	clientRule.setFromServer(parsedRule.getAsString());
				//$$	this.CURRENT_RULES.put(clientRule.getName(), clientRule);
				//$$}
				//#endif
			};

			managerProcessor.accept(CarpetServer.settingsManager);
			for (CarpetExtension extension : CarpetServer.extensions) {
				//#if MC >= 11902
				SettingsManager manager = extension.extensionSettingsManager();
				//#else
				//$$SettingsManager manager = extension.customSettingsManager();
				//#endif
				if (manager != null) {
					managerProcessor.accept(manager);
				}
			}

			this.HANDLING_DATA.set(false);
		}
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

	//#if MC >= 11902
	private CarpetClientRule<?> parseRuleToClientRule(CarpetRule<?> carpetRule, String manager) {
		//#else
		//$$private CarpetClientRule<?> parseRuleToClientRule(ParsedRule<?> carpetRule, String manager) {
		//#endif
		return this.parseRuleToClientRule(carpetRule, null, null, manager);
	}

	//#if MC > 11902
	private CarpetClientRule<?> parseRuleToClientRule(CarpetRule<?> carpetRule, String description, String optionalInfo, String manager) {
		String name = carpetRule.name();
		Rule.Type type = Rule.Type.fromClass(carpetRule.type());
		String defaultValue = RuleHelper.toRuleString(carpetRule.defaultValue());
		if (description == null) {
			description = RuleHelper.translatedDescription(carpetRule);
		}
		List<Text> infos = carpetRule.extraInfo();
		if (optionalInfo == null && !infos.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (Text info : infos) {
				builder.append(info.getString()).append(" ");
			}
			optionalInfo = builder.toString();
		}
		if (carpetRule.categories().contains(RuleCategory.COMMAND)) {
			CycleCarpetRule rule = CycleCarpetRule.commandOf(name, description, defaultValue);
			rule.setOptionalInfo(optionalInfo);
			return rule;
		}
		/* Carpet doesn't allow this :(
		if (carpetRule.isStrict && type != Rule.Type.BOOLEAN) {
			CycleCarpetRule rule = new CycleCarpetRule(name, description, carpetRule.options, defaultValue);
			rule.setOptionalInfo(optionalInfo);
			return rule;
		}
		 */
		//#else
		//$$private CarpetClientRule<?> parseRuleToClientRule(ParsedRule<?> carpetRule, String description, String optionalInfo, String manager) {
		//$$	String name = carpetRule.name;
		//$$	Rule.Type type = Rule.Type.fromClass(carpetRule.type);
		//$$	String defaultValue = carpetRule.defaultAsString;
		//$$	if (description == null) {
		//$$		description = carpetRule.description;
		//$$	}
		//$$	if (optionalInfo == null && !carpetRule.extraInfo.isEmpty()) {
		//$$		StringBuilder builder = new StringBuilder();
		//$$		for (String info : carpetRule.extraInfo) {
		//$$			builder.append(info).append(" ");
		//$$		}
		//$$		optionalInfo = builder.toString();
		//$$	}
		//$$	if (carpetRule.categories.contains(RuleCategory.COMMAND)) {
		//$$		CycleCarpetRule rule = CycleCarpetRule.commandOf(name, description, defaultValue);
		//$$		rule.setOptionalInfo(optionalInfo);
		//$$		return rule;
		//$$	}
		//$$	if (carpetRule.isStrict && type != Rule.Type.BOOLEAN) {
		//$$		CycleCarpetRule rule = new CycleCarpetRule(name, description, carpetRule.options, defaultValue);
		//$$		rule.setOptionalInfo(optionalInfo);
		//$$		return rule;
		//$$	}
		//#endif

		CarpetClientRule<?> rule = switch (type) {
			case BOOLEAN -> {
				yield new BooleanCarpetRule(name, description, "true".equals(defaultValue));
			}
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

		if (rule == null) {
			rule = new StringCarpetRule(name, description, defaultValue);
		}

		this.MANAGERS.add(manager);
		rule.setCustomManager(manager);
		rule.setOptionalInfo(optionalInfo);
		return rule;
	}

	private JsonArray getData(JsonArray fallback) {
		String content = Util.Network.INSTANCE.getStringFromUrl(this.DATA_URL);
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
			this.ALL_RULES.put(rule.getName(), rule);
		});
	}
}
