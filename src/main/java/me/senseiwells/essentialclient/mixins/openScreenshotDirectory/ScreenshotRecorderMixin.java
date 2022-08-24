package me.senseiwells.essentialclient.mixins.openScreenshotDirectory;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {
	@ModifyReceiver(method = "method_1664", at = @At(value = "INVOKE", target = "Ljava/io/File;getAbsolutePath()Ljava/lang/String;", remap = false))
	private static File onFileGetPath(File instance) {
		return ClientRules.OPEN_SCREENSHOT_DIRECTORY.getValue() ? instance.getParentFile() : instance;
	}
}
