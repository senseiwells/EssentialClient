package essentialclient.gui.clientrule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import essentialclient.EssentialClient;
import essentialclient.utils.file.FileHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ClientRuleHelper {

    public static CommandTreeS2CPacket serverPacket;

    protected static Map<String, String> clientRulesMap = new HashMap<>();

    public static final Codec<String> CODEC = Codec.STRING;
    public static final Codec<Map<String, String>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeSaveFile() {
        Path file = getFile();
        try(BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, clientRulesMap)
                    .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write rule data: {}", e))
                    .ifPresent(obj -> GSON.toJson(obj, writer));
        }
        catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save rule data");
        }
    }

    public static void readSaveFile() {
        Path file = getFile();
        clientRulesMap = new HashMap<>();
        if (!Files.isRegularFile(file)) {
            checkRules();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            clientRulesMap = new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                    .getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read rule data: {}", e))
                    .getFirst());
        }
        //many exceptions
        catch (Exception e) {
            try {
                Files.deleteIfExists(file);
                EssentialClient.LOGGER.warn("Removed the outdated/corrupt config file");
            }
            catch (IOException ioException) {
                EssentialClient.LOGGER.error("Something went very wrong, please delete your config file manually");
            }
            EssentialClient.LOGGER.info("Created default rule data");
        }
        checkRules();
    }

    private static Path getFile() {
        FileHelper.checkIfEssentialClientDirExists();
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialClientRules.json");
    }

    public static void executeOnChange(MinecraftClient client, ClientRules settings) {
        ClientPlayerEntity playerEntity = client.player;
        if (settings == ClientRules.HIGHLIGHTLAVASOURCES) {
            client.worldRenderer.reload();
        }
        if (playerEntity != null) {
            if (settings.isCommand) {
                playerEntity.sendMessage(new LiteralText("§cRelog for client command changes to take full effect"), false);
                playerEntity.networkHandler.onCommandTree(serverPacket);
                /* The game still suggests the command but it registers it as invalid, idk how to fix :P
                 * MinecraftClient.getInstance().getNetworkHandler().onCommandTree(new CommandTreeS2CPacket((RootCommandNode<CommandSource>) (Object) ClientCommandManager.DISPATCHER.getRoot()));
                 * MinecraftClient.getInstance().getNetworkHandler().onCommandSuggestions(new CommandSuggestionsS2CPacket());
                 */
            }
        }
    }
    protected static void checkRules() {
        for (ClientRules rule : ClientRules.values()) {
            clientRulesMap.putIfAbsent(rule.name, rule.defaultValue);
        }
    }

    public static Collection<ClientRules> getRules() {
        SortedMap<String, ClientRules> sortedMap = new TreeMap<>();
        for (ClientRules rule : ClientRules.values())
            sortedMap.put(rule.name, rule);
        return sortedMap.values();
    }
}
