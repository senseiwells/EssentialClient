package essentialclient.mixins.core;

import com.mojang.brigadier.StringReader;
import essentialclient.utils.command.CommandHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith("/")){
            StringReader reader = new StringReader(message);
            reader.skip();
            int cursor = reader.getCursor();
            String commandName = reader.canRead() ? reader.readUnquotedString() : "";
            reader.setCursor(cursor);
            if (CommandHelper.isClientCommand(commandName)){
                CommandHelper.executeCommand(reader, message);
                ci.cancel();
            }
            if (CommandHelper.tryRunFunctionCommand(message)) {
                ci.cancel();
            }
        }
    }
}
