package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
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

import java.util.List;
import java.util.Objects;

public class WorldValue extends Value<ClientWorld> {
	public WorldValue(ClientWorld world) {
		super(world);
	}

	@Override
	public Value<ClientWorld> copy(Context context) {
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
	public boolean isEquals(Context context, Value<?> value) {
		return this.value == value.value;
	}

	@Override
	public String getTypeName() {
		return "World";
	}

	public static class ArucasWorldClass extends ArucasClassExtension {
		private final static String IN_RANGE_ERROR = "Position must be in range of player";

		public ArucasWorldClass() {
			super("World");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getBlockAt", List.of("x", "y", "z"), this::getBlockAt),
				new MemberFunction("getBlockAt", "pos", this::getBlockAtPos),
				new MemberFunction("getOtherPlayer", "name", this::getOtherPlayer),
				new MemberFunction("getAllOtherPlayers", this::getAllOtherPlayers),
				new MemberFunction("getClosestPlayer", List.of("entity", "maxDistance"), this::getClosestPlayer),
				new MemberFunction("getAllEntities", this::getAllEntities),
				new MemberFunction("getEntityFromId", "id", this::getEntityFromId),
				new MemberFunction("getFullId", this::getFullId),
				new MemberFunction("getId", this::getId),
				new MemberFunction("getDimensionName", this::getDimensionName),
				new MemberFunction("isRaining", this::isRaining),
				new MemberFunction("isThundering", this::isThunderin),
				new MemberFunction("getTimeOfDay", this::getTimeOfDay),
				new MemberFunction("renderParticle", List.of("particleName", "x", "y", "z"), this::renderParticle),
				new MemberFunction("renderParticle", List.of("particleName", "pos"), this::renderParticlePos),
				new MemberFunction("renderParticle", List.of("particleName", "pos", "xVelocity", "yVelocity", "zVelocity"), this::renderParticleVel),
				new MemberFunction("setGhostBlock", List.of("block", "x", "y", "z"), this::setGhostBlock, "This function is dangerous, be careful!"),
				new MemberFunction("setGhostBlock", List.of("block", "pos"), this::setGhostBlockPos, "This function is dangerous, be careful!"),
				new MemberFunction("isAir", List.of("x", "y", "z"), this::isAir),
				new MemberFunction("isAir", "pos", this::isAirPos),
				new MemberFunction("getEmittedRedstonePower", List.of("x", "y", "z", "direction"), this::getEmittedRedstonePower),
				new MemberFunction("getEmittedRedstonePower", List.of("pos", "direction"), this::getEmittedRedstonePowerPos),
				new MemberFunction("getLight", List.of("x", "y", "z"), this::getLight),
				new MemberFunction("getLight", "pos", this::getLightPos),
				new MemberFunction("getArea", List.of("pos1", "pos2"), this::getArea),
				new MemberFunction("getAreaOfBlocks", List.of("pos1", "pos2"), this::getAreaOfBlocks)
			);
		}

		private Value<?> getBlockAt(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			NumberValue num1 = function.getParameterValueOfType(context, NumberValue.class, 1, IN_RANGE_ERROR);
			NumberValue num2 = function.getParameterValueOfType(context, NumberValue.class, 2, IN_RANGE_ERROR);
			NumberValue num3 = function.getParameterValueOfType(context, NumberValue.class, 3, IN_RANGE_ERROR);
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return new BlockValue(world.getBlockState(blockPos), blockPos);
		}

		private Value<?> getBlockAtPos(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1, IN_RANGE_ERROR);
			BlockPos blockPos = new BlockPos(posValue.value);
			return new BlockValue(world.getBlockState(blockPos), blockPos);
		}

		private Value<?> getOtherPlayer(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			PlayerListEntry playerInfo = networkHandler.getPlayerListEntry(stringValue.value);
			if (playerInfo != null) {
				PlayerEntity player = world.getPlayerByUuid(playerInfo.getProfile().getId());
				if (player instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
					return new OtherPlayerValue(otherClientPlayerEntity);
				}
			}
			return NullValue.NULL;
		}

