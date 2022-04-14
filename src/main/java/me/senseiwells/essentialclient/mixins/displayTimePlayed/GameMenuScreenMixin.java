package me.senseiwells.essentialclient.mixins.displayTimePlayed;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.time.LocalDateTime;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {

	protected GameMenuScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {

		LocalDateTime currentTime = LocalDateTime.now();
		Duration duration = Duration.between(EssentialClient.START_TIME, currentTime);

		String draw = DurationFormatUtils.formatDuration(duration.toMillis(), "H:mm:ss", true);
		if (ClientRules.DISPLAY_TIME_PLAYED.getValue()) {
			drawStringWithShadow(matrixStack, this.textRenderer, draw, 8, 8, 16777215);
		}
	}
}
