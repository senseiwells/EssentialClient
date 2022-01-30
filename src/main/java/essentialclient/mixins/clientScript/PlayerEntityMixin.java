package essentialclient.mixins.clientScript;

import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

	@Inject(method = "onDeath", at = @At("HEAD"))
	private void onDeath(DamageSource source, CallbackInfo ci) {
		if (Objects.equals(MinecraftClient.getInstance().player, this)) {
			MinecraftScriptEvents.ON_DEATH.run(EntityValue.of(source.getAttacker()), StringValue.of(source.getName()));
		}
	}
}
