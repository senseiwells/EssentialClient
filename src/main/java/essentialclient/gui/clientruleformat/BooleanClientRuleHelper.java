package essentialclient.gui.clientruleformat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
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

public class BooleanClientRuleHelper {
    
    public static final MapCodec<BooleanClientRule> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            Codec.STRING.fieldOf("description").forGetter(s -> s.description),
            Codec.BOOL.fieldOf("defaultValue").forGetter(b -> b.defaultValue),
            Codec.BOOL.fieldOf("value").forGetter(b -> b.value),
            Codec.BOOL.fieldOf("isCommand").forGetter(b -> b.isCommand)
    ).apply(it, BooleanClientRule::new));

    public static final Codec<Map<String, BooleanClientRule>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC.codec());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeSaveFile(Map<String, BooleanClientRule> booleanRule) {
        Path file = getFile();
        try(BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, booleanRule)
                    .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write rule data: {}", e))
                    .ifPresent(obj -> GSON.toJson(obj, writer));
        }
        catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save rule data");
        }
    }

    public static Map<String, BooleanClientRule> readSaveFile() {
        Path file = getFile();
        if (!Files.isRegularFile(file))
            return new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                    .getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read rule data: {}", e))
                    .getFirst());
        }
        catch (JsonParseException | IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.warn("Created new rule data");
        }
        return new HashMap<>();
    }

    public static void checkBooleanRules() {
        addRule("commandPlayerClient"                   , "This command allows you to save /player... commands and execute them"                        , true);
        addRule("commandRegion"                         , "This command allows you to determine the region you are in or the region at set coords"      , true);
        addRule("commandTravel"                         , "This command allows you to travel to a set location"                                         , true);
        addRule("removeWarnReceivedPassengers"          , "This removes the 'Received passengers for unkown entity' warning on the client"              , false);
        addRule("stackableShulkersInPlayerInventories"  , "This allows for shulkers to stack only in your inventory"                                    , false);
        addRule("stackableShulkersWithItems"            , "This allows for shulkers with items to stack only in your inventory"                         , false);
    }

    private static Path getFile() {
        FileHelper.checkIfEssentialClientDirExists();
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialBooleanClientRules.json");
    }

    private static void addRule(String name, String description, boolean isCommand) {
        BooleanClientRule.clientBooleanRulesMap.putIfAbsent(name, new BooleanClientRule(name, description, isCommand));
    }

    public static boolean getBoolean(String rule) {
        return BooleanClientRule.clientBooleanRulesMap.get(rule) != null && BooleanClientRule.clientBooleanRulesMap.get(rule).value;
    }
}
