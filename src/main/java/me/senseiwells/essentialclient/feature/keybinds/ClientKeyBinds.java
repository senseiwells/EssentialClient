package me.senseiwells.essentialclient.feature.keybinds;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import me.senseiwells.essentialclient.gui.clientscript.ClientScriptScreen;
import me.senseiwells.essentialclient.gui.config.ConfigScreen;
import me.senseiwells.essentialclient.utils.config.MappedStringConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Supplier;

public class ClientKeyBinds extends MappedStringConfig<ClientKeyBind> {
	private static final String DEFAULT_CATEGORY = "Essential Client";

	public static final ClientKeyBinds INSTANCE = new ClientKeyBinds();

	public static final MultiKeyBind
		ACCURATE_REVERSE,
		ACCURATE_INTO,
		CLIENT_SCRIPT_TOGGLE_ALL,
		CLIENT_SCRIPT_STOP_ALL,
		OPEN_ESSENTIAL_CLIENT_MENU,
		OPEN_CHUNK_DEBUG,
		OPEN_CLIENT_SCRIPT;

	public static final SingleKeyBind
		TOGGLE_DEBUG_MENU;

	static {
		ACCURATE_REVERSE = registerMulti("Accurate Reverse");
		ACCURATE_INTO = registerMulti("Accurate Into");
		CLIENT_SCRIPT_TOGGLE_ALL = registerMulti("Toggle Selected Scripts", client -> ClientScript.INSTANCE.startAllInstances());
		CLIENT_SCRIPT_STOP_ALL = registerMulti("Stop Selected Scripts", client -> ClientScript.INSTANCE.stopAllInstances());
		OPEN_ESSENTIAL_CLIENT_MENU = registerMulti("Open Essential Client Menu", client -> setScreenIfNull(client, () -> new ConfigScreen(null)));
		OPEN_CLIENT_SCRIPT = registerMulti("Open Client Script", client -> setScreenIfNull(client, () -> new ClientScriptScreen(null)));
		OPEN_CHUNK_DEBUG = registerMulti("Open Chunk Debug", client -> {
			if (EssentialClient.CHUNK_NET_HANDLER.isAvailable()) {
				if (client.currentScreen instanceof ChunkDebugScreen) {
					client.currentScreen.close();
					return;
				}
				setScreenIfNull(client, () -> new ChunkDebugScreen(null));
			}
		}, GLFW.GLFW_KEY_F6);

		TOGGLE_DEBUG_MENU = registerSingle("Toggle Debug Menu", GLFW.GLFW_KEY_F3);
	}

	private final Map<String, List<InputUtil.Key>> unregisteredKeyBinds;

	private ClientKeyBinds() {
		this.unregisteredKeyBinds = new HashMap<>();
	}

	public static void load() { }

	@Override
	protected JsonElement valueToJson(ClientKeyBind value) {
		JsonArray keys = new JsonArray();
		for (InputUtil.Key key : value.getKeys()) {
			keys.add(key.getTranslationKey());
		}
		JsonObject keyData = new JsonObject();
		keyData.add("keys", keys);
		keyData.addProperty("canUseInGui", value.canUseInGui());
		return keyData;
	}

	@Override
	protected ClientKeyBind jsonToValue(String key, JsonElement valueElement) {
		ClientKeyBind existingKeyBind = this.map.get(key);

		List<InputUtil.Key> keys = new ArrayList<>();
		if (valueElement.isJsonPrimitive()) {
			keys.add(InputUtil.fromTranslationKey(valueElement.getAsString()));
		} else if (valueElement.isJsonArray()) {
			for (JsonElement element : valueElement.getAsJsonArray()) {
				keys.add(InputUtil.fromTranslationKey(element.getAsString()));
			}
		} else {
			JsonObject keyData = valueElement.getAsJsonObject();
			for (JsonElement element : keyData.get("keys").getAsJsonArray()) {
				keys.add(InputUtil.fromTranslationKey(element.getAsString()));
			}
			if (existingKeyBind != null && keyData.has("canUseInGui")) {
				existingKeyBind.setCanUseInGui(keyData.get("canUseInGui").getAsBoolean());
			}
		}

		if (existingKeyBind != null) {
			existingKeyBind.clearKey();
			for (InputUtil.Key inputKey : keys) {
				existingKeyBind.addKey(inputKey);
			}
		} else {
			this.unregisteredKeyBinds.put(key, keys);
			EssentialClient.LOGGER.warn("Could not load keybind: {}", key);
		}
		return null;
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject object = new JsonObject();
		this.map.forEach((k, v) -> object.add(k, this.valueToJson(v)));
		this.unregisteredKeyBinds.forEach((s, keys) -> {
			JsonArray array = new JsonArray();
			for (InputUtil.Key key : keys) {
				array.add(key.getTranslationKey());
			}
			object.add(s, array);
		});
		return super.getSaveData();
	}

