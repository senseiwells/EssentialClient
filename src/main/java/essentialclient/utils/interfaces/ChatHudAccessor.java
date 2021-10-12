package essentialclient.utils.interfaces;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.util.List;

public interface ChatHudAccessor {

    List<ChatHudLine<Text>> getMessages();

}
