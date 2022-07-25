package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.clientscript.values.EntityValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.clientscript.values.WorldValue;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@SuppressWarnings("unused")
@ClassDoc(
	name = FAKE_ENTITY,
	desc = "This allows you to create a fake entity which can be rendered in the world.",
	importPath = "Minecraft"
)
@ArucasClass(name = FAKE_ENTITY)
public class FakeEntityWrapper implements IArucasWrappedClass {
	private static final Map<UUID, Set<Integer>> FAKE_IDS = new HashMap<>();
	private static final AtomicInteger ID_COUNTER = new AtomicInteger(Integer.MAX_VALUE);
	private static final NumberValue ZERO = NumberValue.of(0);

	@MemberDoc(
		name = "entity",
		desc = "The entity that is being rendered",
		type = ENTITY,
		examples = "fakeEntity.entity;"
	)
	@ArucasMember(assignable = false)
	public EntityValue<?> entity;

	@MemberDoc(
		name = "world",
		desc = "The world that the entity is being rendered in",
		type = WORLD,
		examples = "fakeEntity.world;"
	)
	@ArucasMember(assignable = false)
	public WorldValue world;

	@MemberDoc(
		name = "pos",
		desc = "The position of the entity",
		type = POS,
		examples = "fakeEntity.pos;"
	)
	@ArucasMember(assignable = false)
	public PosValue pos;

	@MemberDoc(
		name = "bodyYaw",
		desc = "The yaw of the entity",
		type = NUMBER,
		examples = "fakeEntity.bodyYaw;"
	)
	@ArucasMember(assignable = false)
	public NumberValue bodyYaw;

	@MemberDoc(
		name = "yaw",
		desc = "The yaw of the entity",
		type = NUMBER,
		examples = "fakeEntity.yaw;"
	)
	@ArucasMember(assignable = false)
	public NumberValue yaw;

	@MemberDoc(
		name = "pitch",
		desc = "The pitch of the entity",
		type = NUMBER,
		examples = "fakeEntity.pitch;"
	)
	@ArucasMember(assignable = false)
	public NumberValue pitch;

	@ConstructorDoc(
		desc = "Creates a new fake entity",
		params = {
			ENTITY, "entity", "The entity that you want to create into a fake entity",
			WORLD, "world", "The world that the entity is being rendered in"
		},
		example = "fakeEntity = new FakeEntity();"
	)
	@ArucasConstructor
	public void constructor(Context context, EntityValue<?> entityValue, WorldValue worldValue) throws CodeError {
		Value value = context.convertValue(entityValue.value.getType().create(worldValue.value));
		if (!(value instanceof EntityValue<?> valueEntity)) {
			throw new RuntimeException("Entity could not be created");
		}
		valueEntity.value.setId(getNextFakeId(context));
		this.entity = valueEntity;
		this.world = worldValue;
		this.pos = new PosValue(0, 0, 0);
		this.bodyYaw = this.yaw = this.pitch = ZERO;
	}

