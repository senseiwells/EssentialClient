package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.misc.Scheduler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Shadow
	@org.jetbrains.annotations.Nullable
	public Screen currentScreen;

	@Shadow
	@Final
	public GameOptions options;

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLIENT_TICK.run();
	}

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	//#if MC >= 11800
	private void onAttack(CallbackInfoReturnable<Boolean> cir) {
		if (MinecraftScriptEvents.ON_ATTACK.run()) {
			cir.setReturnValue(false);
		}
	}
	//#else
	//$$private void onAttack(CallbackInfo ci) {
	//$$	if (MinecraftScriptEvents.ON_ATTACK.run()) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//#endif

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onUse(CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_USE.run()) {
			ci.cancel();
		}
	}

	@Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void onPickBlock(CallbackInfo ci, boolean bl, BlockEntity blockEntity, ItemStack itemStack12) {
		if (MinecraftScriptEvents.ON_PICK_BLOCK.run(itemStack12)) {
			ci.cancel();
		}
	}

	@Inject(method = "joinWorld", at = @At("TAIL"))
	private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
		if (world != null) {
			MinecraftScriptEvents.ON_DIMENSION_CHANGE.run(world);
		}
	}

	@Inject(method = "setScreen", at = @At("HEAD"))
	private void onOpenScreen(Screen screen, CallbackInfo ci) {
		if (screen != null) {
			MinecraftScriptEvents.ON_OPEN_SCREEN.run(screen);
		} else {
			MinecraftScriptEvents.ON_CLOSE_SCREEN.run(this.currentScreen);

			Scheduler.schedule(0, () -> {
				// Needed for attack("hold")
				// Also needs to happen after this may have been set to true
				this.options.attackKey.setPressed(false);
			});
		}
	}
}
