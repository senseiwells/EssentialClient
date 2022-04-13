package me.senseiwells.essentialclient.utils.clientscript;

import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.fabricmc.mapping.reader.v2.MappingGetter;
import net.fabricmc.mapping.reader.v2.TinyMetadata;
import net.fabricmc.mapping.reader.v2.TinyV2Factory;
import net.fabricmc.mapping.reader.v2.TinyVisitor;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Taken mostly from NotEnoughCrashes
 * <p>
 * Need to figure out a way to map Yarn -> Intermediary,
 * so you can call Yarn methods in Arucas, and they would map to
 * Intermediary to be called internally.
 * <br>
 * I think I need to make my own parser for the .tiny file,
 * sounds like pain.
 * </p>
 *
 */
public final class MinecraftDeobfuscator {
	private static final Map<String, String> OFUSCATION_MAPPINGS;
	private static final Map<String, String> DEOFUSCATION_MAPPINGS;
	private static final String MAPPINGS_JAR_LOCATION;
	private static final String NAMESPACE_FROM;
	private static final String NAMESPACE_TO;
	private static final Path MAPPINGS_DIRECTORY;
	private static final Path CACHED_MAPPINGS;

	static {
		OFUSCATION_MAPPINGS = new HashMap<>();
		DEOFUSCATION_MAPPINGS = new HashMap<>();
		MAPPINGS_JAR_LOCATION = "mappings/mappings.tiny";
		NAMESPACE_FROM = "intermediary";
		NAMESPACE_TO = "named";

		MAPPINGS_DIRECTORY = EssentialUtils.getEssentialConfigFile().resolve("Mappings");
		if (!Files.exists(MAPPINGS_DIRECTORY) && !ExceptionUtils.runSafe(() -> Files.createDirectory(MAPPINGS_DIRECTORY))) {
			EssentialClient.LOGGER.error("Failed to create Mappings directory");
		}
		CACHED_MAPPINGS = MAPPINGS_DIRECTORY.resolve("mappings-" + EssentialUtils.getMinecraftVersion() + ".tiny");

		Throwable throwable = ExceptionUtils.returnThrowable(() -> {
			if (!Files.exists(CACHED_MAPPINGS)) {
				downloadAndCacheMappings();
			}
		});

		if (throwable != null) {
			EssentialClient.LOGGER.error("Failed to load mappings!", throwable);
		}
	}

	public static void init() { }

	public static String deobfuscate(String name) {
		if (DEOFUSCATION_MAPPINGS.isEmpty()) {
			loadMappings();
			if (DEOFUSCATION_MAPPINGS.isEmpty()) {
				return name;
			}
		}

		String mapped = DEOFUSCATION_MAPPINGS.get(name);
		return mapped == null ? name : mapped;
	}

	public static String obfuscate(String name) {
		if (OFUSCATION_MAPPINGS.isEmpty()) {
			// loadMappings();
			if (OFUSCATION_MAPPINGS.isEmpty()) {
				return name;
			}
		}

		return name;
	}

	private static void downloadAndCacheMappings() {
		String yarnVersion = ExceptionUtils.catchAsNull(Yarn::getLatestYarn);
		if (yarnVersion == null) {
			EssentialClient.LOGGER.error("Could not get latest yarn build for version");
			return;
		}

		EssentialClient.LOGGER.info("Downloading deobfuscation mappings: " + yarnVersion + " for the first launch");

		String encodedYarnVersion = UrlEscapers.urlFragmentEscaper().escape(yarnVersion);
		// Download V2 jar
		String artifactUrl = "https://maven.fabricmc.net/net/fabricmc/yarn/" + encodedYarnVersion + "/yarn-" + encodedYarnVersion + "-v2.jar";

		File jarFile = MAPPINGS_DIRECTORY.resolve("yarn-mappings.jar").toFile();
		jarFile.deleteOnExit();

		Throwable throwable = ExceptionUtils.returnThrowable(() -> FileUtils.copyURLToFile(new URL(artifactUrl), jarFile));
		if (throwable != null) {
			EssentialClient.LOGGER.error("Failed to downloads mappings!", throwable);
			return;
		}

		try (FileSystem jar = FileSystems.newFileSystem(jarFile.toPath(), (ClassLoader) null)) {
			Files.copy(jar.getPath(MAPPINGS_JAR_LOCATION), CACHED_MAPPINGS, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to extract mappings!", e);
		}
	}


	private static void loadMappings() {
		if (!Files.exists(CACHED_MAPPINGS)) {
			EssentialClient.LOGGER.warn("Could not download mappings, stack trace won't be deobfuscated");
			return;
		}

		try (BufferedReader mappingReader = Files.newBufferedReader(CACHED_MAPPINGS)) {
			TinyV2Factory.visit(mappingReader, new TinyVisitorImpl());
		}
		catch (IOException e) {
			EssentialClient.LOGGER.error("Could not load mappings", e);
		}
	}

	/**
	 * This maps Intermediary -> Yarn
	 */
	private static class TinyVisitorImpl implements TinyVisitor {
		private final Map<String, Integer> namespaceStringToColumn = new HashMap<>();

		private void addMappings(MappingGetter name) {
			DEOFUSCATION_MAPPINGS.put(name.get(this.namespaceStringToColumn.get(NAMESPACE_FROM)).replace('/', '.'),
				name.get(this.namespaceStringToColumn.get(NAMESPACE_TO)).replace('/', '.'));
		}

		@Override
		public void start(TinyMetadata metadata) {
			this.namespaceStringToColumn.put(NAMESPACE_FROM, metadata.index(NAMESPACE_FROM));
			this.namespaceStringToColumn.put(NAMESPACE_TO, metadata.index(NAMESPACE_TO));
		}

		@Override
		public void pushClass(MappingGetter name) {
			this.addMappings(name);
		}

		@Override
		public void pushMethod(MappingGetter name, String descriptor) {
			this.addMappings(name);
		}

		@Override
		public void pushField(MappingGetter name, String descriptor) {
			this.addMappings(name);
		}
	}

	@SuppressWarnings("unused")
	private static class Yarn {
		private static final String YARN_API_ENTRYPOINT;
		private static final Path YARN_VERSION;
		private static String VERSION_CACHE;

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