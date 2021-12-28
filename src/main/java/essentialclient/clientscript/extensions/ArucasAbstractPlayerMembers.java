package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.AbstractPlayerValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Set;

public class ArucasAbstractPlayerMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.abstractPlayerFunctions;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<AbstractPlayerValue> getValueType() {
		return AbstractPlayerValue.class;
	}

	@Override
	public String getName() {
		return "AbstractPlayerMemberFunctions";
	}

	private final Set<MemberFunction> abstractPlayerFunctions = Set.of(
		new MemberFunction("getCurrentSlot", (context, function) -> new NumberValue(this.getOtherPlayer(context, function).inventory.selectedSlot)),
		new MemberFunction("getHeldItem", (context, function) -> new ItemStackValue(this.getOtherPlayer(context, function).inventory.getMainHandStack())),
		new MemberFunction("isInventoryFull", (context, function) -> BooleanValue.of(this.getOtherPlayer(context, function).inventory.getEmptySlot() == -1)),
		new MemberFunction("getPlayerName", (context, function) -> new StringValue(this.getOtherPlayer(context, function).getEntityName())),
		new MemberFunction("getGamemode", this::getGamemode),
		new MemberFunction("getTotalSlots", this::getTotalSlots),
		new MemberFunction("getItemForSlot", "slot", this::getItemForSlot),
		new MemberFunction("getItemForPlayerSlot","slot", this::getItemForPlayerSlot),
		new MemberFunction("getSlotFor", "itemStack", this::getSlotFor),
		new MemberFunction("getAllSlotsFor", "itemStack", this::getAllSlotsFor),
		new MemberFunction("getAbilities", this::getAbilities),
		new MemberFunction("getLevels", this::getLevels)
	);

	private Value<?> getGamemode(Context context, MemberFunction function) throws CodeError {
		AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
		PlayerListEntry playerInfo = ArucasMinecraftExtension.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
		if (playerInfo == null || playerInfo.getGameMode() == null) {
			return NullValue.NULL;
		}
		return new StringValue(playerInfo.getGameMode().getName());
	}

	private Value<?> getTotalSlots(Context context, MemberFunction function) throws CodeError {
		ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
		return new NumberValue(screenHandler.slots.size());
	}

	private Value<?> getItemForSlot(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
		int index = numberValue.value.intValue();
		if (index > screenHandler.slots.size() || index < 0) {
			throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
		}
		ItemStack itemStack = screenHandler.slots.get(index).getStack();
		return new ItemStackValue(itemStack);
	}

	private Value<?> getSlotFor(Context context, MemberFunction function) throws CodeError {
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
		for (Slot slot : screenHandler.slots) {
			if (slot.getStack().getItem() == itemStackValue.value.getItem()) {
				return new NumberValue(slot.id);
			}
		}
		return NullValue.NULL;
	}
	private Value<?> getItemForPlayerSlot(Context context, MemberFunction function) throws CodeError {
		AbstractClientPlayerEntity player = this.getOtherPlayer(context, function);
		int slot = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
		if (slot < 0 || slot > player.inventory.main.size()){throw new RuntimeError("slot number is not valid", function.syntaxPosition, context); }
		ItemStack itemStack = player.inventory.main.get(slot); //slot order might be mixed but main locates hotbar slot at first
		return new ItemStackValue(itemStack);
	}
	private Value<?> getAllSlotsFor(Context context, MemberFunction function) throws CodeError {
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
		ArucasValueList slotList = new ArucasValueList();
		for (Slot slot : screenHandler.slots) {
			if (slot.getStack().getItem() == itemStackValue.value.getItem()) {
				slotList.add(new NumberValue(slot.id));
			}
		}
		return new ListValue(slotList);
	}

	private Value<?> getAbilities(Context context, MemberFunction function) throws CodeError {
		AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
		PlayerAbilities playerAbilities = playerEntity.abilities;
		ArucasValueMap map = new ArucasValueMap() {{
			put(new StringValue("invulnerable"), BooleanValue.of(playerAbilities.invulnerable));
			put(new StringValue("canFly"), BooleanValue.of(playerAbilities.allowFlying));
			put(new StringValue("canBreakBlocks"), BooleanValue.of(playerAbilities.allowModifyWorld));
			put(new StringValue("isCreative"), BooleanValue.of(playerAbilities.creativeMode));
			put(new StringValue("walkSpeed"), new NumberValue(playerAbilities.getWalkSpeed()));
			put(new StringValue("flySpeed"), new NumberValue(playerAbilities.getFlySpeed()));
		}};
		return new MapValue(map);
	}

	private Value<?> getLevels(Context context, MemberFunction function) throws CodeError {
		AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
		return new NumberValue(playerEntity.experienceLevel);
	}

	private AbstractClientPlayerEntity getOtherPlayer(Context context, MemberFunction function) throws CodeError {
		AbstractPlayerValue<?> player = function.getParameterValueOfType(context, AbstractPlayerValue.class, 0);
		if (player == null) {
			throw new RuntimeError("OtherPlayer was null", function.syntaxPosition, context);
		}
		return player.value;
	}
}
