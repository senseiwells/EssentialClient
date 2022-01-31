package essentialclient.mixins.clientScript;

import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.values.BlockValue;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.values.NumberValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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
		if (this.client.world == null) {
			return;
		}
		if (MinecraftScriptEvents.ON_ATTACK_BLOCK.run(new BlockValue(this.client.world.getBlockState(pos), pos))) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
	private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_ATTACK_ENTITY.run(EntityValue.of(target))) {
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
	private void onClickSlot(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
		if (MinecraftScriptEvents.ON_CLICK_SLOT.run(NumberValue.of(slotId))) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
	private void onInteractItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (MinecraftScriptEvents.ON_INTERACT_ITEM.run(new ItemStackValue(itemStack))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		BlockPos pos = hitResult.getBlockPos();
		if (MinecraftScriptEvents.ON_INTERACT_BLOCK.run(new BlockValue(world.getBlockState(pos), pos), new ItemStackValue(player.getStackInHand(hand)))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
	private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (MinecraftScriptEvents.ON_INTERACT_ENTITY.run(EntityValue.of(entity), new ItemStackValue(player.getStackInHand(hand)))) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}
}
