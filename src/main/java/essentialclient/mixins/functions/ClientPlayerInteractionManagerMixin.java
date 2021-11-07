package essentialclient.mixins.functions;

import essentialclient.clientscript.MinecraftEventFunction;
import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
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
import net.minecraft.util.registry.Registry;
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
        MinecraftEventFunction.ON_BLOCK_BROKEN.runFunction(List.of(new BlockStateValue(this.client.world.getBlockState(pos)), new NumberValue(pos.getX()), new NumberValue(pos.getY()), new NumberValue(pos.getZ())));
    }

    @Inject(method = "clickSlot", at = @At("HEAD"))
    private void onClickSlot(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        MinecraftEventFunction.ON_CLICK_SLOT.runFunction(List.of(new NumberValue(slotId)));
    }

    @Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;"), require = 0)
    private TypedActionResult<ItemStack> onInteractItem(ItemStack itemStack, World world, PlayerEntity user, Hand hand) {
        MinecraftEventFunction.ON_INTERACT_ITEM.runFunction(List.of(new ItemStackValue(itemStack)));
        return itemStack.use(world, user, hand);
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = cir.getReturnValue();
        if (result.isAccepted()) {
            MinecraftEventFunction.ON_INTERACT_BLOCK.runFunction(List.of(new BlockStateValue(world.getBlockState(hitResult.getBlockPos()))));
        }
    }

    @Inject(method = "interactEntity", at = @At("TAIL"))
    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        String entityName = Registry.ENTITY_TYPE.getId(entity.getType()).getPath();
        MinecraftEventFunction.ON_INTERACT_ENTITY.runFunction(List.of(new StringValue(entityName)));
    }
}
