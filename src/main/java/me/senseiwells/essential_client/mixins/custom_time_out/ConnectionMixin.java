package me.senseiwells.essential_client.mixins.custom_time_out;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.netty.handler.timeout.ReadTimeoutHandler;
import me.senseiwells.essential_client.EssentialClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.network.Connection$1")
public class ConnectionMixin {
	@Definition(id = "ReadTimeoutHandler", type = ReadTimeoutHandler.class)
	@Expression("new ReadTimeoutHandler(@(30))")
	@ModifyExpressionValue(
		method = "initChannel",
		at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private int getTimeoutSeconds(int constant) {
		return EssentialClientConfig.getInstance().getCustomTimeOut();
	}
}
