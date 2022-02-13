package essentialclient.clientscript.core;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import essentialclient.EssentialClient;
import essentialclient.feature.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientScript {
	public static ClientScript INSTANCE = new ClientScript();

	private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final Set<ClientScriptInstance> scriptInstances;
	private final Set<String> selectedScriptNames;

	private ClientScript() {
		this.scriptInstances = new HashSet<>();
		this.selectedScriptNames = new HashSet<>();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ClientKeybinds.CLIENT_SCRIPT_TOGGLE_ALL.getKeyBinding().wasPressed()) {
				this.startAllInstances();
			}
			if (ClientKeybinds.CLIENT_SCRIPT_STOP_ALL.getKeyBinding().wasPressed()) {
				this.stopAllInstances();
			}
		});
	}

	public void load() {
		this.readScriptConfig();
		this.refreshAllInstances();
	}

	public Set<ClientScriptInstance> getScriptInstances() {
		return this.scriptInstances;
	}

	public List<ClientScriptInstance> getScriptInstancesInOrder() {
		return this.getScriptInstances().stream().sorted(Comparator.comparing(ClientScriptInstance::toString)).collect(Collectors.toList());
	}

	public boolean isSelected(String name) {
		return this.selectedScriptNames.contains(name);
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean startAllInstances() {
		if (this.scriptInstances.isEmpty()) {
			return false;
		}
		for (ClientScriptInstance instance : this.scriptInstances) {
			if (this.selectedScriptNames.contains(instance.toString())) {
				instance.startScript();
			}
		}
		return true;
	}

	public void stopAllInstances() {
		for (ClientScriptInstance instance : this.scriptInstances) {
			instance.stopScript();
		}
	}

	public void addInstance(ClientScriptInstance scriptInstance) {
		this.scriptInstances.add(scriptInstance);
	}

	public void addSelectedInstance(String name) {
		this.selectedScriptNames.add(name);
	}

	public void removeInstance(ClientScriptInstance clientScriptInstance) {
		this.scriptInstances.remove(clientScriptInstance);
	}

	public void removeSelectedInstance(String name) {
		this.selectedScriptNames.remove(name);
	}

	public void refreshAllInstances() {
		for (ClientScriptInstance instance : this.scriptInstances) {
			instance.stopScript();
		}
		this.scriptInstances.clear();
		File[] files = getScriptFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.endsWith(".arucas")) {
				fileName = fileName.substring(0, fileName.length() - 7);
				this.addInstance(new ClientScriptInstance(fileName, file.toPath()));
			}
		}
	}

	private void readScriptConfig() {
		Path file = getScriptConfig();
		if (!Files.isRegularFile(file)) {
			return;
		}
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			JsonArray jsonArray = this.GSON.fromJson(reader, JsonArray.class);
			for (JsonElement element : jsonArray) {
				JsonObject object = element.getAsJsonObject();
				this.selectedScriptNames.add(object.get("name").getAsString());
			}
		}
		catch (IOException e) {
			EssentialClient.LOGGER.error("Could not read Script Config");
		}
	}

	public void saveScriptConfig() {
		Path file = getScriptConfig();
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			JsonWriter jsonWriter = this.GSON.newJsonWriter(writer);
			jsonWriter.beginArray();
			for (ClientScriptInstance instance : this.scriptInstances) {
				if (this.selectedScriptNames.contains(instance.toString())) {
					jsonWriter.beginObject().name("name").value(instance.toString()).endObject();
				}
			}
			jsonWriter.endArray();
			jsonWriter.flush();
			jsonWriter.close();
		}
		catch (IOException e) {
			EssentialClient.LOGGER.error("Could not save Script Config");
		}
	}

	public static Path getDir() {
		return EssentialUtils.getEssentialConfigFile().resolve("Scripts");
	}

	private static Path getScriptConfig() {
		return EssentialUtils.getEssentialConfigFile().resolve("EssentialClientScriptConfig.json");
	}

	private static File[] getScriptFiles() {
		Path scriptPath = getDir();
		File[] files = scriptPath.toFile().listFiles();
		if (files == null) {
			return new File[0];
		}
		return files;
	}
}
