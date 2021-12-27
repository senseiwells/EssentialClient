package essentialclient.mixins.disableNarrator;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@ModifyConstant(method = "onKey", constant = @Constant(intValue = 66), require = 0)
	private int disableNarrator(int old) {
		if (ClientRules.DISABLE_NARRATOR.getValue()) {
			return -1;
		}
		return old;
	}
}
