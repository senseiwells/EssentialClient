package essentialclient.utils.misc;

import com.google.gson.*;
import essentialclient.utils.EssentialUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class ClientUpdater {
	public static final ClientUpdater INSTANCE = new ClientUpdater();

	private final String RELEASES_URL;
	private final Gson GSON;

	private ClientUpdater() {
		this.RELEASES_URL = "https://api.github.com/repos/senseiwells/EssentialClient/releases";
		this.GSON = new GsonBuilder().create();
	}

	public void tryUpdate() {
		String content = NetworkUtils.getStringFromUrl(this.RELEASES_URL);
		if (content == null) {
			return;
		}
		JsonArray releases = this.GSON.fromJson(content, JsonArray.class);
		String minecraftVersion = EssentialUtils.getMinecraftVersion();
		String essentialClientVersion = EssentialUtils.getVersion();
		for (JsonElement element : releases) {
			JsonObject object = element.getAsJsonObject();
			String version = object.get("tag_name").getAsString().substring(1);
			int code = this.compareUpdates(essentialClientVersion, version);
			switch (code) {
				case -1 -> {
					EssentialUtils.sendMessage("You have an unknown version of EssentialClient, can't update!");
					return;
				}
				case 0 -> {
					EssentialUtils.sendMessage("You are already up to date with EssentialClient!");
					return;
				}
			}
			JsonArray assets = object.get("assets").getAsJsonArray();
			for (JsonElement assetElement : assets) {
				JsonObject assetObject = assetElement.getAsJsonObject();
				String assetName = assetObject.get("name").getAsString();
				if (!assetName.equals("essential-client-%s-%s.jar".formatted(minecraftVersion, version))) {
					continue;
				}
				Text message = this.tryDownloadUrl(assetObject.get("browser_download_url").getAsString(), assetName) ?
					new LiteralText("Successfully downloaded '%s' to your mods folder. Please remove the old version.\n".formatted(assetName)).append(
						new LiteralText("[Click here to open mods folder]").formatted(Formatting.GOLD, Formatting.BOLD).styled(style -> style.withClickEvent(
							new ClickEvent(ClickEvent.Action.OPEN_FILE, FabricLoader.getInstance().getGameDir().resolve("mods").toString()))
						)
					) : new LiteralText("Failed to download '%s'".formatted(assetName));
				EssentialUtils.sendMessage(message);
				return;
			}
		}
		EssentialUtils.sendMessage("Could not find any version to update to");
	}

	private boolean tryDownloadUrl(String url, String name) {
		try {
			FileUtils.copyURLToFile(new URL(url), FabricLoader.getInstance().getGameDir().resolve("mods").resolve(name).toFile(), 1000, 1000);
			return true;
		}
		catch (IOException ioException) {
			return false;
		}
	}

	private int compareUpdates(String current, String latest) {
		if (current.equals(latest)) {
			// Same
			return 0;
		}
		String[] currentSplit = current.split("\\.");
		String[] latestSplit = latest.split("\\.");
		if (currentSplit.length != 3 || latestSplit.length != 3) {
			// Invalid
			return -1;
		}
		int[] currentInt = Arrays.stream(currentSplit).mapToInt(Integer::parseInt).toArray();
		int[] latestInt = Arrays.stream(latestSplit).mapToInt(Integer::parseInt).toArray();
		for (int i = 0; i < 3; i++) {
			if (currentInt[i] < latestInt[i]) {
				// Can update
				return 1;
			}
			if (currentInt[i] > latestInt[i]) {
				// Invalid
				return -1;
			}
		}
		return 0;
	}
}
