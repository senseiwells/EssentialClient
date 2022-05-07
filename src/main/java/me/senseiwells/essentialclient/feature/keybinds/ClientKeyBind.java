package me.senseiwells.essentialclient.feature.keybinds;

import me.senseiwells.essentialclient.mixins.keyboard.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class ClientKeyBind extends KeyBinding {
	private Consumer<MinecraftClient> onPressed;

	ClientKeyBind(String translation, int defaultKey, String category, Consumer<MinecraftClient> onPressed) {
		super(translation, defaultKey, category);
		this.onPressed = onPressed;
	}

	public int getKeyCode() {
		InputUtil.Key key = ((KeyBindingAccessor) this).getKey();
		return key == null ? -2 : key.getCode();
	}

	public void onPress(MinecraftClient client) {
		if (this.onPressed != null) {
			this.onPressed.accept(client);
		}
	}

	public void setOnPressed(Consumer<MinecraftClient> onPressed) {
		this.onPressed = onPressed;
	}

	@Override
	public void setBoundKey(InputUtil.Key boundKey) {
		super.setBoundKey(boundKey);
		KeyBinding.updateKeysByCode();
		ClientKeyBinds.INSTANCE.saveConfig();
	}

	@Override
	public int compareTo(KeyBinding keyBinding) {
		return this.getTranslationKey().compareTo(keyBinding.getTranslationKey());
	}
}
