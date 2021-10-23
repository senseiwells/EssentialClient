package essentialclient.mixins.disableFovChangeInWater;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyConstant(method = "getFov", constant = @Constant(doubleValue = 60D))
	private double onSubmergedFov(double old) {
		return ClientRules.DISABLE_FOV_CHANGE_IN_WATER.getBoolean() ? 70D : old;
	}
}
