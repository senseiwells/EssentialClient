package essentialclient.mixins.keyboardRebinds;

import essentialclient.feature.ClientKeybinds;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@ModifyConstant(method = "onKey", constant = @Constant(intValue = 292))
	private int onKey292(int original) {
		return ClientKeybinds.DEBUG_MENU.getKeyCode();
	}
}
