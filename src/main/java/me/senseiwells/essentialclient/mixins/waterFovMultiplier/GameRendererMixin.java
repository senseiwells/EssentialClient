package me.senseiwells.essentialclient.mixins.waterFovMultiplier;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC < 11700
//$$import org.spongepowered.asm.mixin.injection.Constant;
//$$import org.spongepowered.asm.mixin.injection.ModifyConstant;
//#endif

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	//#if MC >= 11900
	@Redirect(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D"))
	private double onLerp(double delta, double start, double end) {
		return MathHelper.lerp(0.01 * ClientRules.WATER_FOV_MULTIPLIER.getValue() * MinecraftClient.getInstance().options.getFovEffectScale().getValue(), 1.0F, 0.85714287F);
	}
	//#elseif MC >= 11700
	//$$@Redirect(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 1))
	//$$private float onLerp(float delta, float start, float end) {
	//$$	return (float) MathHelper.lerp(0.01 * ClientRules.WATER_FOV_MULTIPLIER.getValue() * MinecraftClient.getInstance().options.fovEffectScale, 1.0F, 0.85714287F);
	//$$}
	//#else
	//$$@ModifyConstant(method = "getFov", constant = @Constant(doubleValue = 60D))
	//$$private double onSubmergedFov(double old) {
	//$$	return 0.1 * (100 - ClientRules.WATER_FOV_MULTIPLIER.getValue()) + old;
	//$$}
	//#endif
}
