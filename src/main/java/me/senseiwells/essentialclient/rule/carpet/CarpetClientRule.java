package me.senseiwells.essentialclient.rule.carpet;

import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.rule.impl.SimpleRule;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.List;
import java.util.Objects;

public abstract class CarpetClientRule<T> extends SimpleRule<T> {
	private String manager;

	public CarpetClientRule(String name, String description, T defaultValue) {
		super(name, description, defaultValue);
		this.manager = "carpet";
	}

	public final void setFromServer(String stringValue) {
		T value = this.getValueFromString(stringValue);
		if (value != null) {
			this.setValueQuietly(value);
		}
	}

	public final void setCustomManager(String manager) {
		this.manager = manager;
	}

	@Override
	public void setValue(T value) {
		if (!Objects.equals(value, this.getValue()) && EssentialUtils.playerHasOp()) {
			EssentialUtils.sendChatMessage("/%s %s %s".formatted(this.manager, this.getName(), value));
			if (EssentialUtils.getClient().isInSingleplayer()) {
				this.setFromServer(value.toString());
			}
		}
	}

	@Override
	public final List<RuleListener<T>> getListeners() {
		return null;
	}

	@Override
	public abstract CarpetClientRule<T> shallowCopy();

	public abstract T getValueFromString(String value);

	@Override
	public final void setValueFromString(String stringValue) {
		T value = this.getValueFromString(stringValue);
		if (value != null) {
			this.setValue(value);
		}
	}

	@Override
	public boolean isAvailable() {
		return (CarpetClient.INSTANCE.isServerCarpet() && EssentialUtils.playerHasOp()) || EssentialUtils.getClient().isInSingleplayer();
	}

	@Override
	public final void addListener(RuleListener<T> ruleListener) { }

	@Override
	public final void onValueChange() { }
}
