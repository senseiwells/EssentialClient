package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.AbstractPlayerValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArucasAbstractPlayerMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.abstractPlayerFunctions;
	}

	@Override
	public String getName() {
		return "AbstractPlayerMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> abstractPlayerFunctions = Set.of(
		new MemberFunction("getCurrentSlot", (context, function) -> new NumberValue(this.getOtherPlayer(context, function).inventory.selectedSlot)),
		new MemberFunction("getHeldItem", (context, function) -> new ItemStackValue(this.getOtherPlayer(context, function).inventory.getMainHandStack())),
		new MemberFunction("isInventoryFull", (context, function) -> new BooleanValue(this.getOtherPlayer(context, function).inventory.getEmptySlot() == -1)),
		new MemberFunction("getPlayerName", (context, function) -> new StringValue(this.getOtherPlayer(context, function).getEntityName())),
		new MemberFunction("getGamemode", this::getGamemode),

		new MemberFunction("getTotalSlots", (context, function) -> {
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			return new NumberValue(screenHandler.slots.size());
		}),

		new MemberFunction("getItemForSlot", "slot", (context, function) -> {
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			int index = numberValue.value.intValue();
			if (index > screenHandler.slots.size() || index < 0) {
				throw new RuntimeError("That slot is out of bounds", function.startPos, function.endPos, context);
			}
			ItemStack itemStack = screenHandler.slots.get(index).getStack();
			return new ItemStackValue(itemStack);
		}),

		new MemberFunction("getSlotFor", "itemStack", (context, function) -> {
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == itemStackValue.value.getItem()) {
					return new NumberValue(slot.id);
				}
			}
			return new NullValue();
		}),

		new MemberFunction("getAllSlotsFor", "itemStack", (context, function) -> {
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			List<Value<?>> slotList = new ArrayList<>();
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == itemStackValue.value.getItem()) {
					slotList.add(new NumberValue(slot.id));
				}
			}
			return new ListValue(slotList);
		})
	);

	private Value<?> getGamemode(Context context, MemberFunction function) throws CodeError {
		AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
		PlayerListEntry playerInfo = ArucasMinecraftExtension.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
		if (playerInfo == null || playerInfo.getGameMode() == null) {
			return new NullValue();
		}
		return new StringValue(playerInfo.getGameMode().getName());
	}

	private AbstractClientPlayerEntity getOtherPlayer(Context context, MemberFunction function) throws CodeError {
		AbstractPlayerValue<?> player = function.getParameterValueOfType(context, AbstractPlayerValue.class, 0);
		if (player == null) {
			throw new RuntimeError("OtherPlayer was null", function.startPos, function.endPos, context);
		}
		return player.value;
	}
}