	@FunctionDoc(
		name = "setWorld",
		desc = "Sets the world that the entity is being rendered in",
		params = {WORLD, "world", "The world that the entity is being rendered in"},
		example = "fakeEntity.setWorld(MinecraftClient.getClient().getWorld());"
	)
	@ArucasFunction
	public void setWorld(Context context, WorldValue worldValue) {
		this.world = worldValue;
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the entity",
		params = {
			POS, "pos", "The new position of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		example = "fakeEntity.setPos(new Pos(0, 0, 0), 0);"
	)
	@ArucasFunction
	public void setPos(Context context, PosValue posValue, NumberValue interpolation) {
		this.pos = posValue;
		this.updatePosition(interpolation);
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the entity",
		params = {
			NUMBER, "x", "The new x position of the entity",
			NUMBER, "y", "The new y position of the entity",
			NUMBER, "z", "The new z position of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		example = "fakeEntity.setPos(0, 0, 0, 10);"
	)
	@ArucasFunction
	public void setPos(Context context, NumberValue x, NumberValue y, NumberValue z, NumberValue interpolation) {
		this.setPos(context, new PosValue(x.value, y.value, z.value), interpolation);
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the entity with no interpolation",
		params = {POS, "pos", "The new position of the entity"},
		example = "fakeEntity.setPos(new Pos(0, 0, 0));"
	)
	@ArucasFunction
	public void setPos(Context context, PosValue posValue) {
		this.setPos(context, posValue, ZERO);
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the entity with no interpolation",
		params = {
			NUMBER, "x", "The new x position of the entity",
			NUMBER, "y", "The new y position of the entity",
			NUMBER, "z", "The new z position of the entity"
		},
		example = "fakeEntity.setPos(0, 0, 0);"
	)
	@ArucasFunction
	public void setPos(Context context, NumberValue x, NumberValue y, NumberValue z) {
		this.setPos(context, new PosValue(x.value, y.value, z.value), ZERO);
	}

	@FunctionDoc(
		name = "setYaw",
		desc = "Sets the yaw of the entity",
		params = {
			NUMBER, "yaw", "The new yaw of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		example = "fakeEntity.setYaw(0, 10);"
	)
	@ArucasFunction
	public void setYaw(Context context, NumberValue yaw, NumberValue interpolation) {
		this.yaw = yaw;
		this.updatePosition(interpolation);
	}

	@FunctionDoc(
		name = "setYaw",
		desc = "Sets the yaw of the entity with no interpolation",
		params = {NUMBER, "yaw", "The new yaw of the entity"},
		example = "fakeEntity.setYaw(0);"
	)
	@ArucasFunction
	public void setYaw(Context context, NumberValue yaw) {
		this.setYaw(context, yaw, ZERO);
	}

	@FunctionDoc(
		name = "setBodyYaw",
		desc = "Sets the body yaw of the entity",
		params = {
			NUMBER, "bodyYaw", "The new body yaw of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		example = "fakeEntity.setBodyYaw(0, 10);"
	)
	@ArucasFunction
	public void setBodyYaw(Context context, NumberValue yaw, NumberValue interpolation) {
		this.bodyYaw = yaw;
		this.updatePosition(interpolation);
	}

	@FunctionDoc(
		name = "setBodyYaw",
		desc = "Sets the body yaw of the entity with no interpolation",
		params = {NUMBER, "bodyYaw", "The new body yaw of the entity"},
		example = "fakeEntity.setBodyYaw(0);"
	)
	@ArucasFunction
	public void setBodyYaw(Context context, NumberValue yaw) {
		this.setBodyYaw(context, yaw, ZERO);
	}

	@FunctionDoc(
		name = "setPitch",
		desc = "Sets the pitch of the entity",
		params = {
			NUMBER, "pitch", "The new pitch of the entity",
			NUMBER, "interpolationSteps", "The number of interpolation steps to take"
		},
		example = "fakeEntity.setPitch(0, 10);"
	)
	@ArucasFunction
	public void setPitch(Context context, NumberValue pitch, NumberValue interpolation) {
		this.pitch = pitch;
		this.updatePosition(interpolation);
	}

	@FunctionDoc(
		name = "setPitch",
		desc = "Sets the pitch of the entity with no interpolation",
		params = {NUMBER, "pitch", "The new pitch of the entity"},
		example = "fakeEntity.setPitch(0);"
	)
	@ArucasFunction
	public void setPitch(Context context, NumberValue pitch) {
		this.setPitch(context, pitch, ZERO);
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
		example = "fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0, 10);"
	)
	@ArucasFunction
	public void updatePosAndRotation(Context context, PosValue posValue, NumberValue yaw, NumberValue pitch, NumberValue interpolation) {
		this.pos = posValue;
		this.entity.value.setHeadYaw(yaw.value.floatValue());
		this.entity.value.setPitch(pitch.value.floatValue());
		this.updatePosition(interpolation);
	}

	@FunctionDoc(
		name = "updatePosAndRotation",
		desc = "Updates the position and rotation of the entity with no interpolation",
		params = {
			POS, "pos", "The new position of the entity",
			NUMBER, "yaw", "The new yaw of the entity",
			NUMBER, "pitch", "The new pitch of the entity"
		},
		example = "fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0);"
	)
	@ArucasFunction
	public void updatePosAndRotation(Context context, PosValue posValue, NumberValue yaw, NumberValue pitch) {
		this.updatePosAndRotation(context, posValue, yaw, pitch, ZERO);
	}

	@FunctionDoc(
		name = "spawn",
		desc = "Spawns the entity (makes it render in the world)",
		example = "fakeEntity.spawn();"
	)
	@ArucasFunction
	public void spawn(Context context) {
		EssentialUtils.getClient().execute(() -> {
			Vec3d pos = this.pos.value;
			float yaw = this.yaw.value.floatValue();
			float pitch = this.pitch.value.floatValue();
			this.entity.value.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
			this.entity.value.updateTrackedPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch, 3, true);
			this.world.value.addEntity(this.entity.value.getId(), this.entity.value);
		});
	}

	@FunctionDoc(
		name = "despawn",
		desc = "Despawns the entity (makes it not render in the world)",
		example = "fakeEntity.despawn();"
	)
	@ArucasFunction
	public void despawn(Context context) {
		EssentialUtils.getClient().execute(() -> {
			this.world.value.removeEntity(this.entity.value.getId(), Entity.RemovalReason.DISCARDED);
		});
	}

	private void updatePosition(NumberValue interpolationValue) {
		int interpolation = interpolationValue.value.intValue();
		Vec3d pos = this.pos.value;
		float yaw = this.yaw.value.floatValue();
		float pitch = this.pitch.value.floatValue();
		EssentialUtils.getClient().execute(() -> {
			this.entity.value.setBodyYaw(this.bodyYaw.value.floatValue());
			this.entity.value.updateTrackedPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch, interpolation, interpolation > 0);
		});
	}

	public synchronized static boolean isFakeEntity(int id) {
		for (Set<Integer> idSet : FAKE_IDS.values()) {
			if (idSet.contains(id)) {
				return true;
			}
		}
		return false;
	}

	private synchronized static int getNextFakeId(Context context) {
		int id = ID_COUNTER.getAndDecrement();
		Set<Integer> fakeIdSet = FAKE_IDS.computeIfAbsent(context.getContextId(), uuid -> {
			context.getThreadHandler().addShutdownEvent(() -> {
				Set<Integer> fakeIds = FAKE_IDS.remove(context.getContextId());
				ClientWorld world = EssentialUtils.getWorld();
				if (fakeIds != null && world != null) {
					EssentialUtils.getClient().execute(() -> fakeIds.forEach(fakeId -> world.removeEntity(fakeId, Entity.RemovalReason.DISCARDED)));
				}
			});
			return new HashSet<>();
		});
		fakeIdSet.add(id);
		return id;
	}
}
