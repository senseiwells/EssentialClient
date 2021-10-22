package essentialclient.utils;

import essentialclient.feature.clientscript.ClientScript;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EssentialUtils {

    public static void sendMessageToActionBar(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity playerEntity = client.player;
        if (playerEntity != null) {
            client.execute(() -> playerEntity.sendMessage(new LiteralText(message), true));
        }
    }
    
    public static void sendMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity playerEntity = client.player;
        if (playerEntity != null) {
            client.execute(() -> playerEntity.sendMessage(new LiteralText(message), false));
        }
    }
    
    public static void sendMessage(Text text) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity playerEntity = client.player;
        if (playerEntity != null) {
            client.execute(() -> playerEntity.sendMessage(text, false));
        }
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

    public static Path getEssentialConfigFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
    }
}
