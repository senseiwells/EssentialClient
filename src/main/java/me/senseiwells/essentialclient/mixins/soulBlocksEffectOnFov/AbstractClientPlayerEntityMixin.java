package me.senseiwells.essentialclient.mixins.soulBlocksEffectOnFov;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends LivingEntity {
	@Unique
	private static final Identifier SOUL_SPEED_BOOST_ID = Identifier.ofVanilla("enchantment.soul_speed");

	protected AbstractClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(
		method = "getFovMultiplier",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"
		)
	)
	public void injectedBefore(CallbackInfoReturnable<Float> cir) {
		EntityAttributeInstance genericSpeedAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (genericSpeedAttribute != null && genericSpeedAttribute.getModifier(SOUL_SPEED_BOOST_ID) != null) {
			var enchants = this.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
			var soulSpeed = enchants.getOrThrow(Enchantments.SOUL_SPEED);
			genericSpeedAttribute.removeModifier(SOUL_SPEED_BOOST_ID);
			genericSpeedAttribute.addTemporaryModifier(new EntityAttributeModifier(
				SOUL_SPEED_BOOST_ID,
				0.01 * ClientRules.SOUL_SPEED_FOV_MULTIPLIER.getValue() * (0.03F * (1.0F + EnchantmentHelper.getEquipmentLevel(soulSpeed, this) * 0.35F)),
				EntityAttributeModifier.Operation.ADD_VALUE
			));
		}
	}

	@Inject(
		method = "getFovMultiplier",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D",
			shift = At.Shift.AFTER
		)
	)
	private void injectedAfter(CallbackInfoReturnable<Float> cir) {
		EntityAttributeInstance genericSpeedAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (genericSpeedAttribute != null && genericSpeedAttribute.getModifier(SOUL_SPEED_BOOST_ID) != null) {
			var enchants = this.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
			var soulSpeed = enchants.getOrThrow(Enchantments.SOUL_SPEED);
			genericSpeedAttribute.removeModifier(SOUL_SPEED_BOOST_ID);
			genericSpeedAttribute.addTemporaryModifier(new EntityAttributeModifier(
				SOUL_SPEED_BOOST_ID,
				(0.03F * (1.0F + (float) EnchantmentHelper.getEquipmentLevel(soulSpeed, this) * 0.35F)),
				EntityAttributeModifier.Operation.ADD_VALUE
			));
		}
	}
}
