package essentialclient.mixins.functions;

import essentialclient.feature.clientscript.MinecraftEventFunction;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;
        String itemName = Registry.ITEM.getId(stack.getItem()).getPath();
        MinecraftEventFunction.ON_DROP_ITEM.runFunction(List.of(new StringValue(itemName)));
    }

    @Inject(method = "eatFood", at = @At("HEAD"))
    private void onEat(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;
        String itemName = Registry.ITEM.getId(stack.getItem()).getPath();
        MinecraftEventFunction.ON_EAT.runFunction(List.of(new StringValue(itemName)));
    }
}
