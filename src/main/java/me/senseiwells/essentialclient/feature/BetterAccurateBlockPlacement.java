package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.misc.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import static me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds.ACCURATE_INTO;
import static me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds.ACCURATE_REVERSE;

public class BetterAccurateBlockPlacement {
	public static Direction fakeDirection = null;
	public static int requestedTicks = 0;
	public static float fakeYaw = 0;
	public static float fakePitch = 0;
	public static Direction[] directions = new Direction[6];
	private static float previousFakeYaw = 0;
	private static float previousFakePitch = 0;
	public static boolean wasReversePressed = false;
	public static boolean wasIntoPressed = false;

	static {
		Events.ON_TICK_POST.register(BetterAccurateBlockPlacement::accurateBlockPlacementOnPress);
	}

	public static void load() { }

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
		else {
			fakeDirection = null;
		}
		if (ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			fakeYaw = playerEntity.getYaw();
			fakePitch = playerEntity.getPitch();
			Direction facing = Direction.getEntityFacingOrder(playerEntity)[0];
			if (ACCURATE_INTO.isPressed() && client.crosshairTarget instanceof BlockHitResult blockHitResult) {
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
			if (ACCURATE_REVERSE.isPressed()) {
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

	public static Direction[] getFacingOrder() {
		float f = fakePitch * 0.017453292F;
		float g = -fakeYaw * 0.017453292F;
		float yComponent = MathHelper.sin(f);
		float horizontalComponent = MathHelper.cos(f);
		float horizontalDecompose1 = MathHelper.sin(g);
		float horizontalDecompose2 = MathHelper.cos(g);
		boolean isPositiveX = horizontalDecompose1 > 0.0F;
		boolean isPositiveY = yComponent < 0.0F;
		boolean isPositiveZ = horizontalDecompose2 > 0.0F;
		float absXComponent = isPositiveX ? horizontalDecompose1 : -horizontalDecompose1;
		float absYCartesian = isPositiveY ? -yComponent : yComponent;
		float absZComponent = isPositiveZ ? horizontalDecompose2 : -horizontalDecompose2;
		float absXCartesian = absXComponent * horizontalComponent;
		float absZCartesian = absZComponent * horizontalComponent;
		Direction axisVector1 = isPositiveX ? Direction.EAST : Direction.WEST;
		Direction axisVector2 = isPositiveY ? Direction.UP : Direction.DOWN;
		Direction axisVector3 = isPositiveZ ? Direction.SOUTH : Direction.NORTH;
		if (absXComponent > absZComponent) {
			if (absYCartesian > absXCartesian) {
				return listClosest(axisVector2, axisVector1, axisVector3);
			}
			else {
				return absZCartesian > absYCartesian ? listClosest(axisVector1, axisVector3, axisVector2) : listClosest(axisVector1, axisVector2, axisVector3);
			}
		}
		else if (absYCartesian > absZCartesian) {
			return listClosest(axisVector2, axisVector3, axisVector1);
		}
		else {
			return absXCartesian > absYCartesian ? listClosest(axisVector3, axisVector1, axisVector2) : listClosest(axisVector3, axisVector2, axisVector1);
		}
	}

	private static Direction[] listClosest(Direction first, Direction second, Direction third) {
		return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
	}
}
