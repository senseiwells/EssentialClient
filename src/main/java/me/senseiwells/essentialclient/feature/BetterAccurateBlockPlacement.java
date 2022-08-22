package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.misc.Events;
import net.minecraft.client.MinecraftClient;
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
	private static float previousFakeYaw = 0;
	private static float previousFakePitch = 0;
	public static boolean wasReversePressed = false;
	public static boolean wasIntoPressed = false;

	static {
		Events.ON_TICK_POST.register(BetterAccurateBlockPlacement::accurateBlockPlacementOnPress);
	}

	public static void load() { }

	private static void accurateBlockPlacementOnPress(MinecraftClient client) {
		ClientPlayerEntity playerEntity = client.player;
		if (playerEntity == null) {
			return;
		}
		if (requestedTicks > 0) {
			if (fakeYaw != previousFakeYaw || fakePitch != previousFakePitch) {
				sendLookPacket(playerEntity);
				previousFakeYaw = fakeYaw;
				previousFakePitch = fakePitch;
			}
			requestedTicks--;
			return;
		}
		fakeDirection = null;

		if (ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			fakeYaw = playerEntity.getYaw();
			fakePitch = playerEntity.getPitch();
			Direction facing = Direction.getEntityFacingOrder(playerEntity)[0];
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
					sendLookPacket(playerEntity);
					wasIntoPressed = true;
				}
			} else if (wasIntoPressed) {
				requestedTicks = 1;
				sendLookPacket(playerEntity);
				wasIntoPressed = false;
			}
			if (ACCURATE_REVERSE.isPressed()) {
				requestedTicks = 1;
				switch (facing) {
					case NORTH, SOUTH, EAST, WEST -> fakeYaw = fakeYaw < 0 ? fakeYaw + 180 : fakeYaw - 180;
					case UP, DOWN -> fakePitch = fakePitch < 0 ? fakePitch + 180 : fakePitch - 180;
				}
				if (!wasReversePressed) {
					sendLookPacket(playerEntity);
					wasReversePressed = true;
				}
				facing = facing.getOpposite();
			} else if (wasReversePressed) {
				requestedTicks = 1;
				sendLookPacket(playerEntity);
				wasReversePressed = false;
			}
			previousFakeYaw = fakeYaw;
			previousFakePitch = fakePitch;
			// This is for the client, so it doesn't look jank
			fakeDirection = facing;
		}
	}

	public static void sendLookPacket(ClientPlayerEntity player) {
		EssentialUtils.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(fakeYaw, fakePitch, player.isOnGround()));
	}

	public static Direction[] getFacingOrder() {
		float theta = fakePitch * 0.017453292F;
		float omega = -fakeYaw * 0.017453292F;
		float unitHorizontal = MathHelper.cos(theta);
		float yVector = -MathHelper.sin(theta);
		float xVector = unitHorizontal * MathHelper.sin(omega);
		float zVector = unitHorizontal * MathHelper.cos(omega);
		float yScalar = Math.abs(yVector);
		float xScalar = Math.abs(xVector);
		float zScalar = Math.abs(zVector);
		Direction directionX = xVector > 0.0F ? Direction.EAST : Direction.WEST;
		Direction directionY = yVector > 0.0F ? Direction.UP : Direction.DOWN;
		Direction directionZ = zVector > 0.0F ? Direction.SOUTH : Direction.NORTH;
		if (xScalar > zScalar) {
			if (yScalar > xScalar) {
				return listClosest(directionY, directionX, directionZ);
			}
			return zScalar > yScalar ? listClosest(directionX, directionZ, directionY) : listClosest(directionX, directionY, directionZ);
		}
		if (yScalar > zScalar) {
			return listClosest(directionY, directionZ, directionX);
		}
		return xScalar > yScalar ? listClosest(directionZ, directionX, directionY) : listClosest(directionZ, directionY, directionX);
	}

	private static Direction[] listClosest(Direction first, Direction second, Direction third) {
		return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
	}
}
