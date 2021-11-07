package essentialclient.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import essentialclient.config.rulescreen.ClientRulesScreen;
import net.minecraft.client.gui.screen.Screen;

public class EssentialClientScreenFactory implements ConfigScreenFactory<ClientRulesScreen> {
    @Override
    public ClientRulesScreen create(Screen parent){
        return new ClientRulesScreen(parent);
    }
}
