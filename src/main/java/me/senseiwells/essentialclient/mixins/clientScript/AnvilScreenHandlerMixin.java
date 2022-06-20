package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.values.ItemStackValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow
	private String newItemName;

	@Shadow
	@Final
	private Property levelCost;

	public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "onTakeOutput", at = @At("HEAD"))
	private void onAnvil(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
		if (player instanceof ClientPlayerEntity) {
			MinecraftScriptEvents.ON_ANVIL.run(
				new ItemStackValue(this.input.getStack(0)),
				new ItemStackValue(this.input.getStack(1)),
				new ItemStackValue(stack),
				StringValue.of(this.newItemName),
				NumberValue.of(this.levelCost.get())
			);
		}
	}
}
