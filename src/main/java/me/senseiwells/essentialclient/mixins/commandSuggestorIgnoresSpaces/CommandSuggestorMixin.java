package me.senseiwells.essentialclient.mixins.commandSuggestorIgnoresSpaces;

import me.senseiwells.essentialclient.rule.ClientRules;
//#if MC < 11901
import net.minecraft.client.gui.screen.CommandSuggestor;
//#else
//$$import net.minecraft.client.gui.screen.ChatInputSuggestor;
//#endif
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC < 11901
@Mixin(CommandSuggestor.class)
//#else
//$$@Mixin(ChatInputSuggestor.class)
//#endif

public abstract class CommandSuggestorMixin {
	//#if MC < 11901
	@Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getCursor()I"))
	private int onGetCursor(TextFieldWidget textFieldWidget) {
		int current = textFieldWidget.getCursor();
		boolean hasSpace = false;
		String message = textFieldWidget.getText();
		if (current == 0 || !message.startsWith("/") || !ClientRules.COMMAND_SUGGESTOR_IGNORES_SPACES.getValue()) {
			return current;
		}
		current--;
		while (message.charAt(current) == ' ') {
			hasSpace = true;
			current--;
		}
		current += hasSpace ? 2 : 1;
		return current;
	}
	//#else
	//$$@Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getCursor()I"))
	//$$private int onGetCursor(TextFieldWidget textFieldWidget) {
	//$$	int current = textFieldWidget.getCursor();
	//$$	boolean hasSpace = false;
	//$$	String message = textFieldWidget.getText();
	//$$	if (current == 0 || !message.startsWith("/") || !ClientRules.COMMAND_SUGGESTOR_IGNORES_SPACES.getValue()) {
	//$$		return current;
	//$$	}
	//$$	current--;
	//$$	while (message.charAt(current) == ' ') {
	//$$		hasSpace = true;
	//$$		current--;
	//$$	}
	//$$	current += hasSpace ? 2 : 1;
	//$$	return current;
	//$$}
	//#endif
}
