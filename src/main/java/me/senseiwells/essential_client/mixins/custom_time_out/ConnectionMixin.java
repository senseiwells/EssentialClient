package me.senseiwells.essential_client.mixins.custom_time_out;

import me.senseiwells.essential_client.EssentialClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.network.Connection$1")
public class ConnectionMixin {
	@ModifyConstant(
		method = "initChannel",
		constant = @Constant(intValue = 30)
	)
	private int getTimeoutSeconds(int constant) {
		return EssentialClientConfig.getInstance().getCustomTimeOut();
	}
}
