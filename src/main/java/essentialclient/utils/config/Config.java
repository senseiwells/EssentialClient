package essentialclient.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import essentialclient.EssentialClient;
import essentialclient.utils.EssentialUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Config {
	Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	/**
	 * This is used when displaying
	 * errors about the config
	 *
	 * @return the name of the config
	 */
	String getConfigName();

	/**
	 * This is the path of the config, it's
	 * used to read and write the file
	 *
	 * @return the path of the config
	 */
	Path getConfigPath();

	/**
	 * This gets the data that will
	 * be saved to the config
	 *
	 * @return the data that should be saved
	 */
	JsonArray getSaveData();

	/**
	 * This passes in the config
	 * data to be processed
	 *
	 * @param jsonArray the config data
	 */
	void readConfig(JsonArray jsonArray);

	/**
	 * This should be called when
	 * you want to read a config file
	 */
	default void readConfig() {
		this.readConfig(this.getConfigData());
	}

	/**
	 * This gets the root config folder
	 * in this case in the EssentialClient
	 * folder located in .minecraft/config
	 *
	 * @return the root path of the config
	 */
	default Path getConfigRootPath() {
		Path root = EssentialUtils.getEssentialConfigFile();
		if (!Files.exists(root)) {
			EssentialUtils.throwAsRuntime(() -> Files.createDirectory(root));
		}
		return root;
	}

	/**
	 * This gets the config data
	 * from the specified file
	 *
	 * @return the config data
	 */
	default JsonArray getConfigData() {
		Path configPath = this.getConfigPath();
		if (Files.isRegularFile(configPath)) {
			try (BufferedReader reader = Files.newBufferedReader(configPath)) {
				return this.GSON.fromJson(reader, JsonArray.class);
			}
			catch (IOException e) {
				EssentialClient.LOGGER.error("Failed to read '{}': {}", this.getConfigName(), e);
			}
		}
		return new JsonArray();
	}

	/**
	 * This should be called when you
	 * want to save the config file
	 */
	default void saveConfig() {
		Path configPath = this.getConfigPath();
		try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
			this.GSON.toJson(this.getSaveData(), writer);
		}
		catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to save '{}': {}", this.getConfigName(), e);
		}
	}
}