		private Value<?> getAllOtherPlayers(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			ArucasList otherPlayerValueList = new ArucasList();
			for (AbstractClientPlayerEntity playerEntity : world.getPlayers()) {
				if (playerEntity instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
					otherPlayerValueList.add(new OtherPlayerValue(otherClientPlayerEntity));
				}
			}
			return new ListValue(otherPlayerValueList);
		}

		private Value<?> getClosestPlayer(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 1);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 2);
			return context.convertValue(world.getClosestPlayer(entityValue.value, numberValue.value));
		}

		private Value<?> getAllEntities(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			ArucasList valueList = new ArucasList();
			for (Entity entity : world.getEntities()) {
				valueList.add(context.convertValue(entity));
			}
			return new ListValue(valueList);
		}

		private Value<?> getEntityFromId(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			NumberValue id = function.getParameterValueOfType(context, NumberValue.class, 1);
			return context.convertValue(world.getEntityById(id.value.intValue()));
		}

		private Value<?> getFullId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getWorld(context, function).getRegistryKey().getValue().toString());
		}

		private Value<?> getId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getWorld(context, function).getRegistryKey().getValue().getPath());
		}

		private Value<?> getDimensionName(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getWorld(context, function).getRegistryKey().getValue().getPath());
		}

		private Value<?> isRaining(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getWorld(context, function).isRaining());
		}

		private Value<?> isThunderin(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getWorld(context, function).isThundering());
		}

		private Value<?> getTimeOfDay(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getWorld(context, function).getTimeOfDay());
		}

		private Value<?> renderParticle(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			String particleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, particleName));
			if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
				throw new RuntimeError("Particle Invalid", function.syntaxPosition, context);
			}
			double x = function.getParameterValueOfType(context, NumberValue.class, 2, IN_RANGE_ERROR).value;
			double y = function.getParameterValueOfType(context, NumberValue.class, 3, IN_RANGE_ERROR).value;
			double z = function.getParameterValueOfType(context, NumberValue.class, 4, IN_RANGE_ERROR).value;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> world.addParticle(defaultParticleType, x, y, z, 0, 0, 0));
			return NullValue.NULL;
		}

		private Value<?> renderParticlePos(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			String particleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, particleName));
			if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
				throw new RuntimeError("Particle Invalid", function.syntaxPosition, context);
			}
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 2, IN_RANGE_ERROR);
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

		private Value<?> renderParticleVel(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			String particleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			double xVelocity = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			double yVelocity = function.getParameterValueOfType(context, NumberValue.class, 3).value;
			double zVelocity = function.getParameterValueOfType(context, NumberValue.class, 4).value;
			ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, particleName));
			if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
				throw new RuntimeError("Particle Invalid", function.syntaxPosition, context);
			}
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 2, IN_RANGE_ERROR);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> world.addParticle(
				defaultParticleType,
				posValue.value.getX(),
				posValue.value.getY(),
				posValue.value.getZ(),
				xVelocity,
				yVelocity,
				zVelocity
			));
			return NullValue.NULL;
		}

		@Deprecated
		private Value<?> setGhostBlock(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			BlockState blockState = function.getParameterValueOfType(context, BlockValue.class, 1).value;
			double x = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			double y = function.getParameterValueOfType(context, NumberValue.class, 3).value;
			double z = function.getParameterValueOfType(context, NumberValue.class, 4).value;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			BlockPos blockPos = new BlockPos(x, y, z);
			client.execute(() -> world.setBlockState(blockPos, blockState));
			return NullValue.NULL;
		}

		@Deprecated
		private Value<?> setGhostBlockPos(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			BlockState blockState = function.getParameterValueOfType(context, BlockValue.class, 1).value;
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 2);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			BlockPos blockPos = new BlockPos(posValue.value);
			client.execute(() -> world.setBlockState(blockPos, blockState));
			return NullValue.NULL;
		}

		private Value<?> isAir(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			NumberValue x = function.getParameterValueOfType(context, NumberValue.class, 1, IN_RANGE_ERROR);
			NumberValue y = function.getParameterValueOfType(context, NumberValue.class, 2, IN_RANGE_ERROR);
			NumberValue z = function.getParameterValueOfType(context, NumberValue.class, 3, IN_RANGE_ERROR);
			return BooleanValue.of(world.isAir(new BlockPos(x.value, y.value, z.value)));
		}

		private Value<?> isAirPos(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			PosValue pos = function.getParameterValueOfType(context, PosValue.class, 1, IN_RANGE_ERROR);
			return BooleanValue.of(world.isAir(new BlockPos(pos.value)));
		}

		private Value<?> getEmittedRedstonePower(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			NumberValue x = function.getParameterValueOfType(context, NumberValue.class, 1, IN_RANGE_ERROR);
			NumberValue y = function.getParameterValueOfType(context, NumberValue.class, 2, IN_RANGE_ERROR);
			NumberValue z = function.getParameterValueOfType(context, NumberValue.class, 3, IN_RANGE_ERROR);
			String stringDirection = function.getParameterValueOfType(context, StringValue.class, 4).value;
			BlockPos blockPos = new BlockPos(x.value, y.value, z.value);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection), Direction.NORTH);
			return NumberValue.of(world.getEmittedRedstonePower(blockPos, direction));
		}

		private Value<?> getEmittedRedstonePowerPos(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			String stringDirection = function.getParameterValueOfType(context, StringValue.class, 2).value;
			BlockPos blockPos = new BlockPos(posValue.value);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection), Direction.NORTH);
			return NumberValue.of(world.getEmittedRedstonePower(blockPos, direction));
		}

		private Value<?> getLight(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			NumberValue x = function.getParameterValueOfType(context, NumberValue.class, 1, IN_RANGE_ERROR);
			NumberValue y = function.getParameterValueOfType(context, NumberValue.class, 2, IN_RANGE_ERROR);
			NumberValue z = function.getParameterValueOfType(context, NumberValue.class, 3, IN_RANGE_ERROR);
			return NumberValue.of(world.getLightLevel(new BlockPos(x.value, y.value, z.value)));
		}

		private Value<?> getLightPos(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			BlockPos pos = function.getParameterValueOfType(context, PosValue.class, 1).toBlockPos();
			return NumberValue.of(world.getLightLevel(pos));
		}

		private Value<?> getArea(Context context, MemberFunction function) throws CodeError {
			BlockPos posA = function.getParameterValueOfType(context, PosValue.class, 1).toBlockPos();
			BlockPos posB = function.getParameterValueOfType(context, PosValue.class, 2).toBlockPos();
			ArucasList list = new ArucasList();
			for (BlockPos pos : BlockPos.iterate(posA, posB)) {
				list.add(new PosValue(pos));
			}
			return new ListValue(list);
		}

		private Value<?> getAreaOfBlocks(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			BlockPos posA = function.getParameterValueOfType(context, PosValue.class, 1).toBlockPos();
			BlockPos posB = function.getParameterValueOfType(context, PosValue.class, 2).toBlockPos();

			ArucasList list = new ArucasList();
			for (BlockPos pos : BlockPos.iterate(posA, posB)) {
				BlockState state = world.getBlockState(pos);
				list.add(new BlockValue(state, pos));
			}
			return new ListValue(list);
		}

		private ClientWorld getWorld(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = function.getParameterValueOfType(context, WorldValue.class, 0).value;
			if (world == null) {
				throw new RuntimeError("World was null", function.syntaxPosition, context);
			}
			return world;
		}

		@Override
		public Class<WorldValue> getValueClass() {
			return WorldValue.class;
		}
	}
}
