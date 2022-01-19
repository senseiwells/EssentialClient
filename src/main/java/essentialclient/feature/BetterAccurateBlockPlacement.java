package essentialclient.feature;

import essentialclient.config.clientrule.ClientRules;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class BetterAccurateBlockPlacement {

	public static Direction fakeDirection = null;
	public static float fakeYaw = 0;
	public static float fakePitch = 0;
	public static float fakeRequestYaw = 0;
	public static float fakeRequestPitch = 0;
	public static boolean onRequest = false;
	public static int count = 0;
	private static boolean wasReversePressed = false;
	private static boolean wasIntoPressed = false;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(essentialclient.feature.BetterAccurateBlockPlacement::accurateBlockPlacementOnPress);
		ClientTickEvents.END_CLIENT_TICK.register(essentialclient.feature.BetterAccurateBlockPlacement::accurateBlockPlacementRequest);
	}
	private static void accurateBlockPlacementRequest(MinecraftClient client){
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		ClientPlayerEntity playerEntity = client.player;
		KeyBinding reverseKeyBinding = ClientKeybinds.ACCURATE_REVERSE.getKeyBinding();
		KeyBinding intoKeyBinding = ClientKeybinds.ACCURATE_INTO.getKeyBinding();
		if (playerEntity == null || networkHandler == null) {
			return;
		}
		if (onRequest && count > 0){
			networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(//1.17 then LookAndOnGround
				fakeRequestYaw,
				fakeRequestPitch,
				playerEntity.isOnGround()
			));
			count--;
		} else {
			onRequest = false;
			count = 0;
		}
		if (!onRequest && !reverseKeyBinding.isPressed() && !intoKeyBinding.isPressed()){
			fakeRequestYaw = playerEntity.yaw;
			fakeRequestPitch = playerEntity.pitch;
			fakeDirection = null;
		}
	}
	private static void accurateBlockPlacementOnPress(MinecraftClient client) {
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue() && networkHandler != null) {
			ClientPlayerEntity playerEntity = client.player;
			if (playerEntity == null) {
				return;
			}
			fakeYaw = playerEntity.yaw; //getYaw();
			fakePitch = playerEntity.pitch; //getPicth();
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
				fakeDirection = facing;
			}
			else if (wasReversePressed) {
				sendLookPacket(networkHandler, playerEntity);
				wasReversePressed = false;
			}
			// This is for the client, so it doesn't look jank
		}
	}

	private static void sendLookPacket(ClientPlayNetworkHandler networkHandler, ClientPlayerEntity playerEntity) {
		networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(//1.17 then LookAndOnGround
			fakeYaw,
			fakePitch,
			playerEntity.isOnGround()
		));
	}
}
