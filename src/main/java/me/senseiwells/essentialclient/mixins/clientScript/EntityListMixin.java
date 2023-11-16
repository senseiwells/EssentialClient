package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.utils.interfaces.IEntityList;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(EntityList.class)
public class EntityListMixin implements IEntityList {
	@Unique
	final Set<Entity> ALL_ENTITIES = ConcurrentHashMap.newKeySet();

	@Inject(
		method = "add",
		at = @At("TAIL")
	)
	private void onAdd(Entity entity, CallbackInfo ci) {
		this.ALL_ENTITIES.add(entity);
	}

	@Inject(
		method = "remove",
		at = @At("TAIL")
	)
	private void onRemove(Entity entity, CallbackInfo ci) {
		this.ALL_ENTITIES.remove(entity);
	}

	@Override
	public Entity[] essentialclient$getAllEntities() {
		return this.ALL_ENTITIES.toArray(Entity[]::new);
	}
}
