package essentialclient.mixins.core;

import essentialclient.clientscript.ClientScript;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientInvoker {

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	private void doAttack() { }

	@Shadow
	private void doItemUse() { }

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void onLeaveWorld(Screen screen, CallbackInfo ci) {
		ClientScript.getInstance().stopScript();
	}

	@Override
	public void rightClickMouseAccessor() {
		this.doItemUse();
	}

	@Override
	public void leftClickMouseAccessor() {
		this.doAttack();
	}
}
