package me.senseiwells.essentialclient.utils.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

// Taken from ClientCommands
public class ClientEntitySelector {
	private final BiPredicate<Vec3d, Entity> filter;
	private final BiConsumer<Vec3d, List<Entity>> sorter;
	private final int limit;
	private final boolean senderOnly;
	private final Double originX;
	private final Double originY;
	private final Double originZ;
	private boolean isSingleTarget;

	public ClientEntitySelector(BiPredicate<Vec3d, Entity> filter, BiConsumer<Vec3d, List<Entity>> sorter, int limit, boolean senderOnly, Double originX, Double originY, Double originZ) {
		this.filter = filter;
		this.sorter = sorter;
		this.limit = limit;
		this.senderOnly = senderOnly;
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
	}

	public Entity getEntity(ServerCommandSource source) throws CommandSyntaxException {
		List<Entity> entities = this.getEntities(source);
		if (entities.isEmpty()) {
			throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
		}
		if (entities.size() > 1) {
			throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
		}
		return entities.get(0);
	}

	public List<Entity> getEntities(ServerCommandSource source) {
		Vec3d origin = source.getPosition();
		origin = new Vec3d(this.originX == null ? origin.x : this.originX, this.originY == null ? origin.y : this.originY, this.originZ == null ? origin.z : this.originZ);

		if (this.senderOnly) {
			return this.filter.test(origin, source.getEntity()) ? Collections.singletonList(source.getEntity()) : Collections.emptyList();
		}

		List<Entity> entities = new ArrayList<>();
		ClientWorld world = EssentialUtils.getWorld();
		if (world == null) {
			return List.of();
		}

		for (Entity entity : world.getEntities()) {
			if (this.filter.test(origin, entity)) {
				entities.add(entity);
			}
		}

		this.sorter.accept(origin, entities);

		return entities.size() <= this.limit ? entities : entities.subList(0, this.limit);
	}

	public int getLimit() {
		return this.limit;
	}

	public boolean isSingleTarget() {
		return this.isSingleTarget;
	}

	public void setSingleTarget(boolean isSingleTarget) {
		this.isSingleTarget = isSingleTarget;
	}
}
