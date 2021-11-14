package essentialclient.mixins.functions;

import essentialclient.clientscript.MinecraftEventFunction;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (!MinecraftClient.getInstance().player.equals(this))
            return;
        MinecraftEventFunction.ON_DROP_ITEM.runFunction(List.of(new ItemStackValue(stack)));
    }

    @Inject(method = "eatFood", at = @At("HEAD"))
    private void onEat(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;
        MinecraftEventFunction.ON_EAT.runFunction(List.of(new ItemStackValue(stack)));
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;
        MinecraftEventFunction.ON_DEATH.runFunction(List.of(
            EntityValue.getEntityValue(source.getAttacker()),
            new StringValue(source.getName()),
            new NumberValue(source.getPosition().x),
            new NumberValue(source.getPosition().y),
            new NumberValue(source.getPosition().z)
        ));
    }
}
