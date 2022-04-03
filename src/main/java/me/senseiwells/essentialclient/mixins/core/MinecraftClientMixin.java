package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkHandler;
import me.senseiwells.essentialclient.utils.clientscript.ClientTickSyncer;
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
	private void doAttack() { }

	@Shadow
	private void doItemUse() { }

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		ClientTickSyncer.triggerSync();
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void onLeaveWorld(Screen screen, CallbackInfo ci) {
		MinecraftScriptEvents.ON_DISCONNECT.run();

		CarpetClient.INSTANCE.onDisconnect();

		ChunkClientNetworkHandler.chunkDebugAvailable = false;
		ChunkHandler.clearAllChunks();
		ChunkDebugScreen.chunkGrid = null;
	}

	@Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;close()V"))
	private void onClose(CallbackInfo ci) {
		EssentialClient.saveConfigs();
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
	private void onDisconnect(Screen screen, CallbackInfo ci) {
		ClientScript.INSTANCE.stopAllInstances();
		EssentialClient.saveConfigs();
	}

	@Override
	public void rightClickMouseAccessor() {
		this.execute(this::doItemUse);
	}

	@Override
	public void leftClickMouseAccessor() {
		this.execute(this::doAttack);
	}
}
