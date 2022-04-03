package me.senseiwells.essentialclient.clientscript.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.feature.ClientKeybinds;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientScript implements Config.CList {
	public static ClientScript INSTANCE = new ClientScript();

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
		File[] files = this.getScriptFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.endsWith(".arucas")) {
				fileName = fileName.substring(0, fileName.length() - 7);
				this.addInstance(new ClientScriptInstance(fileName, file.toPath()));
			}
		}
	}

	public Path getScriptDirectory() {
		Path scriptDir = this.getConfigRootPath().resolve("Scripts");
		if (!Files.exists(scriptDir)) {
			EssentialUtils.throwAsRuntime(() -> Files.createDirectory(scriptDir));
		}
		return scriptDir;
	}

	private File[] getScriptFiles() {
		Path scriptPath = this.getScriptDirectory();
		File[] files = scriptPath.toFile().listFiles();
		if (files == null) {
			return new File[0];
		}
		return files;
	}

	@Override
	public String getConfigName() {
		return "ClientScript";
	}

	@Override
	public Path getConfigPath() {
		return this.getConfigRootPath().resolve("ScriptConfig.json");
	}

	@Override
	public JsonElement getSaveData() {
		JsonArray scriptData = new JsonArray();
		this.scriptInstances.forEach(instance -> {
			if (this.selectedScriptNames.contains(instance.toString())) {
				JsonObject scriptObject = new JsonObject();
				scriptObject.addProperty("name", instance.toString());
				scriptObject.addProperty("selected", true);
				scriptData.add(scriptObject);
			}
		});
		return scriptData;
	}

	@Override
	public void readConfig(JsonArray jsonArray) {
		jsonArray.forEach(element -> {
			JsonObject scriptObject = element.getAsJsonObject();
			this.selectedScriptNames.add(scriptObject.get("name").getAsString());
		});
		this.refreshAllInstances();
	}
}
