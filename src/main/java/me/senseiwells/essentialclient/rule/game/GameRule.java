package me.senseiwells.essentialclient.rule.game;

import com.google.gson.JsonElement;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.impl.SimpleRule;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.Objects;

public abstract class GameRule<T> extends SimpleRule<T> {
	private final GameRules.Key<?> gameRuleKey;
	private boolean isAvailable;

	public GameRule(String name, String description, T defaultValue, GameRules.Key<?> gameRuleKey) {
		super(name, description, defaultValue);
		this.gameRuleKey = gameRuleKey;
		this.isAvailable = false;
	}

	public final void setFromServer(String stringValue) {
		this.isAvailable = true;
		T value = this.getValueFromString(stringValue);
		if (value != null) {
			this.setValueQuietly(value);
		}
	}

	public final void reset() {
		this.isAvailable = false;
		this.setValueQuietly(this.getDefaultValue());
	}

	public final GameRules.Key<?> getKey() {
		return this.gameRuleKey;
	}

	@Override
	public abstract GameRule<T> shallowCopy();

	@Override
	public String getCategory() {
		return this.gameRuleKey == null ? "Unknown" : I18n.translate(this.gameRuleKey.getCategory().getCategory());
	}

	public abstract T getValueFromString(String value);

	@Override
	public final boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public final void setValueFromString(String stringValue) {
		T value = this.getValueFromString(stringValue);
		if (value != null) {
			this.setValue(value);
		}
	}

	@Override
	public boolean changeable() {
		return EssentialClient.GAME_RULE_NET_HANDLER.canModifyRules();
	}

	@Override
	public void setValue(T value) {
		if (this.isAvailable && !Objects.equals(this.getValue(), value)) {
			// We don't actually set the value,
			// we wait for the server to accept
			// it then set the value quietly
			EssentialClient.GAME_RULE_NET_HANDLER.sendServerRuleChange(this, value.toString());
		}
	}

	// These should not be used

	@Override
	public final T fromJson(JsonElement element) {
		return null;
	}

	@Override
	public final JsonElement toJson(T value) {
		return null;
	}

	@Override
	public final List<RuleListener<T>> getListeners() {
		return null;
	}

	@Override
	public void onValueChange() { }

	@Override
	public final JsonElement getValueAsJson() {
		return null;
	}

	@Override
	public final JsonElement getDefaultValueAsJson() {
		return null;
	}

	@Override
	public final void setValueFromJson(JsonElement element) { }
}
