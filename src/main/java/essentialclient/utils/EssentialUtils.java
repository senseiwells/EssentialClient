package essentialclient.utils;

import essentialclient.feature.clientmacro.ClientMacro;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.File;
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

    public static void checkifMacroFileExists() {
        Path macroDir = ClientMacro.getDir();
        Path macroFile = ClientMacro.getFile();
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
