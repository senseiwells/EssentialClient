package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import static me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds.ACCURATE_INTO;
import static me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds.ACCURATE_REVERSE;

public class BetterAccurateBlockPlacement {
	public static Direction fakeDirection = null;
	public static int requestedTicks = 0;
	public static float fakeYaw = 0;
	public static float fakePitch = 0;
	private static float previousFakeYaw = 0;
	private static float previousFakePitch = 0;
	public static boolean wasReversePressed = false;
	public static boolean wasIntoPressed = false;

	static {
		ClientTickEvents.END_CLIENT_TICK.register(BetterAccurateBlockPlacement::accurateBlockPlacementOnPress);
	}

	public static void load() { }

	private static void accurateBlockPlacementOnPress(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			return;
		}
		if (requestedTicks > 0) {
			if (fakeYaw != previousFakeYaw || fakePitch != previousFakePitch) {
				sendLookPacket(player);
				previousFakeYaw = fakeYaw;
				previousFakePitch = fakePitch;
			}
			requestedTicks--;
			return;
		}
		fakeDirection = null;

		if (ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			fakeYaw = player.getYaw();
			fakePitch = player.getPitch();
			Direction facing = Direction.getEntityFacingOrder(player)[0];
			if (ACCURATE_INTO.isPressed() && client.crosshairTarget instanceof BlockHitResult blockHitResult) {
				fakeYaw = 0;
				fakePitch = 0;
				requestedTicks = 1;
				facing = blockHitResult.getSide();
				fakeDirection = facing;
				switch (facing) {
					case UP -> fakePitch = -90;
					case DOWN -> fakePitch = 90;
					case EAST -> fakeYaw = -90;
					case WEST -> fakeYaw = 90;
					case NORTH -> fakeYaw = 180;
					case SOUTH -> fakeYaw = 0;
				}
				if (!wasIntoPressed) {
					sendLookPacket(player);
					wasIntoPressed = true;
				}
			} else if (wasIntoPressed) {
				requestedTicks = 1;
				sendLookPacket(player);
				wasIntoPressed = false;
			}
			if (ACCURATE_REVERSE.isPressed()) {
				requestedTicks = 1;
				switch (facing) {
					case NORTH, SOUTH, EAST, WEST -> fakeYaw = fakeYaw < 0 ? fakeYaw + 180 : fakeYaw - 180;
					case UP, DOWN -> fakePitch = fakePitch < 0 ? fakePitch + 180 : fakePitch - 180;
				}
				if (!wasReversePressed) {
					sendLookPacket(player);
					wasReversePressed = true;
				}
				facing = facing.getOpposite();
			} else if (wasReversePressed) {
				requestedTicks = 1;
				sendLookPacket(player);
				wasReversePressed = false;
			}
			previousFakeYaw = fakeYaw;
			previousFakePitch = fakePitch;
			// This is for the client, so it doesn't look jank
			fakeDirection = facing;
		}
	}

	public static void sendLookPacket(ClientPlayerEntity player) {
		EssentialUtils.getNetworkHandler().sendPacket(
			new PlayerMoveC2SPacket.LookAndOnGround(fakeYaw, fakePitch, player.isOnGround())
		);
	}
}
