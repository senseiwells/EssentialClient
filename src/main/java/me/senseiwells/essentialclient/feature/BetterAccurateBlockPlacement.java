package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class BetterAccurateBlockPlacement {
	public static final BetterAccurateBlockPlacement INSTANCE = new BetterAccurateBlockPlacement();

	public static Direction fakeDirection = null;
	public static int requestedTicks = 0;
	public static float fakeYaw = 0;
	public static float fakePitch = 0;
	private static float previousFakeYaw = 0;
	private static float previousFakePitch = 0;
	public static boolean wasReversePressed = false;
	public static boolean wasIntoPressed = false;

	public void load() {
		ClientTickEvents.END_CLIENT_TICK.register(BetterAccurateBlockPlacement::accurateBlockPlacementOnPress);
	}

	private static void accurateBlockPlacementOnPress(MinecraftClient client) {
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		ClientPlayerEntity playerEntity = client.player;
		if (playerEntity == null || networkHandler == null) {
			return;
		}
		if (requestedTicks > 0) {
			if (fakeYaw != previousFakeYaw || fakePitch != previousFakePitch) {
				sendLookPacket(networkHandler, playerEntity);
				previousFakeYaw = fakeYaw;
				previousFakePitch = fakePitch;
			}
			requestedTicks--;
			return;
		}
		if (ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			fakeYaw = playerEntity.getYaw();
			fakePitch = playerEntity.getPitch();
			Direction facing = Direction.getEntityFacingOrder(playerEntity)[0];
			KeyBinding reverseKeyBinding = ClientKeybinds.ACCURATE_REVERSE.getKeyBinding();
			KeyBinding intoKeyBinding = ClientKeybinds.ACCURATE_INTO.getKeyBinding();
			if (intoKeyBinding.isPressed() && client.crosshairTarget instanceof BlockHitResult blockHitResult) {
				fakeYaw = 0;
				fakePitch = 0;
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
					sendLookPacket(networkHandler, playerEntity);
					wasIntoPressed = true;
				}
			}
			else if (wasIntoPressed) {
				sendLookPacket(networkHandler, playerEntity);
				wasIntoPressed = false;
			}
			if (reverseKeyBinding.isPressed()) {
				switch (facing) {
					case NORTH, SOUTH, EAST, WEST -> fakeYaw = fakeYaw < 0 ? fakeYaw + 180 : fakeYaw - 180;
					case UP, DOWN -> fakePitch = fakePitch < 0 ? fakePitch + 180 : fakePitch - 180;
				}
				if (!wasReversePressed) {
					sendLookPacket(networkHandler, playerEntity);
					wasReversePressed = true;
				}
				facing = facing.getOpposite();
			}
			else if (wasReversePressed) {
				sendLookPacket(networkHandler, playerEntity);
				wasReversePressed = false;
			}
			previousFakeYaw = fakeYaw;
			previousFakePitch = fakePitch;
			// This is for the client, so it doesn't look jank
			fakeDirection = facing;
		}
	}

	public static void sendLookPacket(ClientPlayNetworkHandler networkHandler, ClientPlayerEntity playerEntity) {
		networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
			fakeYaw,
			fakePitch,
			playerEntity.isOnGround()
		));
	}
}
