package me.senseiwells.essentialclient.mixins.clientScript;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC >= 11700
import net.minecraft.world.EntityList;
//#endif

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {
	//#if MC >= 11700
	@Accessor("entityList")
	EntityList getEntityList();
	//#endif
}

