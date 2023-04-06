package me.senseiwells.essentialclient.utils.clientscript;

import com.google.gson.*;
import me.senseiwells.arucas.utils.NetworkUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScriptRepositoryManager {
	private static final Gson GSON = new GsonBuilder().create();

	public static final ScriptRepositoryManager INSTANCE = new ScriptRepositoryManager();

	private final Map<Category, Set<ScriptFile>> children;

	private ScriptRepositoryManager() {
		this.children = new HashMap<>();

		ClientRules.CLIENT_SCRIPT_REPOS.addListener(v -> {
			Thread thread = new Thread(() -> {
				for (Category category : Category.values()) {
					this.loadChildren(category);
				}
			}, "ClientScriptRepoLoader");
			thread.setDaemon(false);
			thread.start();
		});
	}

	public String getScriptFromWeb(Category category, String name, boolean fromCache) {
		ScriptFile scriptFile = this.getScriptFromName(category, name);
		if (scriptFile == null) {
			return null;
		}
		if (fromCache && scriptFile.content != null) {
			return scriptFile.content;
		}
		String content = NetworkUtils.getStringFromUrl(scriptFile.downloadUrl);
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

	// Can accept senseiwells/clientscript/tree/main/scripts, senseiwells/clientscript/scripts, senseiwells/clientscript, https links
	private String getApiAddress(String targetString) {
		if (targetString.startsWith("https://github.com/")) {
			targetString = targetString.substring(19);
		}
		if (!targetString.startsWith("https://github.com/") && targetString.contains("/tree/")) {
			String[] split = targetString.split("/");
			if (split.length < 4) {
				return targetString;
			}
			String authorName = split[0];
			String repositoryName = split[1];
			String branchName = split[3];
			return "https://api.github.com/repos/" + authorName + "/" + repositoryName + "/contents/scripts?ref=" + branchName;
		}
		else if (!targetString.startsWith("https://api.github.com/repos/")) {
			String[] split = targetString.split("/");
			if (split.length < 2) {
				return targetString;
			}
			String authorName = split[0];
			String repositoryName = split[1];
			return "https://api.github.com/repos/" + authorName + "/" + repositoryName + "/contents/scripts";
		}
		return targetString;
	}

	private boolean loadChildren(Category category) {
		for (String repo : ClientRules.CLIENT_SCRIPT_REPOS.getValue()) {
			repo = this.getApiAddress(repo);
			// get ?ref=main if exists
			String reference = "";
			if (repo.contains("?")) {
				 reference = repo.substring(repo.indexOf("?"));
				 repo = repo.substring(0, repo.indexOf("?"));
			}
			String response = NetworkUtils.getStringFromUrl(repo + "/" + category.toString() + reference);
			if (response == null) {
				EssentialClient.LOGGER.error("Couldn't request data from: " + repo + "/" + category);
				continue;
			}
			try {
				JsonArray childrenFiles = GSON.fromJson(response, JsonArray.class);
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
				Set<ScriptFile> files = this.children.computeIfAbsent(category, c -> new LinkedHashSet<>());
				files.addAll(scriptFileSet);
			} catch (Exception e) {
				EssentialClient.LOGGER.error("Failed to load scripts from: " + repo, e);
			}
		}
		return this.children.containsKey(category);
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
