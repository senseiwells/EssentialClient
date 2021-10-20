package essentialclient.utils.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import essentialclient.EssentialClient;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.render.ChatColour;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record PlayerClientCommandHelper(String name, Double x, Double y, Double z, Double yaw, Double pitch, String dimension, String gamemode) {

    public static Map<String, PlayerClientCommandHelper> playerClientHelperMap = new HashMap<>();

    public static final MapCodec<PlayerClientCommandHelper> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            Codec.DOUBLE.fieldOf("x").forGetter(d -> d.x),
            Codec.DOUBLE.fieldOf("y").forGetter(d -> d.y),
            Codec.DOUBLE.fieldOf("z").forGetter(d -> d.z),
            Codec.DOUBLE.fieldOf("yaw").forGetter(d -> d.yaw),
            Codec.DOUBLE.fieldOf("pitch").forGetter(d -> d.pitch),
            Codec.STRING.fieldOf("dimension").forGetter(s -> s.dimension),
            Codec.STRING.fieldOf("gamemode").forGetter(s -> s.gamemode)
    ).apply(it, PlayerClientCommandHelper::new));

    public static final Codec<Map<String, PlayerClientCommandHelper>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC.codec());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeSaveFile() {
        Path file = getFile();
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            MAP_CODEC.encodeStart(JsonOps.INSTANCE, PlayerClientCommandHelper.playerClientHelperMap)
                    .resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write /playerclient data: {}", e))
                    .ifPresent(obj -> GSON.toJson(obj, writer));
        } catch (IOException e) {
            e.printStackTrace();
            EssentialClient.LOGGER.error("Failed to save /playerclient data");
        }
    }

    public static void readSaveFile() {
        Path file = getFile();
        if (!Files.isRegularFile(file)) {
            PlayerClientCommandHelper.playerClientHelperMap = new HashMap<>();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            PlayerClientCommandHelper.playerClientHelperMap = new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                    .getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read /playerclient data: {}", e))
                    .getFirst());
            return;
        } catch (JsonParseException | IOException e) {
            e.printStackTrace();
        }
        PlayerClientCommandHelper.playerClientHelperMap = new HashMap<>();
    }

    private static Path getFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialClientPlayer.json");
    }

    public static void sendCommand(ClientPlayerEntity playerEntity, PlayerClientCommandHelper data, double x, double y, double z) {
        playerEntity.sendChatMessage(String.format(
                "/player %s spawn at %s %s %s facing %s %s in %s",
                data.name,
                formatDecimal(data.x + x),
                formatDecimal(data.y + y),
                formatDecimal(data.z + z),
                formatDecimal(data.yaw),
                formatDecimal(data.pitch),
                data.dimension)
        );
    }

    public static String formatDecimal(Double doubleValue) {
        return DecimalFormat.getInstance(Locale.UK).format(doubleValue);
    }

    public static int createNewPlayerClient(CommandContext<ServerCommandSource> context, boolean isHere, boolean isGamemode) throws CommandSyntaxException {
        double x;
        double y;
        double z;
        double yaw;
        double pitch;
        String dimension;
        String gamemode;
        if (!isHere) {
            dimension = context.getArgument("dimension", String.class);
            Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");
            x = pos.x;
            y = pos.y;
            z = pos.z;
            yaw = context.getArgument("yaw", Double.class);
            pitch = context.getArgument("pitch", Double.class);
        } else {
            ClientPlayerEntity clientPlayerEntity = CommandHelper.getPlayer();
            dimension = clientPlayerEntity.world.getRegistryKey().getValue().getPath();
            x = clientPlayerEntity.getX();
            y = clientPlayerEntity.getY();
            z = clientPlayerEntity.getZ();
            yaw = clientPlayerEntity.getYaw();
            pitch = clientPlayerEntity.getPitch();
        }
        switch (dimension) {
            case "overworld":
            case "the_nether":
            case "the_end":
                break;
            default:
                Text text = new LiteralText("That is not a valid dimension");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(text), text);
        }
        if (isGamemode) {
            gamemode = context.getArgument("gamemode", String.class);
            switch (gamemode) {
                case "spectator":
                case "survival":
                case "any":
                    break;
                default:
                    Text text = new LiteralText("That is not a valid gamemode");
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(text), text);
            }
        } else {
            gamemode = "any";
        }
        String name = context.getArgument("playername", String.class);
        PlayerClientCommandHelper.playerClientHelperMap.put(name, new PlayerClientCommandHelper(name, x, y, z, yaw, pitch, dimension, gamemode));
        PlayerClientCommandHelper.writeSaveFile();
        EssentialUtils.sendMessage(ChatColour.GOLD + "Player has been added to config");
        return 0;
    }


    public static int spawnPlayer(CommandContext<ServerCommandSource> context, boolean hasOffset) {
        if (playerClientHelperMap.isEmpty()) {
            EssentialUtils.sendMessage(ChatColour.RED + "You have no players in your config");
            return 0;
        }
        PlayerClientCommandHelper data = playerClientHelperMap.get(context.getArgument("playername", String.class));
        if (data == null) {
            EssentialUtils.sendMessage(ChatColour.RED + "That player doesn't exist in your config");
            return 0;
        }
        ClientPlayerEntity playerEntity = CommandHelper.getPlayer();
        if (data.gamemode.equalsIgnoreCase("spectator") && !playerEntity.isSpectator()) {
            EssentialUtils.sendMessage(ChatColour.RED + "You should be in spectator");
            return 0;
        }
        if (data.gamemode.equalsIgnoreCase("survival") && playerEntity.isSpectator()) {
            EssentialUtils.sendMessage(ChatColour.RED + "You should not be in spectator");
            return 0;
        }
        if (hasOffset)
            sendCommand(playerEntity, data, context.getArgument("x-axis", Double.class), context.getArgument("y-axis", Double.class), context.getArgument("z-axis", Double.class));
        else
            sendCommand(playerEntity, data, 0, 0, 0);
        return 0;
    }

    public static void spawnPlayer(ClientPlayerEntity playerEntity, String name) {
        PlayerClientCommandHelper data = playerClientHelperMap.get(name);
        if (data.gamemode.equalsIgnoreCase("spectator") && !playerEntity.isSpectator()) {
            playerEntity.sendMessage(new LiteralText("§cYou should be in spectator to spawn " + name), false);
            return;
        }
        if (data.gamemode.equalsIgnoreCase("survival") && playerEntity.isSpectator()) {
            playerEntity.sendMessage(new LiteralText("§cYou should not be in spectator to spawn" + name), false);
            return;
        }
        sendCommand(playerEntity, data, 0, 0, 0);
    }

    public static boolean isPlayerNull(String name) {
        return playerClientHelperMap.get(name) == null;
    }

    public static CompletableFuture<Suggestions> suggestPlayerClient(SuggestionsBuilder builder) {
        if (playerClientHelperMap.isEmpty())
            return CommandSource.suggestMatching(new String[]{}, builder);
        return CommandSource.suggestMatching(playerClientHelperMap.keySet(), builder);
    }
}
