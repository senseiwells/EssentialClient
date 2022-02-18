package essentialclient.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import essentialclient.utils.command.PlayerData;
import essentialclient.utils.command.WorldEnum;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.concurrent.CompletableFuture;

public class ConfigPlayerClient extends MappedStringConfig<PlayerData> {
	public static final ConfigPlayerClient INSTANCE = new ConfigPlayerClient();

	private ConfigPlayerClient() { }

	public CompletableFuture<Suggestions> suggestPlayer(SuggestionsBuilder builder) {
		return this.map.isEmpty() ? builder.buildFuture() : CommandSource.suggestMatching(this.map.keySet(), builder);
	}

	@Override
	public String getConfigName() {
		return "PlayerClient";
	}

	@Override
	protected JsonElement valueToJson(PlayerData value) {
		JsonObject playerData = new JsonObject();
		playerData.addProperty("x", value.pos().x);
		playerData.addProperty("y", value.pos().y);
		playerData.addProperty("z", value.pos().z);
		playerData.addProperty("yaw", value.rotation().x);
		playerData.addProperty("pitch", value.rotation().y);
		playerData.addProperty("dimension", value.world().name().toLowerCase());
		playerData.addProperty("gamemode", value.gamemode() == null ? "" : value.gamemode().getName());
		return playerData;
	}

	@Override
	protected PlayerData jsonToValue(String key, JsonElement valueElement) {
		JsonObject playerData = valueElement.getAsJsonObject();
		double x = playerData.get("x").getAsDouble();
		double y = playerData.get("y").getAsDouble();
		double z = playerData.get("z").getAsDouble();
		float yaw = playerData.get("yaw").getAsFloat();
		float pitch = playerData.get("pitch").getAsFloat();
		WorldEnum world = WorldEnum.valueOf(playerData.get("dimension").getAsString().toUpperCase());
		GameMode gameMode = GameMode.byName(playerData.get("gamemode").getAsString(), null);
		return new PlayerData(new Vec3d(x, y, z), new Vec2f(yaw, pitch), world, gameMode);
	}
}
