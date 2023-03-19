package me.senseiwells.essentialclient.mixins.clientScript;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
	@Accessor("thrower")
	UUID getThrower();
}
