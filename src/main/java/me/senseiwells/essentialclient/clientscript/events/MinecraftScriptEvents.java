package me.senseiwells.essentialclient.clientscript.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class MinecraftScriptEvents {
	private static final Map<String, MinecraftScriptEvent> EVENT_MAP = new HashMap<>();

	public static final MinecraftScriptEvent
		ON_CLIENT_TICK = register("onClientTick", "The event is fired on every client tick"),
		ON_HEALTH_UPDATE = register("onHealthUpdate", "This event is fired when the player's health changes", NUMBER, "health", "the new health"),
		ON_BLOCK_UPDATE = register("onBlockUpdate", "This event is fired when a block update is recieved on the client", BLOCK, "block", "the block that was updated"),
		ON_TOTEM = register("onTotem", "This event is fired when the player uses a totem"),
		ON_CLOSE_SCREEN = register("onCloseScreen", "This event is fired when the player closes a screen", SCREEN, "screen", "the screen that was just closed"),
		ON_CONNECT = register("onConnect", "The event is fired when the player connects to a server", PLAYER, "player", "the player entity", WORLD, "world", "the world the player joined"),
		ON_DISCONNECT = register("onDisconnect", "This event is fired when the player disconnects from a server"),
		ON_OPEN_SCREEN = register("onOpenScreen", "This event is fired when the player opens a screen", SCREEN, "screen", "the screen that was just opened"),
		ON_PICK_UP_ITEM = register("onPickUpItem", "This event is fired when the player picks up an item", ITEM_STACK, "itemStack", "the item"),
		ON_ANVIL = register("onAnvil", "This event is fired when the player anvils an item", ITEM_STACK, "first", "the first input", ITEM_STACK, "second", "the second input", ITEM_STACK, "result", "the result of the first and second input", STRING, "newName", "the new name of the item stack", NUMBER, "levelCost", "the amount of xp required"),
		ON_FISH_BITE = register("onFishBite", "This event is fired when a fish bites the player's fishing rod", ENTITY, "entity", "the fishing bobber entity"),
		ON_DEATH = register("onDeath", "This event is fired when the player dies", ENTITY, "entity", "the entity that killed the player, may be null", TEXT, "message", "the death message"),
		ON_RESPAWN = register("onRespawn", "This event is fired when the player respawns", PLAYER, "player", "the respawned player entity"),
		ON_EAT = register("onEat", "This event is fired when the player eats something", ITEM_STACK, "food", "the item stack that was eaten"),
		ON_GAMEMODE_CHANGE = register("onGamemodeChange", "This event is fired when the player changes their gamemode", STRING, "gamemode", "the new gamemode"),
		ON_BLOCK_BROKEN = register("onBlockBroken", "This event is fired when the player breaks a new block", BLOCK, "block", "the block that was broken"),
		ON_BLOCK_PLACED = register("onBlockPlaced", "This event is fired when the player places a block", BLOCK, "block", "the block that was placed"),
		ON_DIMENSION_CHANGE = register("onDimensionChange", "This event is fired when the player changes their dimension", WORLD, "world", "the new world"),
		ON_PLAYER_JOIN = register("onPlayerJoin", "This event is fired when a player joins the server", STRING, "name", "the player's name"),
		ON_PLAYER_LEAVE = register("onPlayerLeave", "This event is fired when a player leaves the server", STRING, "name", "the player's name"),
		ON_ENTITY_SPAWN = register("onEntitySpawn", "This event is fired when an entity spawns, this doesn't include mobs", ENTITY, "entity", "the entity that was spawned"),
		ON_MOB_SPAWN = register("onMobSpawn", "This event is fired when a mob spawns", LIVING_ENTITY, "mob", "the mob that was spawned"),
		ON_ENTITY_REMOVED = register("onEntityRemoved", "This event is fired when an entity is removed", ENTITY, "entity", "the entity that was removed"),
		ON_SCRIPT_PACKET = register("onScriptPacket", "This event is fired when a script packet is received from scarpet on the server", LIST, "packet", "a list of data that was received"),
		ON_PLAYER_LOOK = registerCancellable("onPlayerLook", "This event is fired when the player changes their yaw and/or pitch", NUMBER, "yaw", "the player's yaw", NUMBER, "pitch", "the player's pitch"),
		ON_PICK_BLOCK = registerCancellable("onPickBlock", "This event is fired when the player picks a block with middle mouse", ITEM_STACK, "itemStack", "the item stack that was picked"),
		ON_ATTACK = registerCancellable("onAttack", "This event is fired when the player attacks"),
		ON_USE = registerCancellable("onUse", "This event is fired when the player uses"),
		ON_MOUSE_SCROLL = registerCancellable("onMouseScroll", "This event is fired when the player scrolls", NUMBER, "direction", "either -1 or 1 depending on the scroll direction"),
		ON_KEY_PRESS = registerCancellable("onKeyPress", "This event is fired when a key is pressed", STRING, "key", "the key that was pressed"),
		ON_KEY_RELEASE = registerCancellable("onKeyRelease", "This event is fired when a key is released", STRING, "key", "the key that was released"),
		ON_DROP_ITEM = registerCancellable("onDropItem", "This event is fired when the player tries to drop an item", ITEM_STACK, "itemStack", "the item that is trying to be dropped"),
		ON_INTERACT_ITEM = registerCancellable("onInteractItem", "This event is fired when the player interacts with an item", ITEM_STACK, "itemStack", "the item stack that was interacted with"),
		ON_INTERACT_BLOCK = registerCancellable("onInteractBlock", "This event is fired when the player interacts with a block", BLOCK, "block", "the block the player is interacting with", ITEM_STACK, "itemStack", "the item stack that was interacted with"),
		ON_INTERACT_ENTITY = registerCancellable("onInteractEntity", "This event is fired when the player interacts with an entity", ENTITY, "entity", "the entity that was interacted with", ITEM_STACK, "itemStack", "the item stack that was interacted with"),
		ON_SEND_MESSAGE = registerCancellable("onSendMessage", "This event is fired when the player sends a message in chat", STRING, "message", "the message that was sent"),
		ON_RECEIVE_MESSAGE = registerCancellable("onReceiveMessage", "This event is fired when the player receives a message in chat", STRING, "uuid", "the sender's UUID", STRING, "message", "the message that was received"),
		ON_CLICK_SLOT = registerCancellable("onClickSlot", "This event is fired when the player clicks on a slot in their inventory", NUMBER, "slot", "the slot number that was clicked", STRING, "action", "ths action that was used"),
		ON_CLICK_RECIPE = registerCancellable("onClickRecipe", "This event is fired when the player clicks on a recipe in the recipe book", RECIPE, "recipe", "the recipe that was clicked"),
		ON_ATTACK_BLOCK = registerCancellable("onAttackBlock", "This event is fired when the player attacks a block", BLOCK, "block", "the block that was attacked"),
		ON_ATTACK_ENTITY = registerCancellable("onAttackEntity", "This event is fired when the player attacks an entity", ENTITY, "entity", "the entity that was attacked");

	public static final MinecraftScriptEvent.Unique
		ON_SCRIPT_END = registerUnique("onScriptEnd", "This event is fired when the script is ends");

	protected static void addEventToMap(String name, MinecraftScriptEvent minecraftScriptEvent) {
		EVENT_MAP.put(name, minecraftScriptEvent);
	}

	public static MinecraftScriptEvent getEvent(String name) {
		return EVENT_MAP.get(name);
	}

	public static void clearEventFunctions(UUID id) {
		forEachEvent(e -> e.clearRegisteredEvents(id));
	}

	public static void forEachEvent(Consumer<MinecraftScriptEvent> consumer) {
		EVENT_MAP.values().forEach(consumer);
	}

	private static MinecraftScriptEvent register(String name, String description, String... parameters) {
		return new MinecraftScriptEvent(name, description, parameters);
	}

	private static MinecraftScriptEvent registerCancellable(String name, String description, String... parameters) {
		return new MinecraftScriptEvent(name, description, parameters, true);
	}

	@SuppressWarnings("SameParameterValue")
	private static MinecraftScriptEvent.Unique registerUnique(String name, String description, String... parameters) {
		return new MinecraftScriptEvent.Unique(name, description, parameters);
	}
}
