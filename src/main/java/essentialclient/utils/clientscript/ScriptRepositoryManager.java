package essentialclient.utils.clientscript;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import essentialclient.clientscript.core.ClientScript;
import net.minecraft.command.CommandException;
import net.minecraft.text.LiteralText;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScriptRepositoryManager {
	public static final ScriptRepositoryManager INSTANCE = new ScriptRepositoryManager();

	private final String REPOSITORY_URL;
	private final Gson GSON;
	private final DynamicCommandExceptionType NO_SUCH_SCRIPT = new DynamicCommandExceptionType(o -> new LiteralText("Script with name '%s' doesn't exist".formatted(o)));
	private final Map<Category, Set<ScriptFile>> children;

	private ScriptRepositoryManager() {
		this.REPOSITORY_URL = "https://api.github.com/repos/senseiwells/clientscript/contents/scripts";
		this.GSON = new GsonBuilder().create();
		this.children = new HashMap<>();
	}

	public String getScriptFromWeb(Category category, String name, boolean fromCache) throws CommandSyntaxException {
		ScriptFile scriptFile = this.getScriptFromName(category, name);
		if (fromCache && scriptFile.content != null) {
			return scriptFile.content;
		}
		try {
			return scriptFile.content = getStringFromUrl(scriptFile.downloadUrl);
		}
		catch (IOException ioException) {
			throw this.NO_SUCH_SCRIPT.create(name);
		}
	}

	public void downloadScript(Category category, String name, boolean overwrite) throws CommandSyntaxException {
		Set<ScriptFile> scriptFileSet = this.getChildren(category);
		if (!scriptFileSet.contains(new ScriptFile(name, null))) {
			throw this.NO_SUCH_SCRIPT.create(name);
		}
		Path scriptDir = ClientScript.getDir();
		Path newScript = scriptDir.resolve(name + ".arucas");
		if (!overwrite && Files.exists(newScript)) {
			for (int i = 1; ; i++) {
				newScript = scriptDir.resolve(name + i + ".arucas");
				if (!Files.exists(newScript)) {
					break;
				}
			}
		}
		try (BufferedWriter writer = Files.newBufferedWriter(newScript)) {
			String fileContent = this.getScriptFromWeb(category, name, false);
			writer.write(fileContent);
		}
		catch (IOException ioException) {
			throw this.NO_SUCH_SCRIPT.create(name);
		}
	}

	public List<String> getChildrenNames(Category category) {
		return this.getChildren(category).stream().map(scriptFile -> scriptFile.name).toList();
	}

	private ScriptFile getScriptFromName(Category category, String name) throws CommandSyntaxException {
		return this.getChildren(category).stream().filter(s -> s.name.equals(name)).findAny().orElseThrow(() -> this.NO_SUCH_SCRIPT.create(name));
	}

	private Set<ScriptFile> getChildren(Category category) {
		if (!this.children.containsKey(category)) {
			try {
				this.loadChildren(category);
			}
			catch (IOException ioException) {
				throw new CommandException(new LiteralText("Could not load script names"));
			}
		}
		return this.children.get(category);
	}

	private void loadChildren(Category category) throws IOException {
		String response = getStringFromUrl(this.REPOSITORY_URL + "/" + category.toString());
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
			scriptFileSet.add(new ScriptFile(childName, childDownload));
		}
		this.children.put(category, scriptFileSet);
	}

	private static String getStringFromUrl(String url) throws IOException {
		InputStream inputStream = new URL(url).openStream();
		if (inputStream == null) {
			throw new IOException("Script doesn't exist");
		}
		Writer stringWriter = new StringWriter();
		char[] charBuffer = new char[2048];
		try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			int counter;
			while ((counter = reader.read(charBuffer)) != -1) {
				stringWriter.write(charBuffer, 0, counter);
			}
		}
		finally {
			inputStream.close();
		}
		return stringWriter.toString();
	}

	private static class ScriptFile {
		private final String name;
		private final String downloadUrl;
		private String content;

		ScriptFile(String name, String downloadUrl) {
			this.name = name;
			this.downloadUrl = downloadUrl;
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
		AUTOMATION("automation"),
		UTILITIES("utilities"),
		MISCELLANEOUS("miscellaneous");

		private final String name;

		Category(String prettyName) {
			this.name = prettyName;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
}
