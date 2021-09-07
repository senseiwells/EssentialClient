package essentialclient.feature;

import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AnnounceAFK {

    private static int ticks = 0;
    private static Vec3d prevLocation;

    public static void tickAFK(ClientPlayerEntity playerEntity) {
        if (playerEntity == null)
            return;
        Vec3d location = playerEntity.getPos();
        if (prevLocation == location) {
            ticks ++;
            if (ticks == ClientRules.ANNOUNCE_AFK.getInt()) {
                playerEntity.sendChatMessage(ClientRules.ANNOUNCE_AFK_MESSAGE.getString());
            }
        }
        else {
            prevLocation = location;
            ticks = 0;

        }
    }
}
