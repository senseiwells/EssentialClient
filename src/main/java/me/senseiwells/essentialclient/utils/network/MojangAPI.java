package me.senseiwells.essentialclient.utils.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.senseiwells.arucas.utils.NetworkUtils;

import java.util.*;

public class MojangAPI {
	private static final String NAME_TO_UUID_LINK = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String UUID_TO_NAME_LINK = "https://api.mojang.com/user/profile/";
	private static final Gson GSON = new Gson();

	private static Map<String, UUID> nameToUuidCache;
	private static Map<UUID, String> uuidToNameCache;
	private static Set<String> invalidNames;
	private static Set<UUID> invalidUuids;

	public static UUID getUuidFromName(String name) {
		if (invalidNames != null && invalidNames.contains(name)) {
			return null;
		}
		UUID uuid;
		if (nameToUuidCache != null && (uuid = nameToUuidCache.get(name)) != null) {
			return uuid;
		}
		String response = NetworkUtils.getStringFromUrl(NAME_TO_UUID_LINK + name);
		JsonObject object = GSON.fromJson(response, JsonObject.class);
		if (object == null || object.has("error")) {
			if (invalidNames == null) {
				invalidNames = new HashSet<>();
			}
			invalidNames.add(name);
			return null;
		}
		uuid = UUID.fromString(object.get("id").getAsString());
		if (nameToUuidCache == null) {
			nameToUuidCache = new HashMap<>();
		}
		if (uuidToNameCache == null) {
			uuidToNameCache = new HashMap<>();
		}
		nameToUuidCache.put(name, uuid);
		uuidToNameCache.put(uuid, name);
		return uuid;
	}

	public static String getNameFromUuid(UUID uuid) {
		if (invalidUuids != null && invalidUuids.contains(uuid)) {
			return null;
		}
		String name;
		if (uuidToNameCache != null && (name = uuidToNameCache.get(uuid)) != null) {
			return name;
		}
		String response = NetworkUtils.getStringFromUrl(UUID_TO_NAME_LINK + uuid.toString());
		JsonObject object = GSON.fromJson(response, JsonObject.class);
		if (object == null || object.has("error")) {
			if (invalidUuids == null) {
				invalidUuids = new HashSet<>();
			}
			invalidUuids.add(uuid);
			return null;
		}
		name = object.get("name").getAsString();
		if (nameToUuidCache == null) {
			nameToUuidCache = new HashMap<>();
		}
		if (uuidToNameCache == null) {
			uuidToNameCache = new HashMap<>();
		}
		nameToUuidCache.put(name, uuid);
		uuidToNameCache.put(uuid, name);
		return name;
	}
}
