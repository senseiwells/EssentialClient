package essentialclient.mixins.core;

import essentialclient.EssentialClient;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.feature.clientscript.ClientScript;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
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
    private void doAttack() {}

    @Shadow
    private void doItemUse() {}

    @Inject(method = "<init>",at = @At("RETURN"))
    private void loadMe(CallbackInfo ci)
    {
        EssentialClient.noop();
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
		if (ClientRules.ENABLE_SCRIPT_ON_JOIN.getBoolean()) {
			ClientScript.startScript();
		}
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onLeaveWorld(Screen screen, CallbackInfo ci) {
		ClientScript.stopScript();
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
