package me.senseiwells.essentialclient.mixins.clientScript;

import com.mojang.authlib.GameProfile;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.mapping.EntityHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

//#if MC >= 11901 && MC < 11903
//$$import net.minecraft.network.encryption.PlayerPublicKey;
//$$import net.minecraft.text.Text;
//#endif

//#if MC < 11903
//$$import me.senseiwells.essentialclient.clientscript.core.ClientScriptIO;
//#endif

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	//#if MC >= 11901 && MC < 11903
	//$$public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, PlayerPublicKey publicKey) {
	//$$	super(world, pos, yaw, gameProfile, publicKey);
	//$$}
	//#else
	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}
	//#endif

	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
		ItemStack heldItem = this.getMainHandStack();
		if (MinecraftScriptEvents.ON_DROP_ITEM.run(heldItem)) {
			cir.setReturnValue(false);
		}
	}

	//#if MC == 11902
	//$$@Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
	//$$public void onChatMessage(String message, Text preview, CallbackInfo ci) {
	//$$	if (ClientScriptIO.INSTANCE.submitInput(message) || MinecraftScriptEvents.ON_SEND_MESSAGE.run(message)) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//$$@Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
	//$$public void onCommandMessage(String message, Text preview, CallbackInfo ci) {
	//$$	if (ClientScriptIO.INSTANCE.submitInput(message) || MinecraftScriptEvents.ON_SEND_MESSAGE.run(message)) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//#elseif MC < 11902
	//$$@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	//$$public void onChatMessage(String message, CallbackInfo ci) {
	//$$	if (ClientScriptIO.INSTANCE.submitInput(message) || MinecraftScriptEvents.ON_SEND_MESSAGE.run(message)) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//#endif

	@Inject(method = "closeScreen", at = @At("HEAD"))
	private void onCloseScreen(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLOSE_SCREEN.run(EssentialUtils.getClient().currentScreen);
	}

	@Override
	public ItemStack eatFood(World world, ItemStack stack) {
		MinecraftScriptEvents.ON_EAT.run(stack);
		return super.eatFood(world, stack);
	}

	@Override
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
		float deltaPitch = (float) cursorDeltaY * 0.15F;
		float deltaYaw = (float) cursorDeltaX * 0.15F;
		float newPitch = EntityHelper.getEntityPitch(this) + deltaPitch;
		float newYaw = EntityHelper.getEntityYaw(this) + deltaYaw;

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
