package me.senseiwells.essential_client.mixins.carpet_always_set_default;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import me.senseiwells.essential_client.EssentialClient;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Shadow public abstract void sendCommand(String command);

	@ModifyExpressionValue(
		method = {"sendCommand", "sendUnsignedCommand"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;parseCommand(Ljava/lang/String;)Lcom/mojang/brigadier/ParseResults;"
		)
	)
	private ParseResults<SharedSuggestionProvider> onSendCommand(
		ParseResults<SharedSuggestionProvider> original,
		String command
	) {
		if (!EssentialClientConfig.getInstance().getCarpetAlwaysSetDefault()) {
			return original;
		}

		List<ParsedCommandNode<SharedSuggestionProvider>> nodes = original.getContext().getNodes();
		if (nodes.size() == 3) {
			String root = nodes.get(0).getRange().get(command);
			String ruleName = nodes.get(1).getRange().get(command);
			String ruleValue = nodes.get(2).getRange().get(command);
			if (EssentialClient.INSTANCE.getCarpetClient().isValidRule(ruleName, root)) {
				this.sendCommand(root + " setDefault " + ruleName + " " + ruleValue);
			}
		}
		return original;
	}
}
