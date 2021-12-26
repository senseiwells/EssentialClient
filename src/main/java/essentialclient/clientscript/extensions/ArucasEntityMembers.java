package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.EntityValue;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ArucasEntityMembers implements IArucasValueExtension {
	
	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.entityFunctions;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<EntityValue> getValueType() {
		return EntityValue.class;
	}

	@Override
	public String getName() {
		return "EntityMemberFunctions";
	}

	private final Set<MemberFunction> entityFunctions = Set.of(
		new MemberFunction("isSneaking", (context, function) -> BooleanValue.of(this.getEntity(context, function).isSneaking())),
		new MemberFunction("isSprinting", (context, function) -> BooleanValue.of(this.getEntity(context, function).isSprinting())),
		new MemberFunction("isFalling", (context, function) -> BooleanValue.of(this.getEntity(context, function).fallDistance > 0)),
		new MemberFunction("isOnGround", (context, function) -> BooleanValue.of(this.getEntity(context, function).isOnGround())),
		new MemberFunction("isTouchingWater", (context, function) -> BooleanValue.of(this.getEntity(context, function).isTouchingWater())),
		new MemberFunction("isTouchingWaterOrRain", (context, function) -> BooleanValue.of(this.getEntity(context, function).isTouchingWaterOrRain())),
		new MemberFunction("isSubmergedInWater", (context, function) -> BooleanValue.of(this.getEntity(context, function).isSubmergedInWater())),
		new MemberFunction("isInLava", (context, function) -> BooleanValue.of(this.getEntity(context, function).isInLava())),
		new MemberFunction("isOnFire", (context, function) -> BooleanValue.of(this.getEntity(context, function).isOnFire())),
		new MemberFunction("getLookingAtBlock", this::getLookingAtBlock),
		new MemberFunction("getLookingAtBlock", "maxDistance", this::getLookingAtBlock$1),
		new MemberFunction("getLookingAtPos", "maxDistance", this::getLookingAtPos),
		new MemberFunction("getEntityIdNumber", (context, function) -> new NumberValue(this.getEntity(context, function).getEntityId())),
		new MemberFunction("getX", (context, function) -> new NumberValue(this.getEntity(context, function).getX())),
		new MemberFunction("getY", (context, function) -> new NumberValue(this.getEntity(context, function).getY())),
		new MemberFunction("getZ", (context, function) -> new NumberValue(this.getEntity(context, function).getZ())),
		new MemberFunction("getYaw", this::getYaw),
		new MemberFunction("getPitch", (context, function) -> new NumberValue(this.getEntity(context, function).pitch)),
		new MemberFunction("getDimension", (context, function) -> new StringValue(this.getEntity(context, function).getEntityWorld().getRegistryKey().getValue().getPath())),
		new MemberFunction("getBiome", this::getBiome),
		new MemberFunction("getEntityId", List.of(), (context, function) -> new StringValue(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).getPath()), true),
		new MemberFunction("getId", (context, function) -> new StringValue(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).getPath())),
		new MemberFunction("getAge", (context, function) -> new NumberValue(this.getEntity(context, function).age)),
		new MemberFunction("getCustomName", this::getCustomName),
		new MemberFunction("getEntityUuid", (context, function) -> new StringValue(this.getEntity(context, function).getUuidAsString())),
		new MemberFunction("setGlowing", "boolean", this::setGlowing),
		new MemberFunction("getDistanceTo", "otherEntity", this::getDistanceTo),
		new MemberFunction("getSquaredDistanceTo", "otherEntity", this::getSquaredDistanceTo)
	);

	private Value<?> getLookingAtBlock(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		HitResult result = entity.raycast(20D, 0.0F, true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
			return new BlockStateValue(entity.getEntityWorld().getBlockState(blockPos), blockPos);
		}
		return new BlockStateValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
	}

	private Value<?> getLookingAtBlock$1(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		HitResult result = entity.raycast(numberValue.value, 0.0F, true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
			return new BlockStateValue(entity.getEntityWorld().getBlockState(blockPos), blockPos);
		}
		return new BlockStateValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
	}

	private Value<?> getLookingAtPos(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		double maxDistance = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		Vec3d pos = entity.raycast(maxDistance, 0.0F, true).getPos();
		ArucasValueList list = new ArucasValueList();
		list.add(new NumberValue(pos.x));
		list.add(new NumberValue(pos.y));
		list.add(new NumberValue(pos.z));
		return new ListValue(list);
	}

	private Value<?> getYaw(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		float yaw = entity.yaw % 360;
		return new NumberValue(yaw < -180 ? 360 + yaw : yaw);
	}

	private Value<?> getBiome(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeKey(entity.getBlockPos());
		return biomeKey.isPresent() ? new StringValue(biomeKey.get().getValue().getPath()) : NullValue.NULL;
	}

	private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		Text customName = entity.getCustomName();
		return customName == null ? NullValue.NULL : new StringValue(customName.asString());
	}

	private Value<?> setGlowing(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		entity.setGlowing(booleanValue.value);
		return NullValue.NULL;
	}

	private Value<?> getDistanceTo(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		EntityValue<?> otherEntity = function.getParameterValueOfType(context, EntityValue.class, 1);
		return new NumberValue(entity.distanceTo(otherEntity.value));
	}

	private Value<?> getSquaredDistanceTo(Context context, MemberFunction function) throws CodeError {
		Entity entity = this.getEntity(context, function);
		EntityValue<?> otherEntity = function.getParameterValueOfType(context, EntityValue.class, 1);
		return new NumberValue(entity.squaredDistanceTo(otherEntity.value));
	}

	private Entity getEntity(Context context, MemberFunction function) throws CodeError {
		EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 0);
		if (entityValue.value == null) {
			throw new RuntimeError("Entity was null", function.syntaxPosition, context);
		}
		return entityValue.value;
	}
}
