package me.senseiwells.essentialclient.mixins.compat;

import com.google.common.collect.Lists;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;
import net.earthcomputer.multiconnect.api.Protocols;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This fixes an issue with multiconnect
 * where throwing items out of the inventory
 * does not work properly, this is super
 * jank, but it works so ¯\_(ツ)_/¯
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Unique
	private List<ItemStack> cachedList;

	@Redirect(method = "clickSlot", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;"))
	private <E> ArrayList<E> onCreateList(int initialArraySize) {
		return (ArrayList<E>) (this.cachedList = Lists.newArrayListWithCapacity(initialArraySize));
	}

	@Inject(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
	private void onClickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if (MultiConnectAPI.instance().getProtocolVersion() <= Protocols.V1_16_5 && actionType == SlotActionType.THROW && slotId >= 0) {
			this.cachedList.set(slotId, ItemStack.EMPTY);
		}
	}
}
