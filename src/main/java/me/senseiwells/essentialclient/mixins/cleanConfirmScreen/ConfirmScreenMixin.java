package me.senseiwells.essentialclient.mixins.cleanConfirmScreen;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConfirmScreen.class)
public class ConfirmScreenMixin {
	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/MultilineText;create(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/StringVisitable;I)Lnet/minecraft/client/font/MultilineText;"))
	private MultilineText onDisplayMessage(TextRenderer renderer, StringVisitable text, int width) {
		String message = text.getString();
		if (message.length() > 120) {
			text = new LiteralText(message.substring(0, 120) + "...");
		}
		return MultilineText.create(renderer, text, width);
	}
}
