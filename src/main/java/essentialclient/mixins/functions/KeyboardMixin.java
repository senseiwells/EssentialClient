package essentialclient.mixins.functions;

import essentialclient.feature.clientscript.MinecraftEventFunction;
import essentialclient.utils.keyboard.KeyboardHelper;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;", shift = At.Shift.BEFORE))
    private void onKey(long window, int key, int scancode, int i, int modifiers, CallbackInfo ci) {
        String keyName = KeyboardHelper.translate(key);
        switch (i) {
            case 0 -> MinecraftEventFunction.ON_KEY_RELEASE.tryRunFunction(List.of(new StringValue(keyName)));
            case 1 -> MinecraftEventFunction.ON_KEY_PRESS.tryRunFunction(List.of(new StringValue(keyName)));
            case 2 -> MinecraftEventFunction.ON_KEY_HOLD.tryRunFunction(List.of(new StringValue(keyName)));
        }
    }
}
