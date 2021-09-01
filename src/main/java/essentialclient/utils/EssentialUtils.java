package essentialclient.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EssentialUtils {

    public static void sendMessageToActionBar(String message) {
        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity == null)
            return;
        playerEntity.sendMessage(new LiteralText(message), true);
    }
    public static void sendMessage(String message) {
        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity == null)
            return;
        playerEntity.sendMessage(new LiteralText(message), false);
    }

    public static void checkIfEssentialClientDirExists() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
