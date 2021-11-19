package essentialclient.mixins.functions;

import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.utils.keyboard.KeyboardHelper;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int i, int modifiers, CallbackInfo ci) {
        String keyName = KeyboardHelper.translateKeyToString(key);
        switch (i) {
            case 0 -> MinecraftScriptEvents.ON_KEY_RELEASE.run(new StringValue(keyName));
            case 1 -> MinecraftScriptEvents.ON_KEY_PRESS.run(new StringValue(keyName));
        }
    }
}
