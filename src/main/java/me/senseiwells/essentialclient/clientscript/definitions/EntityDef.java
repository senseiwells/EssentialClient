package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
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

import java.util.List;
import java.util.Optional;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = ENTITY,
	desc = "This class is mostly used to get data about entities.",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class EntityDef extends PrimitiveDefinition<Entity> {
	public EntityDef(Interpreter interpreter) {
		super(MinecraftAPI.ENTITY, interpreter);
	}

	@Deprecated
	@Override
	public ClassInstance create(Entity value) {
		return super.create(value);
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return "Entity{id=%s}".formatted(Registry.ENTITY_TYPE.getId(instance.getPrimitive(this).getType()));
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", this::of)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = {
			"This converts an entityId into an entity instance.",
			"This will throw an error if the id is not valid."
		},
		params = {STRING, "entityId", "the entityId to convert to an entity"},
		returns = {ENTITY, "the entity instance from the id"},
		examples = "Entity.of('minecraft:pig');"
	)
	private ClassInstance of(Arguments arguments) {
		ClientWorld world = EssentialUtils.getWorld();
		String string = arguments.nextPrimitive(StringDef.class);
		return arguments.getInterpreter().convertValue(
			Registry.ENTITY_TYPE.getOrEmpty(ClientScriptUtils.stringToIdentifier(string)).orElseThrow(
				() -> new RuntimeError("'%s' is not a valid entity".formatted(string))
			).create(world)
		);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
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
			MemberFunction.of("getVelocity", this::getVelocity),
			MemberFunction.of("getPos", this::getPos),
			MemberFunction.of("getX", this::getX),
			MemberFunction.of("getY", this::getY),
			MemberFunction.of("getZ", this::getZ),
			MemberFunction.of("getYaw", this::getYaw),
			MemberFunction.of("getPitch", this::getPitch),
			MemberFunction.of("getDimension", this::getDimension),
			MemberFunction.of("getWorld", this::getWorld),
			MemberFunction.of("getBiome", this::getBiome),
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
			MemberFunction.of("collidesWith", 2, this::collidesWithBlockAtPos),
			MemberFunction.of("canSpawnAt", 1, this::canSpawnPos)
		);
	}

	@FunctionDoc(
		name = "isSneaking",
		desc = "Returns true if the player is sneaking",
		returns = {BOOLEAN, "true if the player is sneaking, false if not"},
		examples = "entity.isSneaking();"
	)
	private boolean isSneaking(Arguments arguments) {
		return arguments.nextPrimitive(this).isSneaking();
	}

	@FunctionDoc(
		name = "isSprinting",
		desc = "Returns true if the player is sprinting",
		returns = {BOOLEAN, "true if the player is sprinting, false if not"},
		examples = "entity.isSprinting();"
	)
	private boolean isSprinting(Arguments arguments) {
		return arguments.nextPrimitive(this).isSprinting();
	}

	@FunctionDoc(
		name = "isFalling",
		desc = "Returns true if the entity is falling",
		returns = {BOOLEAN, "true if the entity is falling, false if not"},
		examples = "entity.isFalling();"
	)
	private boolean isFalling(Arguments arguments) {
		return arguments.nextPrimitive(this).fallDistance > 0;
	}

	@FunctionDoc(
		name = "isOnGround",
		desc = "Returns true if the entity is on the ground",
		returns = {BOOLEAN, "true if the entity is on the ground, false if not"},
		examples = "entity.isOnGround();"
	)
	private boolean isOnGround(Arguments arguments) {
		return arguments.nextPrimitive(this).isOnGround();
	}

	@FunctionDoc(
		name = "isTouchingWater",
		desc = "Returns true if the entity is touching water",
		returns = {BOOLEAN, "true if the entity is touching water, false if not"},
		examples = "entity.isTouchingWater();"
	)
	private boolean isTouchingWater(Arguments arguments) {
		return arguments.nextPrimitive(this).isTouchingWater();
	}

	@FunctionDoc(
		name = "isTouchingWaterOrRain",
		desc = "Returns true if the entity is touching water or rain",
		returns = {BOOLEAN, "true if the entity is touching water or rain, false if not"},
		examples = "entity.isTouchingWaterOrRain();"
	)
	private boolean isTouchingWaterOrRain(Arguments arguments) {
		return arguments.nextPrimitive(this).isTouchingWaterOrRain();
	}

	@FunctionDoc(
		name = "isSubmergedInWater",
		desc = "Returns true if the entity is submerged in water",
		returns = {BOOLEAN, "true if the entity is submerged in water, false if not"},
		examples = "entity.isSubmergedInWater();"
	)
	private boolean isSubmergedInWater(Arguments arguments) {
		return arguments.nextPrimitive(this).isSubmergedInWater();
	}

	@FunctionDoc(
		name = "isInLava",
		desc = "Returns true if the entity is in lava",
		returns = {BOOLEAN, "true if the entity is in lava, false if not"},
		examples = "entity.isInLava();"
	)
	private boolean isInLava(Arguments arguments) {
		return arguments.nextPrimitive(this).isInLava();
	}

	@FunctionDoc(
		name = "isOnFire",
		desc = "Returns true if the entity is on fire",
		returns = {BOOLEAN, "true if the entity is on fire, false if not"},
		examples = "entity.isOnFire();"
	)
	private boolean isOnFire(Arguments arguments) {
		return arguments.nextPrimitive(this).isOnFire();
	}

	@FunctionDoc(
		name = "isGlowing",
		desc = "Returns true if the entity is glowing",
		returns = {BOOLEAN, "true if the entity is glowing, false if not"},
		examples = "entity.isGlowing();"
	)
	private boolean isGlowing(Arguments arguments) {
		return arguments.nextPrimitive(this).isGlowing();
	}

	@FunctionDoc(
		name = "getLookingAtBlock",
		desc = {
			"This gets the block that the entity is currently looking at",
			"with a max range of 20 blocks, if there is no block then it will return air"
		},
		returns = {BLOCK, "the block that the entity is looking at, containing the position"},
		examples = "entity.getLookingAtBlock();"
	)
	private ScriptBlockState getLookingAtBlock(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		HitResult result = entity.raycast(20D, 0.0F, true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
			return new ScriptBlockState(entity.world.getBlockState(blockPos), blockPos);
		}
		return new ScriptBlockState(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
	}

	@FunctionDoc(
		name = "getLookingAtBlock",
		desc = {
			"This gets the block that the entity is currently looking at",
			"with a specific max range, if there is no block then it will return air"
		},
		params = {NUMBER, "maxDistance", "the max range to ray cast"},
		returns = {BLOCK, "the block that the entity is looking at, containing the position"},
		examples = "entity.getLookingAtBlock(10);"
	)
	private ScriptBlockState getLookingAtBlock1(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		double maxDistance = arguments.nextPrimitive(NumberDef.class);
		HitResult result = entity.raycast(maxDistance, 0.0F, true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
			return new ScriptBlockState(entity.world.getBlockState(blockPos), blockPos);
		}
		return new ScriptBlockState(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
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
		examples = "entity.getLookingAtBlock(10, 'sources');"
	)
	private ScriptBlockState getLookingAtBlock2(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		double maxDistance = arguments.nextPrimitive(NumberDef.class);
		String fluidString = arguments.nextPrimitive(StringDef.class);
		RaycastContext.FluidHandling fluidType = ClientScriptUtils.stringToFluidType(fluidString);
		Vec3d camera = entity.getCameraPosVec(0.0F);
		Vec3d rotation = entity.getRotationVec(0.0F);
		Vec3d end = camera.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
		BlockHitResult result = entity.world.raycast(new RaycastContext(camera, end, RaycastContext.ShapeType.OUTLINE, fluidType, entity));
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = result.getBlockPos();
			return new ScriptBlockState(entity.world.getBlockState(blockPos), blockPos);
		}
		return new ScriptBlockState(Blocks.AIR.getDefaultState(), new BlockPos(result.getPos()));
	}

	@FunctionDoc(
		name = "getLookingAtPos",
		desc = "This gets the position that the entity is currently looking at with a specific max range",
		params = {NUMBER, "maxDistance", "the max range to ray cast"},
		returns = {POS, "the position that the entity is looking at, containing the x, y, and z"},
		examples = "entity.getLookingAtPos(10);"
	)
	private Vec3d getLookingAtPos(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		double maxDistance = arguments.nextPrimitive(NumberDef.class);
		return entity.raycast(maxDistance, 0.0F, true).getPos();
	}

	@FunctionDoc(
		name = "getEntityIdNumber",
		desc = "This gets the entity id number of the entity",
		returns = {NUMBER, "the entity id number"},
		examples = "entity.getEntityIdNumber();"
	)
	private int getEntityIdNumber(Arguments arguments) {
		return arguments.nextPrimitive(this).getId();
	}

	@FunctionDoc(
		name = "getVelocity",
		desc = "This gets the velocity of the entity in a list in the form [x, y, z]",
		returns = {LIST, "the velocity of the entity"},
		examples = "entity.getVelocity();"
	)
	private ArucasList getVelocity(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		Vec3d velocity = entity.getVelocity();
		NumberDef numberDef = arguments.getInterpreter().getPrimitive(NumberDef.class);
		return ArucasList.of(
			numberDef.create(velocity.x),
			numberDef.create(velocity.y),
			numberDef.create(velocity.z)
		);
	}

	@FunctionDoc(
		name = "getPos",
		desc = "This gets the position of the entity",
		returns = {POS, "the position of the entity"},
		examples = "entity.getPos();"
	)
	private Vec3d getPos(Arguments arguments) {
		return arguments.nextPrimitive(this).getPos();
	}

	@FunctionDoc(
		name = "getX",
		desc = "This gets the x position of the entity",
		returns = {NUMBER, "the x position of the entity"},
		examples = "entity.getX();"
	)
	private double getX(Arguments arguments) {
		return arguments.nextPrimitive(this).getX();
	}

	@FunctionDoc(
		name = "getY",
		desc = "This gets the y position of the entity",
		returns = {NUMBER, "the y position of the entity"},
		examples = "entity.getY();"
	)
	private double getY(Arguments arguments) {
		return arguments.nextPrimitive(this).getY();
	}

	@FunctionDoc(
		name = "getZ",
		desc = "This gets the z position of the entity",
		returns = {NUMBER, "the z position of the entity"},
		examples = "entity.getZ();"
	)
	private double getZ(Arguments arguments) {
		return arguments.nextPrimitive(this).getZ();
	}

	@FunctionDoc(
		name = "getYaw",
		desc = "This gets the yaw of the entity (horizontal head rotation)",
		returns = {NUMBER, "the yaw of the entity, between -180 and 180"},
		examples = "entity.getYaw();"
	)
	private float getYaw(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		float yaw = entity.getYaw() % 360;
		return yaw < -180 ? 360 + yaw : yaw;
	}

	@FunctionDoc(
		name = "getPitch",
		desc = "This gets the pitch of the entity (vertical head rotation)",
		returns = {NUMBER, "the pitch of the entity, between -90 and 90"},
		examples = "entity.getPitch();"
	)
	private float getPitch(Arguments arguments) {
		return arguments.nextPrimitive(this).getPitch();
	}

	@FunctionDoc(
		name = "getDimension",
		desc = "This gets the dimension of the entity",
		returns = {STRING, "the dimension id of dimension the entity is in"},
		examples = "entity.getDimension();"
	)
	private String getDimension(Arguments arguments) {
		return arguments.nextPrimitive(this).getEntityWorld().getRegistryKey().getValue().getPath();
	}

	@FunctionDoc(
		name = "getWorld",
		desc = "This gets the world the entity is in",
		returns = {WORLD, "the world the entity is in"},
		examples = "entity.getWorld();"
	)
	private ClientWorld getWorld(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		if (entity.world instanceof ClientWorld world) {
			return world;
		}
		throw new RuntimeError("Could not get entity '%s's world".formatted(entity));
	}

	@FunctionDoc(
		name = "getBiome",
		desc = "This gets the biome of the entity",
		returns = {BIOME, "the biome the entity is in"},
		examples = "entity.getBiome();"
	)
	private Biome getBiome(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		//#if MC >= 11800
		return entity.getEntityWorld().getBiome(entity.getBlockPos()).value();
		//#else
		//$$return entity.getEntityWorld().getBiome(entity.getBlockPos());
		//#endif
	}

	@FunctionDoc(
		name = "getFullId",
		desc = {
			"This gets the full id of the entity, this returns the full id, so for example",
			"'minecraft:cow' you can find all entityNames on",
			"[Joa's Entity Property Encyclopedia](https://joakimthorsen.github.io/MCPropertyEncyclopedia/entities.html)"
		},
		returns = {STRING, "the full id of the entity"},
		examples = "entity.getFullId();"
	)
	private String getFullId(Arguments arguments) {
		return Registry.ENTITY_TYPE.getId(arguments.nextPrimitive(this).getType()).toString();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This gets the id of the entity, this returns the id, so for examples 'cow'",
		returns = {STRING, "the id of the entity"},
		examples = "entity.getId();"
	)
	private String getId(Arguments arguments) {
		return Registry.ENTITY_TYPE.getId(arguments.nextPrimitive(this).getType()).getPath();
	}

	@FunctionDoc(
		name = "isOf",
		desc = "This checks if the entity is of the given entity id",
		params = {STRING, "entityId", "the entity id to check"},
		returns = {BOOLEAN, "true if the entity is of the given entity id"},
		examples = "entity.isOf('cow');"
	)
	private boolean isOf(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		String id = arguments.nextPrimitive(StringDef.class);
		EntityType<?> type = Registry.ENTITY_TYPE.getOrEmpty(ClientScriptUtils.stringToIdentifier(id)).orElse(null);
		return entity.getType() == type;
	}

	@FunctionDoc(
		name = "getAge",
		desc = "This gets the age of the entity in ticks",
		returns = {NUMBER, "the age of the entity in ticks"},
		examples = "entity.getAge();"
	)
	private int getAge(Arguments arguments) {
		return arguments.nextPrimitive(this).age;
	}

	@FunctionDoc(
		name = "getCustomName",
		desc = "This gets the custom name of the entity if it has one",
		returns = {STRING, "the custom name of the entity if it has one, otherwise null"},
		examples = "entity.getCustomName();"
	)
	private String getCustomName(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		Text customName = entity.getCustomName();
		return customName == null ? null : customName.getString();
	}

	@FunctionDoc(
		name = "getEntityUuid",
		desc = "This gets the uuid of the entity",
		returns = {STRING, "the uuid of the entity"},
		examples = "entity.getEntityUuid();"
	)
	private String getEntityUuid(Arguments arguments) {
		return arguments.nextPrimitive(this).getUuidAsString();
	}

	@FunctionDoc(
		name = "setGlowing",
		desc = "This sets the entity to either start glowing or stop glowing on the client",
		params = {BOOLEAN, "glowing", "the glowing state"},
		examples = "entity.setGlowing(true);"
	)
	private Void setGlowing(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		entity.setGlowing(arguments.nextPrimitive(BooleanDef.class));
		return null;
	}

	@FunctionDoc(
		name = "getDistanceTo",
		desc = "This gets the distance between the entity and the other entity",
		params = {ENTITY, "otherEntity", "the other entity"},
		returns = {NUMBER, "the distance between the entities"},
		examples = "entity.getDistanceTo(Player.get());"
	)
	private double getDistanceTo(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		return entity.distanceTo(arguments.nextPrimitive(this));
	}

	@FunctionDoc(
		name = "getSquaredDistanceTo",
		desc = "This gets the squared distance between the entity and the other entity",
		params = {ENTITY, "otherEntity", "the other entity"},
		returns = {NUMBER, "the squared distance between the entities"},
		examples = "entity.getSquaredDistanceTo(Player.get());"
	)
	private double getSquaredDistanceTo(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		return entity.squaredDistanceTo(arguments.nextPrimitive(this));
	}

	@FunctionDoc(
		name = "getNbt",
		desc = "This gets the nbt of the entity as a map",
		returns = {MAP, "the nbt of the entity"},
		examples = "entity.getNbt();"
	)
	private ArucasMap getNbt(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
		return ClientScriptUtils.nbtToMap(arguments.getInterpreter(), nbtCompound, 10);
	}

	@FunctionDoc(
		name = "getTranslatedName",
		desc = "This gets the translated name of the entity, for examples 'minecraft:pig' would return 'Pig' if your language is in english",
		returns = {STRING, "the translated name of the entity"},
		examples = "entity.getTranslatedName();"
	)
	private String getTranslatedName(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		return I18n.translate(entity.getType().getTranslationKey());
	}

	@FunctionDoc(
		name = "getHitbox",
		desc = "This gets the hitbox of the entity in a list containing the two corners of the hitbox, the minimum point and the maximum point",
		returns = {LIST, "the hitbox of the entity"},
		examples = "entity.getHitbox();"
	)
	private ArucasList getHitbox(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		Box box = entity.getBoundingBox();
		PosDef posDef = arguments.getInterpreter().getPrimitive(PosDef.class);
		return ArucasList.of(
			posDef.create(new ScriptPos(box.minX, box.minY, box.minZ)),
			posDef.create(new ScriptPos(box.maxX, box.maxY, box.maxZ))
		);
	}

	@FunctionDoc(
		name = "collidesWith",
		desc = "This checks whether the entity collides with a block at a given position",
		params = {
			POS, "pos", "the position to check",
			BLOCK, "block", "the block to check"
		},
		returns = {BOOLEAN, "whether the entity collides with the block"},
		examples = "entity.collidesWith(Pos.get(0, 0, 0), Block.of('minecraft:stone'));"
	)
	private boolean collidesWithBlockAtPos(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		ScriptBlockState block = arguments.nextPrimitive(BlockDef.class);
		return entity.collidesWithStateAtPos(pos.getBlockPos(), block.state);
	}

	@FunctionDoc(
		name = "canSpawnAt",
		desc = "This checks whether the entity can spawn at given position with regard to light and hitbox",
		params = {
			POS, "pos", "the position to check"
		},
		returns = {BOOLEAN, "whether entity type can spawn at given position"},
		examples = "entity.canSpawnAt(new Pos(0,0,0));"
	)
	private boolean canSpawnPos(Arguments arguments) {
		Entity entity = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return EssentialUtils.canSpawn(EssentialUtils.getWorld(), pos.getBlockPos(), entity.getType());
	}
}
