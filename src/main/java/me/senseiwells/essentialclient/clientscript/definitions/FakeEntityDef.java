package me.senseiwells.essentialclient.clientscript.definitions;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ConstructorFunction;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptFakeEntity;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = FAKE_ENTITY,
	desc = "This allows you to create a fake entity which can be rendered in the world.",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class FakeEntityDef extends CreatableDefinition<ScriptFakeEntity> {
	public FakeEntityDef(Interpreter interpreter) {
		super(FAKE_ENTITY, interpreter);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(2, this::construct)
		);
	}

	@ConstructorDoc(
		desc = "Creates a new fake entity",
		params = {
			ENTITY, "entity", "The entity that you want to create into a fake entity",
			WORLD, "world", "The world that the entity is being rendered in"
		},
		examples = "fakeEntity = new FakeEntity();"
	)
	private Unit construct(Arguments arguments) {
		ClassInstance instance = arguments.next();
		Entity entity = arguments.nextPrimitive(EntityDef.class);
		World world = arguments.nextPrimitive(WorldDef.class);
		if (!(world instanceof ClientWorld clientWorld)) {
			throw new RuntimeError("World must be a client world");
		}
		instance.setPrimitive(this, new ScriptFakeEntity(arguments.getInterpreter(), entity, clientWorld));
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setWorld", 1, this::setWorld),
			MemberFunction.of("getWorld", this::getWorld),
			MemberFunction.of("setPos", 1, this::setPos1),
			MemberFunction.of("setPos", 2, this::setPos2),
			MemberFunction.of("getPos", this::getPos),
			MemberFunction.of("setYaw", 1, this::setYaw1),
			MemberFunction.of("setYaw", 2, this::setYaw2),
			MemberFunction.of("getYaw", this::getYaw),
			MemberFunction.of("setBodyYaw", 1, this::setBodyYaw1),
			MemberFunction.of("setBodyYaw", 2, this::setBodyYaw2),
			MemberFunction.of("getBodyYaw", this::getBodyYaw),
			MemberFunction.of("setPitch", 1, this::setPitch1),
			MemberFunction.of("setPitch", 2, this::setPitch2),
			MemberFunction.of("getPitch", this::getPitch),
			MemberFunction.of("updatePosAndRotation", 3, this::updatePosAndRotation3),
			MemberFunction.of("updatePosAndRotation", 4, this::updatePosAndRotation4),
			MemberFunction.of("spawn", this::spawn),
			MemberFunction.of("despawn", this::despawn)
		);
	}

	@FunctionDoc(
		name = "setWorld",
		desc = "Sets the world that the entity is being rendered in",
		params = {WORLD, "world", "The world that the entity is being rendered in"},
		examples = "fakeEntity.setWorld(MinecraftClient.getClient().getWorld());"
	)
	private Void setWorld(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		World world = arguments.nextPrimitive(WorldDef.class);
		if (!(world instanceof ClientWorld clientWorld)) {
			throw new RuntimeError("World must be a client world");
		}
		entity.setWorld(clientWorld);
		return null;
	}

	@FunctionDoc(
		name = "getWorld",
		desc = "Gets the world that the entity is being rendered in",
		returns = {WORLD, "The world that the entity is being rendered in"},
		examples = "world = fakeEntity.getWorld();"
	)
	private World getWorld(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		return entity.getWorld();
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the entity with no interpolation",
		params = {POS, "pos", "The new position of the entity"},
		examples = "fakeEntity.setPos(new Pos(0, 0, 0));"
	)
	private Void setPos1(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		entity.setPos(pos.getVec3d());
		return null;
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the entity",
		params = {
			POS, "pos", "The new position of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		examples = "fakeEntity.setPos(new Pos(0, 0, 0), 0);"
	)
	private Void setPos2(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		int interpolationSteps = arguments.nextPrimitive(NumberDef.class).intValue();
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		entity.setPos(pos.getVec3d(), interpolationSteps);
		return null;
	}

	@FunctionDoc(
		name = "getPos",
		desc = "Gets the position of the entity",
		returns = {POS, "The position of the entity"},
		examples = "pos = fakeEntity.getPos();"
	)
	private ScriptPos getPos(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		return new ScriptPos(entity.getPos());
	}

	@FunctionDoc(
		name = "setYaw",
		desc = "Sets the yaw of the entity with no interpolation",
		params = {NUMBER, "yaw", "The new yaw of the entity"},
		examples = "fakeEntity.setYaw(0);"
	)
	private Void setYaw1(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		float yaw = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.setYaw(yaw);
		return null;
	}

	@FunctionDoc(
		name = "setYaw",
		desc = "Sets the yaw of the entity",
		params = {
			NUMBER, "yaw", "The new yaw of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		examples = "fakeEntity.setYaw(0, 10);"
	)
	private Void setYaw2(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		int interpolationSteps = arguments.nextPrimitive(NumberDef.class).intValue();
		float yaw = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.setYaw(yaw, interpolationSteps);
		return null;
	}

	@FunctionDoc(
		name = "getYaw",
		desc = "Gets the yaw of the entity",
		returns = {NUMBER, "The yaw of the entity"},
		examples = "yaw = fakeEntity.getYaw();"
	)
	private float getYaw(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		return entity.getYaw();
	}

	@FunctionDoc(
		name = "setBodyYaw",
		desc = "Sets the body yaw of the entity with no interpolation",
		params = {NUMBER, "bodyYaw", "The new body yaw of the entity"},
		examples = "fakeEntity.setBodyYaw(0);"
	)
	private Void setBodyYaw1(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		float bodyYaw = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.setBodyYaw(bodyYaw);
		return null;
	}

	@FunctionDoc(
		name = "setBodyYaw",
		desc = "Sets the body yaw of the entity",
		params = {
			NUMBER, "bodyYaw", "The new body yaw of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		examples = "fakeEntity.setBodyYaw(0, 10);"
	)
	private Void setBodyYaw2(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		int interpolationSteps = arguments.nextPrimitive(NumberDef.class).intValue();
		float bodyYaw = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.setBodyYaw(bodyYaw, interpolationSteps);
		return null;
	}

	@FunctionDoc(
		name = "getBodyYaw",
		desc = "Gets the body yaw of the entity",
		returns = {NUMBER, "The body yaw of the entity"},
		examples = "bodyYaw = fakeEntity.getBodyYaw();"
	)
	private float getBodyYaw(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		return entity.getBodyYaw();
	}

	@FunctionDoc(
		name = "setPitch",
		desc = "Sets the pitch of the entity with no interpolation",
		params = {NUMBER, "pitch", "The new pitch of the entity"},
		examples = "fakeEntity.setPitch(0);"
	)
	private Void setPitch1(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		float pitch = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.setPitch(pitch);
		return null;
	}

	@FunctionDoc(
		name = "setPitch",
		desc = "Sets the pitch of the entity",
		params = {
			NUMBER, "pitch", "The new pitch of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		examples = "fakeEntity.setPitch(0, 10);"
	)
	private Void setPitch2(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		int interpolationSteps = arguments.nextPrimitive(NumberDef.class).intValue();
		float pitch = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.setPitch(pitch, interpolationSteps);
		return null;
	}

	@FunctionDoc(
		name = "getPitch",
		desc = "Gets the pitch of the entity",
		returns = {NUMBER, "The pitch of the entity"},
		examples = "pitch = fakeEntity.getPitch();"
	)
	private float getPitch(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		return entity.getPitch();
	}

	@FunctionDoc(
		name = "updatePosAndRotation",
		desc = "Updates the position and rotation of the entity with no interpolation",
		params = {
			POS, "pos", "The new position of the entity",
			NUMBER, "yaw", "The new yaw of the entity",
			NUMBER, "pitch", "The new pitch of the entity"
		},
		examples = "fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0);"
	)
	private Void updatePosAndRotation3(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		float yaw = arguments.nextPrimitive(NumberDef.class).floatValue();
		float pitch = arguments.nextPrimitive(NumberDef.class).floatValue();
		entity.updatePosAndRotation(pos.getVec3d(), yaw, pitch);
		return null;
	}

	@FunctionDoc(
		name = "updatePosAndRotation",
		desc = "Updates the position and rotation of the entity",
		params = {
			POS, "pos", "The new position of the entity",
			NUMBER, "yaw", "The new yaw of the entity",
			NUMBER, "pitch", "The new pitch of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		examples = "fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0, 10);"
	)
	private Void updatePosAndRotation4(Arguments arguments) {
		ScriptFakeEntity entity = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		float yaw = arguments.nextPrimitive(NumberDef.class).floatValue();
		float pitch = arguments.nextPrimitive(NumberDef.class).floatValue();
		int interpolationSteps = arguments.nextPrimitive(NumberDef.class).intValue();
		entity.updatePosAndRotation(pos.getVec3d(), yaw, pitch, interpolationSteps);
		return null;
	}

	@FunctionDoc(
		name = "spawn",
		desc = "Spawns the entity (makes it render in the world)",
		examples = "fakeEntity.spawn();"
	)
	private Void spawn(Arguments arguments) {
		arguments.nextPrimitive(this).spawn();
		return null;
	}

	@FunctionDoc(
		name = "despawn",
		desc = "Despawns the entity (makes it not render in the world)",
		examples = "fakeEntity.despawn();"
	)
	private Void despawn(Arguments arguments) {
		arguments.nextPrimitive(this).despawn();
		return null;
	}
}
