package essentialclient;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.ClientKeybinds;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class EssentialClient implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("EssentialClient");

	public static LocalDateTime startTime = LocalDateTime.now();

	@Override
	public void onInitialize() {
		ClientRules.init();
		ClientKeybinds.loadKeybinds();
	}
}
