package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.clientscript.core.ClientScriptScreen;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import me.senseiwells.essentialclient.gui.ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum ClientKeybinds {
	DEBUG_MENU("Toggle Debug Menu", GLFW.GLFW_KEY_F3),
	CLIENT_SCRIPT_TOGGLE_ALL("Toggle Selected Scripts", GLFW.GLFW_KEY_UNKNOWN),
	CLIENT_SCRIPT_STOP_ALL("Stop Selected Scripts", GLFW.GLFW_KEY_UNKNOWN),
	ACCURATE_REVERSE("Accurate Reverse", GLFW.GLFW_KEY_UNKNOWN),
	ACCURATE_INTO("Accurate Into", GLFW.GLFW_KEY_UNKNOWN),
	OPEN_ESSENTIAL_CLIENT_MENU("Open Essential Client Menu", GLFW.GLFW_KEY_UNKNOWN, client -> setScreenIfNull(client, () -> new ConfigScreen(null))),
	OPEN_CHUNK_DEBUG("Open Chunk Debug", GLFW.GLFW_KEY_F6, client -> setScreenIfNull(client, () -> new ChunkDebugScreen(null))),
	OPEN_CLIENT_SCRIPT("Open Client Script", GLFW.GLFW_KEY_UNKNOWN, client -> setScreenIfNull(client, () -> new ClientScriptScreen(null)));

	private final KeyBinding key;
	private final Consumer<MinecraftClient> onPressed;

	ClientKeybinds(String translation, int defaultKey) {
		this(translation, defaultKey, null);
	}

	ClientKeybinds(String translation, int defaultKey, Consumer<MinecraftClient> onPressed) {
		this.key = new KeyBinding(translation, defaultKey, "Essential Client");
		this.onPressed = onPressed;
	}

	public int getKeyCode() {
		return Math.abs(KeyBindingHelper.getBoundKeyOf(this.key).getCode());
	}

	public KeyBinding getKeyBinding() {
		return this.key;
	}

	static {
		for (ClientKeybinds clientKeybinds : ClientKeybinds.values()) {
			KeyBindingHelper.registerKeyBinding(clientKeybinds.key);
		}
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			Arrays.stream(ClientKeybinds.values()).forEach(key -> {
				while (key.getKeyBinding().wasPressed() && key.onPressed != null) {
					key.onPressed.accept(client);
				}
			});
		});
	}

	public static void loadKeybinds() { }

	private static void setScreenIfNull(MinecraftClient client, Supplier<Screen> screenFactory) {
		if (client.currentScreen == null) {
			client.setScreen(screenFactory.get());
		}
	}
}
