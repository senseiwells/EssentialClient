package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.OtherPlayerValue;
import essentialclient.clientscript.values.WorldValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
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
		new MemberFunction("getTimeOfDay", (context, function) -> new NumberValue(this.getWorld(context, function).getTimeOfDay()))
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
		List<Value<?>> otherPlayerValueList = new ArrayList<>();
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
		List<Value<?>> valueList = new ArrayList<>();
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

	private ClientWorld getWorld(Context context, MemberFunction function) throws CodeError {
		ClientWorld world = function.getParameterValueOfType(context, WorldValue.class, 0).value;
		if (world == null) {
			throw new RuntimeError("World was null", function.startPos, function.endPos, context);
		}
		return world;
	}
}
