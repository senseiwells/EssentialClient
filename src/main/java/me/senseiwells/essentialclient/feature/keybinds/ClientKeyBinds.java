package me.senseiwells.essentialclient.feature.keybinds;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptScreen;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import me.senseiwells.essentialclient.gui.config.ConfigScreen;
import me.senseiwells.essentialclient.utils.config.MappedStringConfig;
import me.senseiwells.essentialclient.utils.misc.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ClientKeyBinds extends MappedStringConfig<ClientKeyBind> {
	public static final ClientKeyBinds INSTANCE = new ClientKeyBinds();

	public static final ClientKeyBind
		ACCURATE_REVERSE,
		ACCURATE_INTO,
		CLIENT_SCRIPT_TOGGLE_ALL,
		CLIENT_SCRIPT_STOP_ALL,
		TOGGLE_DEBUG_MENU,
		OPEN_ESSENTIAL_CLIENT_MENU,
		OPEN_CHUNK_DEBUG,
		OPEN_CLIENT_SCRIPT;

	static {
		ACCURATE_REVERSE = register("Accurate Reverse", GLFW.GLFW_KEY_UNKNOWN);
		ACCURATE_INTO = register("Accurate Into", GLFW.GLFW_KEY_UNKNOWN);
		CLIENT_SCRIPT_TOGGLE_ALL = register("Toggle Selected Scripts", GLFW.GLFW_KEY_UNKNOWN, client -> ClientScript.INSTANCE.startAllInstances());
		CLIENT_SCRIPT_STOP_ALL = register("Stop Selected Scripts", GLFW.GLFW_KEY_UNKNOWN, client -> ClientScript.INSTANCE.stopAllInstances());
		TOGGLE_DEBUG_MENU = register("Toggle Debug Menu", GLFW.GLFW_KEY_F3);
		OPEN_ESSENTIAL_CLIENT_MENU = register("Open Essential Client Menu", GLFW.GLFW_KEY_UNKNOWN, client -> setScreenIfNull(client, () -> new ConfigScreen(null)));
		OPEN_CLIENT_SCRIPT = register("Open Client Script", GLFW.GLFW_KEY_UNKNOWN, client -> setScreenIfNull(client, () -> new ClientScriptScreen(null)));
		OPEN_CHUNK_DEBUG = register("Open Chunk Debug", GLFW.GLFW_KEY_F6, client -> {
			if (ChunkClientNetworkHandler.chunkDebugAvailable){
				setScreenIfNull(client, () -> new ChunkDebugScreen(null));
			}
		});

		Events.ON_TICK_POST.register(client -> {
			for (ClientKeyBind key : getAllKeyBinds()) {
				while (key.wasPressed()) {
					key.onPress(client);
				}
			}
		});
	}

	public static ClientKeyBind register(String name, int key) {
		return register(name, key, null);
	}

	public static ClientKeyBind register(String name, int key, Consumer<MinecraftClient> onPressed) {
		return register(name, key, "Essential Client", onPressed);
	}

	public static ClientKeyBind register(String name, int key, String category, Consumer<MinecraftClient> onPressed) {
		ClientKeyBind clientKeyBind = new ClientKeyBind(name, key, category, onPressed);
		if (ClientKeyBinds.INSTANCE.map.containsKey(name)) {
			EssentialClient.LOGGER.error("Tried to register KeyBind with duplicate name: {}", name);
			return clientKeyBind;
		}
		ClientKeyBinds.INSTANCE.map.put(name, clientKeyBind);
		return clientKeyBind;
	}

	private ClientKeyBinds() { }

	public static void load() { }

	public static Collection<ClientKeyBind> getAllKeyBinds() {
		return INSTANCE.map.values();
	}

	@Override
	protected JsonElement valueToJson(ClientKeyBind value) {
		return new JsonPrimitive(value.getBoundKeyTranslationKey());
	}

	@Override
	protected ClientKeyBind jsonToValue(String key, JsonElement valueElement) {
		ClientKeyBind existingKeyBind = this.map.get(key);
		if (existingKeyBind != null) {
			String keyTranslation = valueElement.getAsString();
			existingKeyBind.setBoundKey(InputUtil.fromTranslationKey(keyTranslation));
		}
		return null;
	}

	@Override
	public String getConfigName() {
		return "ClientKeyBinds";
	}

	private static void setScreenIfNull(MinecraftClient client, Supplier<Screen> screenFactory) {
		if (client.currentScreen == null) {
			client.setScreen(screenFactory.get());
		}
	}
}
