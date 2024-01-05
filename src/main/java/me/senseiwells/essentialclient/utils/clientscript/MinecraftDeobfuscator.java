package me.senseiwells.essentialclient.utils.clientscript;

import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.fabricmc.loader.impl.lib.mappingio.MappedElementKind;
import net.fabricmc.loader.impl.lib.mappingio.MappingVisitor;
import net.fabricmc.loader.impl.lib.mappingio.format.tiny.Tiny2FileReader;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;

/**
 * Taken mostly from NotEnoughCrashes
 * <p>
 * Modified to map Yarn -> Intermediary
 */
public final class MinecraftDeobfuscator {
	/**
	 * This map will only contain class deobfuscation mappings since
	 * we don't care about methods, fields, etc. We only deobfuscate
	 * when obfuscating method names, and we need the deobfuscated class
	 * name to do so (to ensure we have the correct method + declaring class)
	 */
	private static final Map<String, String> CLASS_DEOBFUSCATION_MAPPINGS;
	private static final Map<String, String> OBFUSCATION_MAPPINGS;
	private static final String MAPPINGS_JAR_LOCATION;
	private static final Path MAPPINGS_DIRECTORY;
	private static final Path CACHED_MAPPINGS;

	private static boolean triedLoadingMappings;

	static {
		OBFUSCATION_MAPPINGS = new HashMap<>();
		CLASS_DEOBFUSCATION_MAPPINGS = new HashMap<>();
		MAPPINGS_JAR_LOCATION = "mappings/mappings.tiny";
		triedLoadingMappings = false;

		MAPPINGS_DIRECTORY = EssentialUtils.getEssentialConfigFile().resolve("Mappings");
		try {
			if (!Files.exists(MAPPINGS_DIRECTORY)) {
				Files.createDirectory(MAPPINGS_DIRECTORY);
			}
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to create Mappings directory");
		}

		CACHED_MAPPINGS = MAPPINGS_DIRECTORY.resolve("mappings-" + EssentialUtils.getMinecraftVersion() + ".tiny");

		if (!Files.exists(CACHED_MAPPINGS)) {
			downloadAndCacheMappings();
		}
	}

	public static void load() {  }

	public static String obfuscate(String name) {
		if (!triedLoadingMappings) {
			loadMappings();
		}

		String mapped = OBFUSCATION_MAPPINGS.get(name);
		if (ClientRules.CLIENTSCRIPT_DEBUGGER.getValue()) {
			EssentialClient.LOGGER.info("Obfuscating {} -> {}", name, Objects.requireNonNullElse(mapped, name));
		}
		return mapped == null ? name : mapped;
	}

	public static String deobfuscateClass(String name) {
		if (!triedLoadingMappings) {
			loadMappings();
		}

		String mapped = CLASS_DEOBFUSCATION_MAPPINGS.get(name);
		if (ClientRules.CLIENTSCRIPT_DEBUGGER.getValue()) {
			EssentialClient.LOGGER.info("Deobfuscating {} -> {}", name, Objects.requireNonNullElse(mapped, name));
		}
		return mapped == null ? name : mapped;
	}

