package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.OtherPlayerValue;
import essentialclient.clientscript.values.WorldValue;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.render.RenderHelper;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
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
import java.util.Set;

public class ArucasWorldMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.worldFunctions;
	}

	@Override
	public Class<WorldValue> getValueType() {
		return WorldValue.class;
	}

	@Override
	public String getName() {
		return "WorldMemberFunctions";
	}

	private final Set<MemberFunction> worldFunctions = Set.of(
		new MemberFunction("getBlockAt", List.of("x", "y", "z"), this::getBlockAt),
		new MemberFunction("getOtherPlayer", "name", this::getOtherPlayer),
		new MemberFunction("getAllOtherPlayers", this::getAllOtherPlayers),
		new MemberFunction("getClosestPlayer", List.of("entity", "maxDistance"), this::getClosestPlayer),
		new MemberFunction("getAllEntities", this::getAllEntities),
		new MemberFunction("getEntityFromId", "id", this::getEntityFromId),
		new MemberFunction("getEmittedRedstonePower", List.of("x", "y", "z", "direction"), this::getEmittedRedstonePower),
		new MemberFunction("getDimensionName", (context, function) -> new StringValue(this.getWorld(context, function).getRegistryKey().getValue().getPath())),
		new MemberFunction("isRaining", (context, function) -> BooleanValue.of(this.getWorld(context, function).isRaining())),
		new MemberFunction("isAir", List.of("x", "y", "z"), this::isAir),
		new MemberFunction("isThundering", (context, function) -> BooleanValue.of(this.getWorld(context, function).isThundering())),
		new MemberFunction("getTimeOfDay", (context, function) -> new NumberValue(this.getWorld(context, function).getTimeOfDay())),
		new MemberFunction("renderParticle", List.of("particleName", "x", "y", "z"), this::renderParticle),
		new MemberFunction("setGhostBlock", List.of("block", "x", "y", "z"), this::setGhostBlock, true),
		new MemberFunction("spawnGhostEntity", List.of("entity", "x", "y", "z", "yaw", "pitch", "bodyYaw"), this::spawnGhostEntity),
		new MemberFunction("removeGhostEntity", "entityId", this::removeFakeEntity)
	);
	private Value<?> getEmittedRedstonePower(Context context, MemberFunction function) throws CodeError {
		final String error = "Position must be in range of player";
		ClientWorld world = this.getWorld(context, function);
		NumberValue num1 = function.getParameterValueOfType(context, NumberValue.class, 1, error);
		NumberValue num2 = function.getParameterValueOfType(context, NumberValue.class, 2, error);
		NumberValue num3 = function.getParameterValueOfType(context, NumberValue.class, 3, error);
		BlockPos blockPos = new BlockPos(Math.floor(num1.value), num2.value, Math.floor(num3.value));
		Direction direction = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 4).value);
		int power = world.getEmittedRedstonePower(blockPos, direction);
		return new NumberValue(power);
	}
	private Value<?> getBlockAt(Context context, MemberFunction function) throws CodeError {
		final String error = "Position must be in range of player";
		ClientWorld world = this.getWorld(context, function);
		NumberValue num1 = function.getParameterValueOfType(context, NumberValue.class, 1, error);
		NumberValue num2 = function.getParameterValueOfType(context, NumberValue.class, 2, error);
		NumberValue num3 = function.getParameterValueOfType(context, NumberValue.class, 3, error);
		BlockPos blockPos = new BlockPos(Math.floor(num1.value), num2.value, Math.floor(num3.value));
		return new BlockStateValue(world.getBlockState(blockPos), blockPos);
	}
	private Value<?> isAir(Context context, MemberFunction function) throws CodeError {
		final String error = "Position must be in range of player";
		ClientWorld world = this.getWorld(context, function);
		NumberValue num1 = function.getParameterValueOfType(context, NumberValue.class, 1, error);
		NumberValue num2 = function.getParameterValueOfType(context, NumberValue.class, 2, error);
		NumberValue num3 = function.getParameterValueOfType(context, NumberValue.class, 3, error);
		BlockPos blockPos = new BlockPos(Math.floor(num1.value), num2.value, Math.floor(num3.value));
		return BooleanValue.of(world.isAir(blockPos)); //not sure but minecraft do this way
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
		ArucasValueList otherPlayerValueList = new ArucasValueList();
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
		return EntityValue.getEntityValue(world.getClosestPlayer(entityValue.value, numberValue.value));
	}

	private Value<?> getAllEntities(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		ArucasValueList valueList = new ArucasValueList();
		for (Entity entity : world.getEntities()) {
			valueList.add(EntityValue.getEntityValue(entity));
		}
		return new ListValue(valueList);
	}

	private Value<?> getEntityFromId(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		NumberValue id = function.getParameterValueOfType(context, NumberValue.class, 1);
		return EntityValue.getEntityValue(world.getEntityById(id.value.intValue()));
	}

	private Value<?> renderParticle(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		String particleName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, particleName));
		if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
			throw new RuntimeError("Particle Invalid", function.syntaxPosition, context);
		}
		double x = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double y = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		double z = function.getParameterValueOfType(context, NumberValue.class, 4).value;
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> world.addParticle(defaultParticleType, x, y, z, 0, 0, 0));
		return NullValue.NULL;
	}

	@Deprecated
	private Value<?> setGhostBlock(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		BlockState blockState = function.getParameterValueOfType(context, BlockStateValue.class, 1).value;
		int x = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
		int y = function.getParameterValueOfType(context, NumberValue.class, 3).value.intValue();
		int z = function.getParameterValueOfType(context, NumberValue.class, 4).value.intValue();
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		BlockPos blockPos = new BlockPos(x, y, z);
		client.execute(() -> world.setBlockState(blockPos, blockState));
		return NullValue.NULL;
	}

	private Value<?> spawnGhostEntity(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 1);
		double x = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double y = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		double z = function.getParameterValueOfType(context, NumberValue.class, 4).value;
		float yaw = function.getParameterValueOfType(context, NumberValue.class, 5).value.floatValue();
		float pitch = function.getParameterValueOfType(context, NumberValue.class, 6).value.floatValue();
		float bodyYaw = function.getParameterValueOfType(context, NumberValue.class, 7).value.floatValue();
		Entity entity = entityValue.value;
		Entity newEntity = entity.getType().create(world);
		if (newEntity == null) {
			return NullValue.NULL;
		}
		int nextId = RenderHelper.getNextEntityId();
		newEntity.setEntityId(nextId);
		EssentialUtils.getClient().execute(() -> {
			newEntity.setBodyYaw(bodyYaw);
			newEntity.setHeadYaw(yaw);
			newEntity.refreshPositionAndAngles(x, y, z, yaw, pitch);
			newEntity.updatePositionAndAngles(x, y, z, yaw, pitch);
			newEntity.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, 3, true);
			newEntity.refreshPositionAfterTeleport(x, y, z);
			world.addEntity(nextId, newEntity);
		});
		return new NumberValue(nextId);
	}

	private Value<?> removeFakeEntity(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		int entityId = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
		if (!RenderHelper.removeFakeEntity(entityId)) {
			throw new RuntimeError("No such fake entity exists", function.syntaxPosition, context);
		}
		EssentialUtils.getClient().execute(() -> world.removeEntity(entityId));
		return NullValue.NULL;
	}

	private ClientWorld getWorld(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = function.getParameterValueOfType(context, WorldValue.class, 0).value;
		if (world == null) {
			throw new RuntimeError("World was null", function.syntaxPosition, context);
		}
		return world;
	}
}
