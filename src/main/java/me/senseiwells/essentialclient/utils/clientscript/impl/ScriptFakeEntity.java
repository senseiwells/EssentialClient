package me.senseiwells.essentialclient.utils.clientscript.impl;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ScriptFakeEntity {
	private static final Map<UUID, IntSet> FAKE_IDS = new HashMap<>();
	private static final AtomicInteger FAKE_ID_COUNTER = new AtomicInteger(Integer.MAX_VALUE);

	private final Entity entity;
	private ClientWorld world;
	private Vec3d pos;
	private float bodyYaw;
	private float yaw;
	private float pitch;

	public ScriptFakeEntity(Interpreter interpreter, Entity entity, ClientWorld world) {
		this.entity = entity.getType().create(world);
		if (this.entity == null) {
			throw new RuntimeError("Could not create fake entity");
		}
		entity.setId(getNextFakeId(interpreter));
		this.world = world;
		this.pos = Vec3d.ZERO;
		this.bodyYaw = this.yaw = this.pitch = 0;
	}

	public void updatePosAndRotation(Vec3d pos, float yaw, float pitch) {
		this.updatePosAndRotation(pos, yaw, pitch, 0);
	}

	public void setWorld(ClientWorld world) {
		this.world = world;
	}

	public ClientWorld getWorld() {
		return this.world;
	}

	public void setPos(Vec3d pos) {
		this.setPos(pos, 0);
	}

	public void setPos(Vec3d pos, int interpolation) {
		this.pos = pos;
		this.updatePosition(interpolation);
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public void setYaw(float yaw) {
		this.setYaw(yaw, 0);
	}

	public void setYaw(float yaw, int interpolation) {
		this.yaw = yaw;
		this.updatePosition(interpolation);
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setBodyYaw(float bodyYaw) {
		this.setBodyYaw(bodyYaw, 0);
	}

	public void setBodyYaw(float bodyYaw, int interpolation) {
		this.bodyYaw = bodyYaw;
		this.updatePosition(interpolation);
	}

	public float getBodyYaw() {
		return this.bodyYaw;
	}

	public void setPitch(float pitch) {
		this.setPitch(pitch, 0);
	}

	public void setPitch(float pitch, int interpolation) {
		this.pitch = pitch;
		this.updatePosition(interpolation);
	}

	public float getPitch() {
		return this.pitch;
	}

	public void updatePosAndRotation(Vec3d pos, float yaw, float pitch, int interpolation) {
		this.pos = pos;
		this.entity.setHeadYaw(yaw);
		this.entity.setPitch(pitch);
		this.updatePosition(interpolation);
	}

	public void spawn() {
		EssentialUtils.getClient().execute(() -> {
			this.entity.refreshPositionAndAngles(this.pos.x, this.pos.y, this.pos.z, this.yaw, this.pitch);
			this.entity.updateTrackedPositionAndAngles(this.pos.x, this.pos.y, this.pos.z, this.yaw, this.pitch, 3, true);
			this.world.addEntity(this.entity.getId(), this.entity);
		});
	}

	public void despawn() {
		this.world.removeEntity(this.entity.getId(), Entity.RemovalReason.DISCARDED);
	}

	private void updatePosition(int interpolation) {
		EssentialUtils.getClient().execute(() -> {
			this.entity.setBodyYaw(this.bodyYaw);
			this.entity.updateTrackedPositionAndAngles(this.pos.x, this.pos.y, this.pos.z, this.yaw, this.pitch, interpolation, interpolation > 0);
		});
	}

	public synchronized static boolean isFakeEntity(int id) {
		for (IntSet set : FAKE_IDS.values()) {
			if (set.contains(id)) {
				return true;
			}
		}
		return false;
	}

	private synchronized static int getNextFakeId(Interpreter interpreter) {
		int id = FAKE_ID_COUNTER.getAndDecrement();
		IntSet fakeIds = FAKE_IDS.computeIfAbsent(interpreter.getProperties().getId(), uuid -> {
			interpreter.getThreadHandler().addShutdownEvent(() -> {
				IntSet ids = FAKE_IDS.get(uuid);
				ClientWorld world = EssentialUtils.getWorld();
				if (ids != null && world != null) {
					EssentialUtils.getClient().execute(() -> {
						//#if MC >= 11800
						ids.forEach(i -> world.removeEntity(i, Entity.RemovalReason.DISCARDED));
						//#else
						//$$ids.forEach((java.util.function.IntConsumer) i -> world.removeEntity(i, Entity.RemovalReason.DISCARDED));
						//#endif
					});
				}
			});
			return new IntOpenHashSet();
		});
		fakeIds.add(id);
		return id;
	}
}
