package me.senseiwells.essentialclient.utils.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.senseiwells.arucas.utils.NetworkUtils;

import java.util.*;

public class MojangAPI {
	private static final String NAME_TO_UUID_LINK = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String UUID_TO_NAME_LINK = "https://api.mojang.com/user/profile/";
	private static final Gson GSON = new Gson();

	private static Map<String, UUID> NAME_TO_UUID_CACHE;
	private static Map<UUID, String> UUID_TO_NAME_CACHE;
	private static Set<String> INVALID_NAMES;
	private static Set<UUID> INVALID_UUIDS;

	public static UUID getUuidFromName(String name) {
		if (INVALID_NAMES != null && INVALID_NAMES.contains(name)) {
			return null;
		}
		UUID uuid;
		if (NAME_TO_UUID_CACHE != null && (uuid = NAME_TO_UUID_CACHE.get(name)) != null) {
			return uuid;
		}
		String response = NetworkUtils.getStringFromUrl(NAME_TO_UUID_LINK + name);
		JsonObject object = GSON.fromJson(response, JsonObject.class);
		if (object == null || object.has("error")) {
			if (INVALID_NAMES == null) {
				INVALID_NAMES = new HashSet<>();
			}
			INVALID_NAMES.add(name);
			return null;
		}
		uuid = UUID.fromString(object.get("id").getAsString());
		if (NAME_TO_UUID_CACHE == null) {
			NAME_TO_UUID_CACHE = new HashMap<>();
		}
		if (UUID_TO_NAME_CACHE == null) {
			UUID_TO_NAME_CACHE = new HashMap<>();
		}
		NAME_TO_UUID_CACHE.put(name, uuid);
		UUID_TO_NAME_CACHE.put(uuid, name);
		return uuid;
	}

	public static String getNameFromUuid(UUID uuid) {
		if (INVALID_UUIDS != null && INVALID_UUIDS.contains(uuid)) {
			return null;
		}
		String name;
		if (UUID_TO_NAME_CACHE != null && (name = UUID_TO_NAME_CACHE.get(uuid)) != null) {
			return name;
		}
		String response = NetworkUtils.getStringFromUrl(UUID_TO_NAME_LINK + uuid.toString());
		JsonObject object = GSON.fromJson(response, JsonObject.class);
		if (object == null || object.has("error")) {
			if (INVALID_UUIDS == null) {
				INVALID_UUIDS = new HashSet<>();
			}
			INVALID_UUIDS.add(uuid);
			return null;
		}
		name = object.get("name").getAsString();
		if (NAME_TO_UUID_CACHE == null) {
			NAME_TO_UUID_CACHE = new HashMap<>();
		}
		if (UUID_TO_NAME_CACHE == null) {
			UUID_TO_NAME_CACHE = new HashMap<>();
		}
		NAME_TO_UUID_CACHE.put(name, uuid);
		UUID_TO_NAME_CACHE.put(uuid, name);
		return name;
	}
}
