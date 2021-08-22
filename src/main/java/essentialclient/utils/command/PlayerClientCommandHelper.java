package essentialclient.utils.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import essentialclient.EssentialClient;
import essentialclient.utils.file.FileHelper;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerClientCommandHelper {

    public final String name;
    public final Double x;
    public final Double y;
    public final Double z;
    public final Double yaw;
    public final Double pitch;
    public final String dimension;

    public static Map<String, PlayerClientCommandHelper> playerClientHelperMap = new HashMap<>();

    public static final MapCodec<PlayerClientCommandHelper> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            Codec.DOUBLE.fieldOf("x").forGetter(d -> d.x),
            Codec.DOUBLE.fieldOf("y").forGetter(d -> d.y),
            Codec.DOUBLE.fieldOf("z").forGetter(d -> d.z),
            Codec.DOUBLE.fieldOf("yaw").forGetter(d -> d.yaw),
            Codec.DOUBLE.fieldOf("pitch").forGetter(d -> d.pitch),
            Codec.STRING.fieldOf("dimension").forGetter(s -> s.dimension)
    ).apply(it, PlayerClientCommandHelper::new));

    public static final Codec<Map<String, PlayerClientCommandHelper>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC.codec());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public PlayerClientCommandHelper(String name, Double x, Double y, Double z, Double yaw, Double pitch, String dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public static void writeSaveFile(Map<String, PlayerClientCommandHelper> playerClientHelperMap) {
        Path file = getFile();
        try(BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, playerClientHelperMap)
                    .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write /playerclient data: {}", e))
                    .ifPresent(obj -> GSON.toJson(obj, writer));
        }
        catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save /playerclient data");
        }
    }

    public static Map<String, PlayerClientCommandHelper> readSaveFile() {
        Path file = getFile();
        if (!Files.isRegularFile(file))
            return new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                    .getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read /playerclient data: {}", e))
                    .getFirst());
        }
        catch (JsonParseException | IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private static Path getFile() {
        FileHelper.checkIfEssentialClientDirExists();
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialClientPlayer.json");
    }

    public static void sendCommand(ClientPlayerEntity playerEntity, PlayerClientCommandHelper data) {
        playerEntity.sendChatMessage(String.format("/player %s spawn at %f %f %f facing %f %f in %s", data.name, data.x, data.y, data.z, data.yaw, data.pitch, data.dimension));
    }

    public static int createNewPlayerClient(CommandContext<FabricClientCommandSource> context) {
        String name;
        String dimension;
        dimension = context.getArgument("dimension", String.class);
        switch (dimension) {
            case "minecraft:overworld": case "minecraft:the_nether": case "minecraft:the_end":
                break;
                default:
                    context.getSource().sendFeedback(new LiteralText("§cThat is not a valid dimension"));
                    return 0;
        }
        name = context.getArgument("playername", String.class);
        PlayerClientCommandHelper.playerClientHelperMap.put(name, new PlayerClientCommandHelper(name, context.getArgument("x", Double.class), context.getArgument("y", Double.class), context.getArgument("z", Double.class), context.getArgument("yaw", Double.class), context.getArgument("pitch", Double.class), dimension));
        PlayerClientCommandHelper.writeSaveFile(PlayerClientCommandHelper.playerClientHelperMap);
        context.getSource().sendFeedback(new LiteralText("§6Player has been added to config"));
        return 0;
    }

    public static CompletableFuture<Suggestions> suggestPlayerClient(SuggestionsBuilder builder) {
        if (playerClientHelperMap.isEmpty())
            return CommandSource.suggestMatching(new String[]{}, builder);
        return CommandSource.suggestMatching(playerClientHelperMap.keySet(), builder);
    }
}
