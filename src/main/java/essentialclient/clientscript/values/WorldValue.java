package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
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
import java.util.stream.Collectors;

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
				new MemberFunction("getDimensionName", (context, function) -> StringValue.of(this.getWorld(context, function).getRegistryKey().getValue().getPath())),
				new MemberFunction("isRaining", (context, function) -> BooleanValue.of(this.getWorld(context, function).isRaining())),
				new MemberFunction("isThundering", (context, function) -> BooleanValue.of(this.getWorld(context, function).isThundering())),
				new MemberFunction("getTimeOfDay", (context, function) -> NumberValue.of(this.getWorld(context, function).getTimeOfDay())),
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
				new MemberFunction("getArea", List.of("posA", "posB"), this::getArea),
				new MemberFunction("getAreaMatch", List.of("posA", "posB", "containsString"), this::getAreaMatch),
				new MemberFunction("getAreaPredicate", List.of("posA", "posB", "function"), this::getAreaPredicate)
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
			return EntityValue.of(world.getClosestPlayer(entityValue.value, numberValue.value));
		}

		private Value<?> getAllEntities(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			ArucasList valueList = new ArucasList();
			for (Entity entity : world.getEntities()) {
				valueList.add(EntityValue.of(entity));
			}
			return new ListValue(valueList);
		}

		private Value<?> getEntityFromId(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			NumberValue id = function.getParameterValueOfType(context, NumberValue.class, 1);
			return EntityValue.of(world.getEntityById(id.value.intValue()));
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
			ArucasList arucasList = new ArucasList();
			arucasList.addAll(BlockPos.stream(posA,posB).map(PosValue::new).collect(Collectors.toList()));
			return new ListValue(arucasList);
		}
		private Value<?> getAreaMatch(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = this.getWorld(context, function);
			BlockPos posA = function.getParameterValueOfType(context, PosValue.class, 1).toBlockPos();
			BlockPos posB = function.getParameterValueOfType(context, PosValue.class, 2).toBlockPos();
			String match = function.getParameterValueOfType(context, StringValue.class, 3).value;
			ArucasList arucasList = new ArucasList();
			arucasList.addAll(BlockPos.stream(posA,posB).
				filter(a-> world.getBlockState(a).getBlock().getName().toString().contains(match)).
				map(PosValue::new).
				collect(Collectors.toList()));
			return new ListValue(arucasList);
		}
		private Value<?> getAreaPredicate(Context context, MemberFunction function) throws CodeError {
			BlockPos posA = function.getParameterValueOfType(context, PosValue.class, 1).toBlockPos();
			BlockPos posB = function.getParameterValueOfType(context, PosValue.class, 2).toBlockPos();
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 3);
			ArucasList arucasList = new ArucasList();
			Context branchContext = context.createBranch();
			//try once by dummy pos
			try {
				functionValue.call(branchContext, List.of(new PosValue(BlockPos.ORIGIN)));
			}
			catch (CodeError e){
				throw new RuntimeError("Predicate function must accept PosValue as parameter and return Boolean", function.syntaxPosition, branchContext);
			}
			arucasList.addAll(BlockPos.stream(posA,posB).
				map(PosValue::new).
				filter(a-> {
					try {
						Value<?> value = functionValue.call(branchContext, List.of(a));
						return value instanceof BooleanValue booleanValue && booleanValue.value;
					} catch (CodeError e) {
						return false;
					}
				}).
				collect(Collectors.toList()));
			return new ListValue(arucasList);
		}

		private ClientWorld getWorld(Context context, MemberFunction function) throws CodeError {
			ClientWorld world = function.getParameterValueOfType(context, WorldValue.class, 0).value;
			if (world == null) {
				throw new RuntimeError("World was null", function.syntaxPosition, context);
			}
			return world;
		}

		@Override
		public Class<?> getValueClass() {
			return WorldValue.class;
		}
	}
}
