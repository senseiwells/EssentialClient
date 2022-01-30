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
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Final
	@Shadow
	private MinecraftClient client;

	@Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE))
	private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (this.client.world == null) {
			return;
		}
		MinecraftScriptEvents.ON_BLOCK_BROKEN.run(List.of(new BlockValue(this.client.world.getBlockState(pos), pos)));
	}

	@Inject(method = "clickSlot", at = @At("HEAD"))
	private void onClickSlot(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
		MinecraftScriptEvents.ON_CLICK_SLOT.run(NumberValue.of(slotId));
	}

	@Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;"), require = 0)
	private TypedActionResult<ItemStack> onInteractItem(ItemStack itemStack, World world, PlayerEntity user, Hand hand) {
		MinecraftScriptEvents.ON_INTERACT_ITEM.run(new ItemStackValue(itemStack));
		return itemStack.use(world, user, hand);
	}

	@Inject(method = "interactBlock", at = @At("RETURN"))
	private void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		ActionResult result = cir.getReturnValue();
		if (result.isAccepted()) {
			BlockPos pos = hitResult.getBlockPos();
			MinecraftScriptEvents.ON_INTERACT_BLOCK.run(new BlockValue(world.getBlockState(pos), pos));
		}
	}

	@Inject(method = "interactEntity", at = @At("TAIL"))
	private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		MinecraftScriptEvents.ON_INTERACT_ENTITY.run(EntityValue.of(entity));
	}
}
