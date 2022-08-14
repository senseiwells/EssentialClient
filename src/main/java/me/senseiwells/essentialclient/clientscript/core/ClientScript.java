package me.senseiwells.essentialclient.clientscript.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.utils.config.Config;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public enum ClientScript implements Config.CList {
	INSTANCE;

	private final Map<String, ClientScriptInstance> scriptInstances;
	private final Set<String> selectedScriptNames;

	ClientScript() {
		this.scriptInstances = new HashMap<>();
		this.selectedScriptNames = new HashSet<>();
	}

	public ClientScriptInstance getScriptInstance(String name) {
		return this.scriptInstances.get(name);
	}

	public Collection<ClientScriptInstance> getScriptInstances() {
		return this.scriptInstances.values();
	}

	public List<ClientScriptInstance> getScriptInstancesInOrder() {
		return this.getScriptInstances().stream().sorted(Comparator.comparing(ClientScriptInstance::getName)).toList();
	}

	public Set<String> getScriptInstanceNames() {
		return this.scriptInstances.keySet();
	}

	public boolean hasScriptInstance(String name) {
		return this.getScriptInstances().stream().anyMatch(i -> i.getName().equals(name));
	}

	public boolean isSelected(String name) {
		return this.selectedScriptNames.contains(name);
	}

	public void startAllInstances() {
		if (this.scriptInstances.isEmpty()) {
			return;
		}
		for (String selected : this.selectedScriptNames) {
			ClientScriptInstance instance = this.scriptInstances.get(selected);
			if (instance != null) {
				instance.toggleScript();
			}
		}
	}

	public void stopAllInstances() {
		for (ClientScriptInstance instance : this.getScriptInstances()) {
			instance.stopScript();
		}
	}

	public void addInstance(ClientScriptInstance scriptInstance) {
		this.scriptInstances.put(scriptInstance.getName(), scriptInstance);
	}

	public void addSelectedInstance(String name) {
		this.selectedScriptNames.add(name);
		this.saveConfig();
	}

	public void removeInstance(ClientScriptInstance clientScriptInstance) {
		this.scriptInstances.remove(clientScriptInstance.getName());
	}

	public void removeSelectedInstance(String name) {
		this.selectedScriptNames.remove(name);
		this.saveConfig();
	}

	public void replaceSelectedInstance(String oldName, String newName) {
		if (this.selectedScriptNames.remove(oldName)) {
			this.selectedScriptNames.add(newName);
		}
		this.saveConfig();
	}

	public void refresh() {
		File[] files = this.getScriptFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.endsWith(".arucas")) {
				fileName = fileName.substring(0, fileName.length() - 7);
				if (!this.hasScriptInstance(fileName)) {
					new ClientScriptInstance(fileName, file.toPath());
				}
			}
		}
	}

	public Path getScriptDirectory() {
		Path scriptDir = this.getConfigRootPath().resolve("Scripts");
		return Util.File.INSTANCE.ensureExists(scriptDir);
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
		this.getScriptInstances().forEach(instance -> {
			if (this.selectedScriptNames.contains(instance.getName())) {
				JsonObject scriptObject = new JsonObject();
				scriptObject.addProperty("name", instance.getName());
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
		this.refresh();
	}
}
