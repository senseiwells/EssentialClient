package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import essentialclient.utils.clientscript.NbtUtils;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Optional;

public class EntityValue<T extends Entity> extends Value<T> {
	protected EntityValue(T value) {
		super(value);
	}

	@Override
	public Value<T> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "Entity{id=%s}".formatted(Registry.ENTITY_TYPE.getId(this.value.getType()).getPath());
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> value) {
		if (value instanceof EntityValue<?> entityValue) {
			return this.value.getEntityId() == entityValue.value.getEntityId();
		}
		return false;
	}

	public static Value<?> of(Entity entity) {
		if (entity != null) {
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				return new PlayerValue(clientPlayerEntity);
			}
			if (entity instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
				return new OtherPlayerValue(otherClientPlayerEntity);
			}
			if (entity instanceof LivingEntity livingEntity) {
				return new LivingEntityValue<>(livingEntity);
			}
			return new EntityValue<>(entity);
		}
		return NullValue.NULL;
	}

	public static class ArucasEntityClass extends ArucasClassExtension {
		public ArucasEntityClass() {
			super("Entity");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "string", this::of)
			);
		}

		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return EntityValue.of(Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, stringValue.value)).create(world));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
				new MemberFunction("getLookingAtBlock", "maxDistance", this::getLookingAtBlock1),
				new MemberFunction("getLookingAtPos", "maxDistance", this::getLookingAtPos),
				new MemberFunction("getEntityIdNumber", (context, function) -> NumberValue.of(this.getEntity(context, function).getEntityId())),
				new MemberFunction("getPos", (context, function) -> new PosValue(this.getEntity(context, function).getPos())),
				new MemberFunction("getX", (context, function) -> NumberValue.of(this.getEntity(context, function).getX())),
				new MemberFunction("getY", (context, function) -> NumberValue.of(this.getEntity(context, function).getY())),
				new MemberFunction("getZ", (context, function) -> NumberValue.of(this.getEntity(context, function).getZ())),
				new MemberFunction("getYaw", this::getYaw),
				new MemberFunction("getPitch", (context, function) -> NumberValue.of(this.getEntity(context, function).pitch)),
				new MemberFunction("getDimension", (context, function) -> StringValue.of(this.getEntity(context, function).getEntityWorld().getRegistryKey().getValue().getPath())),
				new MemberFunction("getBiome", this::getBiome),
				new MemberFunction("getId", (context, function) -> StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).getPath())),
				new MemberFunction("getAge", (context, function) -> NumberValue.of(this.getEntity(context, function).age)),
				new MemberFunction("getCustomName", this::getCustomName),
				new MemberFunction("getEntityUuid", (context, function) -> StringValue.of(this.getEntity(context, function).getUuidAsString())),
				new MemberFunction("setGlowing", "boolean", this::setGlowing),
				new MemberFunction("getDistanceTo", "otherEntity", this::getDistanceTo),
				new MemberFunction("getSquaredDistanceTo", "otherEntity", this::getSquaredDistanceTo),
				new MemberFunction("getNbt", this::getNbt),
				new MemberFunction("getTranslatedName", this::getTranslatedName),
				new MemberFunction("getHitbox", this::getHitbox),
				new MemberFunction("collidesWith", List.of("pos","block"), this::collidesWithBlockAtPos)
			);
		}

		private Value<?> getLookingAtBlock(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			HitResult result = entity.raycast(20D, 0.0F, true);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
				return new BlockValue(entity.getEntityWorld().getBlockState(blockPos), blockPos);
			}
			return new BlockValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
		}

		private Value<?> getLookingAtBlock1(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			HitResult result = entity.raycast(numberValue.value, 0.0F, true);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
				return new BlockValue(entity.getEntityWorld().getBlockState(blockPos), blockPos);
			}
			return new BlockValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
		}

		private Value<?> getLookingAtPos(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			double maxDistance = function.getParameterValueOfType(context, NumberValue.class, 1).value;
			Vec3d pos = entity.raycast(maxDistance, 0.0F, true).getPos();
			return new PosValue(pos);
		}

		private Value<?> getYaw(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			float yaw = entity.yaw % 360;
			return NumberValue.of(yaw < -180 ? 360 + yaw : yaw);
		}

		private Value<?> getBiome(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeKey(entity.getBlockPos());
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().getPath()) : NullValue.NULL;
		}

		private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Text customName = entity.getCustomName();
			return customName == null ? NullValue.NULL : StringValue.of(customName.asString());
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
			return NumberValue.of(entity.distanceTo(otherEntity.value));
		}

		private Value<?> getSquaredDistanceTo(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			EntityValue<?> otherEntity = function.getParameterValueOfType(context, EntityValue.class, 1);
			return NumberValue.of(entity.squaredDistanceTo(otherEntity.value));
		}

		private Value<?> getNbt(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
			ArucasMap mappedNbt = NbtUtils.mapNbt(context, nbtCompound, 0);
			return new MapValue(mappedNbt);
		}

		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			return StringValue.of(I18n.translate(entity.getType().getTranslationKey()));
		}
		private Value<?> getHitbox(Context context, MemberFunction function) throws CodeError {
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 0);
			if (entityValue.value == null) {
				throw new RuntimeError("Entity was null", function.syntaxPosition, context);
			}
			Box box = entityValue.value.getBoundingBox();
			ArucasList boxList = new ArucasList();
			boxList.add(new PosValue(box.minX,box.minY,box.minZ));
			boxList.add(new PosValue(box.maxX,box.maxY,box.maxZ));
			return new ListValue(boxList);
		}
		private Value<?> collidesWithBlockAtPos(Context context, MemberFunction function) throws CodeError {
			//if block is placed at position, will entity collide with it? then it can't be placed.
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 0);
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 2);
			if (entityValue.value == null) {
				throw new RuntimeError("Entity was null", function.syntaxPosition, context);
			}
			return BooleanValue.of(entityValue.value.method_30632(posValue.toBlockPos(), blockStateValue.value));
		}
		private Entity getEntity(Context context, MemberFunction function) throws CodeError {
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 0);
			if (entityValue.value == null) {
				throw new RuntimeError("Entity was null", function.syntaxPosition, context);
			}
			return entityValue.value;
		}

		@Override
		public Class<?> getValueClass() {
			return EntityValue.class;
		}
	}
}
