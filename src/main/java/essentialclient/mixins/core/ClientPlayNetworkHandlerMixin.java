package essentialclient.mixins.core;

import essentialclient.commands.CommandRegister;
import essentialclient.gui.clientrule.*;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.command.PlayerListCommandHelper;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onCommandTree", at = @At("HEAD"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        ClientRuleHelper.serverPacket = packet;
        CommandRegister.registerCommands(ClientCommandManager.DISPATCHER);
        //packet.getCommandTree().getChildren().forEach(commandSourceCommandNode -> System.out.println(commandSourceCommandNode.toString()));
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        //
    }

    @Redirect(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private void checkMessages(InGameHud inGameHud, MessageType type, Text message, UUID sender) {
        if (type == MessageType.SYSTEM && ClientRules.DISABLEOPMESSAGES.getBoolean()) {
            if (message instanceof TranslatableText && !ClientRules.DISABLEJOINLEAVEMESSAGES.getBoolean()) {
                switch (((TranslatableText) message).getKey()) {
                    case "multiplayer.player.joined": case "multiplayer.player.left": case "multiplayer.player.joined.renamed":
                        break;
                    default:
                        return;
                }
            }
            else
                return;
        }
        inGameHud.addChatMessage(type, message, sender);
    }
}
