package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
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
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Optional;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class EntityValue<T extends Entity> extends GenericValue<T> {
	protected EntityValue(T value) {
		super(Objects.requireNonNull(value));
	}

	@Override
	public GenericValue<T> copy(Context context) {
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
	public boolean isEquals(Context context, Value value) {
		if (value instanceof EntityValue<?> entityValue) {
			return this.value.getId() == entityValue.value.getId();
		}
		return false;
	}

	@Override
	public String getTypeName() {
		return ENTITY;
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

	@ClassDoc(
		name = ENTITY,
		desc = "This class is mostly used to get data about entities.",
		importPath = "Minecraft"
	)
	public static class ArucasEntityClass extends ArucasClassExtension {
		public ArucasEntityClass() {
			super(ENTITY);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("of", 1, this::of)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This converts an entityId into an entity instance",
			params = {STRING, "entityId", "the entityId to convert to an entity"},
			returns = {ENTITY, "the entity instance from the id"},
			throwMsgs = "... is not a valid entity",
			example = "Entity.of('minecraft:pig');"
		)
		private Value of(Arguments arguments) throws CodeError {
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			StringValue stringValue = arguments.getNextString();
			Context context = arguments.getContext();
			return context.convertValue(
				Registry.ENTITY_TYPE.getOrEmpty(ArucasMinecraftExtension.getId(context, arguments.getPosition(), stringValue.value)).orElseThrow(
					() -> arguments.getError("'%s' is not a valid entity", stringValue.value)
				).create(world)
			);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("isSneaking", this::isSneaking),
				MemberFunction.of("isSprinting", this::isSprinting),
				MemberFunction.of("isFalling", this::isFalling),
				MemberFunction.of("isOnGround", this::isOnGround),
				MemberFunction.of("isTouchingWater", this::isTouchingWater),
				MemberFunction.of("isTouchingWaterOrRain", this::isTouchingWaterOrRain),
				MemberFunction.of("isSubmergedInWater", this::isSubmergedInWater),
				MemberFunction.of("isInLava", this::isInLava),
				MemberFunction.of("isOnFire", this::isOnFire),
				MemberFunction.of("isGlowing", this::isGlowing),
				MemberFunction.of("getLookingAtBlock", this::getLookingAtBlock),
				MemberFunction.of("getLookingAtBlock", 1, this::getLookingAtBlock1),
				MemberFunction.of("getLookingAtBlock", 2, this::getLookingAtBlock2),
				MemberFunction.of("getLookingAtPos", 1, this::getLookingAtPos),
				MemberFunction.of("getEntityIdNumber", this::getEntityIdNumber),
				MemberFunction.of("getPos", this::getPos),
				MemberFunction.of("getX", this::getX),
				MemberFunction.of("getY", this::getY),
				MemberFunction.of("getZ", this::getZ),
				MemberFunction.of("getYaw", this::getYaw),
				MemberFunction.of("getPitch", this::getPitch),
				MemberFunction.of("getDimension", this::getDimension),
				MemberFunction.of("getWorld", this::getWorld),
				MemberFunction.of("getBiome", this::getBiome),
				MemberFunction.of("getFullBiome", this::getFullBiome),
				MemberFunction.of("getFullId", this::getFullId),
				MemberFunction.of("getId", this::getId),
				MemberFunction.of("isOf", 1, this::isOf),
				MemberFunction.of("getAge", this::getAge),
				MemberFunction.of("getCustomName", this::getCustomName),
				MemberFunction.of("getEntityUuid", this::getEntityUuid),
				MemberFunction.of("setGlowing", 1, this::setGlowing),
				MemberFunction.of("getDistanceTo", 1, this::getDistanceTo),
				MemberFunction.of("getSquaredDistanceTo", 1, this::getSquaredDistanceTo),
				MemberFunction.of("getNbt", this::getNbt),
				MemberFunction.of("getTranslatedName", this::getTranslatedName),
				MemberFunction.of("getHitbox", this::getHitbox),
				MemberFunction.of("collidesWith", 2, this::collidesWithBlockAtPos)
			);
		}

		@FunctionDoc(
			name = "isSneaking",
			desc = "Returns true if the player is sneaking",
			returns = {BOOLEAN, "true if the player is sneaking, false if not"},
			example = "entity.isSneaking();"
		)
		private Value isSneaking(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isSneaking());
		}

		@FunctionDoc(
			name = "isSprinting",
			desc = "Returns true if the player is sprinting",
			returns = {BOOLEAN, "true if the player is sprinting, false if not"},
			example = "entity.isSprinting();"
		)
		private Value isSprinting(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isSprinting());
		}

		@FunctionDoc(
			name = "isFalling",
			desc = "Returns true if the entity is falling",
			returns = {BOOLEAN, "true if the entity is falling, false if not"},
			example = "entity.isFalling();"
		)
		private Value isFalling(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).fallDistance > 0);
		}

		@FunctionDoc(
			name = "isOnGround",
			desc = "Returns true if the entity is on the ground",
			returns = {BOOLEAN, "true if the entity is on the ground, false if not"},
			example = "entity.isOnGround();"
		)
		private Value isOnGround(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isOnGround());
		}

		@FunctionDoc(
			name = "isTouchingWater",
			desc = "Returns true if the entity is touching water",
			returns = {BOOLEAN, "true if the entity is touching water, false if not"},
			example = "entity.isTouchingWater();"
		)
		private Value isTouchingWater(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isTouchingWater());
		}

		@FunctionDoc(
			name = "isTouchingWaterOrRain",
			desc = "Returns true if the entity is touching water or rain",
			returns = {BOOLEAN, "true if the entity is touching water or rain, false if not"},
			example = "entity.isTouchingWaterOrRain();"
		)
		private Value isTouchingWaterOrRain(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isTouchingWaterOrRain());
		}

		@FunctionDoc(
			name = "isSubmergedInWater",
			desc = "Returns true if the entity is submerged in water",
			returns = {BOOLEAN, "true if the entity is submerged in water, false if not"},
			example = "entity.isSubmergedInWater();"
		)
		private Value isSubmergedInWater(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isSubmergedInWater());
		}

		@FunctionDoc(
			name = "isInLava",
			desc = "Returns true if the entity is in lava",
			returns = {BOOLEAN, "true if the entity is in lava, false if not"},
			example = "entity.isInLava();"
		)
		private Value isInLava(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isInLava());
		}

		@FunctionDoc(
			name = "isOnFire",
			desc = "Returns true if the entity is on fire",
			returns = {BOOLEAN, "true if the entity is on fire, false if not"},
			example = "entity.isOnFire();"
		)
		private Value isOnFire(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isOnFire());
		}

		@FunctionDoc(
			name = "isGlowing",
			desc = "Returns true if the entity is glowing",
			returns = {BOOLEAN, "true if the entity is glowing, false if not"},
			example = "entity.isGlowing();"
		)
		private Value isGlowing(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getEntity(arguments).isGlowing());
		}

		@FunctionDoc(
			name = "getLookingAtBlock",
			desc = {
				"This gets the block that the entity is currently looking at",
				"with a max range of 20 blocks, if there is no block then it will return air"
			},
			returns = {BLOCK, "the block that the entity is looking at, containing the position"},
			example = "entity.getLookingAtBlock();"
		)
		private Value getLookingAtBlock(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			HitResult result = entity.raycast(20D, 0.0F, true);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
				return new BlockValue(entity.world.getBlockState(blockPos), blockPos);
			}
			return new BlockValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
		}

		@FunctionDoc(
			name = "getLookingAtBlock",
			desc = {
				"This gets the block that the entity is currently looking at",
				"with a specific max range, if there is no block then it will return air"
			},
			params = {NUMBER, "maxDistance", "the max range to ray cast"},
			returns = {BLOCK, "the block that the entity is looking at, containing the position"},
			example = "entity.getLookingAtBlock(10);"
		)
		private Value getLookingAtBlock1(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			HitResult result = entity.raycast(numberValue.value, 0.0F, true);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
				return new BlockValue(entity.world.getBlockState(blockPos), blockPos);
			}
			return new BlockValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
		}

		@FunctionDoc(
			name = "getLookingAtBlock",
			desc = {
				"This gets the block that the entity is currently looking at",
				"with a specific max range, and optionally whether fluids should",
				"be included, if there is no block then it will return air"
			},
			params = {
				NUMBER, "maxDistance", "the max range to ray cast",
				STRING, "fluidType", "the types of fluids to include, either 'none', 'sources', or 'all'"
			},
			returns = {BLOCK, "the block that the entity is looking at, containing the position"},
			example = "entity.getLookingAtBlock(10, 'sources');"
		)
		private Value getLookingAtBlock2(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			double maxDistance = arguments.getNextGeneric(NumberValue.class);
			String fluidString = arguments.getNextGeneric(StringValue.class);
			RaycastContext.FluidHandling fluidType = switch (fluidString) {
				case "none" -> RaycastContext.FluidHandling.NONE;
				case "source", "sources", "sources_only" -> RaycastContext.FluidHandling.SOURCE_ONLY;
				case "all", "any" -> RaycastContext.FluidHandling.ANY;
				default -> throw arguments.getError("'%s' is not a valid fluid type", fluidString);
			};
			Vec3d camera = entity.getCameraPosVec(0.0F);
			Vec3d rotation = entity.getRotationVec(0.0F);
			Vec3d end = camera.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
			BlockHitResult result = entity.world.raycast(new RaycastContext(camera, end, RaycastContext.ShapeType.OUTLINE, fluidType, entity));
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = result.getBlockPos();
				return new BlockValue(entity.world.getBlockState(blockPos), blockPos);
			}
			return new BlockValue(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
		}

		@FunctionDoc(
			name = "getLookingAtPos",
			desc = "This gets the position that the entity is currently looking at with a specific max range",
			params = {NUMBER, "maxDistance", "the max range to ray cast"},
			returns = {POS, "the position that the entity is looking at, containing the x, y, and z"},
			example = "entity.getLookingAtPos(10);"
		)
		private Value getLookingAtPos(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			double maxDistance = arguments.getNextGeneric(NumberValue.class);
			Vec3d pos = entity.raycast(maxDistance, 0.0F, true).getPos();
			return new PosValue(pos);
		}

		@FunctionDoc(
			name = "getEntityIdNumber",
			desc = "This gets the entity id number of the entity",
			returns = {NUMBER, "the entity id number"},
			example = "entity.getEntityIdNumber();"
		)
		private Value getEntityIdNumber(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getEntity(arguments).getId());
		}

		@FunctionDoc(
			name = "getPos",
			desc = "This gets the position of the entity",
			returns = {POS, "the position of the entity"},
			example = "entity.getPos();"
		)
		private Value getPos(Arguments arguments) throws CodeError {
			return new PosValue(this.getEntity(arguments).getPos());
		}

		@FunctionDoc(
			name = "getX",
			desc = "This gets the x position of the entity",
			returns = {NUMBER, "the x position of the entity"},
			example = "entity.getX();"
		)
		private Value getX(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getEntity(arguments).getX());
		}

		@FunctionDoc(
			name = "getY",
			desc = "This gets the y position of the entity",
			returns = {NUMBER, "the y position of the entity"},
			example = "entity.getY();"
		)
		private Value getY(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getEntity(arguments).getY());
		}

		@FunctionDoc(
			name = "getZ",
			desc = "This gets the z position of the entity",
			returns = {NUMBER, "the z position of the entity"},
			example = "entity.getZ();"
		)
		private Value getZ(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getEntity(arguments).getZ());
		}

		@FunctionDoc(
			name = "getYaw",
			desc = "This gets the yaw of the entity (horizontal head rotation)",
			returns = {NUMBER, "the yaw of the entity, between -180 and 180"},
			example = "entity.getYaw();"
		)
		private Value getYaw(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			float yaw = entity.getYaw() % 360;
			return NumberValue.of(yaw < -180 ? 360 + yaw : yaw);
		}

		@FunctionDoc(
			name = "getPitch",
			desc = "This gets the pitch of the entity (vertical head rotation)",
			returns = {NUMBER, "the pitch of the entity, between -90 and 90"},
			example = "entity.getPitch();"
		)
		private Value getPitch(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getEntity(arguments).getPitch());
		}

		@FunctionDoc(
			name = "getDimension",
			desc = "This gets the dimension of the entity",
			returns = {STRING, "the dimension id of dimension the entity is in"},
			example = "entity.getDimension();"
		)
		private Value getDimension(Arguments arguments) throws CodeError {
			return StringValue.of(this.getEntity(arguments).getEntityWorld().getRegistryKey().getValue().getPath());
		}

		@FunctionDoc(
			name = "getWorld",
			desc = "This gets the world the entity is in",
			returns = {WORLD, "the world the entity is in"},
			example = "entity.getWorld();"
		)
		private Value getWorld(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			if (entity.world instanceof ClientWorld world) {
				return new WorldValue(world);
			}
			throw arguments.getError("Could not get entity '%s's world", entity);
		}

		@FunctionDoc(
			name = "getBiome",
			desc = "This gets the biome of the entity, this only returns the path, so for example 'plains'",
			returns = {STRING, "the biome id of the biome the entity is in"},
			example = "entity.getBiome();"
		)
		private Value getBiome(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiome(entity.getBlockPos()).getKey();
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().getPath()) : NullValue.NULL;
		}

		@FunctionDoc(
			name = "getFullBiome",
			desc = "This gets the biome of the entity, this returns the full biome id, so for example 'minecraft:plains'",
			returns = {STRING, "the biome id of the biome the entity is in"},
			example = "entity.getFullBiome();"
		)
		private Value getFullBiome(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiome(entity.getBlockPos()).getKey();
			return biomeKey.isPresent() ? StringValue.of(biomeKey.get().getValue().toString()) : NullValue.NULL;
		}

		@FunctionDoc(
			name = "getFullId",
			desc = {
				"This gets the full id of the entity, this returns the full id, so for example",
				"'minecraft:cow' you can find all entityNames on",
				"[Joa's Entity Property Encyclopedia](https://joakimthorsen.github.io/MCPropertyEncyclopedia/entities.html)"
			},
			returns = {STRING, "the full id of the entity"},
			example = "entity.getFullId();"
		)
		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(arguments).getType()).toString());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This gets the id of the entity, this returns the id, so for example 'cow'",
			returns = {STRING, "the id of the entity"},
			example = "entity.getId();"
		)
		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.ENTITY_TYPE.getId(this.getEntity(arguments).getType()).getPath());
		}

		@FunctionDoc(
			name = "isOf",
			desc = "This checks if the entity is of the given entity id",
			params = {STRING, "entityId", "the entity id to check"},
			returns = {BOOLEAN, "true if the entity is of the given entity id"},
			example = "entity.isOf('cow');"
		)
		private Value isOf(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			StringValue stringValue = arguments.getNextString();
			EntityType<?> type = Registry.ENTITY_TYPE.getOrEmpty(ArucasMinecraftExtension.getId(arguments, stringValue.value)).orElse(null);
			return BooleanValue.of(entity.getType() == type);
		}

		@FunctionDoc(
			name = "getAge",
			desc = "This gets the age of the entity in ticks",
			returns = {NUMBER, "the age of the entity in ticks"},
			example = "entity.getAge();"
		)
		private Value getAge(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getEntity(arguments).age);
		}

		@FunctionDoc(
			name = "getCustomName",
			desc = "This gets the custom name of the entity if it has one",
			returns = {STRING, "the custom name of the entity if it has one, otherwise null"},
			example = "entity.getCustomName();"
		)
		private Value getCustomName(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			Text customName = entity.getCustomName();
			return customName == null ? NullValue.NULL : StringValue.of(customName.getString());
		}

		@FunctionDoc(
			name = "getEntityUuid",
			desc = "This gets the uuid of the entity",
			returns = {STRING, "the uuid of the entity"},
			example = "entity.getEntityUuid();"
		)
		private Value getEntityUuid(Arguments arguments) throws CodeError {
			return StringValue.of(this.getEntity(arguments).getUuidAsString());
		}

		@FunctionDoc(
			name = "setGlowing",
			desc = "This sets the entity to either start glowing or stop glowing on the client",
			params = {BOOLEAN, "glowing", "the glowing state"},
			example = "entity.setGlowing(true);"
		)
		private Value setGlowing(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			BooleanValue booleanValue = arguments.getNextBoolean();
			entity.setGlowing(booleanValue.value);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getDistanceTo",
			desc = "This gets the distance between the entity and the other entity",
			params = {ENTITY, "otherEntity", "the other entity"},
			returns = {NUMBER, "the distance between the entities"},
			example = "entity.getDistanceTo(Player.get());"
		)
		private Value getDistanceTo(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			EntityValue<?> otherEntity = arguments.getNext(EntityValue.class);
			return NumberValue.of(entity.distanceTo(otherEntity.value));
		}

		@FunctionDoc(
			name = "getSquaredDistanceTo",
			desc = "This gets the squared distance between the entity and the other entity",
			params = {ENTITY, "otherEntity", "the other entity"},
			returns = {NUMBER, "the squared distance between the entities"},
			example = "entity.getSquaredDistanceTo(Player.get());"
		)
		private Value getSquaredDistanceTo(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			EntityValue<?> otherEntity = arguments.getNext(EntityValue.class);
			return NumberValue.of(entity.squaredDistanceTo(otherEntity.value));
		}

		@FunctionDoc(
			name = "getNbt",
			desc = "This gets the nbt of the entity as a map",
			returns = {MAP, "the nbt of the entity"},
			example = "entity.getNbt();"
		)
		private Value getNbt(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
			ArucasMap mappedNbt = NbtUtils.nbtToMap(arguments.getContext(), nbtCompound, 10);
			return new MapValue(mappedNbt);
		}

		@FunctionDoc(
			name = "getTranslatedName",
			desc = "This gets the translated name of the entity, for example 'minecraft:pig' would return 'Pig' if your language is in english",
			returns = {STRING, "the translated name of the entity"},
			example = "entity.getTranslatedName();"
		)
		private Value getTranslatedName(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			return StringValue.of(I18n.translate(entity.getType().getTranslationKey()));
		}

		@FunctionDoc(
			name = "getHitbox",
			desc = "This gets the hitbox of the entity in a list containing the two corners of the hitbox, the minimum point and the maximum point",
			returns = {LIST, "the hitbox of the entity"},
			example = "entity.getHitbox();"
		)
		private Value getHitbox(Arguments arguments) throws CodeError {
			Entity entity = this.getEntity(arguments);
			Box box = entity.getBoundingBox();
			ArucasList boxList = new ArucasList();
			boxList.add(new PosValue(box.minX, box.minY, box.minZ));
			boxList.add(new PosValue(box.maxX, box.maxY, box.maxZ));
			return new ListValue(boxList);
		}

		@FunctionDoc(
			name = "collidesWith",
			desc = "This checks whether the entity collides with a block at a given position",
			params = {
				POS, "pos", "the position to check",
				BLOCK, "block", "the block to check"
			},
			returns = {BOOLEAN, "whether the entity collides with the block"},
			example = "entity.collidesWith(Pos.get(0, 0, 0), Block.of('minecraft:stone'));"
		)
		private Value collidesWithBlockAtPos(Arguments arguments) throws CodeError {
			// If block is placed at position, will entity collide with it? Then it can't be placed.
			Entity entity = this.getEntity(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockValue blockStateValue = arguments.getNext(BlockValue.class);
			return BooleanValue.of(entity.collidesWithStateAtPos(posValue.toBlockPos(), blockStateValue.value));
		}

		private Entity getEntity(Arguments arguments) throws CodeError {
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			if (entityValue.value == null) {
				throw arguments.getError("Entity was null");
			}
			return entityValue.value;
		}

		@Override
		public Class<? extends Value> getValueClass() {
			return EntityValue.class;
		}
	}
}
