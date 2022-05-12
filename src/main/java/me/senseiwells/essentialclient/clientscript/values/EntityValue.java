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
import net.minecraft.entity.EntityType;
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

	/**
	 * Entity class for Arucas. This class is mostly used to get data about entities. <br>
	 * Import the class with <code>import Entity from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
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

		/**
		 * Name: <code>Entity.of(entityId)</code> <br>
		 * Description: This converts an entityId into an entity instance <br>
		 * Returns - Entity: the entity instance from the id <br>
		 * Throws - Error: <code>... is not a valid entity</code> if the id is not a valid entity id <br>
		 * Example: <code>Entity.of("minecraft:pig");</code>
		 */
		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return context.convertValue(
				Registry.ENTITY_TYPE.getOrEmpty(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).orElseThrow(
					() -> new RuntimeError("'%s' is not a valid entity", function.syntaxPosition, context)
				).create(world)
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
				new MemberFunction("isGlowing", this::isGlowing),
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
				new MemberFunction("isOf", "id", this::isOf),
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

		/**
		 * Name: <code>&lt;Entity>.isSneaking()</code> <br>
		 * Description: Returns true if the player is sneaking <br>
		 * Returns - Boolean: true if the player is sneaking, false if not <br>
		 * Example: <code>entity.isSneaking();</code>
		 */
		private Value<?> isSneaking(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isSneaking());
		}

		/**
		 * Name: <code>&lt;Entity>.isSprinting()</code> <br>
		 * Description: Returns true if the player is sprinting <br>
		 * Returns - Boolean: true if the player is sprinting, false if not <br>
		 * Example: <code>entity.isSprinting();</code>
		 */
		private Value<?> isSprinting(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isSprinting());
		}

		/**
		 * Name: <code>&lt;Entity>.isFalling()</code> <br>
		 * Description: Returns true if the entity is falling <br>
		 * Returns - Boolean: true if the entity is falling, false if not <br>
		 * Example: <code>entity.isFalling();</code>
		 */
		private Value<?> isFalling(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).fallDistance > 0);
		}

		/**
		 * Name: <code>&lt;Entity>.isOnGround()</code> <br>
		 * Description: Returns true if the entity is on the ground <br>
		 * Returns - Boolean: true if the entity is on the ground, false if not <br>
		 * Example: <code>entity.isOnGround();</code>
		 */
		private Value<?> isOnGround(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isOnGround());
		}

		/**
		 * Name: <code>&lt;Entity>.isTouchingWater()</code> <br>
		 * Description: Returns true if the entity is touching water <br>
		 * Returns - Boolean: true if the entity is touching water, false if not <br>
		 * Example: <code>entity.isTouchingWater();</code>
		 */
		private Value<?> isTouchingWater(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isTouchingWater());
		}

		/**
		 * Name: <code>&lt;Entity>.isTouchingWaterOrRain()</code> <br>
		 * Description: Returns true if the entity is touching water or rain <br>
		 * Returns - Boolean: true if the entity is touching water or rain, false if not <br>
		 * Example: <code>entity.isTouchingWaterOrRain();</code>
		 */
		private Value<?> isTouchingWaterOrRain(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isTouchingWaterOrRain());
		}

		/**
		 * Name: <code>&lt;Entity>.isSubmergedInWater()</code> <br>
		 * Description: Returns true if the entity is submerged in water <br>
		 * Returns - Boolean: true if the entity is submerged in water, false if not <br>
		 * Example: <code>entity.isSubmergedInWater();</code>
		 */
		private Value<?> isSubmergedInWater(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isSubmergedInWater());
		}

		/**
		 * Name: <code>&lt;Entity>.isInLava()</code> <br>
		 * Description: Returns true if the entity is in lava <br>
		 * Returns - Boolean: true if the entity is in lava, false if not <br>
		 * Example: <code>entity.isInLava();</code>
		 */
		private Value<?> isInLava(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isInLava());
		}

		/**
		 * Name: <code>&lt;Entity>.isOnFire()</code> <br>
		 * Description: Returns true if the entity is on fire <br>
		 * Returns - Boolean: true if the entity is on fire, false if not <br>
		 * Example: <code>entity.isOnFire();</code>
		 */
		private Value<?> isOnFire(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isOnFire());
		}

		private Value<?> isGlowing(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getEntity(context, function).isGlowing());
		}

		/**
		 * Name: <code>&lt;Entity>.getLookingAtBlock()</code> <br>
		 * Description: This gets the block that the entity is currently looking at
		 * with a max range of 20 blocks, if there is no block then it will return air <br>
		 * Returns - Block: the block that the entity is looking at, containing the position <br>
		 * Example: <code>entity.getLookingAtBlock();</code>
		 */
		private Value<?> getLookingAtBlock(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			HitResult result = entity.raycast(20D, 0.0F, true);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
				return new BlockValue(entity.getEntityWorld().getBlockState(blockPos), blockPos);
			}
			return new BlockValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
		}

		/**
		 * Name: <code>&lt;Entity>.getLookingAtBlock(maxDistance)</code> <br>
		 * Description: This gets the block that the entity is currently looking at
		 * with a specific max range, if there is no block then it will return air <br>
		 * Parameter - Number: the max range to ray cast <br>
		 * Returns - Block: the block that the entity is looking at, containing the position <br>
		 * Example: <code>entity.getLookingAtBlock(10);</code>
		 */
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

		/**
		 * Name: <code>&lt;Entity>.getLookingAtPos(maxDistance)</code> <br>
		 * Description: This gets the position that the entity is currently looking at with a specific max range <br>
		 * Parameter - Number: the max range to ray cast <br>
		 * Returns - Pos: the position that the entity is looking at, containing the x, y, and z <br>
		 * Example: <code>entity.getLookingAtPos(10);</code>
		 */
		private Value<?> getLookingAtPos(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			double maxDistance = function.getParameterValueOfType(context, NumberValue.class, 1).value;
			Vec3d pos = entity.raycast(maxDistance, 0.0F, true).getPos();
			return new PosValue(pos);
		}

		/**
		 * Name: <code>&lt;Entity>.getEntityIdNumber()</code> <br>
		 * Description: This gets the entity id number of the entity <br>
		 * Returns - Number: the entity id number <br>
		 * Example: <code>entity.getEntityIdNumber();</code>
		 */
		private Value<?> getEntityIdNumber(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getId());
		}

		/**
		 * Name: <code>&lt;Entity>.getPos()</code> <br>
		 * Description: This gets the position of the entity <br>
		 * Returns - Pos: the position of the entity <br>
		 * Example: <code>entity.getPos();</code>
		 */
		private Value<?> getPos(Context context, MemberFunction function) throws CodeError {
			return new PosValue(this.getEntity(context, function).getPos());
		}

		/**
		 * Name: <code>&lt;Entity>.getX()</code> <br>
		 * Description: This gets the x position of the entity <br>
		 * Returns - Number: the x position of the entity <br>
		 * Example: <code>entity.getX();</code>
		 */
		private Value<?> getX(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getX());
		}

		/**
		 * Name: <code>&lt;Entity>.getY()</code> <br>
		 * Description: This gets the y position of the entity <br>
		 * Returns - Number: the y position of the entity <br>
		 * Example: <code>entity.getY();</code>
		 */
		private Value<?> getY(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getY());
		}

		/**
		 * Name: <code>&lt;Entity>.getZ()</code> <br>
		 * Description: This gets the z position of the entity <br>
		 * Returns - Number: the z position of the entity <br>
		 * Example: <code>entity.getZ();</code>
		 */
		private Value<?> getZ(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getZ());
		}

		/**
		 * Name: <code>&lt;Entity>.getYaw()</code> <br>
		 * Description: This gets the yaw of the entity (horizontal head rotation) <br>
		 * Returns - Number: the yaw of the entity, between -180 and 180 <br>
		 * Example: <code>entity.getYaw();</code>
		 */
		private Value<?> getYaw(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			float yaw = entity.getYaw() % 360;
			return NumberValue.of(yaw < -180 ? 360 + yaw : yaw);
		}

		/**
		 * Name: <code>&lt;Entity>.getPitch()</code> <br>
		 * Description: This gets the pitch of the entity (vertical head rotation) <br>
		 * Returns - Number: the pitch of the entity, between -90 and 90 <br>
		 * Example: <code>entity.getPitch();</code>
		 */
		private Value<?> getPitch(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).getPitch());
		}

		/**
		 * Name: <code>&lt;Entity>.getDimension()</code> <br>
		 * Description: This gets the dimension of the entity <br>
		 * Returns - String: the dimension id of dimension the entity is in <br>
		 * Example: <code>entity.getDimension();</code>
		 */
		private Value<?> getDimension(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getEntity(context, function).getEntityWorld().getRegistryKey().getValue().getPath());
		}

		/**
		 * Name: <code>&lt;Entity>.getBiome()</code> <br>
		 * Description: This gets the biome of the entity, this only returns the path,
		 * so for example <code>'plains'</code> <br>
		 * Returns - String: the biome id of the biome the entity is in <br>
		 * Example: <code>entity.getBiome();</code>
		 */
		private Value<?> getBiome(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeKey(entity.getBlockPos());
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().getPath()) : NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;Entity>.getFullBiome()</code> <br>
		 * Description: This gets the biome of the entity, this returns the full biome id,
		 * so for example <code>'minecraft:plains'</code> <br>
		 * Returns - String: the biome id of the biome the entity is in <br>
		 * Example: <code>entity.getFullBiome();</code>
		 */
		private Value<?> getFullBiome(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeKey(entity.getBlockPos());
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().toString()) : NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;Entity>.getFullId()</code> <br>
		 * Description: This gets the full id of the entity, this returns the full id,
		 * so for example <code>'minecraft:cow'</code> you can find all entityNames on
		 * [Joa's Entity Property Encyclopedia](https://joakimthorsen.github.io/MCPropertyEncyclopedia/entities.html) <br>
		 * Returns - String: the full id of the entity <br>
		 * Example: <code>entity.getFullId();</code>
		 */
		private Value<?> getFullId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).toString());
		}

		/**
		 * Name: <code>&lt;Entity>.getId()</code> <br>
		 * Description: This gets the id of the entity, this returns the id,
		 * so for example <code>'cow'</code> <br>
		 * Returns - String: the id of the entity <br>
		 * Example: <code>entity.getId();</code>
		 */
		private Value<?> getId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(context, function).getType()).getPath());
		}

		/**
		 * Name: <code>&lt;Entity>.isOf(entityId)</code> <br>
		 * Description: This checks if the entity is of the given entity id <br>
		 * Parameter - String: the entity id to check <br>
		 * Returns - Boolean: true if the entity is of the given entity id <br>
		 * Example: <code>entity.isOf("cow");</code>
		 */
		private Value<?> isOf(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			EntityType<?> type = Registry.ENTITY_TYPE.getOrEmpty(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value)).orElse(null);
			return BooleanValue.of(entity.getType() == type);
		}

		/**
		 * Name: <code>&lt;Entity>.getAge()</code> <br>
		 * Description: This gets the age of the entity  in ticks <br>
		 * Returns - Integer: the age of the entity in ticks <br>
		 * Example: <code>entity.getAge();</code>
		 */
		private Value<?> getAge(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getEntity(context, function).age);
		}

		/**
		 * Name: <code>&lt;Entity>.getCustomName()</code> <br>
		 * Description: This gets the custom name of the entity if it has one <br>
		 * Returns - String/Null: the custom name of the entity if it has one, otherwise null <br>
		 * Example: <code>entity.getCustomName();</code>
		 */
		private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Text customName = entity.getCustomName();
			return customName == null ? NullValue.NULL : StringValue.of(customName.asString());
		}

		/**
		 * Name: <code>&lt;Entity>.getEntityUuid()</code> <br>
		 * Description: This gets the uuid of the entity <br>
		 * Returns - String: the uuid of the entity <br>
		 * Example: <code>entity.getEntityUuid();</code>
		 */
		private Value<?> getEntityUuid(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getEntity(context, function).getUuidAsString());
		}

		/**
		 * Name: <code>&lt;Entity>.setGlowing(glowing)</code> <br>
		 * Description: This sets the entity to either start glowing or stop glowing on the client <br>
		 * Parameter - Boolean: the glowing state <br>
		 * Example: <code>entity.setGlowing(true);</code>
		 */
		private Value<?> setGlowing(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
			entity.setGlowing(booleanValue.value);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;Entity>.getDistanceTo(otherEntity)</code> <br>
		 * Description: This gets the distance between the entity and the other entity <br>
		 * Parameter - Entity: the other entity <br>
		 * Returns - Number: the distance between the entities <br>
		 * Example: <code>entity.getDistanceTo(Player.get());</code>
		 */
		private Value<?> getDistanceTo(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			EntityValue<?> otherEntity = function.getParameterValueOfType(context, EntityValue.class, 1);
			return NumberValue.of(entity.distanceTo(otherEntity.value));
		}

		/**
		 * Name: <code>&lt;Entity>.getSquaredDistanceTo(otherEntity)</code> <br>
		 * Description: This gets the squared distance between the entity and the other entity <br>
		 * Parameter - Entity: the other entity <br>
		 * Returns - Number: the squared distance between the entities <br>
		 * Example: <code>entity.getSquaredDistanceTo(Player.get());</code>
		 */
		private Value<?> getSquaredDistanceTo(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			EntityValue<?> otherEntity = function.getParameterValueOfType(context, EntityValue.class, 1);
			return NumberValue.of(entity.squaredDistanceTo(otherEntity.value));
		}

		/**
		 * Name: <code>&lt;Entity>.getNbt()</code> <br>
		 * Description: This gets the nbt of the entity as a map <br>
		 * Returns - Map: the nbt of the entity <br>
		 * Example: <code>entity.getNbt();</code>
		 */
		private Value<?> getNbt(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
			ArucasMap mappedNbt = NbtUtils.nbtToMap(context, nbtCompound, 10);
			return new MapValue(mappedNbt);
		}

		/**
		 * Name: <code>&lt;Entity>.getTranslatedName()</code> <br>
		 * Description: This gets the translated name of the entity, for
		 * example <code>'minecraft:pig'</code> would return <code>'Pig'</code>
		 * if your language is in english <br>
		 * Returns - String: the translated name of the entity <br>
		 * Example: <code>entity.getTranslatedName();</code>
		 */
		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			return StringValue.of(I18n.translate(entity.getType().getTranslationKey()));
		}

		/**
		 * Name: <code>&lt;Entity>.getHitbox()</code> <br>
		 * Description: This gets the hitbox of the entity in a list containing
		 * the two corners of the hitbox, the minimum point and the maximum point <br>
		 * Returns - List: the hitbox of the entity <br>
		 * Example: <code>entity.getHitbox();</code>
		 */
		private Value<?> getHitbox(Context context, MemberFunction function) throws CodeError {
			Entity entity = this.getEntity(context, function);
			Box box = entity.getBoundingBox();
			ArucasList boxList = new ArucasList();
			boxList.add(new PosValue(box.minX, box.minY, box.minZ));
			boxList.add(new PosValue(box.maxX, box.maxY, box.maxZ));
			return new ListValue(boxList);
		}

		/**
		 * Name: <code>&lt;Entity>.collidesWithBlockAsPos(pos)</code> <br>
		 * Description: This checks if the entity collides with the block at the given position <br>
		 * Parameter - Pos: the position to check <br>
		 * Returns - Boolean: if the entity collides with the block <br>
		 * Example: <code>entity.collidesWithBlockAsPos(new Pos(100, 0, 100));</code>
		 */
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
