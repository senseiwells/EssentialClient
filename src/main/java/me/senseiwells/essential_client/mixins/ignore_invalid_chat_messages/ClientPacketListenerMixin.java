package me.senseiwells.essential_client.mixins.ignore_invalid_chat_messages;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
        method = "handlePlayerChat",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
            remap = false
        ),
        cancellable = true
    )
    private void onHandleInvalidOrderedChat(ClientboundPlayerChatPacket packet, CallbackInfo ci) {
        if (EssentialClientConfig.getInstance().getIgnoreInvalidChatMessages()) {
            this.handleInvalidChat(packet);
            ci.cancel();
        }
    }

    @Inject(
        method = "handlePlayerChat",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V",
            remap = false,
            ordinal = 0
        ),
        cancellable = true
    )
    private void onHandleInvalidChatSignature(ClientboundPlayerChatPacket packet, CallbackInfo ci) {
        if (EssentialClientConfig.getInstance().getIgnoreInvalidChatMessages()) {
            this.handleInvalidChat(packet);
            ci.cancel();
        }
    }

    @Unique
    private void handleInvalidChat(ClientboundPlayerChatPacket packet) {
        Component message = packet.unsignedContent() == null ?
            Component.literal(packet.body().content()) : packet.unsignedContent();
        this.minecraft.getChatListener().handleDisguisedChatMessage(message, packet.chatType());
    }
}
