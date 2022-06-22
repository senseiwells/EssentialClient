package me.senseiwells.essentialclient.feature.keybinds;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.util.InputUtil;

import java.util.LinkedHashSet;
import java.util.Set;

public class MultiKeyBind extends ClientKeyBind {
	private final Set<InputUtil.Key> keys;
	private final InputUtil.Key[] defaultKeys;
	private InputUtil.Key firstKey;

	MultiKeyBind(String name, String category, InputUtil.Key... defaultKeys) {
		super(name, category);
		this.keys = new LinkedHashSet<>();
		this.defaultKeys = defaultKeys;

		this.resetKey();
	}

	public void addKeys(int... keys) {
		for (int key : keys) {
			InputUtil.Key inputKey = InputUtil.fromKeyCode(key, 0);
			this.addKey(inputKey);
		}
	}

	public InputUtil.Key getFirstKey() {
		return this.firstKey == null ? InputUtil.UNKNOWN_KEY : this.firstKey;
	}

	@Override
	public void press(InputUtil.Key key) {
		if (!this.isPressed() && this.keys.contains(key)) {
			long handle = EssentialUtils.getClient().getWindow().getHandle();
			for (InputUtil.Key inputKey : this.keys) {
				if (!InputUtil.isKeyPressed(handle, inputKey.getCode())) {
					return;
				}
			}

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
		if (this.firstKey == null) {
			this.firstKey = key;
		}
		this.keys.add(key);
	}

	@Override
	public void clearKey() {
		this.firstKey = null;
		this.setPressed(false);
		this.keys.clear();
	}

	@Override
	public void resetKey() {
		for (InputUtil.Key key : this.defaultKeys) {
			this.addKey(key);
		}
	}

	@Override
	public boolean isSingleKey() {
		return false;
	}

	@Override
	public boolean isDefault() {
		if (this.keys.size() != this.defaultKeys.length) {
			return false;
		}

		for (InputUtil.Key key : this.defaultKeys) {
			if (!this.keys.contains(key)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<InputUtil.Key> getKeys() {
		return this.keys;
	}
}
