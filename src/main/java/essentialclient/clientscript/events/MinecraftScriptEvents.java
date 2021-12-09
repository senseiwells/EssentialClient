package essentialclient.clientscript.events;

import java.util.HashMap;
import java.util.Map;

public class MinecraftScriptEvents {
	private static final Map<String, MinecraftScriptEvent> eventMap = new HashMap<>();

	public static final MinecraftScriptEvent
		ON_CLIENT_TICK = new MinecraftScriptEvent("onClientTick"),

		ON_HEALTH_UPDATE = new MinecraftScriptEvent("onHealthUpdate"),
		ON_TOTEM = new MinecraftScriptEvent("onTotem"),
		ON_ATTACK = new MinecraftScriptEvent("onAttack"),
		ON_USE = new MinecraftScriptEvent("onUse"),
		ON_PICK_BLOCK = new MinecraftScriptEvent("onPickBlock"),
		ON_CLOSE_SCREEN = new MinecraftScriptEvent("onCloseScreen"),

		ON_KEY_PRESS = new MinecraftScriptEvent("onKeyPress"),
		ON_KEY_RELEASE = new MinecraftScriptEvent("onKeyRelease"),
		ON_COMMAND = new MinecraftScriptEvent("onCommand"),
		ON_OPEN_SCREEN = new MinecraftScriptEvent("onOpenScreen"),
		ON_PICK_UP_ITEM = new MinecraftScriptEvent("onPickUpItem"),
		ON_FISH_BITE = new MinecraftScriptEvent("onFishBite"),
		ON_DROP_ITEM = new MinecraftScriptEvent("onDropItem"),
		ON_DEATH = new MinecraftScriptEvent("onDeath"),
		ON_RESPAWN = new MinecraftScriptEvent("onRespawn"),
		ON_EAT = new MinecraftScriptEvent("onHealthUpdate"),
		ON_INTERACT_ITEM = new MinecraftScriptEvent("onInteractItem"),
		ON_INTERACT_BLOCK = new MinecraftScriptEvent("onInteractBlock"),
		ON_INTERACT_ENTITY = new MinecraftScriptEvent("onInteractEntity"),
		ON_SEND_MESSAGE = new MinecraftScriptEvent("onSendMessage"),
		ON_RECEIVE_MESSAGE = new MinecraftScriptEvent("onReceiveMessage"),
		ON_GAMEMODE_CHANGE = new MinecraftScriptEvent("onGamemodeChange"),
		ON_CLICK_SLOT = new MinecraftScriptEvent("onClickSlot"),
		ON_BLOCK_BROKEN = new MinecraftScriptEvent("onBlockBroken"),
		ON_DIMENSION_CHANGE = new MinecraftScriptEvent("onDimensionChange"),
		ON_PLAYER_JOIN = new MinecraftScriptEvent("onPlayerJoin"),
		ON_PLAYER_LEAVE = new MinecraftScriptEvent("onPlayerLeave");

	protected static void addEventToMap(String name, MinecraftScriptEvent minecraftScriptEvent) {
		eventMap.put(name, minecraftScriptEvent);
	}

	public static MinecraftScriptEvent getEvent(String name) {
		return eventMap.get(name);
	}

	public static void clearEventFunctions() {
		eventMap.values().forEach(MinecraftScriptEvent::clearFunctions);
	}
}
