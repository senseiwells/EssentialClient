package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.interfaces.MinecraftClientInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements MinecraftClientInvoker {
	@Shadow
	public ClientPlayerEntity player;

	public MinecraftClientMixin(String string) {
		super(string);
	}

	@Shadow
	private void doItemUse() { }

	@Shadow
	protected abstract boolean doAttack();

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void onLeaveWorld(Screen screen, CallbackInfo ci) {
		MinecraftScriptEvents.ON_DISCONNECT.run();
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
	private void onDisconnect(Screen screen, CallbackInfo ci) {
		ClientScript.INSTANCE.stopAllInstances();
	}

	@Override
	public void essentialclient$rightClick() {
		this.execute(this::doItemUse);
	}

	@Override
	public void essentialclient$leftClick() {
		this.execute(this::doAttack);
	}
}
