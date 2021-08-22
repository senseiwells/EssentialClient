package essentialclient.clientrules;

import essentialclient.gui.clientruleformat.NumberClientRule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AnnounceAFK {

    private static int ticks = 0;
    private static int maxTicks = NumberClientRule.clientNumberRulesMap.get("announceAFK").value;
    private static Vec3d prevLocation;

    public static void tickAFK(ClientPlayerEntity playerEntity) {
        Vec3d location = playerEntity.getPos();
        if (prevLocation == location) {
            ticks ++;
            if (ticks == maxTicks) {
                playerEntity.sendChatMessage("I am now AFK");
            }
        }
        else {
            prevLocation = location;
            ticks = 0;
            maxTicks = NumberClientRule.clientNumberRulesMap.get("announceAFK").value;
        }
    }
}
