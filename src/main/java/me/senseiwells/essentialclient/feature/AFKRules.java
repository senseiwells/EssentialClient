package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.misc.Events;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AFKRules {
	private static Vec3d prevPlayerLocation;

	private static int ticks = 0;
	private static double prevMouseX;
	private static double prevMouseY;
	private static boolean wasAfk = false;

	static {
		Events.ON_DISCONNECT.register(client -> {
			wasAfk = false;
		});
		Events.ON_TICK_POST.register(client -> {
			ClientPlayerEntity playerEntity = client.player;
			int announceAfk = ClientRules.ANNOUNCE_AFK.getValue();
			int logout = ClientRules.AFK_LOGOUT.getValue();
			if (playerEntity == null || (announceAfk < 1 && logout < 200)) {
				return;
			}
			Vec3d playerLocation = playerEntity.getPos();
			double mouseX = client.mouse.getX();
			double mouseY = client.mouse.getX();
			if (playerLocation.equals(prevPlayerLocation) && mouseX == prevMouseX && mouseY == prevMouseY) {
				ticks++;
				if (ticks == announceAfk) {
					EssentialUtils.sendChatMessage(ClientRules.ANNOUNCE_AFK_MESSAGE.getValue());
					wasAfk = true;
				}
				if (logout >= 200 && ticks == logout) {
					playerEntity.networkHandler.onDisconnected(Texts.AFK);
				}
				return;
			}
			prevPlayerLocation = playerLocation;
			prevMouseX = mouseX;
			prevMouseY = mouseY;
			ticks = 0;
			if (wasAfk) {
				String message = ClientRules.ANNOUNCE_BACK_MESSAGE.getValue();
				if (!message.isBlank()) {
					EssentialUtils.sendChatMessage(message);
				}
				wasAfk = false;
			}
		});
	}

	public static void load() { }
}
