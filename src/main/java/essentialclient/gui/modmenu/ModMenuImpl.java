package essentialclient.gui.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import essentialclient.gui.rulescreen.RulesScreen;

public class ModMenuImpl implements ModMenuApi {
	@Override
	public ConfigScreenFactory<RulesScreen> getModConfigScreenFactory() {
		return (screen) -> new RulesScreen(screen, false);
	}
}
