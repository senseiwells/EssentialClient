package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
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

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.WORLD;

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

	public static class ArucasWorldClass extends ArucasClassExtension {
		public ArucasWorldClass() {
			super(WORLD);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getBlockAt", 3, this::getBlockAt),
				MemberFunction.of("getBlockAt", 1, this::getBlockAtPos),
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
				MemberFunction.of("getArea", 2, this::getArea),
				MemberFunction.of("getAreaOfBlocks", 2, this::getAreaOfBlocks)
			);
		}

		private Value getBlockAt(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue num1 = arguments.getNextNumber();
			NumberValue num2 = arguments.getNextNumber();
			NumberValue num3 = arguments.getNextNumber();
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return new BlockValue(world.getBlockState(blockPos), blockPos);
		}

		private Value getBlockAtPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			return new BlockValue(world.getBlockState(blockPos), blockPos);
		}

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

		private Value getClosestPlayer(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			NumberValue numberValue = arguments.getNextNumber();
			return arguments.getContext().convertValue(ThreadSafeUtils.getClosestPlayer(world, entityValue.value, numberValue.value));
		}

		private Value getAllEntities(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			ArucasList valueList = new ArucasList();

			for (Entity entity : ThreadSafeUtils.getEntitiesSafe(world)) {
				valueList.add(arguments.getContext().convertValue(entity));
			}
			return new ListValue(valueList);
		}

		private Value getEntityFromId(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue id = arguments.getNextNumber();
			return arguments.getContext().convertValue(world.getEntityById(id.value.intValue()));
		}

		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(this.getWorld(arguments).getRegistryKey().getValue().toString());
		}

		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(this.getWorld(arguments).getRegistryKey().getValue().getPath());
		}

		private Value getDimensionName(Arguments arguments) throws CodeError {
			return StringValue.of(this.getWorld(arguments).getRegistryKey().getValue().getPath());
		}

		private Value isRaining(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getWorld(arguments).isRaining());
		}

		private Value isThundering(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getWorld(arguments).isThundering());
		}

		private Value getTimeOfDay(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getWorld(arguments).getTimeOfDay());
		}

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

		private Value setGhostBlockPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockState blockState = arguments.getNextGeneric(BlockValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			BlockPos blockPos = new BlockPos(posValue.value);
			client.execute(() -> world.setBlockState(blockPos, blockState));
			return NullValue.NULL;
		}

		private Value isAir(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			return BooleanValue.of(world.isAir(new BlockPos(x.value, y.value, z.value)));
		}

		private Value isAirPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue pos = arguments.getNext(PosValue.class);
			return BooleanValue.of(world.isAir(new BlockPos(pos.value)));
		}

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

		private Value getEmittedRedstonePowerPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			String stringDirection = arguments.getNextGeneric(StringValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection), Direction.NORTH);
			return NumberValue.of(world.getEmittedRedstonePower(blockPos, direction));
		}

		private Value getLight(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			NumberValue x = arguments.getNextNumber();
			NumberValue y = arguments.getNextNumber();
			NumberValue z = arguments.getNextNumber();
			return NumberValue.of(world.getLightLevel(new BlockPos(x.value, y.value, z.value)));
		}

		private Value getLightPos(Arguments arguments) throws CodeError {
			ClientWorld world = this.getWorld(arguments);
			BlockPos pos = arguments.getNext(PosValue.class).toBlockPos();
			return NumberValue.of(world.getLightLevel(pos));
		}

		private Value getArea(Arguments arguments) throws CodeError {
			BlockPos posA = arguments.skip().getNext(PosValue.class).toBlockPos();
			BlockPos posB = arguments.getNext(PosValue.class).toBlockPos();
			ArucasList list = new ArucasList();
			for (BlockPos pos : BlockPos.iterate(posA, posB)) {
				list.add(new PosValue(pos));
			}
			return new ListValue(list);
		}

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
