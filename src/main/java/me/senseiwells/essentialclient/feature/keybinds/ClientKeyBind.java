package me.senseiwells.essentialclient.feature.keybinds;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public abstract class ClientKeyBind implements Comparable<ClientKeyBind> {
	private final String name;
	private final String category;

	private boolean isPressed;

	private Callback callback;

	ClientKeyBind(String name, String category) {
		this.name = name;
		this.category = category;
	}

	public abstract void press(InputUtil.Key key);

	public abstract void release(InputUtil.Key key);

	public abstract void addKey(InputUtil.Key key);

	public abstract void clearKey();

	public abstract void resetKey();

	public abstract boolean isSingleKey();

	public abstract boolean isDefault();

	public abstract Collection<InputUtil.Key> getKeys();

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public Callback getCallback() {
		return this.callback;
	}

	void setPressed(boolean pressed) {
		this.isPressed = pressed;
	}

	void callCallback() {
		if (this.callback != null) {
			this.callback.pressed(EssentialUtils.getClient());
		}
	}

	public boolean isPressed() {
		return this.isPressed;
	}

	public String getName() {
		return this.name;
	}

	public String getCategory() {
		return this.category;
	}

	public String getDisplay() {
		StringBuilder builder = new StringBuilder();
		Iterator<InputUtil.Key> keyIterator = this.getKeys().iterator();
		while (keyIterator.hasNext()) {
			builder.append(keyIterator.next().getLocalizedText().getString());
			if (keyIterator.hasNext()) {
				builder.append(" + ");
			}
		}
		return builder.toString();
	}

	@Override
	public int compareTo(@NotNull ClientKeyBind o) {
		return this.name.compareTo(o.name);
	}

	@FunctionalInterface
	public interface Callback {
		void pressed(MinecraftClient client);
	}
}
