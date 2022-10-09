package me.senseiwells.essentialclient.utils.clientscript;

import com.google.gson.*;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScriptRepositoryManager {
	public static final ScriptRepositoryManager INSTANCE = new ScriptRepositoryManager();

	private final String REPOSITORY_URL;
	private final Gson GSON;
	private final Map<Category, Set<ScriptFile>> children;

	private ScriptRepositoryManager() {
		this.REPOSITORY_URL = "https://api.github.com/repos/senseiwells/clientscript/contents/scripts";
		this.GSON = new GsonBuilder().create();
		this.children = new HashMap<>();
	}

	public String getScriptFromWeb(Category category, String name, boolean fromCache) {
		ScriptFile scriptFile = this.getScriptFromName(category, name);
		if (scriptFile == null) {
			return null;
		}
		if (fromCache && scriptFile.content != null) {
			return scriptFile.content;
		}
		String content = Util.Network.INSTANCE.getStringFromUrl(scriptFile.downloadUrl);
		if (content == null) {
			return null;
		}
		return scriptFile.content = content;
	}

	// Returns true if download failed
	public boolean downloadScript(Category category, String name, boolean overwrite) {
		Set<ScriptFile> scriptFileSet = this.getChildren(category);
		if (!scriptFileSet.contains(new ScriptFile(name, null, null))) {
			return true;
		}
		Path scriptDir = ClientScript.INSTANCE.getScriptDirectory();
		Path newScript = scriptDir.resolve(name + ".arucas");
		if (!overwrite && Files.exists(newScript)) {
			for (int i = 1; ; i++) {
				newScript = scriptDir.resolve(name + i + ".arucas");
				if (!Files.exists(newScript)) {
					break;
				}
			}
		}
		String fileContent = this.getScriptFromWeb(category, name, false);
		if (fileContent != null) {
			try (BufferedWriter writer = Files.newBufferedWriter(newScript)) {
				writer.write(fileContent);
				return false;
			} catch (IOException ignored) {

			}
		}
		return true;
	}

	public String getViewableLink(Category category, String scriptName) {
		ScriptFile scriptFile = this.getScriptFromName(category, scriptName);
		if (scriptFile == null || scriptFile.viewableUrl == null) {
			return "https://github.com/senseiwells/clientscript";
		}
		return scriptFile.viewableUrl;
	}

	public List<String> getChildrenNames(Category category) {
		return this.getChildren(category).stream().map(scriptFile -> scriptFile.name).toList();
	}

	private ScriptFile getScriptFromName(Category category, String name) {
		return this.getChildren(category).stream().filter(s -> s.name.equals(name)).findAny().orElse(null);
	}

	private Set<ScriptFile> getChildren(Category category) {
		if (!this.children.containsKey(category) && !this.loadChildren(category)) {
			return Set.of();
		}
		return this.children.get(category);
	}

	private boolean loadChildren(Category category) {
		String response = Util.Network.INSTANCE.getStringFromUrl(this.REPOSITORY_URL + "/" + category.toString());
		if (response == null) {
			return false;
		}
		JsonArray childrenFiles = this.GSON.fromJson(response, JsonArray.class);
		Set<ScriptFile> scriptFileSet = new HashSet<>();
		for (JsonElement element : childrenFiles) {
			JsonObject object = element.getAsJsonObject();
			String childName = object.get("name").getAsString();
			if (!childName.endsWith(".arucas")) {
				continue;
			}
			childName = childName.substring(0, childName.length() - 7);
			String childDownload = object.get("download_url").getAsString();
			JsonObject links = object.get("_links").getAsJsonObject();
			String viewableUrl = links.get("html").getAsString();
			scriptFileSet.add(new ScriptFile(childName, childDownload, viewableUrl));
		}
		this.children.put(category, scriptFileSet);
		return true;
	}

	private static class ScriptFile {
		private final String name;
		private final String downloadUrl;
		private final String viewableUrl;
		private String content;

		ScriptFile(String name, String downloadUrl, String viewableUrl) {
			this.name = name;
			this.downloadUrl = downloadUrl;
			this.viewableUrl = viewableUrl;
			this.content = null;
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			return o instanceof ScriptFile scriptFile && scriptFile.name.equals(this.name);
		}
	}

	public enum Category {
		AUTOMATION(Texts.AUTOMATION),
		UTILITIES(Texts.UTILITIES),
		MISCELLANEOUS(Texts.MISCELLANEOUS);

		private final Text prettyName;

		Category(Text prettyName) {
			this.prettyName = prettyName;
		}

		public Text getPrettyName() {
			return this.prettyName;
		}

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}
}