	@Override
	public String getConfigName() {
		return "ClientKeyBinds";
	}

	public static void onKeyPress(InputUtil.Key key, boolean isInGui) {
		for (ClientKeyBind keyBind : getAllKeyBinds()) {
			if (keyBind.canUseInGui() || !isInGui) {
				keyBind.press(key);
			}
		}
	}

	public static void onKeyRelease(InputUtil.Key key, boolean isInGui) {
		for (ClientKeyBind keyBind : getAllKeyBinds()) {
			if (keyBind.canUseInGui() || !isInGui) {
				keyBind.release(key);
			}
		}
	}

	public static Collection<ClientKeyBind> getAllKeyBinds() {
		return INSTANCE.map.values();
	}

	public static MultiKeyBind registerMulti(String name, int... keys) {
		return registerMulti(name, null, keys);
	}

	public static MultiKeyBind registerMulti(String name, ClientKeyBind.Callback onPressed, int... keys) {
		return registerMulti(name, DEFAULT_CATEGORY, onPressed, keys);
	}

	public static MultiKeyBind registerMulti(String name, String category, ClientKeyBind.Callback onPressed, Collection<InputUtil.Key> keys) {
		return registerMulti(name, category, onPressed, keys.stream().mapToInt(InputUtil.Key::getCode).toArray());
	}

	public static MultiKeyBind registerMulti(String name, String category, ClientKeyBind.Callback onPressed, int... keys) {
		ClientKeyBind clientKeyBind = ClientKeyBinds.INSTANCE.map.get(name);
		if (clientKeyBind instanceof MultiKeyBind multiKeyBind) {
			multiKeyBind.setCallback(onPressed);
			return multiKeyBind;
		}

		if (clientKeyBind != null) {
			EssentialClient.LOGGER.warn("Overwriting key bind {}", name);
		}

		MultiKeyBind keyBind = new MultiKeyBind(name, category);
		List<InputUtil.Key> inputKeys = INSTANCE.unregisteredKeyBinds.remove(name);
		if (inputKeys != null) {
			keyBind.clearKey();
			inputKeys.forEach(keyBind::addKey);
		} else {
			keyBind.addKeys(keys);
		}
		keyBind.setCallback(onPressed);

		INSTANCE.map.put(name, keyBind);
		return keyBind;
	}

	public static SingleKeyBind registerSingle(String name, int key) {
		return registerSingle(name, null, key);
	}

	public static SingleKeyBind registerSingle(String name, ClientKeyBind.Callback onPressed, int key) {
		return registerSingle(name, DEFAULT_CATEGORY, onPressed, key);
	}

	public static SingleKeyBind registerSingle(String name, String category, ClientKeyBind.Callback onPressed, int key) {
		ClientKeyBind clientKeyBind = INSTANCE.map.get(name);
		if (clientKeyBind instanceof SingleKeyBind singleKeyBind) {
			singleKeyBind.setCallback(onPressed);
			return singleKeyBind;
		}

		if (clientKeyBind != null) {
			EssentialClient.LOGGER.warn("Overwriting key bind {}", name);
		}

		SingleKeyBind keyBind = new SingleKeyBind(name, category, InputUtil.fromKeyCode(key, 0));
		List<InputUtil.Key> inputKeys = INSTANCE.unregisteredKeyBinds.remove(name);
		if (inputKeys != null && inputKeys.size() == 1) {
			keyBind.addKey(inputKeys.get(0));
		}
		keyBind.setCallback(onPressed);

		INSTANCE.map.put(name, keyBind);
		return keyBind;
	}

	public static ClientKeyBind unregisterKeyBind(String name) {
		return INSTANCE.map.remove(name);
	}

	private static void setScreenIfNull(MinecraftClient client, Supplier<Screen> screenFactory) {
		if (client.currentScreen == null) {
			client.setScreen(screenFactory.get());
		}
	}
}
