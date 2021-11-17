package essentialclient.config.clientrule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import essentialclient.EssentialClient;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.render.CapeHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ClientRuleHelper {
    public static CommandTreeS2CPacket serverPacket;

    public static final Codec<String> CODEC = Codec.STRING;
    public static final Codec<Map<String, String>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeSaveFile() {
        Path file = getFile();
        Map<String, String> stringClientRulesMap = ClientRules.rulesAsStringMap();
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, stringClientRulesMap)
                .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write rule data: {}", e))
                .ifPresent(obj -> GSON.toJson(obj, writer));
        }
        catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save rule data");
        }
    }

    protected static void readSaveFile() {
        Path file = getFile();
        if (!Files.isRegularFile(file)) {
            return;
        }
        Map<String, String> stringClientRulesMap;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            stringClientRulesMap = new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                .getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read rule data: {}", e))
                .getFirst());
        }
        catch (Exception e) {
            try {
                Files.deleteIfExists(file);
                EssentialClient.LOGGER.warn("Removed the outdated/corrupt config file");
            }
            catch (IOException ioException) {
                EssentialClient.LOGGER.error("Something went very wrong, please delete your config file manually");
            }
            return;
        }
        for (Map.Entry<String, String> entry : stringClientRulesMap.entrySet()) {
            ClientRule<?> rule =  ClientRules.ruleFromString(entry.getKey());
            if (rule == null) {
                continue;
            }
            try {
                rule.setValueFromString(entry.getValue());
            }
            catch (Exception e) {
                EssentialClient.LOGGER.error("Error reading EssentialClient config for rule: {}.\n{}", rule.getName(), e.toString());
            }
        }
    }

    private static Path getFile() {
        return EssentialUtils.getEssentialConfigFile().resolve("EssentialClientRules.json");
    }

    public static void refreshCommand() {
        ClientPlayerEntity playerEntity =  EssentialUtils.getPlayer();
        if (playerEntity != null) {
            playerEntity.networkHandler.onCommandTree(serverPacket);
        }
    }

    public static void refreshCape() {
        CapeHelper.setCapeTexture(ClientRules.CUSTOM_CLIENT_CAPE.getValue());
    }

    public static void refreshScreen() {
        MinecraftClient client = EssentialUtils.getClient();
        if (client.currentScreen instanceof RulesScreen rulesScreen) {
            rulesScreen.refreshRules(rulesScreen.getSearchBoxText());
        }
    }
}
