package essentialclient.gui.clientrule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import essentialclient.EssentialClient;
import essentialclient.utils.file.FileHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ClientRuleHelper {

    public static final MapCodec<ClientRule> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            Codec.STRING.fieldOf("value").forGetter(b -> b.value)
    ).apply(it, (name, value) -> {
        ClientRule data = ClientRule.clientRulesMap.remove(name);
        data.value = value;
        ClientRule.clientRulesMap.put(name, data);
        return data;
    }));

    public static final Codec<Map<String, ClientRule>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC.codec());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeSaveFile() {
        Path file = getFile();
        try(BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, ClientRule.clientRulesMap)
                    .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write rule data: {}", e))
                    .ifPresent(obj -> GSON.toJson(obj, writer));
        }
        catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save rule data");
        }
    }

    public static void readSaveFile(){
        Path file = getFile();
        ClientRule.clientRulesMap = new HashMap<>();
        checkRules();
        if (!Files.isRegularFile(file))
            return;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                    .getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read rule data: {}", e))
                    .getFirst());
        }
        //many exceptions
        catch (Exception e) {
            e.printStackTrace();
            try {
                Files.deleteIfExists(file);
                EssentialClient.LOGGER.warn("Removed the config file");
            }
            catch (IOException ioException) {
                EssentialClient.LOGGER.warn("Something went very wrong, please delete your config file manually");
            }
            EssentialClient.LOGGER.warn("Created new rule data");
        }
    }

    private static void checkRules() {
        //Boolean Rules
        addDefaultBooleanRule("commandPlayerClient"                   , "This command allows you to save /player... commands and execute them"                        , true);
        addDefaultBooleanRule("commandRegion"                         , "This command allows you to determine the region you are in or the region at set coords"      , true);
        addDefaultBooleanRule("commandTravel"                         , "This command allows you to travel to a set location"                                         , true);
        addDefaultBooleanRule("removeWarnReceivedPassengers"          , "This removes the 'Received passengers for unkown entity' warning on the client"              , false);
        addDefaultBooleanRule("stackableShulkersInPlayerInventories"  , "This allows for shulkers to stack only in your inventory"                                    , false);
        addDefaultBooleanRule("stackableShulkersWithItems"            , "This allows for shulkers with items to stack only in your inventory"                         , false);

        //Number Rules
        addDefaultNumberRule ("announceAFK"                           , "This announces when you become afk after a set amount of time (ticks)");

        //String Rules
        addDefaultStringRule ("announceAFKMessage"                    , "This is the message you announce after you are afk"                                           , "I am now AFK");
    }

    private static Path getFile() {
        FileHelper.checkIfEssentialClientDirExists();
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialClientRules.json");
    }

    private static void addDefaultBooleanRule(String name, String description, boolean isCommand) {
        addRule(name, "boolean", description, "false", "false", isCommand);
    }

    private static void addDefaultNumberRule(String name, String description) {
        addRule(name, "number", description, "0", "0", false);
    }

    private static void addDefaultStringRule(String name, String description, String defaultValue) {
        addRule(name, "string", description, defaultValue, defaultValue, false);
    }

    private static void addRule(String name, String type, String description, String defaultValue, String value, boolean isCommand) {
        ClientRule.clientRulesMap.putIfAbsent(name, new ClientRule(name, type, description, defaultValue, value, isCommand));
    }

    public static boolean getBoolean(String rule) {
        ClientRule data =  ClientRule.clientRulesMap.get(rule);
        if (data != null)
            return Boolean.parseBoolean(data.value);
        return false;
    }

    public static int getNumber(String rule) {
        ClientRule data =  ClientRule.clientRulesMap.get(rule);
        if (data != null && data.type.equalsIgnoreCase("number"))
            return Integer.parseInt(data.value);
        return - 1;
    }

    public static String getString(String rule) {
        ClientRule data =  ClientRule.clientRulesMap.get(rule);
        return data == null ? null : data.value;
    }
}
