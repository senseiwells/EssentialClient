package essentialclient.feature;

import essentialclient.feature.clientrule.ClientRules;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AFKRules {
    public static AFKRules INSTANCE = new AFKRules();

    private Vec3d prevPlayerLocation;

    private int ticks = 0;
    private double prevMouseX;
    private double prevMouseY;

    public void registerAFKRules() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity playerEntity = client.player;
            if (playerEntity == null || (ClientRules.ANNOUNCE_AFK.getInt() < 1 && ClientRules.AFK_LOGOUT.getInt() < 200))
                return;
            Vec3d playerLocation = playerEntity.getPos();
            double mouseX = client.mouse.getX();
            double mouseY = client.mouse.getX();
            if (playerLocation == this.prevPlayerLocation && mouseX == this.prevMouseX && mouseY == this.prevMouseY) {
                this.ticks++;
                if (this.ticks == ClientRules.ANNOUNCE_AFK.getInt())
                    playerEntity.sendChatMessage(ClientRules.ANNOUNCE_AFK_MESSAGE.getString());
                int logout = ClientRules.AFK_LOGOUT.getInt();
                if (logout >= 200 && this.ticks == logout)
                    playerEntity.clientWorld.disconnect();
                return;
            }
            this.prevPlayerLocation = playerLocation;
            this.prevMouseX = mouseX;
            this.prevMouseY = mouseY;
            this.ticks = 0;
        });
    }
}
