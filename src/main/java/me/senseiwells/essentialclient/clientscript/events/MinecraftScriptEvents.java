package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.arucas.utils.Context;

import java.util.HashMap;
import java.util.Map;

public class MinecraftScriptEvents {
	private static final Map<String, MinecraftScriptEvent> EVENT_MAP = new HashMap<>();

	public static final MinecraftScriptEvent
		ON_CLIENT_TICK = new MinecraftScriptEvent("onClientTick"),
		ON_HEALTH_UPDATE = new MinecraftScriptEvent("onHealthUpdate"),
		ON_TOTEM = new MinecraftScriptEvent("onTotem"),
		ON_PICK_BLOCK = new MinecraftScriptEvent("onPickBlock"),
		ON_CLOSE_SCREEN = new MinecraftScriptEvent("onCloseScreen"),
		ON_CONNECT = new MinecraftScriptEvent("onConnect"),
		ON_DISCONNECT = new MinecraftScriptEvent("onDisconnect"),
		ON_OPEN_SCREEN = new MinecraftScriptEvent("onOpenScreen"),
		ON_PICK_UP_ITEM = new MinecraftScriptEvent("onPickUpItem"),
		ON_FISH_BITE = new MinecraftScriptEvent("onFishBite"),
		ON_DEATH = new MinecraftScriptEvent("onDeath"),
		ON_RESPAWN = new MinecraftScriptEvent("onRespawn"),
		ON_EAT = new MinecraftScriptEvent("onEat"),
		ON_GAMEMODE_CHANGE = new MinecraftScriptEvent("onGamemodeChange"),
		ON_BLOCK_BROKEN = new MinecraftScriptEvent("onBlockBroken"),
		ON_DIMENSION_CHANGE = new MinecraftScriptEvent("onDimensionChange"),
		ON_PLAYER_JOIN = new MinecraftScriptEvent("onPlayerJoin"),
		ON_PLAYER_LEAVE = new MinecraftScriptEvent("onPlayerLeave"),
		ON_ATTACK = new MinecraftScriptEvent("onAttack", true),
		ON_USE = new MinecraftScriptEvent("onUse", true),
		ON_KEY_PRESS = new MinecraftScriptEvent("onKeyPress", true),
		ON_KEY_RELEASE = new MinecraftScriptEvent("onKeyRelease", true),
		ON_DROP_ITEM = new MinecraftScriptEvent("onDropItem", true),
		ON_INTERACT_ITEM = new MinecraftScriptEvent("onInteractItem", true),
		ON_INTERACT_BLOCK = new MinecraftScriptEvent("onInteractBlock", true),
		ON_INTERACT_ENTITY = new MinecraftScriptEvent("onInteractEntity", true),
		ON_SEND_MESSAGE = new MinecraftScriptEvent("onSendMessage", true),
		ON_RECEIVE_MESSAGE = new MinecraftScriptEvent("onReceiveMessage", true),
		ON_CLICK_SLOT = new MinecraftScriptEvent("onClickSlot", true),
		ON_ATTACK_BLOCK = new MinecraftScriptEvent("onAttackBlock", true),
		ON_ATTACK_ENTITY = new MinecraftScriptEvent("onAttackEntity", true);

	protected static void addEventToMap(String name, MinecraftScriptEvent minecraftScriptEvent) {
		EVENT_MAP.put(name, minecraftScriptEvent);
	}

	public static MinecraftScriptEvent getEvent(String name) {
		return EVENT_MAP.get(name);
	}

	public static void clearEventFunctions(Context context) {
		EVENT_MAP.values().forEach(minecraftScriptEvent -> minecraftScriptEvent.clearRegisteredEvents(context));
	}
}
