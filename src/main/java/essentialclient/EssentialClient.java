package essentialclient;

import essentialclient.clientrule.ClientRules;
import essentialclient.clientscript.core.ClientScript;
import essentialclient.feature.CarpetClient;
import essentialclient.feature.ClientKeybinds;
import essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import essentialclient.utils.config.Config;
import essentialclient.utils.config.ConfigClientNick;
import essentialclient.utils.config.ConfigPlayerClient;
import essentialclient.utils.config.ConfigPlayerList;
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
	public static final Set<Config> CONFIG_SET;
	public static final String ARUCAS_VERSION;
	public static final String VERSION;

	static {
		LOGGER = LogManager.getLogger("EssentialClient");
		CHUNK_NET_HANDLER = new ChunkClientNetworkHandler();
		START_TIME = LocalDateTime.now();
		ARUCAS_VERSION = "1.1.3";
		VERSION = "1.1.5";
		CONFIG_SET = new HashSet<>();
		registerConfigs();
	}

	@Override
	public void onInitialize() {
		CONFIG_SET.forEach(Config::readConfig);
		ClientRules.load();
		ClientKeybinds.loadKeybinds();
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
