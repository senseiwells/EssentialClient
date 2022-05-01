package me.senseiwells.essentialclient.mixins.openScreenshotDirectory;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {
	@Redirect(method = "method_1664", at = @At(value = "INVOKE", target = "Ljava/io/File;getAbsolutePath()Ljava/lang/String;", remap = false))
	private static String onGetFilePath(File instance) {
		return ClientRules.OPEN_SCREENSHOT_DIRECTORY.getValue() ? instance.getParentFile().getAbsolutePath() : instance.getAbsolutePath();
	}
}
