package essentialclient.feature;

import essentialclient.config.clientrule.ClientRules;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class BetterAccurateBlockPlacement {

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getBoolean() && client.getNetworkHandler() != null) {
				ClientPlayerEntity playerEntity = client.player;
				if (playerEntity == null) {
					return;
				}
				boolean reversePressed = ClientKeybinds.ACCURATE_REVERSE.getKeyBinding().isPressed();
				boolean intoPressed = ClientKeybinds.ACCURATE_INTO.getKeyBinding().isPressed();
				if (reversePressed || intoPressed) {
					float fakeYaw = playerEntity.yaw;
					float fakePitch = playerEntity.pitch;
					Direction facing = Direction.getEntityFacingOrder(playerEntity)[0];
					BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
					if (intoPressed && blockHitResult != null) {
						fakeYaw = 0;
						fakePitch = 0;
						facing = blockHitResult.getSide().getOpposite();
						switch (blockHitResult.getSide()) {
							case UP -> fakePitch = -90;
							case DOWN -> fakePitch = 90;
							case EAST -> fakeYaw = -90;
							case WEST -> fakeYaw = 90;
							case NORTH -> fakeYaw = 180;
							case SOUTH -> fakeYaw = 0;
						}
					}
					if (reversePressed) {
						switch (facing) {
							case NORTH, SOUTH, EAST, WEST -> fakeYaw = fakeYaw < 0 ? fakeYaw + 180 : fakeYaw - 180;
							case UP, DOWN -> fakePitch = fakePitch < 0 ? fakePitch + 180 : fakePitch - 180;
						}
					}
					client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookOnly(
						fakeYaw,
						fakePitch,
						playerEntity.isOnGround()
					));
				}
			}
		});
	}
}
