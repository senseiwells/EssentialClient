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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClientRuleHelper {

    public static final MapCodec<ClientRule> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            Codec.STRING.fieldOf("value").forGetter(b -> b.value)
    ).apply(it, (name, value) -> {
        ClientRule data = ClientRule.clientRulesMap.remove(name);
        if (data != null) {
            data.value = value;
            ClientRule.clientRulesMap.put(name, data);
        }
        return new ClientRule();
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
        ClientRules.checkRules();
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

    private static Path getFile() {
        FileHelper.checkIfEssentialClientDirExists();
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("EssentialClientRules.json");
    }

    public static void executeOnChange(MinecraftClient client, ClientRule settings) {
        ClientPlayerEntity playerEntity = client.player;
        if (playerEntity != null) {
            playerEntity.unlockRecipes(client.world.getRecipeManager().values());
            //Everything to do with ClientPlayerEntity in here
        }
        if (settings.isCommand /*&& client.getNetworkHandler() != null*/) {
            client.inGameHud.getChatHud().addMessage(new LiteralText("§cRelog for client command changes to take full effect"));
            /* The game still suggests the command but it registers it as invalid, idk how to fix :P
             * MinecraftClient.getInstance().getNetworkHandler().onCommandTree(new CommandTreeS2CPacket((RootCommandNode<CommandSource>) (Object) ClientCommandManager.DISPATCHER.getRoot()));
             * MinecraftClient.getInstance().getNetworkHandler().onCommandSuggestions(new CommandSuggestionsS2CPacket());
             */
        }

    }
}
