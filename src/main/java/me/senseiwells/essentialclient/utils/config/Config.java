package me.senseiwells.essentialclient.utils.config;

import com.google.gson.*;
import me.senseiwells.arucas.utils.FileUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Config<T extends JsonElement> {
	Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();

	/**
	 * This is used when displaying
	 * errors about the config
	 *
	 * @return the name of the config
	 */
	String getConfigName();

	/**
	 * This gets the type of Json, we cannot cast
	 * to T as it will throw outside the catch
	 *
	 * @return the JsonElement type
	 */
	Class<T> getJsonType();

	/**
	 * This gets the data that will
	 * be saved to the config
	 *
	 * @return the data that should be saved
	 */
	JsonElement getSaveData();

	/**
	 * This passes in the config
	 * data to be processed
	 *
	 * @param element the config data
	 */
	void readConfig(T element);

	/**
	 * This should be called when
	 * you want to read a config file
	 */
	default void readConfig() {
		T element = this.getConfigData();
		if (element != null) {
			this.readConfig(element);
		}
	}

	/**
	 * This is the path of the config, it's
	 * used to read and write the file
	 *
	 * @return the path of the config
	 */
	default Path getConfigPath() {
		return this.getConfigRootPath().resolve(this.getConfigName() + ".json");
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
		FileUtils.ensureExists(root);
		return root;
	}

	/**
	 * This gets the config data
	 * from the specified file
	 *
	 * @return the config data
	 */
	default T getConfigData() {
		Path configPath = this.getConfigPath();
		if (Files.isRegularFile(configPath)) {
			try (BufferedReader reader = Files.newBufferedReader(configPath)) {
				return this.GSON.fromJson(reader, this.getJsonType());
			} catch (IOException | JsonParseException e) {
				EssentialClient.LOGGER.error("Failed to read '{}': {}", this.getConfigName(), e);
			}
		}
		return null;
	}

	/**
	 * This should be called when you
	 * want to save the config file
	 */
	default void saveConfig() {
		Path configPath = this.getConfigPath();
		try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
			this.GSON.toJson(this.getSaveData(), writer);
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to save '{}': {}", this.getConfigName(), e);
		}
	}

	interface CList extends Config<JsonArray> {
		@Override
		default Class<JsonArray> getJsonType() {
			return JsonArray.class;
		}
	}

	interface CMap extends Config<JsonObject> {
		@Override
		default Class<JsonObject> getJsonType() {
			return JsonObject.class;
		}
	}
}
