package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.EntityValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Set;

public class ArucasEntityMembers implements IArucasExtension {
	
	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.entityFunctions;
	}

	@Override
	public String getName() {
		return "EntityMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> entityFunctions = Set.of(
		new MemberFunction("isSneaking", (context, function) -> new BooleanValue(this.getEntity(context, function).isSneaking())),
		new MemberFunction("isSprinting", (context, function) -> new BooleanValue(this.getEntity(context, function).isSprinting())),
		new MemberFunction("isFalling", (context, function) -> new BooleanValue(this.getEntity(context, function).fallDistance > 0)),
		new MemberFunction("getLookingAtBlock", this::getLookingAtBlock),
		new MemberFunction("getEntityId", (context, function) -> new NumberValue(this.getEntity(context, function).getEntityId())),
		new MemberFunction("getX", (context, function) -> new NumberValue(this.getEntity(context, function).getX())),
		new MemberFunction("getY", (context, function) -> new NumberValue(this.getEntity(context, function).getY())),
		new MemberFunction("getZ", (context, function) -> new NumberValue(this.getEntity(context, function).getZ())),
		new MemberFunction("getYaw", this::getYaw),
		new MemberFunction("getPitch", (context, function) -> new NumberValue(this.getEntity(context, function).pitch)),
		new MemberFunction("getDimension", (context, function) -> new StringValue(this.getEntity(context, function).getEntityWorld().getRegistryKey().getValue().getPath())),
		new MemberFunction("getBiome", this::getBiome),
		new MemberFunction("getEntityType", (context, function) -> new StringValue(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).getPath())),
		new MemberFunction("getCustomName", this::getCustomName),
		new MemberFunction("getEntityUuid", (context, function) -> new StringValue(this.getEntity(context, function).getUuidAsString())),
		new MemberFunction("setGlowing", "boolean", this::setGlowing)
	);

	private Value<?> getLookingAtBlock(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		HitResult result = entity.raycast(20D, 0.0F, true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
			return new BlockStateValue(entity.getEntityWorld().getBlockState(blockPos));
		}
		return new BlockStateValue(Blocks.AIR.getDefaultState());
	}

	private Value<?> getYaw(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		float yaw = entity.yaw % 360;
		return new NumberValue(yaw < -180 ? 360 + yaw : yaw);
	}

	private Value<?> getBiome(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		return new StringValue(entity.getEntityWorld().getBiome(entity.getBlockPos()).getCategory().getName());
	}

	private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		Text customName = entity.getCustomName();
		return customName == null ? new NullValue() : new StringValue(customName.asString());
	}

	private Value<?> setGlowing(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		entity.setGlowing(booleanValue.value);
		return new NullValue();
	}

	private Entity getEntity(Context context, MemberFunction function) throws CodeError {
		EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 0);
		if (entityValue.value == null) {
			throw new RuntimeError("Entity was null", function.startPos, function.endPos, context);
		}
		return entityValue.value;
	}
}
