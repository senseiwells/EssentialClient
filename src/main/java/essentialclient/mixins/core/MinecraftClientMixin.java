package essentialclient.mixins.core;

import essentialclient.EssentialClient;
import essentialclient.commands.TravelCommand;
import essentialclient.feature.AnnounceAFK;
import essentialclient.feature.AFKLogout;
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
public abstract class MinecraftClientMixin implements MinecraftClientInvoker {

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

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (TravelCommand.enabled)
            TravelCommand.tickTravel();
        if (ClientRules.ANNOUNCE_AFK.getInt() > 0)
            AnnounceAFK.tickAFK(player);
        if (ClientRules.AFK_LOGOUT.getInt() > 199)
             AFKLogout.tickAFK(player);
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
        ClientScript.enabled = ClientRules.ENABLE_SCRIPT_ON_JOIN.getBoolean();
        ClientScript.run();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onLeaveWorld(Screen screen, CallbackInfo ci) {
        ClientScript.enabled = false;
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
