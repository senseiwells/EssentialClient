package me.senseiwells.essentialclient.mixins.survivalInventoryInCreative;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.interfaces.IScreenInventory;
import me.senseiwells.essentialclient.utils.render.Texts;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin extends Screen implements IScreenInventory {
	@Unique
	private boolean forced;

	protected InventoryScreenMixin(Text title) {
		super(title);
	}

	@Redirect(
		method = "handledScreenTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasCreativeInventory()Z"),
		require = 0
	)
	private boolean hasCreativeInventory(ClientPlayerInteractionManager instance) {
		return !this.forced && instance.hasCreativeInventory();
	}

	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasCreativeInventory()Z"), require = 0)
	private boolean hasCreativeInventoryInit(ClientPlayerInteractionManager instance) {
		return !this.forced && instance.hasCreativeInventory();
	}

	@Inject(
		method = "init",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"
		)
	)
	private void onInit(CallbackInfo ci) {
		if (ClientRules.SURVIVAL_INVENTORY_IN_CREATIVE.getValue() && this.client != null && this.client.interactionManager != null && this.client.player != null && this.client.interactionManager.hasCreativeInventory()) {
			this.addDrawableChild(WidgetHelper.newButton(5, 5, 100, 20, Texts.SWAP_INVENTORY, button -> {
				CreativeInventoryScreen screen = new CreativeInventoryScreen(
					this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()
				);
				((IScreenInventory) screen).essentialclient$setForced();
				this.client.setScreen(screen);
			}));
		}
	}

	@Override
	public void essentialclient$setForced() {
		this.forced = true;
	}
}
