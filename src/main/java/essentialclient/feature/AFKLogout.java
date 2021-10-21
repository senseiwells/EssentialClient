package essentialclient.feature;

import essentialclient.feature.clientrule.ClientRules;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.mixins.core.MinecraftClientMixin;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Shadow;

public class AFKLogout {

    private static int ticks = 0;
    private static Vec3d prevLocation;


    public static void tickAFK(ClientPlayerEntity playerEntity) {
        if (playerEntity == null)
            return;
        Vec3d location = playerEntity.getPos();
        if (prevLocation == location) {
            ticks ++;
            if (ticks == ClientRules.AFK_LOGOUT.getInt()) {
                playerEntity.clientWorld.disconnect();
            }
        }
        else {
            prevLocation = location;
            ticks = 0;

        }
    }
}