	private static void downloadAndCacheMappings() {
		String yarnVersion;
		try {
			yarnVersion = Yarn.getLatestYarn();
		} catch (Exception e) {
			EssentialClient.LOGGER.error("Could not get latest yarn build for version");
			return;
		}

		EssentialClient.LOGGER.info("Downloading deobfuscation mappings: " + yarnVersion + " for the first launch");

		String encodedYarnVersion = UrlEscapers.urlFragmentEscaper().escape(yarnVersion);
		// Download V2 jar
		String artifactUrl = "https://maven.fabricmc.net/net/fabricmc/yarn/" + encodedYarnVersion + "/yarn-" + encodedYarnVersion + "-v2.jar";

		File jarFile = MAPPINGS_DIRECTORY.resolve("yarn-mappings.jar").toFile();
		jarFile.deleteOnExit();

		try {
			FileUtils.copyURLToFile(new URL(artifactUrl), jarFile);
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to downloads mappings!", e);
			return;
		}

		try (FileSystem jar = FileSystems.newFileSystem(jarFile.toPath(), (ClassLoader) null)) {
			Files.copy(jar.getPath(MAPPINGS_JAR_LOCATION), CACHED_MAPPINGS, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to extract mappings!", e);
		}
	}

	private static void loadMappings() {
		if (!Files.exists(CACHED_MAPPINGS)) {
			EssentialClient.LOGGER.warn("Could not download mappings");
			return;
		}

		triedLoadingMappings = true;
		try (BufferedReader mappingReader = Files.newBufferedReader(CACHED_MAPPINGS)) {
			Tiny2FileReader.read(mappingReader, new MappingVisitorImpl());
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Could not load mappings", e);
		}
	}

	/**
	 * This maps Intermediary -> Yarn (For classes only),
	 * and also maps Yarn -> Intermediary
	 */
	private static class MappingVisitorImpl implements MappingVisitor {
		private String fromClass;
		private String toClass;
		private String intermediary;

		@Override
		public void visitNamespaces(String s, List<String> list) {

		}

		@Override
		public boolean visitClass(String s) {
			this.fromClass = s.replace('/', '.');
			return true;
		}

		@Override
		public boolean visitField(String s, @Nullable String s1) {
			this.intermediary = s;
			return true;
		}

		@Override
		public boolean visitMethod(String s, @Nullable String s1) {
			this.intermediary = s;
			return true;
		}

		@Override
		public boolean visitMethodArg(int i, int i1, @Nullable String s) {
			return true;
		}

		@Override
		public boolean visitMethodVar(int i, int i1, int i2, int i3, @Nullable String s) {
			return true;
		}

		@Override
		public void visitDstName(MappedElementKind mappedElementKind, int i, String s) {
			if (s.equals("<init>")) {
				return;
			}

			if (mappedElementKind == MappedElementKind.CLASS) {
				this.toClass = s.replace('/', '.');
				CLASS_DEOBFUSCATION_MAPPINGS.put(this.fromClass, this.toClass);
				OBFUSCATION_MAPPINGS.put(this.toClass, this.fromClass);
				return;
			}

			String from = this.fromClass + "#" + this.intermediary;
			String to = this.toClass + "#" + s;
			if (mappedElementKind == MappedElementKind.METHOD) {
				from += "()";
				to += "()";
			} else if (mappedElementKind != MappedElementKind.FIELD) {
				return;
			}

			OBFUSCATION_MAPPINGS.put(from, to);
		}

		@Override
		public void visitComment(MappedElementKind mappedElementKind, String s) {

		}
	}

	@SuppressWarnings("unused")
	private static class Yarn {
		private static final String YARN_API_ENTRYPOINT;
		private static final Path YARN_VERSION;
		private static String VERSION_CACHE; // Checkstyle ignore

		public String gameVersion;
		public String separator;
		public int build;
		public String maven;
		public String version;
		public boolean stable;

		static {
			YARN_API_ENTRYPOINT = "https://meta.fabricmc.net/v2/versions/yarn/" + EssentialUtils.getMinecraftVersion();
			YARN_VERSION = MAPPINGS_DIRECTORY.resolve("yarn-version.txt");
		}

		private static String getLatestYarn() throws IOException {
			if (VERSION_CACHE == null) {
				if (!Files.exists(YARN_VERSION)) {
					URL url = new URL(YARN_API_ENTRYPOINT);
					URLConnection request = url.openConnection();
					request.connect();

					InputStream response = (InputStream) request.getContent();
					Yarn[] versions = new Gson().fromJson(new InputStreamReader(response), Yarn[].class);
					if (versions.length == 0) {
						throw new IllegalStateException("No yarn versions were received at the API endpoint. Received json: " + getString(response));
					}
					String version = Arrays.stream(versions).max(Comparator.comparingInt(v -> v.build)).get().version;
					Files.write(YARN_VERSION, version.getBytes());
					VERSION_CACHE = version;
				} else {
					VERSION_CACHE = new String(Files.readAllBytes(YARN_VERSION));
				}
			}

			return VERSION_CACHE;
		}

		private static String getString(InputStream inputStream) throws IOException {
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
