package me.senseiwells.essentialclient;

import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.feature.GameRuleNetworkHandler;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientTickSyncer;
import me.senseiwells.essentialclient.utils.clientscript.MinecraftDeobfuscator;
import me.senseiwells.essentialclient.utils.clientscript.ScriptNetworkHandler;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerClient;
import me.senseiwells.essentialclient.utils.config.ConfigPlayerList;
import me.senseiwells.essentialclient.utils.misc.Events;
import me.senseiwells.essentialclient.utils.misc.Scheduler;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class EssentialClient implements ModInitializer {
	public static final ChunkClientNetworkHandler CHUNK_NET_HANDLER;
	public static final GameRuleNetworkHandler GAME_RULE_NET_HANDLER;
	public static final ScriptNetworkHandler SCRIPT_NET_HANDLER;

	public static final Logger LOGGER;
	public static final LocalDateTime START_TIME;
	public static final Set<NetworkHandler> NETWORK_HANDLERS;
	public static final Set<Config<?>> CONFIG_SET;
	public static final String VERSION;

	static {
		LOGGER = LogManager.getLogger("EssentialClient");
		CHUNK_NET_HANDLER = new ChunkClientNetworkHandler();
		GAME_RULE_NET_HANDLER = new GameRuleNetworkHandler();
		SCRIPT_NET_HANDLER = new ScriptNetworkHandler();
		START_TIME = LocalDateTime.now();
		VERSION = EssentialUtils.getEssentialVersion();
		NETWORK_HANDLERS = new LinkedHashSet<>();
		CONFIG_SET = new LinkedHashSet<>();
		registerConfigs();

		Events.ON_CLOSE.register(client -> EssentialClient.saveConfigs());
		Events.ON_DISCONNECT_POST.register(v -> EssentialClient.saveConfigs());

		NETWORK_HANDLERS.add(CHUNK_NET_HANDLER);
		NETWORK_HANDLERS.add(GAME_RULE_NET_HANDLER);
		NETWORK_HANDLERS.add(SCRIPT_NET_HANDLER);
	}

	@Override
	public void onInitialize() {
		// Run async for faster boot, saves ~2000ms on my machine
		new Thread(() -> {
			CONFIG_SET.forEach(Config::readConfig);
			ClientRules.load();
			ClientKeyBinds.load();
			MinecraftDeobfuscator.load();
			CraftingSharedConstants.load();
			ClientScriptUtils.load();
			Scheduler.load();
			ClientTickSyncer.load();
		}, "EssentialClient Init Thread").start();
	}

	public static void registerConfigs() {
		CONFIG_SET.add(ConfigClientNick.INSTANCE);
		CONFIG_SET.add(ConfigPlayerClient.INSTANCE);
		CONFIG_SET.add(ConfigPlayerList.INSTANCE);
		CONFIG_SET.add(ClientRules.INSTANCE);
		CONFIG_SET.add(ClientScript.INSTANCE);
		CONFIG_SET.add(CarpetClient.INSTANCE);
		CONFIG_SET.add(ClientKeyBinds.INSTANCE);
	}

	public static void saveConfigs() {
		CONFIG_SET.forEach(Config::saveConfig);
	}

	public static void onDisconnect() {
		NETWORK_HANDLERS.forEach(NetworkHandler::onDisconnect);
	}
}
