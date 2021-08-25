package essentialclient;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import essentialclient.clientrulecode.HighlightLavaSources;
import essentialclient.gui.clientrule.ClientRuleHelper;
import essentialclient.utils.carpet.CarpetSettingsClientNetworkHandler;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import essentialclient.utils.carpet.Reference;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.command.PlayerListCommandHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EssentialClient implements CarpetExtension, ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("EssentialClient");

    @Override
    public void onInitialize() {
        PlayerClientCommandHelper.readSaveFile();
        PlayerListCommandHelper.readSaveFile();
        ClientRuleHelper.readSaveFile();
        HighlightLavaSources.init();
    }

    public static void noop() {
    }

    static {
        CarpetServer.manageExtension(new EssentialClient());
    }

    @Override
    public String version() {
        return "essential-client";
    }

    @Override
    public void onGameStarted() {
        // let's /carpet handle our few simple settings
        CarpetServer.settingsManager.addRuleObserver((source, parsedRule, s) -> {
            try {
                CarpetSettingsServerNetworkHandler.updateCarpetClientRules(parsedRule.name, parsedRule.getAsString(), source.getPlayer());
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        // reloading of /carpet settings is handled by carpet
        // reloading of own settings is handled as an extension, since we claim own settings manager
        Reference.isCarpetClientPresent = true;
        CarpetSettingsClientNetworkHandler.attachServer(server);
    }

    @Override
    public void onTick(MinecraftServer server) {
        // no need to add this.
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayerEntity player) {
        CarpetSettingsServerNetworkHandler.sendGUIInfo(player);
    }

    @Override
    public void onPlayerLoggedOut(ServerPlayerEntity player) {

    }
}
