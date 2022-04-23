package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
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

@ArucasClass(name = "FakeEntity")
public class FakeEntityWrapper implements IArucasWrappedClass {
	private static final Map<UUID, Set<Integer>> FAKE_IDS = new HashMap<>();
	private static final AtomicInteger ID_COUNTER = new AtomicInteger(Integer.MAX_VALUE);
	private static final NumberValue ZERO = NumberValue.of(0);

	@ArucasMember(assignable = false)
	public EntityValue<?> entity;
	@ArucasMember(assignable = false)
	public WorldValue world;
	@ArucasMember(assignable = false)
	public PosValue pos;
	@ArucasMember(assignable = false)
	public NumberValue bodyYaw;
	@ArucasMember(assignable = false)
	public NumberValue yaw;
	@ArucasMember(assignable = false)
	public NumberValue pitch;

	@ArucasConstructor
	public void constructor(Context context, EntityValue<?> entityValue, WorldValue worldValue) throws CodeError {
		Value<?> value = context.convertValue(entityValue.value.getType().create(worldValue.value));
		if (!(value instanceof EntityValue<?> entity)) {
			throw new RuntimeException("Entity could not be created");
		}
		entity.value.setId(getNextFakeId(context));
		this.entity = entity;
		this.world = worldValue;
		this.pos = new PosValue(0, 0, 0);
		this.bodyYaw = this.yaw = this.pitch = ZERO;
	}

	@ArucasFunction
	public Value<?> setWorld(Context context, WorldValue worldValue) {
		this.world = worldValue;
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> setPos(Context context, PosValue posValue, NumberValue interpolation) {
		this.pos = posValue;
		this.updatePosition(interpolation);
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> setPos(Context context, NumberValue x, NumberValue y, NumberValue z, NumberValue interpolation) {
		return this.setPos(context, new PosValue(x.value, y.value, z.value), interpolation);
	}

	@ArucasFunction
	public Value<?> setPos(Context context, PosValue posValue) {
		return this.setPos(context, posValue, ZERO);
	}

	@ArucasFunction
	public Value<?> setPos(Context context, NumberValue x, NumberValue y, NumberValue z) {
		return this.setPos(context, new PosValue(x.value, y.value, z.value), ZERO);
	}

	@ArucasFunction
	public Value<?> setYaw(Context context, NumberValue yaw, NumberValue interpolation) {
		this.yaw = yaw;
		this.updatePosition(interpolation);
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> setYaw(Context context, NumberValue yaw) {
		return this.setYaw(context, yaw, ZERO);
	}

	@ArucasFunction
	public Value<?> setBodyYaw(Context context, NumberValue yaw, NumberValue interpolation) {
		this.bodyYaw = yaw;
		this.updatePosition(interpolation);
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> setBodyYaw(Context context, NumberValue yaw) {
		return this.setBodyYaw(context, yaw, ZERO);
	}

	@ArucasFunction
	public Value<?> setPitch(Context context, NumberValue pitch, NumberValue interpolation) {
		this.pitch = pitch;
		this.updatePosition(interpolation);
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> setPitch(Context context, NumberValue pitch) {
		return this.setPitch(context, pitch, ZERO);
	}

	@ArucasFunction
	public Value<?> updatePosAndRotation(Context context, PosValue posValue, NumberValue yaw, NumberValue pitch, NumberValue interpolation) {
		this.pos = posValue;
		this.entity.value.setHeadYaw(yaw.value.floatValue());
		this.entity.value.setPitch(pitch.value.floatValue());
		this.updatePosition(interpolation);
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> updatePosAndRotation(Context context, PosValue posValue, NumberValue yaw, NumberValue pitch) {
		return this.updatePosAndRotation(context, posValue, yaw, pitch, ZERO);
	}

	@ArucasFunction
	public Value<?> spawn(Context context) {
		EssentialUtils.getClient().execute(() -> {
			Vec3d pos = this.pos.value;
			float yaw = this.yaw.value.floatValue();
			float pitch = this.pitch.value.floatValue();
			this.entity.value.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
			this.entity.value.updateTrackedPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch, 3, true);
			this.world.value.addEntity(this.entity.value.getId(), this.entity.value);
		});
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value<?> despawn(Context context) {
		EssentialUtils.getClient().execute(() -> {
			this.world.value.removeEntity(this.entity.value.getId(), Entity.RemovalReason.DISCARDED);
		});
		return NullValue.NULL;
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
