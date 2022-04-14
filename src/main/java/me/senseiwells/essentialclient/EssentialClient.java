package me.senseiwells.essentialclient;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.feature.ClientKeybinds;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.feature.MultiConnectSupport;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import me.senseiwells.essentialclient.utils.clientscript.MinecraftDeobfuscator;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerClient;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerList;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EssentialClient implements ModInitializer {
	public static final Logger LOGGER;
	public static final ChunkClientNetworkHandler CHUNK_NET_HANDLER;
	public static final LocalDateTime START_TIME;
	public static final Set<Config<?>> CONFIG_SET;
	public static final String VERSION;

	static {
		LOGGER = LogManager.getLogger("EssentialClient");
		CHUNK_NET_HANDLER = new ChunkClientNetworkHandler();
		START_TIME = LocalDateTime.now();
		VERSION = "1.1.7";
		CONFIG_SET = new HashSet<>();
		registerConfigs();
	}

	@Override
	public void onInitialize() {
		CONFIG_SET.forEach(Config::readConfig);
		ClientRules.load();
		ClientKeybinds.loadKeybinds();
		CraftingSharedConstants.registerHandlers();
		MultiConnectSupport.setupMultiConnectAPI();
		MinecraftDeobfuscator.init();
	}

	public static void registerConfigs() {
		CONFIG_SET.add(ConfigClientNick.INSTANCE);
		CONFIG_SET.add(ConfigPlayerClient.INSTANCE);
		CONFIG_SET.add(ConfigPlayerList.INSTANCE);
		CONFIG_SET.add(ClientRules.INSTANCE);
		CONFIG_SET.add(ClientScript.INSTANCE);
		CONFIG_SET.add(CarpetClient.INSTANCE);
	}

	public static void saveConfigs() {
		CONFIG_SET.forEach(Config::saveConfig);
	}
}
