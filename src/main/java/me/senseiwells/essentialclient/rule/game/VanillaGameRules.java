package me.senseiwells.essentialclient.rule.game;

import net.minecraft.world.GameRules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VanillaGameRules {
	private static final Map<String, GameRule<?>> GAME_RULES = new HashMap<>();

	@SuppressWarnings("unused")
	public static final BooleanGameRule
		DO_FIRE_TICK = registerBoolean("doFireTick", "Whether fire should spread and naturally extinguish", true, GameRules.DO_FIRE_TICK),
		MOB_GRIEFING = registerBoolean("mobGriefing", "Whether creepers, zombies, endermen, ghasts, withers, ender dragons, rabbits, sheep, villagers, silverfish, snow golems, and end crystals should be able to change blocks, and whether mobs can pick up items. When mobGriefing is disabled, piglins will not pick up gold ingots, but a player can still barter with them by using the item on the mob. Similarly, villagers will not pick up food items but can still breed until they run out of any food already in their inventory. This also affects the capability of zombie-like creatures like zombified piglins and drowned to pathfind to turtle eggs", true, GameRules.DO_MOB_GRIEFING),
		KEEP_INVENTORY = registerBoolean("keepInventory", "Whether the player should keep items and experience in their inventory after death", false, GameRules.KEEP_INVENTORY),
		DO_MOB_SPAWNING = registerBoolean("doMobSpawning", "Whether mobs should naturally spawn. Does not affect monster spawners", true, GameRules.DO_MOB_SPAWNING),
		DO_MOB_LOOT = registerBoolean("doMobLoot", "Whether mobs should drop items and experience orbs", true, GameRules.DO_MOB_LOOT),
		DO_TILE_DROPS = registerBoolean("doTileDrops", "Whether blocks should have drops", true, GameRules.DO_TILE_DROPS),
		DO_ENTITY_DROPS = registerBoolean("doEntityDrops", "Whether entities that are not mobs should have drops", true, GameRules.DO_ENTITY_DROPS),
		COMMAND_BLOCK_OUTPUT = registerBoolean("commandBlockOutput", "Whether command blocks should notify admins when they perform commands", true, GameRules.COMMAND_BLOCK_OUTPUT),
		NATURAL_REGENERATION = registerBoolean("naturalRegeneration", "Whether the player can regenerate health naturally if their hunger is full enough (doesn't affect external healing, such as golden apples, the Regeneration effect, etc.)", true, GameRules.NATURAL_REGENERATION),
		DO_DAYLIGHT_CYCLE = registerBoolean("doDaylightCycle", "Whether the daylight cycle and moon phases progress", true, GameRules.DO_DAYLIGHT_CYCLE),
		LOG_ADMIN_COMMANDS = registerBoolean("logAdminCommands", "Whether to log admin commands to server log", true, GameRules.LOG_ADMIN_COMMANDS),
		SHOW_DEATH_MESSAGES = registerBoolean("showDeathMessages", "Whether death messages are put into chat when a player dies. Also affects whether a message is sent to the pet's owner when the pet dies.", true, GameRules.SHOW_DEATH_MESSAGES),
		SEND_COMMAND_FEEDBACK = registerBoolean("sendCommandFeedback", "Whether the feedback from commands executed by a player should show up in chat. Also affects the default behavior of whether command blocks store their output text", true, GameRules.SEND_COMMAND_FEEDBACK),
		REDUCED_DEBUG_INFO = registerBoolean("reducedDebugInfo", "Whether the debug screen shows all or reduced information; and whether the effects of F3 + B (entity hitboxes) and F3 + G (chunk boundaries) are shown", false, GameRules.REDUCED_DEBUG_INFO),
		SPECTATORS_GENERATE_CHUNKS = registerBoolean("spectatorsGenerateChunks", "Whether players in spectator mode can generate chunks", true, GameRules.SPECTATORS_GENERATE_CHUNKS),
		DISABLE_ELYTRA_MOVEMENT_CHECK = registerBoolean("disableElytraMovementCheck", "Whether the server should skip checking player speed when the player is wearing elytra. Often helps with jittering due to lag in multiplayer", false, GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK),
		DO_WEATHER_CYCLE = registerBoolean("doWeatherCycle", "Whether the weather can change naturally. The /weather command can still change weather", true, GameRules.DO_WEATHER_CYCLE),
		DO_LIMITED_CRAFTING = registerBoolean("doLimitedCrafting", "Whether players should be able to craft only those recipes that they've unlocked first", false, GameRules.DO_LIMITED_CRAFTING),
		ANNOUNCE_ADVANCEMENTS = registerBoolean("announceAdvancements", "Whether advancements should be announced in chat", true, GameRules.ANNOUNCE_ADVANCEMENTS),
		DISABLE_RAIDS = registerBoolean("disableRaids", "Whether raids are disabled", false, GameRules.DISABLE_RAIDS),
		DO_INSOMNIA = registerBoolean("doInsomnia", "Whether phantoms can spawn in the nighttime", true, GameRules.DO_INSOMNIA),
		DO_IMMEDIATE_RESPAWN = registerBoolean("doImmediateRespawn", "Players respawn immediately without showing the death screen", false, GameRules.DO_IMMEDIATE_RESPAWN),
		DROWNING_DAMAGE = registerBoolean("drowningDamage", "Whether the player should take damage when drowning", true, GameRules.DROWNING_DAMAGE),
		FALL_DAMAGE = registerBoolean("fallDamage", "Whether the player should take fall damage", true, GameRules.FALL_DAMAGE),
		FIRE_DAMAGE = registerBoolean("fireDamage", "Whether the player should take damage in fire, lava, campfires, or on magma blocks", true, GameRules.FIRE_DAMAGE),
		FREEZE_DAMAGE = registerBoolean("freezeDamage", "Whether the player should take damage when inside powder snow", true, GameRules.FREEZE_DAMAGE),
		DO_PATROL_SPAWNING = registerBoolean("doPatrolSpawning", "Whether patrols can spawn", true, GameRules.DO_PATROL_SPAWNING),
		DO_TRADER_SPAWNING = registerBoolean("doTraderSpawning", "Whether wandering traders can spawn", true, GameRules.DO_TRADER_SPAWNING),
		FORGIVE_DEAD_PLAYERS = registerBoolean("forgiveDeadPlayers", "Makes angered neutral mobs stop being angry when the targeted player dies nearby", true, GameRules.FORGIVE_DEAD_PLAYERS),
		UNIVERSAL_ANGER = registerBoolean("universalAnger", "Makes angered neutral mobs attack any nearby player, not just the player that angered them. Works best if forgiveDeadPlayers is disabled", false, GameRules.UNIVERSAL_ANGER);
	//#if MC >= 11900
	public static final BooleanGameRule DO_WARDEN_SPAWNING = registerBoolean("doWardenSpawning", "Whether wardens can spawn, this rule is only available for 1.19+", true, GameRules.DO_WARDEN_SPAWNING);
	//#else
	//$$public static final BooleanGameRule DO_WARDEN_SPAWNING = registerBoolean("doWardenSpawning", "Whether wardens can spawn, this rule is only available for 1.19+", true, null);
	//#endif

	@SuppressWarnings("unused")
	public static final IntegerGameRule
		RANDOM_TICK_SPEED = registerInteger("randomTickSpeed", "How often a random block tick occurs (such as plant growth, leaf decay, etc.) per chunk section per game tick. 0 and negative values disables random ticks, higher numbers increase random ticks. Setting to a high integer results in high speeds of decay and growth. Numbers over 4096 make plant growth or leaf decay instantaneous", 3, GameRules.RANDOM_TICK_SPEED),
		SPAWN_RADIUS = registerInteger("spawnRadius", "The number of blocks outward from the world spawn coordinates that a player spawns in when first joining a server or when dying without a personal spawnpoint. Has no effect on servers where the default game mode is adventure", 10, GameRules.SPAWN_RADIUS),
		MAX_ENTITY_CRAMMING = registerInteger("maxEntityCramming", "The maximum number of pushable entities a mob or player can push, before taking 3♥ suffocation damage per half-second. Setting to 0 or lower disables the rule. Damage affects survival-mode or adventure-mode players, and all mobs but bats. Pushable entities include non-spectator-mode players, any mob except bats, as well as boats and minecarts", 24, GameRules.MAX_ENTITY_CRAMMING),
		MAX_COMMAND_CHAIN_LENGTH = registerInteger("maxCommandChainLength", "The maximum length of a chain of commands that can be executed during one tick. Applies to command blocks and functions", 65536, GameRules.MAX_COMMAND_CHAIN_LENGTH),
		PLAYERS_SLEEPING_PERCENTAGE = registerInteger("playersSleepingPercentage", "What percentage of players must sleep to skip the night", 100, GameRules.PLAYERS_SLEEPING_PERCENTAGE);

	private static BooleanGameRule registerBoolean(String name, String description, boolean defaultValue, GameRules.Key<?> ruleKey) {
		return register(new BooleanGameRule(name, description, defaultValue, ruleKey));
	}

	private static IntegerGameRule registerInteger(String name, String description, int defaultValue, GameRules.Key<?> ruleKey) {
		return register(new IntegerGameRule(name, description, defaultValue, ruleKey));
	}

	private static <T extends GameRule<?>> T register(T rule) {
		GAME_RULES.put(rule.getName(), rule);
		return rule;
	}

	public static GameRule<?> ruleFromString(String name) {
		return GAME_RULES.get(name);
	}

	public static Collection<GameRule<?>> getGameRules() {
		return GAME_RULES.values();
	}
}
