package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.clientscript.core.ClientScriptScreen;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import me.senseiwells.essentialclient.gui.ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public enum ClientKeybinds {
	DEBUG_MENU(new KeyBinding("Toggle Debug Menu", GLFW.GLFW_KEY_F3, "Essential Client")),
	CLIENT_SCRIPT_TOGGLE_ALL(new KeyBinding("Toggle Selected Scripts", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
	CLIENT_SCRIPT_STOP_ALL(new KeyBinding("Stop Selected Scripts", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
	ACCURATE_REVERSE(new KeyBinding("Accurate Reverse", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
	ACCURATE_INTO(new KeyBinding("Accurate Into", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
	OPEN_ESSENTIAL_CLIENT_MENU(new KeyBinding("Open Essential Client Menu", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
	OPEN_CHUNK_DEBUG(new KeyBinding("Open Chunk Debug", GLFW.GLFW_KEY_F6, "Essential Client")),
	OPEN_CLIENT_SCRIPT(new KeyBinding("Open Client Script", GLFW.GLFW_KEY_UNKNOWN, "Essential Client"));

	private final KeyBinding key;

	ClientKeybinds(KeyBinding keyBinding) {
		this.key = keyBinding;
	}

	public int getKeyCode() {
		return Math.abs(KeyBindingHelper.getBoundKeyOf(this.key).getCode());
	}

	public KeyBinding getKeyBinding() {
		return this.key;
	}

	public static void loadKeybinds() {
		for (ClientKeybinds clientKeybinds : ClientKeybinds.values()) {
			KeyBindingHelper.registerKeyBinding(clientKeybinds.key);
		}
		load();
	}

	public static void load() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.currentScreen == null) {
				while (OPEN_ESSENTIAL_CLIENT_MENU.getKeyBinding().wasPressed()) {
					client.setScreen(new ConfigScreen(null));
				}
				while (OPEN_CHUNK_DEBUG.getKeyBinding().wasPressed()) {
					client.setScreen(new ChunkDebugScreen(null));
				}
				while (OPEN_CLIENT_SCRIPT.getKeyBinding().wasPressed()) {
					client.setScreen(new ClientScriptScreen(null));
				}
			}
		});
	}
}
