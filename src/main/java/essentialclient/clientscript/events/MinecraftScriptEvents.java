package essentialclient.clientscript.events;

import java.util.HashMap;
import java.util.Map;

public class MinecraftScriptEvents {
	private static final Map<String, MinecraftScriptEvent> eventMap = new HashMap<>();

	public static final MinecraftScriptEvent ON_CLIENT_TICK = new MinecraftScriptEvent("onClientTick");

	public static final MinecraftScriptEvent ON_HEALTH_UPDATE = new MinecraftScriptEvent("onHealthUpdate");
	public static final MinecraftScriptEvent ON_TOTEM = new MinecraftScriptEvent("onTotem");
	public static final MinecraftScriptEvent ON_ATTACK = new MinecraftScriptEvent("onAttack");
	public static final MinecraftScriptEvent ON_USE = new MinecraftScriptEvent("onUse");
	public static final MinecraftScriptEvent ON_PICK_BLOCK = new MinecraftScriptEvent("onPickBlock");
	public static final MinecraftScriptEvent ON_CLOSE_SCREEN = new MinecraftScriptEvent("onCloseScreen");

	public static final MinecraftScriptEvent ON_KEY_PRESS = new MinecraftScriptEvent("onKeyPress");
	public static final MinecraftScriptEvent ON_KEY_RELEASE = new MinecraftScriptEvent("onKeyRelease");
	public static final MinecraftScriptEvent ON_COMMAND = new MinecraftScriptEvent("onCommand");
	public static final MinecraftScriptEvent ON_OPEN_SCREEN = new MinecraftScriptEvent("onOpenScreen");
	public static final MinecraftScriptEvent ON_PICK_UP_ITEM = new MinecraftScriptEvent("onPickUpItem");
	public static final MinecraftScriptEvent ON_DROP_ITEM = new MinecraftScriptEvent("onDropItem");
	public static final MinecraftScriptEvent ON_DEATH = new MinecraftScriptEvent("onDeath");
	public static final MinecraftScriptEvent ON_RESPAWN = new MinecraftScriptEvent("onRespawn");
	public static final MinecraftScriptEvent ON_EAT = new MinecraftScriptEvent("onHealthUpdate");
	public static final MinecraftScriptEvent ON_INTERACT_ITEM = new MinecraftScriptEvent("onInteractItem");
	public static final MinecraftScriptEvent ON_INTERACT_BLOCK = new MinecraftScriptEvent("onInteractBlock");
	public static final MinecraftScriptEvent ON_INTERACT_ENTITY = new MinecraftScriptEvent("onInteractEntity");
	public static final MinecraftScriptEvent ON_SEND_MESSAGE = new MinecraftScriptEvent("onSendMessage");
	public static final MinecraftScriptEvent ON_RECEIVE_MESSAGE = new MinecraftScriptEvent("onReceiveMessage");
	public static final MinecraftScriptEvent ON_GAMEMODE_CHANGE = new MinecraftScriptEvent("onGamemodeChange");
	public static final MinecraftScriptEvent ON_CLICK_SLOT = new MinecraftScriptEvent("onClickSlot");
	public static final MinecraftScriptEvent ON_BLOCK_BROKEN = new MinecraftScriptEvent("onBlockBroken");
	public static final MinecraftScriptEvent ON_DIMENSION_CHANGE = new MinecraftScriptEvent("onDimensionChange");
	public static final MinecraftScriptEvent ON_PLAYER_JOIN = new MinecraftScriptEvent("onPlayerJoin");
	public static final MinecraftScriptEvent ON_PLAYER_LEAVE = new MinecraftScriptEvent("onPlayerLeave");

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
