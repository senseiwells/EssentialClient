package me.senseiwells.essentialclient.mixins.clientScript;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {
	@Accessor("entityList")
	EntityList getEntityList();
}
