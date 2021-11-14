package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.OtherPlayerValue;
import essentialclient.clientscript.values.WorldValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Set;

public class ArucasWorldMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.worldFunctions;
	}

	@Override
	public String getName() {
		return "WorldMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> worldFunctions = Set.of(
		new MemberFunction("getBlockAt", List.of("x", "y", "z"), this::getBlockAt),
		new MemberFunction("getOtherPlayer", "name", this::getOtherPlayer),
		new MemberFunction("getAllOtherPlayers", this::getAllOtherPlayers),
		new MemberFunction("getClosestPlayer", List.of("entity", "maxDistance"), this::getClosestPlayer),
		new MemberFunction("getAllEntities", this::getAllEntities),
		new MemberFunction("getEntityFromId", "id", this::getEntityFromId),
		new MemberFunction("isRaining", (context, function) -> new BooleanValue(this.getWorld(context, function).isRaining())),
		new MemberFunction("isThundering", (context, function) -> new BooleanValue(this.getWorld(context, function).isThundering())),
		new MemberFunction("getTimeOfDay", (context, function) -> new NumberValue(this.getWorld(context, function).getTimeOfDay())),
		new MemberFunction("renderParticle", List.of("particleName", "x", "y", "z"), this::renderParticle),
		new MemberFunction("setGhostBlock", List.of("block", "x", "y", "z"), this::setGhostBlock)
	);

	private Value<?> getBlockAt(Context context, MemberFunction function) throws CodeError {
		final String error = "Position must be in range of player";
		ClientWorld world = this.getWorld(context, function);
		NumberValue num1 = function.getParameterValueOfType(context, NumberValue.class, 1, error);
		NumberValue num2 = function.getParameterValueOfType(context, NumberValue.class, 2, error);
		NumberValue num3 = function.getParameterValueOfType(context, NumberValue.class, 3, error);
		BlockPos blockPos = new BlockPos(Math.floor(num1.value), num2.value, Math.floor(num3.value));
		return new BlockStateValue(world.getBlockState(blockPos));
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
		return new NullValue();
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
		ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(new Identifier(particleName));
		if (!(particleType instanceof DefaultParticleType defaultParticleType)) {
			throw new RuntimeError("Particle Invalid", function.syntaxPosition, context);
		}
		double x = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double y = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		double z = function.getParameterValueOfType(context, NumberValue.class, 4).value;
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> world.addParticle(defaultParticleType, x, y, z, 0, 0, 0));
		return new NullValue();
	}

	private Value<?> setGhostBlock(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = this.getWorld(context, function);
		BlockState blockState = function.getParameterValueOfType(context, BlockStateValue.class, 1).value;
		int x = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
		int y = function.getParameterValueOfType(context, NumberValue.class, 3).value.intValue();
		int z = function.getParameterValueOfType(context, NumberValue.class, 4).value.intValue();
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		BlockPos blockPos = new BlockPos(x, y, z);
		client.execute(() -> world.setBlockState(blockPos, blockState));
		return new NullValue();
	}

	private ClientWorld getWorld(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = function.getParameterValueOfType(context, WorldValue.class, 0).value;
		if (world == null) {
			throw new RuntimeError("World was null", function.syntaxPosition, context);
		}
		return world;
	}
}
