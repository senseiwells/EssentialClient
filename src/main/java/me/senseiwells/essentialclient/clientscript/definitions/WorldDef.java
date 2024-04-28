package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasIterable;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.PosIterator;
import me.senseiwells.essentialclient.utils.clientscript.ThreadSafeUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.WORLD;
import static me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils.warnMainThread;

@ClassDoc(
	name = WORLD,
	desc = "This class represents worlds, and allows you to interact with things inside of them.",
	language = Language.Java
)
public class WorldDef extends CreatableDefinition<World> {
	public WorldDef(Interpreter interpreter) {
		super(WORLD, interpreter);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		World world = instance.asPrimitive(this);
		return "World{level=" + world + "dimension=" + world.getRegistryKey().getValue().getPath() + "}";
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getBlockAt", 3, this::getBlockAt),
			MemberFunction.of("getBlockAt", 1, this::getBlockAtPos),
			MemberFunction.of("isLoaded", 1, this::isLoaded),
			MemberFunction.of("getBiomeAt", 3, this::getBiomeAt),
			MemberFunction.of("getBiomeAt", 1, this::getBiomeAtPos),
			MemberFunction.of("getPlayer", 1, this::getPlayer),
			MemberFunction.of("getAllPlayers", this::getAllPlayers),
			MemberFunction.of("getOtherPlayer", 1, this::getOtherPlayer, "Use '<World>.getPlayer(<String>)' instead"),
			MemberFunction.of("getAllOtherPlayers", this::getAllOtherPlayers, "Use '<World>.getAllPlayers()' instead"),
			MemberFunction.of("getClosestPlayer", 2, this::getClosestPlayer),
			MemberFunction.of("getAllEntities", this::getAllEntities),
			MemberFunction.of("getEntityFromId", 1, this::getEntityFromId),
			MemberFunction.of("getFullId", this::getFullId),
			MemberFunction.of("getId", this::getId),
			MemberFunction.of("getDimensionName", this::getDimensionName),
			MemberFunction.of("isRaining", this::isRaining),
			MemberFunction.of("isThundering", this::isThundering),
			MemberFunction.of("getTimeOfDay", this::getTimeOfDay),
			MemberFunction.of("renderParticle", 4, this::renderParticle),
			MemberFunction.of("renderParticle", 2, this::renderParticlePos),
			MemberFunction.of("renderParticle", 5, this::renderParticleVel),
			MemberFunction.of("setGhostBlock", 4, this::setGhostBlock, "This function is dangerous, be careful!"),
			MemberFunction.of("setGhostBlock", 2, this::setGhostBlockPos, "This function is dangerous, be careful!"),
			MemberFunction.of("isAir", 3, this::isAir),
			MemberFunction.of("isAir", 1, this::isAirPos),
			MemberFunction.of("getEmittedRedstonePower", 4, this::getEmittedRedstonePower),
			MemberFunction.of("getEmittedRedstonePower", 2, this::getEmittedRedstonePowerPos),
			MemberFunction.of("getLight", 3, this::getLight),
			MemberFunction.of("getLight", 1, this::getLightPos),
			MemberFunction.of("getSkyLight", 1, this::getSkyLight),
			MemberFunction.of("getBlockLight", 1, this::getBlockLight),
			MemberFunction.of("getArea", 2, this::getArea, "This function is memory intensive, use '<World>'.getPositions(pos1, pos2)"),
			MemberFunction.of("getAreaOfBlocks", 2, this::getAreaOfBlocks, "This function is memory intensive, use '<World>.getBlocks(pos1, pos2)'"),
			MemberFunction.of("getPositions", 2, this::getPositions),
			MemberFunction.of("getBlocks", 2, this::getBlocks),
			MemberFunction.of("getPositionsFromCentre", 4, this::getPositionsFromCentre),
			MemberFunction.of("getBlocksFromCentre", 4, this::getBlocksFromCentre),
			MemberFunction.of("reloadChunks", this::reloadChunks)
		);
	}

	@FunctionDoc(
		name = "getBlockAt",
		desc = "This function gets the block at the given coordinates",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z coordinate")
		},
		returns = @ReturnDoc(type = BlockDef.class, desc = "the block at the given coordinates"),
		examples = "world.getBlockAt(0, 100, 0);"
	)
	private ScriptBlockState getBlockAt(Arguments arguments) {
		warnMainThread("getBlockAt", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos blockPos = new BlockPos(x, y, z);
		return new ScriptBlockState(world.getBlockState(blockPos), blockPos);
	}

	@FunctionDoc(
		name = "getBlockAt",
		desc = "This function gets the block at the given coordinates",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position")},
		returns = @ReturnDoc(type = BlockDef.class, desc = "the block at the given coordinates"),
		examples = "world.getBlockAt(new Pos(0, 100, 0));"
	)
	private ScriptBlockState getBlockAtPos(Arguments arguments) {
		warnMainThread("getBlockAt", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		BlockPos blockPos = pos.getBlockPos();
		return new ScriptBlockState(world.getBlockState(blockPos), blockPos);
	}

	@FunctionDoc(
		name = "isLoaded",
		desc = "This function returns loaded state of given coordinates(client side)",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether the block is loaded at the given coordinates"),
		examples = "world.isLoaded(new Pos(0, 100, 0));"
	)
	private boolean isLoaded(Arguments arguments) {
		warnMainThread("isLoaded", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		BlockPos blockPos = pos.getBlockPos();
		return world.getChunkManager().isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	@FunctionDoc(
		name = "getBiomeAt",
		desc = "This function gets the biome at the given coordinates",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z coordinate")
		},
		returns = @ReturnDoc(type = BiomeDef.class, desc = "the biome at the given coordinates"),
		examples = "world.getBiomeAt(0, 100, 0);"
	)
	private Biome getBiomeAt(Arguments arguments) {
		warnMainThread("getBiomeAt", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos blockPos = new BlockPos(x, y, z);
		return world.getBiome(blockPos).value();
	}

	@FunctionDoc(
		name = "getBiomeAt",
		desc = "This function gets the biome at the given coordinates",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position")},
		returns = @ReturnDoc(type = BiomeDef.class, desc = "the biome at the given coordinates"),
		examples = "world.getBiomeAt(new Pos(0, 100, 0));"
	)
	private Biome getBiomeAtPos(Arguments arguments) {
		warnMainThread("getBiomeAt", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		BlockPos blockPos = pos.getBlockPos();
		return world.getBiome(blockPos).value();
	}

	@FunctionDoc(
		name = "getPlayer",
		desc = "This function gets the player with the given name",
		params = {@ParameterDoc(type = StringDef.class, name = "name", desc = "the name of the player")},
		returns = @ReturnDoc(type = PlayerDef.class, desc = "the player with the given name"),
		examples = "world.getPlayer('player');"
	)
	private PlayerEntity getPlayer(Arguments arguments) {
		warnMainThread("getPlayer", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		String playerName = arguments.nextPrimitive(StringDef.class);
		PlayerListEntry playerInfo = EssentialUtils.getNetworkHandler().getPlayerListEntry(playerName);
		if (playerInfo != null) {
			return ThreadSafeUtils.getPlayerByUuid(world, playerInfo.getProfile().getId());
		}
		return null;
	}

	@FunctionDoc(
		name = "getAllPlayers",
		desc = "This function gets all players in the world that are loaded",
		returns = @ReturnDoc(type = ListDef.class, desc = "all players in the world"),
		examples = "world.getAllPlayers();"
	)
	private ArucasList getAllPlayers(Arguments arguments) {
		warnMainThread("getAllPlayers", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ArucasList players = new ArucasList();
		for (PlayerEntity playerEntity : ThreadSafeUtils.getPlayersSafe(world)) {
			players.add(arguments.getInterpreter().convertValue(playerEntity));
		}
		return players;
	}

	@FunctionDoc(
		deprecated = "Use '<World>.getPlayer(name)' instead",
		name = "getOtherPlayer",
		desc = "This gets another player from the given username",
		params = {@ParameterDoc(type = StringDef.class, name = "username", desc = "the username of the other player")},
		returns = @ReturnDoc(type = PlayerDef.class, desc = "the other player, null if not found"),
		examples = "world.getOtherPlayer('senseiwells');"
	)
	private PlayerEntity getOtherPlayer(Arguments arguments) {
		warnMainThread("getOtherPlayer", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		String playerName = arguments.nextPrimitive(StringDef.class);
		PlayerListEntry playerInfo = EssentialUtils.getNetworkHandler().getPlayerListEntry(playerName);
		if (playerInfo != null) {
			PlayerEntity player = ThreadSafeUtils.getPlayerByUuid(world, playerInfo.getProfile().getId());
			if (player instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
				return otherClientPlayerEntity;
			}
		}
		return null;
	}

	@FunctionDoc(
		deprecated = "Use '<World>.getAllPlayers()' instead",
		name = "getAllOtherPlayers",
		desc = "This will get all other players in the world",
		returns = @ReturnDoc(type = ListDef.class, desc = "a list of all other players"),
		examples = "world.getAllOtherPlayers();"
	)
	private ArucasList getAllOtherPlayers(Arguments arguments) {
		warnMainThread("getAllOtherPlayers", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ArucasList otherPlayerValueList = new ArucasList();
		for (PlayerEntity playerEntity : ThreadSafeUtils.getPlayersSafe(world)) {
			if (playerEntity instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
				otherPlayerValueList.add(arguments.getInterpreter().convertValue(otherClientPlayerEntity));
			}
		}
		return otherPlayerValueList;
	}

	@FunctionDoc(
		name = "getClosestPlayer",
		desc = "This will get the closest player to another entity in the world",
		params = {
			@ParameterDoc(type = EntityDef.class, name = "entity", desc = "the entity to get the closest player to"),
			@ParameterDoc(type = NumberDef.class, name = "maxDistance", desc = "the maximum distance to search for a player in blocks")
		},
		returns = @ReturnDoc(type = PlayerDef.class, desc = "the closest player, null if not found"),
		examples = "world.getClosestPlayer(Player.get(), 100);"
	)
	private PlayerEntity getClosestPlayer(Arguments arguments) {
		warnMainThread("getClosestPlayer", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		Entity entity = arguments.nextPrimitive(EntityDef.class);
		double distance = arguments.nextPrimitive(NumberDef.class);
		return ThreadSafeUtils.getClosestPlayer(world, entity, distance);
	}

	@FunctionDoc(
		name = "getAllEntities",
		desc = "This will get all entities in the world",
		returns = @ReturnDoc(type = ListDef.class, desc = "a list of all entities"),
		examples = "world.getAllEntities();"
	)
	private ArucasList getAllEntities(Arguments arguments) {
		warnMainThread("getAllEntities", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ArucasList valueList = new ArucasList();
		if (world instanceof ClientWorld clientWorld) {
			for (Entity entity : ThreadSafeUtils.getEntitiesSafe(clientWorld)) {
				valueList.add(arguments.getInterpreter().convertValue(entity));
			}
		}
		return valueList;
	}

	@FunctionDoc(
		name = "getEntityFromId",
		desc = "This will get an entity from the given entity id",
		params = {@ParameterDoc(type = NumberDef.class, name = "entityId", desc = "the entity id")},
		returns = @ReturnDoc(type = EntityDef.class, desc = "the entity, null if not found"),
		examples = "world.getEntityFromId(1);"
	)
	private Object getEntityFromId(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		int id = arguments.nextPrimitive(NumberDef.class).intValue();
		return world.getEntityById(id);
	}

	@FunctionDoc(
		name = "getFullId",
		desc = "This will get the full id of the world",
		returns = @ReturnDoc(type = StringDef.class, desc = "the full id of the world, for example: 'minecraft:overworld'"),
		examples = "world.getFullId();"
	)
	private String getFullId(Arguments arguments) {
		return arguments.nextPrimitive(this).getRegistryKey().getValue().toString();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This will get the id of the world",
		returns = @ReturnDoc(type = StringDef.class, desc = "the id of the world, for example: 'overworld'"),
		examples = "world.getId();"
	)
	private String getId(Arguments arguments) {
		return arguments.nextPrimitive(this).getRegistryKey().getValue().getPath();
	}

	@FunctionDoc(
		deprecated = "You should use 'world.getId()' instead",
		name = "getDimensionName",
		desc = "This will get the id of the world",
		returns = @ReturnDoc(type = StringDef.class, desc = "the id of the world, for example: 'overworld'"),
		examples = "world.getDimensionName();"
	)
	private String getDimensionName(Arguments arguments) {
		return arguments.nextPrimitive(this).getRegistryKey().getValue().getPath();
	}

	@FunctionDoc(
		name = "isRaining",
		desc = "This will check if the world is currently raining",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the world is currently raining"),
		examples = "world.isRaining();"
	)
	private boolean isRaining(Arguments arguments) {
		return arguments.nextPrimitive(this).isRaining();
	}

	@FunctionDoc(
		name = "isThundering",
		desc = "This will check if the world is currently thundering",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the world is currently thundering"),
		examples = "world.isThundering();"
	)
	private boolean isThundering(Arguments arguments) {
		return arguments.nextPrimitive(this).isThundering();
	}

	@FunctionDoc(
		name = "getTimeOfDay",
		desc = {
			"This will get the time of day of the world",
			"info on the time of day [here](https://minecraft.fandom.com/wiki/Daylight_cycle)"
		},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the time of day of the world, between 0 and 24000"),
		examples = "world.getTimeOfDay();"
	)
	private long getTimeOfDay(Arguments arguments) {
		return arguments.nextPrimitive(this).getTimeOfDay();
	}

	@FunctionDoc(
		name = "renderParticle",
		desc = {
			"This will render a particle in the world, you can find a list of all",
			"the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),",
			"if the id is invalid it will throw an error"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "particleId", desc = "the id of the particle"),
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x position of the particle"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y position of the particle"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z position of the particle")
		},
		examples = "world.renderParticle('end_rod', 10, 10, 10);"
	)
	private Void renderParticle(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		String particleName = arguments.nextPrimitive(StringDef.class);
		ParticleType<?> particleType = Registries.PARTICLE_TYPE.get(ClientScriptUtils.stringToIdentifier(particleName));
		if (!(particleType instanceof SimpleParticleType defaultParticleType)) {
			throw new RuntimeError("Particle Invalid");
		}
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		ClientScriptUtils.ensureMainThread("renderParticle", arguments.getInterpreter(), () -> {
			world.addParticle(defaultParticleType, x, y, z, 0, 0, 0);
		});
		return null;
	}

	@FunctionDoc(
		name = "renderParticle",
		desc = {
			"This will render a particle in the world, you can find a list of all",
			"the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),",
			"this will throw an error if the id is invalid"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "particleId", desc = "the id of the particle"),
			@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the particle")
		},
		examples = "world.renderParticle('end_rod', pos);"
	)
	private Void renderParticlePos(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		String particleName = arguments.nextPrimitive(StringDef.class);
		ParticleType<?> particleType = Registries.PARTICLE_TYPE.get(ClientScriptUtils.stringToIdentifier(particleName));
		if (!(particleType instanceof SimpleParticleType defaultParticleType)) {
			throw new RuntimeError("Particle Invalid");
		}
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		ClientScriptUtils.ensureMainThread("renderParticle", arguments.getInterpreter(), () -> {
			world.addParticle(defaultParticleType, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
		});
		return null;
	}

	@FunctionDoc(
		name = "renderParticle",
		desc = {
			"This will render a particle in the world with a velocity, you can find a list of all",
			"the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),",
			"this will throw an error if the id is invalid"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "particleId", desc = "the id of the particle"),
			@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the particle"),
			@ParameterDoc(type = NumberDef.class, name = "velX", desc = "the velocity of the particle on the x axis"),
			@ParameterDoc(type = NumberDef.class, name = "velY", desc = "the velocity of the particle on the y axis"),
			@ParameterDoc(type = NumberDef.class, name = "velZ", desc = "the velocity of the particle on the z axis")
		},
		examples = "world.renderParticle('end_rod', pos, 0.5, 0.5, 0.5);"
	)
	private Void renderParticleVel(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		String particleName = arguments.nextPrimitive(StringDef.class);
		ParticleType<?> particleType = Registries.PARTICLE_TYPE.get(ClientScriptUtils.stringToIdentifier(particleName));
		if (!(particleType instanceof SimpleParticleType defaultParticleType)) {
			throw new RuntimeError("Particle Invalid");
		}
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		double velX = arguments.nextPrimitive(NumberDef.class);
		double velY = arguments.nextPrimitive(NumberDef.class);
		double velZ = arguments.nextPrimitive(NumberDef.class);
		ClientScriptUtils.ensureMainThread("renderParticle", arguments.getInterpreter(), () -> {
			world.addParticle(defaultParticleType, pos.getX(), pos.getY(), pos.getZ(), velX, velY, velZ);
		});
		return null;
	}

	@FunctionDoc(
		deprecated = "This function is dangerous, use at your own risk",
		name = "setGhostBlock",
		desc = "This sets a ghost block in the world as if it were a real block, may cause issues",
		params = {
			@ParameterDoc(type = BlockDef.class, name = "block", desc = "the block to set"),
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z position of the block")
		},
		examples = "world.setGhostBlock(Material.BEDROCK.asBlock(), 0, 100, 0);"
	)
	private Void setGhostBlock(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		ScriptBlockState blockState = arguments.nextPrimitive(BlockDef.class);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos blockPos = new BlockPos(x, y, z);
		ClientScriptUtils.ensureMainThread("setGhostBlock", arguments.getInterpreter(), () -> {
			world.setBlockState(blockPos, blockState.state);
		});
		return null;
	}

	@FunctionDoc(
		deprecated = "This function is dangerous, use at your own risk",
		name = "setGhostBlock",
		desc = "This sets a ghost block in the world as if it were a real block, may cause issues",
		params = {
			@ParameterDoc(type = BlockDef.class, name = "block", desc = "the block to set"),
			@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the block")
		},
		examples = "world.setGhostBlock(Material.BEDROCK.asBlock(), new Pos(0, 100, 0));"
	)
	private Void setGhostBlockPos(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		ScriptBlockState blockState = arguments.nextPrimitive(BlockDef.class);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		ClientScriptUtils.ensureMainThread("setGhostBlock", arguments.getInterpreter(), () -> {
			world.setBlockState(pos.getBlockPos(), blockState.state);
		});
		return null;
	}

	@FunctionDoc(
		name = "isAir",
		desc = "Returns true if the block at the given position is air",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z position of the block")
		},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the block is air"),
		examples = "world.isAir(0, 100, 0);"
	)
	private boolean isAir(Arguments arguments) {
		warnMainThread("isAir", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		return world.isAir(new BlockPos(x, y, z));
	}

	@FunctionDoc(
		name = "isAir",
		desc = "Returns true if the block at the given position is air",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the block")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the block is air"),
		examples = "world.isAir(new Pos(0, 100, 0));"
	)
	private boolean isAirPos(Arguments arguments) {
		warnMainThread("isAir", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return world.isAir(pos.getBlockPos());
	}

	@FunctionDoc(
		name = "getEmittedRedstonePower",
		desc = "Gets the emitted restone power at the given position and direction",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z position of the block"),
			@ParameterDoc(type = StringDef.class, name = "direction", desc = "the direction to check, for example 'north', 'east', 'up', etc.")
		},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the emitted redstone power"),
		examples = "world.getEmittedRedstonePower(0, 100, 0, 'north');"
	)
	private int getEmittedRedstonePower(Arguments arguments) {
		warnMainThread("getEmittedRedstonePower", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		String stringDirection = arguments.nextConstant();
		BlockPos blockPos = new BlockPos(x, y, z);
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		return world.getEmittedRedstonePower(blockPos, direction);
	}

	@FunctionDoc(
		name = "getEmittedRedstonePower",
		desc = "Gets the emitted restone power at the given position and direction",
		params = {
			@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the block"),
			@ParameterDoc(type = StringDef.class, name = "direction", desc = "the direction to check, for example 'north', 'east', 'up', etc.")
		},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the emitted redstone power"),
		examples = "world.getEmittedRedstonePower(new Pos(0, 100, 0), 'north');"
	)
	private int getEmittedRedstonePowerPos(Arguments arguments) {
		warnMainThread("getEmittedRedstonePower", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		String stringDirection = arguments.nextConstant();
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		return world.getEmittedRedstonePower(pos.getBlockPos(), direction);
	}

	@FunctionDoc(
		name = "getLight",
		desc = "Gets the light level at the given position, takes the max of either sky light of block light",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y position of the block"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z position of the block")
		},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the light level between 0 - 15"),
		examples = "world.getLight(0, 100, 0);"
	)
	private int getLight(Arguments arguments) {
		warnMainThread("getLight", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		return world.getLightLevel(new BlockPos(x, y, z));
	}

	@FunctionDoc(
		name = "getLight",
		desc = "Gets the light level at the given position, takes the max of either sky light of block light",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the block")},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the light level between 0 - 15"),
		examples = "world.getLight(new Pos(0, 100, 0));"
	)
	private int getLightPos(Arguments arguments) {
		warnMainThread("getLight", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return world.getLightLevel(pos.getBlockPos());
	}

	@FunctionDoc(
		name = "getSkyLight",
		desc = "Gets the sky light at the given position ignoring block light",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the block")},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the light level between 0 - 15"),
		examples = "world.getSkyLight(new Pos(0, 0, 0));"
	)
	private int getSkyLight(Arguments arguments) {
		warnMainThread("getSkyLight", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return world.getLightLevel(LightType.SKY, pos.getBlockPos());
	}

	@FunctionDoc(
		name = "getBlockLight",
		desc = "Gets the block light at the given position ignoring sky light",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position of the block")},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the light level between 0 - 15"),
		examples = "world.getBlockLight(new Pos(0, 0, 0));"
	)
	private int getBlockLight(Arguments arguments) {
		warnMainThread("getBlockLight", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return world.getLightLevel(LightType.BLOCK, pos.getBlockPos());
	}

	@FunctionDoc(
		deprecated = "This function is memory intensive, you should use `<World>.getPositions(pos1, pos2)`",
		name = "getArea",
		desc = "This gets a list of all block positions between the two positions",
		params = {
			@ParameterDoc(type = PosDef.class, name = "pos1", desc = "the first position"),
			@ParameterDoc(type = PosDef.class, name = "pos2", desc = "the second position")
		},
		returns = @ReturnDoc(type = ListDef.class, desc = "the list of positions"),
		examples = "world.getArea(new Pos(0, 100, 0), new Pos(0, 100, 0));"
	)
	private ArucasList getArea(Arguments arguments) {
		warnMainThread("getArea", arguments.getInterpreter());
		BlockPos posA = arguments.skip().nextPrimitive(PosDef.class).getBlockPos();
		BlockPos posB = arguments.nextPrimitive(PosDef.class).getBlockPos();
		ArucasList list = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		for (BlockPos pos : BlockPos.iterate(posA, posB)) {
			list.add(interpreter.create(PosDef.class, new ScriptPos(pos)));
		}
		return list;
	}

	@FunctionDoc(
		deprecated = "This function is memory intensive, you should use `<World>.getBlocks(pos1, pos2)`",
		name = "getAreaOfBlocks",
		desc = "This gets a list of all blocks (with positions) between the two positions",
		params = {
			@ParameterDoc(type = PosDef.class, name = "pos1", desc = "the first position"),
			@ParameterDoc(type = PosDef.class, name = "pos2", desc = "the second position")
		},
		returns = @ReturnDoc(type = ListDef.class, desc = "the list of blocks"),
		examples = "world.getAreaOfBlocks(new Pos(0, 100, 0), new Pos(0, 100, 0));"
	)
	private Object getAreaOfBlocks(Arguments arguments) {
		warnMainThread("getAreaOfBlocks", arguments.getInterpreter());
		World world = arguments.nextPrimitive(this);
		BlockPos posA = arguments.nextPrimitive(PosDef.class).getBlockPos();
		BlockPos posB = arguments.nextPrimitive(PosDef.class).getBlockPos();

		ArucasList list = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		for (BlockPos pos : BlockPos.iterate(posA, posB)) {
			BlockState state = world.getBlockState(pos);
			list.add(interpreter.create(BlockDef.class, new ScriptBlockState(state, pos)));
		}
		return list;
	}

	@FunctionDoc(
		name = "getPositions",
		desc = "This gets an iterator for all positions between two positions",
		params = {
			@ParameterDoc(type = PosDef.class, name = "pos1", desc = "the first position"),
			@ParameterDoc(type = PosDef.class, name = "pos2", desc = "the second position")
		},
		returns = @ReturnDoc(type = IterableDef.class, desc = "the iterator for the positions"),
		examples = "foreach (pos : world.getPositions(new Pos(0, 100, 100), new Pos(0, 100, 0)));"
	)
	private ArucasIterable getPositions(Arguments arguments) {
		BlockPos posA = arguments.skip().nextPrimitive(PosDef.class).getBlockPos();
		BlockPos posB = arguments.nextPrimitive(PosDef.class).getBlockPos();
		Iterable<BlockPos> posIterable = BlockPos.iterate(posA, posB);
		return () -> new PosIterator(posIterable.iterator(), o -> arguments.getInterpreter().convertValue(o));
	}

	@FunctionDoc(
		name = "getBlocks",
		desc = "This gets an iterator for all blocks (and positions) between two positions",
		params = {
			@ParameterDoc(type = PosDef.class, name = "pos1", desc = "the first position"),
			@ParameterDoc(type = PosDef.class, name = "pos2", desc = "the second position")
		},
		returns = @ReturnDoc(type = IterableDef.class, desc = "the iterator for the blocks"),
		examples = "foreach (block : world.getBlocks(new Pos(0, 100, 100), new Pos(0, 100, 0)));"
	)
	private ArucasIterable getBlocks(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		BlockPos posA = arguments.nextPrimitive(PosDef.class).getBlockPos();
		BlockPos posB = arguments.nextPrimitive(PosDef.class).getBlockPos();
		Iterable<BlockPos> posIterable = BlockPos.iterate(posA, posB);
		return () -> new PosIterator.Block(world, posIterable.iterator(), o -> arguments.getInterpreter().convertValue(o));
	}

	@FunctionDoc(
		name = "getPositionsFromCentre",
		desc = {
			"This gets an iterator for all positions between two positions.",
			"The iterator iterates from the centre outwards"
		},
		params = {
			@ParameterDoc(type = PosDef.class, name = "centre", desc = "the central position"),
			@ParameterDoc(type = NumberDef.class, name = "xRange", desc = "how far to iterate on the x axis"),
			@ParameterDoc(type = NumberDef.class, name = "yRange", desc = "how far to iterate on the y axis"),
			@ParameterDoc(type = NumberDef.class, name = "zRange", desc = "how far to iterate on the z axis")
		},
		returns = @ReturnDoc(type = IterableDef.class, desc = "the iterator for the positions"),
		examples = "foreach (pos : world.getPositionsFromCentre(new Pos(0, 100, 100), 10, 10, 10));"
	)
	private ArucasIterable getPositionsFromCentre(Arguments arguments) {
		BlockPos posA = arguments.skip().nextPrimitive(PosDef.class).getBlockPos();
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		Iterable<BlockPos> posIterable = BlockPos.iterateOutwards(posA, x, y, z);
		return () -> new PosIterator(posIterable.iterator(), o -> arguments.getInterpreter().convertValue(o));
	}

	@FunctionDoc(
		name = "getBlocksFromCentre",
		desc = {
			"This gets an iterator for all blocks (and positions) between two positions.",
			"The iterator iterates from the centre outwards"
		},
		params = {
			@ParameterDoc(type = PosDef.class, name = "centre", desc = "the central position"),
			@ParameterDoc(type = NumberDef.class, name = "xRange", desc = "how far to iterate on the x axis"),
			@ParameterDoc(type = NumberDef.class, name = "yRange", desc = "how far to iterate on the y axis"),
			@ParameterDoc(type = NumberDef.class, name = "zRange", desc = "how far to iterate on the z axis")
		},
		returns = @ReturnDoc(type = IterableDef.class, desc = "the iterator for the blocks"),
		examples = "foreach (block : world.getBlocksFromCentre(new Pos(0, 100, 100), 10, 5, 60));"
	)
	private ArucasIterable getBlocksFromCentre(Arguments arguments) {
		World world = arguments.nextPrimitive(this);
		BlockPos posA = arguments.nextPrimitive(PosDef.class).getBlockPos();
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		Iterable<BlockPos> posIterable = BlockPos.iterateOutwards(posA, x, y, z);
		return () -> new PosIterator.Block(world, posIterable.iterator(), o -> arguments.getInterpreter().convertValue(o));
	}

	@FunctionDoc(
		name = "reloadChunks",
		desc = "This reloads all the chunks, as if you were to press F3 + A.",
		examples = "world.reloadChunks();"
	)
	private Void reloadChunks(Arguments arguments) {
		ClientScriptUtils.ensureMainThread("reloadChunks", arguments.getInterpreter(), () -> {
			EssentialUtils.getClient().worldRenderer.reload();
		});
		return null;
	}
}
