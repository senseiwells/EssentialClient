package essentialclient.mixins.clientScript;

import com.mojang.authlib.GameProfile;
import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.values.ItemStackValue;
import essentialclient.clientscript.values.ScreenValue;
import essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
		ItemStack heldItem = this.getMainHandStack();
		if (MinecraftScriptEvents.ON_DROP_ITEM.run(new ItemStackValue(heldItem))) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void onChatMessage(String message, CallbackInfo ci) {
		if (MinecraftScriptEvents.ON_SEND_MESSAGE.run(StringValue.of(message))) {
			ci.cancel();
		}
	}

	@Inject(method = "closeScreen", at = @At("HEAD"))
	private void onCloseScreen(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLOSE_SCREEN.run(ScreenValue.of(EssentialUtils.getClient().currentScreen));
	}
}
