package me.senseiwells.essentialclient.mixins.clientScript;

import com.mojang.authlib.GameProfile;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
		ItemStack heldItem = this.getMainHandStack();
		if (MinecraftScriptEvents.ON_DROP_ITEM.run(new ItemStackValue(heldItem))) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void onChatMessage(String message, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_SEND_MESSAGE.run(StringValue.of(message))) {
			ci.cancel();
		}
	}

	@Inject(method = "closeScreen", at = @At("HEAD"))
	private void onCloseScreen(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLOSE_SCREEN.run(c -> EssentialUtils.arrayListOf(c.convertValue(EssentialUtils.getClient().currentScreen)));
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
		if (MinecraftScriptEvents.ON_PLAYER_LOOK.run(NumberValue.of(parameterYaw), NumberValue.of(newPitch))) {
			return;
		}

		this.setPitch(newPitch);
		this.setYaw(newYaw);

		this.setPitch(MathHelper.clamp(this.getPitch(), -90.0F, 90.0F));
		this.prevPitch += deltaPitch;
		this.prevYaw += deltaYaw;
		this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
		if (this.getVehicle() != null) {
			this.getVehicle().onPassengerLookAround(this);
		}
	}
}
