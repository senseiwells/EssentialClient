package essentialclient.utils;

import essentialclient.clientscript.ClientScript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class EssentialUtils {

    public static void sendMessageToActionBar(String message) {
        MinecraftClient client = getClient();
        ClientPlayerEntity playerEntity = getPlayer();
        if (playerEntity != null) {
            client.execute(() -> playerEntity.sendMessage(new LiteralText(message), true));
        }
    }
    
    public static void sendMessage(String message) {
        MinecraftClient client = getClient();
        ClientPlayerEntity playerEntity = getPlayer();
        if (playerEntity != null) {
            client.execute(() -> playerEntity.sendMessage(new LiteralText(message), false));
        }
    }
    
    public static void sendMessage(Text text) {
        MinecraftClient client = getClient();
        ClientPlayerEntity playerEntity = getPlayer();
        if (playerEntity != null) {
            client.execute(() -> playerEntity.sendMessage(text, false));
        }
    }

    public static Path getEssentialConfigFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

	public static ClientPlayerEntity getPlayer() {
		return getClient().player;
	}

    public static ClientWorld getWorld() {
        return getClient().world;
    }

    @SuppressWarnings("unused")
    public static ClientPlayerInteractionManager getInteractionManager() {
        return getClient().interactionManager;
    }

    @SuppressWarnings("unused")
    public static ClientPlayNetworkHandler getNetworkHandler() {
        return getClient().getNetworkHandler();
    }

    private static ModContainer getModContainer() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("essential-client");
        return modContainer.isEmpty() ? null : modContainer.get();
    }

    public static String getVersion() {
        ModContainer modContainer = getModContainer();
        if (modContainer == null)
            return "unknown";
        return modContainer.getMetadata().getVersion().getFriendlyString();
    }

    public static String getArucasVersion() {
        ModContainer modContainer = getModContainer();
        if (modContainer != null)
            for (ModDependency modDependency : modContainer.getMetadata().getSuggests())
                if (modDependency.getModId().equals("arucas"))
                    return modDependency.getVersionRequirements().toString()
                            .replaceAll("\\[=", "")
                            .replaceAll("]", "");
        return "unknown";
    }

    public static String getMinecraftVersion() {
        return MinecraftVersion.field_25319.getName();
    }

    public static void checkIfEssentialClientDirExists() {
        Path configFile = getEssentialConfigFile();
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectory(configFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void checkifScriptFileExists() {
        Path macroDir = ClientScript.getDir();
        Path macroFile = ClientScript.getFile();
        if (!Files.exists(macroDir)) {
            try {
                Files.createDirectory(macroDir);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(macroFile)) {
            try {
                Files.createFile(macroFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
