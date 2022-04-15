package me.senseiwells.essentialclient.gui.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.senseiwells.essentialclient.gui.config.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
	@Override
	public ConfigScreenFactory<ConfigScreen> getModConfigScreenFactory() {
		return ConfigScreen::new;
	}
}
