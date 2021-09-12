package essentialclient.mixins.core;

import essentialclient.EssentialClient;
import essentialclient.feature.AnnounceAFK;
import essentialclient.commands.TravelCommand;
import essentialclient.gui.clientrule.ClientRules;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import net.minecraft.client.MinecraftClient;
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
