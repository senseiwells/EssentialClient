package me.senseiwells.essentialclient.mixins.survivalInventoryInCreative;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.interfaces.IScreenInventory;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import me.senseiwells.essentialclient.utils.render.Texts;
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

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin extends Screen implements IScreenInventory {
	@Unique
	private boolean forced;

	protected CreativeInventoryScreenMixin(Text title) {
		super(title);
	}

	@Redirect(
		//#if MC >= 11700
		method = "handledScreenTick",
		//#else
		//$$method = "tick",
		//#endif
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

	@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;setSelectedTab(Lnet/minecraft/item/ItemGroup;)V"))
	private void onInit(CallbackInfo ci) {
		if (ClientRules.SURVIVAL_INVENTORY_IN_CREATIVE.getValue() && this.client != null && this.client.player != null) {
			this.addDrawableChild(WidgetHelper.newButton(5, 5, 100, 20, Texts.SWAP_INVENTORY, button -> {
				InventoryScreen screen = new InventoryScreen(this.client.player);
				((IScreenInventory) screen).setForced();
				this.client.setScreen(screen);
			}));
		}
	}

	@Override
	public void setForced() {
		this.forced = true;
	}
}
