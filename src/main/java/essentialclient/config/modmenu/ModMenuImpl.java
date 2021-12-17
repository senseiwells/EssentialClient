package essentialclient.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import essentialclient.config.rulescreen.ClientRulesScreen;

public class ModMenuImpl implements ModMenuApi{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (ConfigScreenFactory<ClientRulesScreen>) ClientRulesScreen::new;
	}
}
