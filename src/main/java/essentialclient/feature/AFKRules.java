package essentialclient.feature;

import essentialclient.config.clientrule.ClientRules;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;

public class AFKRules {
	public static AFKRules INSTANCE = new AFKRules();

	private Vec3d prevPlayerLocation;

	private int ticks = 0;
	private double prevMouseX;
	private double prevMouseY;

	public void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClientPlayerEntity playerEntity = client.player;
			int announceAfk = ClientRules.ANNOUNCE_AFK.getValue();
			int logout = ClientRules.AFK_LOGOUT.getValue();
			if (playerEntity == null || (announceAfk < 1 && logout < 200)) {
				return;
			}
			Vec3d playerLocation = playerEntity.getPos();
			double mouseX = client.mouse.getX();
			double mouseY = client.mouse.getX();
			if (playerLocation.equals(this.prevPlayerLocation) && mouseX == this.prevMouseX && mouseY == this.prevMouseY) {
				this.ticks++;
				if (this.ticks == announceAfk) {
					playerEntity.sendChatMessage(ClientRules.ANNOUNCE_AFK_MESSAGE.getValue());
				}
				if (logout >= 200 && this.ticks == logout) {
					playerEntity.networkHandler.onDisconnected(new LiteralText("You've been lazy! (AFK Logout)"));
				}
				return;
			}
			this.prevPlayerLocation = playerLocation;
			this.prevMouseX = mouseX;
			this.prevMouseY = mouseY;
			this.ticks = 0;
		});
	}
}
