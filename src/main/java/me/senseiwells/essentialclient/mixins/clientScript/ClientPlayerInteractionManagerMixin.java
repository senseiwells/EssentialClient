package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Final
	@Shadow
	private MinecraftClient client;

	@Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
	private void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (this.client.world != null) {
			if (MinecraftScriptEvents.ON_ATTACK_BLOCK.run(new ScriptBlockState(this.client.world.getBlockState(pos), pos))) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
	private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_ATTACK_ENTITY.run(target)) {
			ci.cancel();
		}
	}

	@Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE))
	private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (this.client.world != null) {
			MinecraftScriptEvents.ON_BLOCK_BROKEN.run(new ScriptBlockState(this.client.world.getBlockState(pos), pos));
		}
	}

	@Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
	private void onClickSlot(
		int syncId,
		int slotId,
		int button,
		SlotActionType actionType,
		PlayerEntity player,
		//#if MC >= 11700
		CallbackInfo ci
		//#else
		//$$CallbackInfoReturnable<ItemStack> cir
		//#endif
	) {
		if (MinecraftScriptEvents.ON_CLICK_SLOT.run(slotId, actionType.name(), new ScriptItemStack(slotId < 0 ? ItemStack.EMPTY : player.currentScreenHandler.slots.get(slotId).getStack().copy()))) {
			//#if MC >= 11700
			ci.cancel();
			//#else
			//$$cir.setReturnValue(ItemStack.EMPTY);
			//#endif
		}
	}

	@Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true)
	private void onClickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_CLICK_RECIPE.run(recipe)) {
			ci.cancel();
		}
	}

	@Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
	//#if MC >= 11900
	private void onInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		//#else
		//$$private void onInteractItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		//#endif
		ItemStack itemStack = player.getStackInHand(hand);
		if (MinecraftScriptEvents.ON_INTERACT_ITEM.run(new ScriptItemStack(itemStack))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	//#if MC >= 11900
	private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		//#else
		//$$private void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		//#endif
		BlockPos pos = hitResult.getBlockPos();
		if (this.client.world != null && MinecraftScriptEvents.ON_INTERACT_BLOCK.run(new ScriptBlockState(this.client.world.getBlockState(pos), pos), new ScriptItemStack(player.getStackInHand(hand)))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
	private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (MinecraftScriptEvents.ON_INTERACT_ENTITY.run(entity, new ScriptItemStack(player.getStackInHand(hand)))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}
}
