package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.utils.interfaces.IEntityList;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//#if MC >= 11700
import net.minecraft.world.EntityList;
//#else
//$$import net.minecraft.server.world.ServerWorld;
//#endif

//#if MC >= 11700
@Mixin(EntityList.class)
//#else
//$$@Mixin(ServerWorld.class)
//#endif
public class EntityListMixin implements IEntityList {
	@Unique
	final Set<Entity> ALL_ENTITIES = ConcurrentHashMap.newKeySet();

	@Inject(
		//#if MC >= 11700
		method = "add",
		//#else
		//$$method = "loadEntityUnchecked",
		//#endif
		at = @At("TAIL")
	)
	private void onAdd(Entity entity, CallbackInfo ci) {
		this.ALL_ENTITIES.add(entity);
	}

	@Inject(
		//#if MC >= 11700
		method = "remove",
		//#else
		//$$method = "unloadEntity",
		//#endif
		at = @At("TAIL")
	)
	private void onRemove(Entity entity, CallbackInfo ci) {
		this.ALL_ENTITIES.remove(entity);
	}

	@Override
	public Entity[] getAllEntities() {
		return this.ALL_ENTITIES.toArray(Entity[]::new);
	}
}
