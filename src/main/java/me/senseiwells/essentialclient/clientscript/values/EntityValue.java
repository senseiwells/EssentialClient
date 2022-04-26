package me.senseiwells.essentialclient.clientscript.values;

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
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.clientscript.NbtUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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
import java.util.Objects;
import java.util.Optional;

public class EntityValue<T extends Entity> extends Value<T> {
	protected EntityValue(T value) {
		super(Objects.requireNonNull(value));
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
			return this.value.getId() == entityValue.value.getId();
		}
		return false;
	}

	@Override
	public String getTypeName() {
		return "Entity";
	}

	/**
	 * This method should not be called directly,
	 * if you want to convert an Entity to an EntityValue
	 * you should use {@link Context#convertValue(Object)}
	 */
	@Deprecated
	public static EntityValue<?> of(Entity entity) {
		return new EntityValue<>(entity);
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
			return context.convertValue(
				Registry.ENTITY_TYPE.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, stringValue.value)).create(world)
			);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("isSneaking", this::isSneaking),
				new MemberFunction("isSprinting", this::isSprinting),
				new MemberFunction("isFalling", this::isFalling),
				new MemberFunction("isOnGround", this::isOnGround),
				new MemberFunction("isTouchingWater", this::isTouchingWater),
				new MemberFunction("isTouchingWaterOrRain", this::isTouchingWaterOrRain),
				new MemberFunction("isSubmergedInWater", this::isSubmergedInWater),
				new MemberFunction("isInLava", this::isInLava),
				new MemberFunction("isOnFire", this::isOnFire),
				new MemberFunction("getLookingAtBlock", this::getLookingAtBlock),
				new MemberFunction("getLookingAtBlock", "maxDistance", this::getLookingAtBlock1),
				new MemberFunction("getLookingAtPos", "maxDistance", this::getLookingAtPos),
				new MemberFunction("getEntityIdNumber", this::getEntityIdNumber),
				new MemberFunction("getPos", this::getPos),
				new MemberFunction("getX", this::getX),
				new MemberFunction("getY", this::getY),
				new MemberFunction("getZ", this::getZ),
				new MemberFunction("getYaw", this::getYaw),
				new MemberFunction("getPitch", this::getPitch),
				new MemberFunction("getDimension", this::getDimension),
				new MemberFunction("getBiome", this::getBiome),
				new MemberFunction("getFullBiome", this::getFullBiome),
				new MemberFunction("getFullId", this::getFullId),
				new MemberFunction("getId", this::getId),
				new MemberFunction("getAge", this::getAge),
				new MemberFunction("getCustomName", this::getCustomName),
				new MemberFunction("getEntityUuid", this::getEntityUuid),
				new MemberFunction("setGlowing", "boolean", this::setGlowing),
				new MemberFunction("getDistanceTo", "otherEntity", this::getDistanceTo),
				new MemberFunction("getSquaredDistanceTo", "otherEntity", this::getSquaredDistanceTo),
				new MemberFunction("getNbt", this::getNbt),
				new MemberFunction("getTranslatedName", this::getTranslatedName),
				new MemberFunction("getHitbox", this::getHitbox),
				new MemberFunction("collidesWith", List.of("pos", "block"), this::collidesWithBlockAtPos)
			);
		}

		private Value<?> isSneaking(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isSneaking());
		}

		private Value<?> isSprinting(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isSprinting());
		}

		private Value<?> isFalling(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).fallDistance > 0);
		}

		private Value<?> isOnGround(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isOnGround());
		}

		private Value<?> isTouchingWater(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isTouchingWater());
		}

		private Value<?> isTouchingWaterOrRain(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isTouchingWaterOrRain());
		}

		private Value<?> isSubmergedInWater(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isSubmergedInWater());
		}

		private Value<?> isInLava(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isInLava());
		}

		private Value<?> isOnFire(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isOnFire());
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

		private Value<?> getEntityIdNumber(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getId());
		}

		private Value<?> getPos(Context context, MemberFunction function) throws CodeError {
			return new PosValue(this.getEntity(context, function).getPos());
		}

		private Value<?> getX(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getX());
		}

		private Value<?> getY(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getY());
		}

		private Value<?> getZ(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getZ());
		}

		private Value<?> getYaw(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			float yaw = entity.getYaw() % 360;
			return NumberValue.of(yaw < -180 ? 360 + yaw : yaw);
		}

		private Value<?> getPitch(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getPitch());
		}

		private Value<?> getDimension(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getEntity(context, function).getEntityWorld().getRegistryKey().getValue().getPath());
		}

		private Value<?> getBiome(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeKey(entity.getBlockPos());
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().getPath()) : NullValue.NULL;
		}

		private Value<?> getFullBiome(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeKey(entity.getBlockPos());
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().toString()) : NullValue.NULL;
		}

		private Value<?> getFullId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).toString());
		}

		private Value<?> getId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).getPath());
		}

		private Value<?> getAge(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).age);
		}

		private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Text customName = entity.getCustomName();
			return customName == null ? NullValue.NULL : StringValue.of(customName.asString());
		}

		private Value<?> getEntityUuid(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getEntity(context, function).getUuidAsString());
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
			ArucasMap mappedNbt = NbtUtils.nbtToMap(context, nbtCompound, 10);
			return new MapValue(mappedNbt);
		}

		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			return StringValue.of(I18n.translate(entity.getType().getTranslationKey()));
		}

		private Value<?> getHitbox(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Box box = entity.getBoundingBox();
			ArucasList boxList = new ArucasList();
			boxList.add(new PosValue(box.minX, box.minY, box.minZ));
			boxList.add(new PosValue(box.maxX, box.maxY, box.maxZ));
			return new ListValue(boxList);
		}

		private Value<?> collidesWithBlockAtPos(Context context, MemberFunction function) throws CodeError {
			// If block is placed at position, will entity collide with it? Then it can't be placed.
			Entity entity = this.getEntity(context, function);
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 2);
			return BooleanValue.of(entity.collidesWithStateAtPos(posValue.toBlockPos(), blockStateValue.value));
		}

		private Entity getEntity(Context context, MemberFunction function) throws CodeError {
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 0);
			if (entityValue.value == null) {
				throw new RuntimeError("Entity was null", function.syntaxPosition, context);
			}
			return entityValue.value;
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return EntityValue.class;
		}
	}
}
