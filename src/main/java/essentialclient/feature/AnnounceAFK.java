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
            if (ticks == ClientRules.ANNOUNCEAFK.getInt()) {
                playerEntity.sendChatMessage(ClientRules.ANNOUNCEAFKMESSAGE.getString());
            }
        }
        else {
            prevLocation = location;
            ticks = 0;

        }
    }
}
