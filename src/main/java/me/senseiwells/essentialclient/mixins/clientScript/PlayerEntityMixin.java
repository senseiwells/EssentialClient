package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.values.ItemStackValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(method = "eatFood", at = @At("HEAD"))
	private void onEat(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		if (Objects.equals(MinecraftClient.getInstance().player, this)) {
			MinecraftScriptEvents.ON_EAT.run(new ItemStackValue(stack));
		}
	}
}
