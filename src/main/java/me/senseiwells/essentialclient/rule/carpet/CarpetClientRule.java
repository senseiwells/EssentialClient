package me.senseiwells.essentialclient.rule.carpet;

import me.senseiwells.essentialclient.rule.client.ClientRule;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.List;

public abstract class CarpetClientRule<T> extends ClientRule<T> {
	private String manager;

	public CarpetClientRule(String name, String description, T defaultValue) {
		super(name, description, defaultValue);
		this.manager = "carpet";
	}

	public final void setCustomManager(String manager) {
		this.manager = manager;
	}

	@Override
	public final void onValueChange() {
		if (EssentialUtils.playerHasOp()) {
			EssentialUtils.sendChatMessage("/%s %s %s".formatted(this.manager, this.getName(), this.getValue()));
		}
	}

	@Override
	public final List<RuleListener<T>> getListeners() {
		return null;
	}

	@Override
	public abstract CarpetClientRule<T> shallowCopy();

	@Override
	public final void addListener(RuleListener<T> ruleListener) { }
}
