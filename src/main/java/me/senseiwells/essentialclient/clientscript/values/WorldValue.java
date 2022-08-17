package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.clientscript.PosIterator;
import me.senseiwells.essentialclient.utils.clientscript.ThreadSafeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class WorldValue extends GenericValue<ClientWorld> {
	public WorldValue(ClientWorld world) {
		super(world);
	}

	@Override
	public GenericValue<ClientWorld> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "World{level=%s, dimension=%s}".formatted(this.value.toString(), this.value.getRegistryKey().getValue().getPath());
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return WORLD;
	}

	@ClassDoc(
		name = WORLD,
		desc = "This class represents worlds, and allows you to interact with things inside of them.",
		importPath = "Minecraft"
	)
	public static class ArucasWorldClass extends ArucasClassExtension {
		public ArucasWorldClass() {
			super(WORLD);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getBlockAt", 3, this::getBlockAt),
				MemberFunction.of("getBlockAt", 1, this::getBlockAtPos),
				MemberFunction.of("getBiomeAt", 3, this::getBiomeAt),
				MemberFunction.of("getBiomeAt", 1, this::getBiomeAtPos),
				MemberFunction.of("getOtherPlayer", 1, this::getOtherPlayer),
				MemberFunction.of("getAllOtherPlayers", this::getAllOtherPlayers),
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
				MemberFunction.of("getArea", 2, this::getArea, "This function is memory intensive, use '<World>'.getPositions(pos1, pos2)"),
				MemberFunction.of("getAreaOfBlocks", 2, this::getAreaOfBlocks, "This function is memory intensive, use '<World>.getBlocks(pos1, pos2)'"),
				MemberFunction.of("getPositions", 2, this::getPositions),
				MemberFunction.of("getPositions", 6, this::getPositionsFull),
				MemberFunction.of("getBlocks", 2, this::getBlocks),
				MemberFunction.of("getBlocks", 6, this::getBlocksFull)
			);
		}

		@FunctionDoc(
			name = "getBlockAt",
			desc = "This function gets the block at the given coordinates",
			params = {
				NUMBER, "x", "the x coordinate",
				NUMBER, "y", "the y coordinate",
				NUMBER, "z", "the z coordinate"
			},
			returns = {BLOCK, "the block at the given coordinates"},
			example = "world.getBlockAt(0, 100, 0);"
		)
		private Value getBlockAt(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue num1 = arguments.getNextNumber();
			NumberValue num2 = arguments.getNextNumber();
			NumberValue num3 = arguments.getNextNumber();
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return new BlockValue(world.getBlockState(blockPos), blockPos);
		}

		@FunctionDoc(
			name = "getBlockAt",
			desc = "This function gets the block at the given coordinates",
			params = {POS, "pos", "the position"},
			returns = {BLOCK, "the block at the given coordinates"},
			example = "world.getBlockAt(new Pos(0, 100, 0));"
		)
		private Value getBlockAtPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			return new BlockValue(world.getBlockState(blockPos), blockPos);
		}

		@FunctionDoc(
			name = "getBiomeAt",
			desc = "This function gets the block at the given coordinates",
			params = {
				NUMBER, "x", "the x coordinate",
				NUMBER, "y", "the y coordinate",
				NUMBER, "z", "the z coordinate"
			},
			returns = {BLOCK, "the block at the given coordinates"},
			example = "world.getBiomeAt(0, 100, 0);"
		)
		private Value getBiomeAt(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue num1 = arguments.getNextNumber();
			NumberValue num2 = arguments.getNextNumber();
			NumberValue num3 = arguments.getNextNumber();
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return new BiomeValue(world.getBiome(blockPos).value());
		}

		@FunctionDoc(
			name = "getBiomeAt",
			desc = "This function gets the block at the given coordinates",
			params = {POS, "pos", "the position"},
			returns = {BLOCK, "the block at the given coordinates"},
			example = "world.getBiomeAt(new Pos(0, 100, 0));"
		)
		private Value getBiomeAtPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			return new BiomeValue(world.getBiome(blockPos).value());
		}

		@FunctionDoc(
			name = "getOtherPlayer",
			desc = "This gets another player from the given username",
			params = {STRING, "username", "the username of the other player"},
			returns = {PLAYER, "the other player, null if not found"},
			example = "world.getOtherPlayer('senseiwells');"
		)
		private Value getOtherPlayer(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			StringValue stringValue = arguments.getNextString();
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			PlayerListEntry playerInfo = networkHandler.getPlayerListEntry(stringValue.value);
			if (playerInfo != null) {
				PlayerEntity player = ThreadSafeUtils.getPlayerByUuid(world, playerInfo.getProfile().getId());
				if (player instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
					return new OtherPlayerValue(otherClientPlayerEntity);
				}
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getAllOtherPlayers",
			desc = "This will get all other players in the world",
			returns = {LIST, "a list of all other players"},
			example = "world.getAllOtherPlayers();"
		)
		private Value getAllOtherPlayers(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			ArucasList otherPlayerValueList = new ArucasList();
			for (PlayerEntity playerEntity : ThreadSafeUtils.getPlayersSafe(world)) {
				if (playerEntity instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
					otherPlayerValueList.add(new OtherPlayerValue(otherClientPlayerEntity));
				}
			}
			return new ListValue(otherPlayerValueList);
		}

		@FunctionDoc(
			name = "getClosestPlayer",
			desc = "This will get the closest player to another entity in the world",
			params = {
				ENTITY, "entity", "the entity to get the closest player to",
				NUMBER, "maxDistance", "the maximum distance to search for a player in blocks"
			},
			returns = {PLAYER, "the closest player, null if not found"},
			example = "world.getClosestPlayer(Player.get(), 100);"
		)
		private Value getClosestPlayer(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			NumberValue numberValue = arguments.getNextNumber();
			return arguments.getContext().convertValue(ThreadSafeUtils.getClosestPlayer(world, entityValue.value, numberValue.value));
		}

		@FunctionDoc(
			name = "getAllEntities",
			desc = "This will get all entities in the world",
			returns = {LIST, "a list of all entities"},
			example = "world.getAllEntities();"
		)
		private Value getAllEntities(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			ArucasList valueList = new ArucasList();

			for (Entity entity : ThreadSafeUtils.getEntitiesSafe(world)) {
				valueList.add(arguments.getContext().convertValue(entity));
			}
			return new ListValue(valueList);
		}

		@FunctionDoc(
			name = "getEntityFromId",
			desc = "This will get an entity from the given entity id",
			params = {NUMBER, "entityId", "the entity id"},
			returns = {ENTITY, "the entity, null if not found"},
			example = "world.getEntityFromId(1);"
		)
		private Value getEntityFromId(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue id = arguments.getNextNumber();
			return arguments.getContext().convertValue(world.getEntityById(id.value.intValue()));
		}

		@FunctionDoc(
			name = "getFullId",
			desc = "This will get the full id of the world",
			returns = {STRING, "the full id of the world, for example: 'minecraft:overworld'"},
			example = "world.getFullId();"
		)
		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(this.getWorld(arguments).getRegistryKey().getValue().toString());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This will get the id of the world",
			returns = {STRING, "the id of the world, for example: 'overworld'"},
			example = "world.getId();"
		)
		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(this.getWorld(arguments).getRegistryKey().getValue().getPath());
		}

		@FunctionDoc(
			deprecated = "You should use 'world.getId()' instead",
			name = "getDimensionName",
			desc = "This will get the id of the world",
			returns = {STRING, "the id of the world, for example: 'overworld'"},
			example = "world.getDimensionName();"
		)
		private Value getDimensionName(Arguments arguments) throws CodeError {
			return StringValue.of(this.getWorld(arguments).getRegistryKey().getValue().getPath());
		}

		@FunctionDoc(
			name = "isRaining",
			desc = "This will check if the world is currently raining",
			returns = {BOOLEAN, "true if the world is currently raining"},
			example = "world.isRaining();"
		)
		private Value isRaining(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getWorld(arguments).isRaining());
		}

		@FunctionDoc(
			name = "isThundering",
			desc = "This will check if the world is currently thundering",
			returns = {BOOLEAN, "true if the world is currently thundering"},
			example = "world.isThundering();"
		)
		private Value isThundering(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getWorld(arguments).isThundering());
		}

		@FunctionDoc(
			name = "getTimeOfDay",
			desc = {
				"This will get the time of day of the world",
				"info on the time of day [here](https://minecraft.fandom.com/wiki/Daylight_cycle)"
			},
			returns = {NUMBER, "the time of day of the world, between 0 and 24000"},
			example = "world.getTimeOfDay();"
		)
		private Value getTimeOfDay(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getWorld(arguments).getTimeOfDay());
		}

		@FunctionDoc(
			name = "renderParticle",
			desc = {
				"This will render a particle in the world, you can find a list of all",
				"the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles)"
			},
			params = {
				STRING, "particleId", "the id of the particle",
				NUMBER, "x", "the x position of the particle",
				NUMBER, "y", "the y position of the particle",
				NUMBER, "z", "the z position of the particle"
			},
			throwMsgs = "Particle Invalid",
			example = "world.renderParticle('end_rod', 10, 10, 10);"
		)
		private Value renderParticle(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			String particleName = arguments.getNextGeneric(StringValue.class);
			ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getId(arguments, particleName));
			if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
				throw arguments.getError("Particle Invalid");
			}
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> world.addParticle(defaultParticleType, x.value, y.value, z.value, 0, 0, 0));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "renderParticle",
			desc = {
				"This will render a particle in the world, you can find a list of all",
				"the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles)"
			},
			params = {
				STRING, "particleId", "the id of the particle",
				POS, "pos", "the position of the particle"
			},
			throwMsgs = "Particle Invalid",
			example = "world.renderParticle('end_rod', pos);"
		)
		private Value renderParticlePos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			String particleName = arguments.getNextGeneric(StringValue.class);
			ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getId(arguments, particleName));
			if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
				throw arguments.getError("Particle Invalid");
			}
			PosValue posValue = arguments.getNext(PosValue.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> world.addParticle(
				defaultParticleType,
				posValue.value.getX(),
				posValue.value.getY(),
				posValue.value.getZ(),
				0,
				0,
				0
			));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "renderParticle",
			desc = {
				"This will render a particle in the world with a velocity, you can find a list of all",
				"the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles)"
			},
			params = {
				STRING, "particleId", "the id of the particle",
				POS, "pos", "the position of the particle",
				NUMBER, "velX", "the velocity of the particle on the x axis",
				NUMBER, "velY", "the velocity of the particle on the y axis",
				NUMBER, "velZ", "the velocity of the particle on the z axis"
			},
			throwMsgs = "Particle Invalid",
			example = "world.renderParticle('end_rod', pos, 0.5, 0.5, 0.5);"
		)
		private Value renderParticleVel(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			String particleName = arguments.getNextGeneric(StringValue.class);
			NumberValue xVelocity = arguments.getNextNumber();
			NumberValue yVelocity = arguments.getNextNumber();
			NumberValue zVelocity = arguments.getNextNumber();
			ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getId(arguments, particleName));
			if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
				throw arguments.getError("Particle Invalid");
			}
			PosValue posValue = arguments.getNext(PosValue.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> world.addParticle(
				defaultParticleType,
				posValue.value.getX(),
				posValue.value.getY(),
				posValue.value.getZ(),
				xVelocity.value,
				yVelocity.value,
				zVelocity.value
			));
			return NullValue.NULL;
		}

		@FunctionDoc(
			deprecated = "This function is dangerous, use at your own risk",
			name = "setGhostBlock",
			desc = "This sets a ghost block in the world as if it were a real block, may cause issues",
			params = {
				BLOCK, "block", "the block to set",
				NUMBER, "x", "the x position of the block",
				NUMBER, "y", "the y position of the block",
				NUMBER, "z", "the z position of the block"
			},
			example = "world.setGhostBlock(Material.BEDROCK.asBlock(), 0, 100, 0);"
		)
		private Value setGhostBlock(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockState blockState = arguments.getNextGeneric(BlockValue.class);
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			BlockPos blockPos = new BlockPos(x.value, y.value, z.value);
			client.execute(() -> world.setBlockState(blockPos, blockState));
			return NullValue.NULL;
		}

		@FunctionDoc(
			deprecated = "This function is dangerous, use at your own risk",
			name = "setGhostBlock",
			desc = "This sets a ghost block in the world as if it were a real block, may cause issues",
			params = {
				BLOCK, "block", "the block to set",
				POS, "pos", "the position of the block"
			},
			example = "world.setGhostBlock(Material.BEDROCK.asBlock(), new Pos(0, 100, 0));"
		)
		private Value setGhostBlockPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockState blockState = arguments.getNextGeneric(BlockValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			BlockPos blockPos = new BlockPos(posValue.value);
			client.execute(() -> world.setBlockState(blockPos, blockState));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "isAir",
			desc = "Returns true if the block at the given position is air",
			params = {
				NUMBER, "x", "the x position of the block",
				NUMBER, "y", "the y position of the block",
				NUMBER, "z", "the z position of the block"
			},
			returns = {BOOLEAN, "true if the block is air"},
			example = "world.isAir(0, 100, 0);"
		)
		private Value isAir(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			return BooleanValue.of(world.isAir(new BlockPos(x.value, y.value, z.value)));
		}

		@FunctionDoc(
			name = "isAir",
			desc = "Returns true if the block at the given position is air",
			params = {POS, "pos", "the position of the block"},
			returns = {BOOLEAN, "true if the block is air"},
			example = "world.isAir(new Pos(0, 100, 0));"
		)
		private Value isAirPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue pos = arguments.getNext(PosValue.class);
			return BooleanValue.of(world.isAir(new BlockPos(pos.value)));
		}

		@FunctionDoc(
			name = "getEmittedRedstonePower",
			desc = "Gets the emitted restone power at the given position and direction",
			params = {
				NUMBER, "x", "the x position of the block",
				NUMBER, "y", "the y position of the block",
				NUMBER, "z", "the z position of the block",
				STRING, "direction", "the direction to check, for example 'north', 'east', 'up', etc."
			},
			returns = {NUMBER, "the emitted redstone power"},
			example = "world.getEmittedRedstonePower(0, 100, 0, 'north');"
		)
		private Value getEmittedRedstonePower(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			String stringDirection = arguments.getNextGeneric(StringValue.class);
			BlockPos blockPos = new BlockPos(x.value, y.value, z.value);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection), Direction.NORTH);
			return NumberValue.of(world.getEmittedRedstonePower(blockPos, direction));
		}

		@FunctionDoc(
			name = "getEmittedRedstonePower",
			desc = "Gets the emitted restone power at the given position and direction",
			params = {
				POS, "pos", "the position of the block",
				STRING, "direction", "the direction to check, for example 'north', 'east', 'up', etc."
			},
			returns = {NUMBER, "the emitted redstone power"},
			example = "world.getEmittedRedstonePower(new Pos(0, 100, 0), 'north');"
		)
		private Value getEmittedRedstonePowerPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			String stringDirection = arguments.getNextGeneric(StringValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection), Direction.NORTH);
			return NumberValue.of(world.getEmittedRedstonePower(blockPos, direction));
		}

		@FunctionDoc(
			name = "getLight",
			desc = "Gets the light level at the given position",
			params = {
				NUMBER, "x", "the x position of the block",
				NUMBER, "y", "the y position of the block",
				NUMBER, "z", "the z position of the block"
			},
			returns = {NUMBER, "the light level"},
			example = "world.getLight(0, 100, 0);"
		)
		private Value getLight(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			return NumberValue.of(world.getLightLevel(new BlockPos(x.value, y.value, z.value)));
		}

		@FunctionDoc(
			name = "getLight",
			desc = "Gets the light level at the given position",
			params = {POS, "pos", "the position of the block"},
			returns = {NUMBER, "the light level"},
			example = "world.getLight(new Pos(0, 100, 0));"
		)
		private Value getLightPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockPos pos = arguments.getNext(PosValue.class).toBlockPos();
			return NumberValue.of(world.getLightLevel(pos));
		}

		@FunctionDoc(
			deprecated = "This function is memory intensive, you should use `<World>.getPositions(pos1, pos2)`",
			name = "getArea",
			desc = "This gets a list of all block positions between the two positions",
			params = {
				POS, "pos1", "the first position",
				POS, "pos2", "the second position"
			},
			returns = {LIST, "the list of positions"},
			example = "world.getArea(new Pos(0, 100, 0), new Pos(0, 100, 0));"
		)
		private Value getArea(Arguments arguments) throws CodeError {
			BlockPos posA = arguments.skip().getNext(PosValue.class).toBlockPos();
			BlockPos posB = arguments.getNext(PosValue.class).toBlockPos();
			ArucasList list = new ArucasList();
			for (BlockPos pos : BlockPos.iterate(posA, posB)) {
				list.add(new PosValue(pos));
			}
			return new ListValue(list);
		}

		@FunctionDoc(
			deprecated = "This function is memory intensive, you should use `<World>.getBlocks(pos1, pos2)`",
			name = "getAreaOfBlocks",
			desc = "This gets a list of all blocks (with positions) between the two positions",
			params = {
				POS, "pos1", "the first position",
				POS, "pos2", "the second position"
			},
			returns = {LIST, "the list of blocks"},
			example = "world.getAreaOfBlocks(new Pos(0, 100, 0), new Pos(0, 100, 0));"
		)
		private Value getAreaOfBlocks(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockPos posA = arguments.getNext(PosValue.class).toBlockPos();
			BlockPos posB = arguments.getNext(PosValue.class).toBlockPos();

			ArucasList list = new ArucasList();
			for (BlockPos pos : BlockPos.iterate(posA, posB)) {
				BlockState state = world.getBlockState(pos);
				list.add(new BlockValue(state, pos));
			}
			return new ListValue(list);
		}

		@FunctionDoc(
			name = "getPositions",
			desc = "This gets an iterator for all positions between two positions",
			params = {
				POS, "pos1", "the first position",
				POS, "pos2", "the second position"
			},
			returns = {ITERATOR, "the iterator for the positions"},
			example = "foreach (pos : world.getPositions(new Pos(0, 100, 100), new Pos(0, 100, 0)));"
		)
		private Value getPositions(Arguments arguments) throws RuntimeError {
			BlockPos posA = arguments.skip().getNext(PosValue.class).toBlockPos();
			BlockPos posB = arguments.getNext(PosValue.class).toBlockPos();
			Iterable<BlockPos> posIterable = BlockPos.iterate(posA, posB);
			return new IteratorValue(() -> new PosIterator(posIterable.iterator()));
		}

		@FunctionDoc(
			name = "getPositions",
			desc = "This gets an iterator for all positions between two positions",
			params = {
				NUMBER, "x1", "the first position X",
				NUMBER, "y1", "the first position Y",
				NUMBER, "z1", "the first position Z",
				NUMBER, "x2", "the second position X",
				NUMBER, "y2", "the second position Y",
				NUMBER, "z2", "the second position Z"
			},
			returns = {ITERATOR, "the iterator for the positions"},
			example = "foreach (pos : world.getPositions(0, 100, 100, 0, 100, 0));"
		)
		private Value getPositionsFull(Arguments arguments) throws RuntimeError {
			double x = arguments.getNextNumber().value;
			double y = arguments.getNextNumber().value;
			double z = arguments.getNextNumber().value;
			double x2 = arguments.getNextNumber().value;
			double y2 = arguments.getNextNumber().value;
			double z2 = arguments.getNextNumber().value;
			BlockPos posA = new BlockPos(x, y, z);
			BlockPos posB = new BlockPos(x2, y2, z2);
			Iterable<BlockPos> posIterable = BlockPos.iterate(posA, posB);
			return new IteratorValue(() -> new PosIterator(posIterable.iterator()));
		}

		@FunctionDoc(
			name = "getBlocks",
			desc = "This gets an iterator for all blocks (and positions) between two positions",
			params = {
				POS, "pos1", "the first position",
				POS, "pos2", "the second position"
			},
			returns = {ITERATOR, "the iterator for the blocks"},
			example = "foreach (block : world.getBlocks(new Pos(0, 100, 100), new Pos(0, 100, 0)));"
		)
		private Value getBlocks(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockPos posA = arguments.getNext(PosValue.class).toBlockPos();
			BlockPos posB = arguments.getNext(PosValue.class).toBlockPos();
			Iterable<BlockPos> posIterable = BlockPos.iterate(posA, posB);
			return new IteratorValue(() -> new PosIterator.Block(world, posIterable.iterator()));
		}

		@FunctionDoc(
			name = "getBlocks",
			desc = "This gets an iterator for all blocks (and positions) between two positions",
			params = {
				NUMBER, "x1", "the first position X",
				NUMBER, "y1", "the first position Y",
				NUMBER, "z1", "the first position Z",
				NUMBER, "x2", "the second position X",
				NUMBER, "y2", "the second position Y",
				NUMBER, "z2", "the second position Z"
			},
			returns = {ITERATOR, "the iterator for the blocks"},
			example = "foreach (block : world.getBlocks(0, 100, 100, 0, 100, 0));"
		)
		private Value getBlocksFull(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			double x = arguments.getNextNumber().value;
			double y = arguments.getNextNumber().value;
			double z = arguments.getNextNumber().value;
			double x2 = arguments.getNextNumber().value;
			double y2 = arguments.getNextNumber().value;
			double z2 = arguments.getNextNumber().value;
			BlockPos posA = new BlockPos(x, y, z);
			BlockPos posB = new BlockPos(x2, y2, z2);
			Iterable<BlockPos> posIterable = BlockPos.iterate(posA, posB);
			return new IteratorValue(() -> new PosIterator.Block(world, posIterable.iterator()));
		}

		private ClientWorld getWorld(Arguments arguments) throws CodeError {
			ClientWorld world = arguments.getNextGeneric(WorldValue.class);
			if (world == null) {
				throw arguments.getError("World was null");
			}
			return world;
		}

		@Override
		public Class<WorldValue> getValueClass() {
			return WorldValue.class;
		}
	}
}
