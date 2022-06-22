package me.senseiwells.essentialclient.feature.keybinds;

import net.minecraft.client.util.InputUtil;

import java.util.List;

public class SingleKeyBind extends ClientKeyBind {
	private final InputUtil.Key defaultKey;
	private InputUtil.Key boundKey;

	SingleKeyBind(String name, String category, InputUtil.Key defaultKey) {
		super(name, category);
		this.defaultKey = this.boundKey = defaultKey;
	}

	public InputUtil.Key getBoundKey() {
		return this.boundKey;
	}

	@Override
	public void press(InputUtil.Key key) {
		if (key.equals(this.boundKey)) {
			this.setPressed(true);
			this.callCallback();
		}
	}

	@Override
	public void release(InputUtil.Key key) {
		this.setPressed(false);
	}

	@Override
	public void addKey(InputUtil.Key key) {
		this.boundKey = key;
	}

	@Override
	public void clearKey() {
		this.boundKey = InputUtil.UNKNOWN_KEY;
	}

	@Override
	public void resetKey() {
		this.boundKey = this.defaultKey;
	}

	@Override
	public boolean isSingleKey() {
		return true;
	}

	@Override
	public boolean isDefault() {
		return this.defaultKey.equals(this.boundKey);
	}

	@Override
	public List<InputUtil.Key> getKeys() {
		return List.of(this.boundKey);
	}
}
