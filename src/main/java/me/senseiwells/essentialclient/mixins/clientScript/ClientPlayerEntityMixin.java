package me.senseiwells.essentialclient.mixins.clientScript;

import com.mojang.authlib.GameProfile;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	@Shadow public abstract float getPitch(float tickDelta);

	@Shadow public abstract float getYaw(float tickDelta);

	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
		ItemStack heldItem = this.getMainHandStack();
		if (MinecraftScriptEvents.ON_DROP_ITEM.run(heldItem)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "closeScreen", at = @At("HEAD"))
	private void onCloseScreen(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLOSE_SCREEN.run(EssentialUtils.getClient().currentScreen);
	}

	@Override
	public ItemStack eatFood(World world, ItemStack stack, FoodComponent foodComponent) {
		MinecraftScriptEvents.ON_EAT.run(stack);
		return super.eatFood(world, stack, foodComponent);
	}

	@Override
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
		float deltaPitch = (float) cursorDeltaY * 0.15F;
		float deltaYaw = (float) cursorDeltaX * 0.15F;
		float newPitch = this.getPitch() + deltaPitch;
		float newYaw = this.getYaw() + deltaYaw;

		if (this.prevPitch == newPitch && this.prevYaw == newYaw) {
			return;
		}

		float parameterYaw = newYaw % 360;
		if (parameterYaw < -180) {
			parameterYaw += 360;
		}
		if (MinecraftScriptEvents.ON_PLAYER_LOOK.run(parameterYaw, newPitch)) {
			return;
		}

		super.changeLookDirection(cursorDeltaX, cursorDeltaY);
	}
}
