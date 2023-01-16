package me.senseiwells.essentialclient.mixins.clientNick;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC >= 11700
import net.minecraft.client.render.entity.EntityRendererFactory;
//#else
//$$import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRendererMixin( // Checkstyle ignore
									  //#if MC >= 11700
									  EntityRendererFactory.Context context,
									  //#else
									  //$$EntityRenderDispatcher context,
									  //#endif
									  PlayerEntityModel<AbstractClientPlayerEntity> model,
									  float shadowRadius
	) {
		super(context, model, shadowRadius);
	}

	@Redirect(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1))
	private void onRenderLabel(LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> livingEntityRenderer, Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			String playerName = entity.getEntityName();
			String newPlayerName = ConfigClientNick.INSTANCE.get(playerName);
			text = newPlayerName != null ? Texts.literal(newPlayerName) : text;
		}
		super.renderLabelIfPresent((AbstractClientPlayerEntity) entity, text, matrices, vertexConsumers, light);
	}
}
