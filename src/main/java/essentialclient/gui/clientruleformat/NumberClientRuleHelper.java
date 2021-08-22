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

public class NumberClientRuleHelper {

    public static final MapCodec<NumberClientRule> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            Codec.STRING.fieldOf("description").forGetter(s -> s.description),
            Codec.INT.fieldOf("defaultValue").forGetter(b -> b.defaultValue),
            Codec.INT.fieldOf("value").forGetter(b -> b.value),
            Codec.BOOL.fieldOf("isCommand").forGetter(b -> b.isCommand)
    ).apply(it, NumberClientRule::new));

    public static final Codec<Map<String, NumberClientRule>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC.codec());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeSaveFile(Map<String, NumberClientRule> numberRule) {
        Path file = getFile();
        try(BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, numberRule)
                    .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write rule data: {}", e))
                    .ifPresent(obj -> GSON.toJson(obj, writer));
        }
        catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save rule data");
        }
    }

    public static Map<String, NumberClientRule> readSaveFile() {
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

    public static void checkNumberRules() {
        addRule("announceAFK"                           , "This announces when you become afk after a set amount of time (ticks)"                               , false);
    }

    private static Path getFile() {
        FileHelper.checkIfEssentialClientDirExists();
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialNumberClientRules.json");
    }

    private static void addRule(String name, String description, boolean isCommand) {
        NumberClientRule.clientNumberRulesMap.putIfAbsent(name, new NumberClientRule(name, description, isCommand));
    }

    public static int getInt(String rule) {
        return NumberClientRule.clientNumberRulesMap.get(rule) == null ? -1 : NumberClientRule.clientNumberRulesMap.get(rule).value;
    }

}
