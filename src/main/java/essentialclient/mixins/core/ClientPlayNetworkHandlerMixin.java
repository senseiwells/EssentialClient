package essentialclient.mixins.core;

import com.mojang.brigadier.CommandDispatcher;
import essentialclient.clientscript.ClientScript;
import essentialclient.commands.CommandRegister;
import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@SuppressWarnings("unchecked")
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "onCommandTree", at = @At("TAIL"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        ClientRuleHelper.serverPacket = packet;
        CommandRegister.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }

    @Redirect(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private void checkMessages(InGameHud inGameHud, MessageType type, Text message, UUID sender) {
        if (type == MessageType.SYSTEM && ClientRules.DISABLE_OP_MESSAGES.getValue())
                return;
        if (message instanceof TranslatableText && ClientRules.DISABLE_JOIN_LEAVE_MESSAGES.getValue()) {
            switch (((TranslatableText) message).getKey()) {
                case "multiplayer.player.joined": case "multiplayer.player.left": case "multiplayer.player.joined.renamed":
                    return;
            }
        }
        inGameHud.addChatMessage(type, message, sender);
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (ClientRules.ENABLE_SCRIPT_ON_JOIN.getValue()) {
            ClientScript.getInstance().startScript();
        }
    }
}
