package essentialclient.gui.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import net.minecraft.client.gui.screen.Screen;

public class EssentialClientScreenFactory implements ConfigScreenFactory<ClientRulesScreen> {
    @Override
    public ClientRulesScreen create(Screen parent){
        return new ClientRulesScreen(parent);
    }
}
