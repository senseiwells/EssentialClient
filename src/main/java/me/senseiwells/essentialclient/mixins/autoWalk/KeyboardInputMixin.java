package me.senseiwells.essentialclient.mixins.autoWalk;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
	@Shadow
	@Final
	private GameOptions settings;

	@Unique
	private int ticks = 0;
	@Unique
	private boolean shouldAutoHold = false;

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(boolean slowDown, float f, CallbackInfo ci) {
		if (this.settings.forwardKey.isPressed()) {
			int autoWalk = ClientRules.AUTO_WALK.getValue();
			this.shouldAutoHold = autoWalk > 0 && this.ticks++ > autoWalk;
			if (this.shouldAutoHold) {
				EssentialUtils.sendMessageToActionBar(Texts.literal("You are now autowalking").formatted(Formatting.GREEN));
			}
			this.pressingForward = true;
		} else {
			this.ticks = 0;
			this.pressingForward = this.shouldAutoHold;
		}

		if (this.settings.backKey.isPressed()) {
			this.ticks = 0;
			this.shouldAutoHold = false;
		}
	}
}
