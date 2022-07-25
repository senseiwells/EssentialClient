package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.values.BlockValue;
import me.senseiwells.essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.essentialclient.clientscript.values.RecipeValue;
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
@SuppressWarnings("unused")
public class ClientPlayerInteractionManagerMixin {
	@Final
	@Shadow
	private MinecraftClient client;

	@Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
	private void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (this.client.world != null) {
			if (MinecraftScriptEvents.ON_ATTACK_BLOCK.run(new BlockValue(this.client.world.getBlockState(pos), pos))) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
	private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_ATTACK_ENTITY.run(c -> ArucasList.arrayListOf(c.convertValue(target)))) {
			ci.cancel();
		}
	}

	@Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE))
	private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (this.client.world != null) {
			MinecraftScriptEvents.ON_BLOCK_BROKEN.run(new BlockValue(this.client.world.getBlockState(pos), pos));
		}
	}

	@Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
	private void onClickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_CLICK_SLOT.run(NumberValue.of(slotId), StringValue.of(actionType.name()), new ItemStackValue(slotId < 0 ? ItemStack.EMPTY : player.currentScreenHandler.slots.get(slotId).getStack().copy()))) {
			ci.cancel();
		}
	}

	@Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true)
	private void onClickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_CLICK_RECIPE.run(new RecipeValue(recipe))) {
			ci.cancel();
		}
	}

	@Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
	private void onInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (MinecraftScriptEvents.ON_INTERACT_ITEM.run(new ItemStackValue(itemStack))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		BlockPos pos = hitResult.getBlockPos();
		if (this.client.world != null && MinecraftScriptEvents.ON_INTERACT_BLOCK.run(new BlockValue(this.client.world.getBlockState(pos), pos), new ItemStackValue(player.getStackInHand(hand)))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
	private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (MinecraftScriptEvents.ON_INTERACT_ENTITY.run(c -> ArucasList.arrayListOf(c.convertValue(entity), new ItemStackValue(player.getStackInHand(hand))))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}
}
