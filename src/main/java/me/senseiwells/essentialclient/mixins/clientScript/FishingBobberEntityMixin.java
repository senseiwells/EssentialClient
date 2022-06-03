package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {
	@Shadow
	public abstract PlayerEntity getPlayerOwner();

	public FishingBobberEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;setVelocity(DDD)V", shift = At.Shift.BEFORE))
	private void onFishBite(TrackedData<?> data, CallbackInfo ci) {
		PlayerEntity playerEntity = this.getPlayerOwner();
		if (playerEntity != null && playerEntity == EssentialUtils.getPlayer()) {
			MinecraftScriptEvents.ON_FISH_BITE.run(c -> ArucasList.arrayListOf(c.convertValue(this)));
		}
	}
}
