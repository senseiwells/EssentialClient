package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class OtherPlayerValue extends AbstractPlayerValue<OtherClientPlayerEntity> {
	public OtherPlayerValue(OtherClientPlayerEntity player) {
		super(player);
	}

	@Override
	public Value<OtherClientPlayerEntity> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "OtherPlayer{name=%s}".formatted(this.value.getEntityName());
	}

	@Override
	public String getTypeName() {
		return "OtherPlayer";
	}

	/**
	 * This is a dummy class so OtherPlayer can be used as
	 * a value otherwise Arucas will not register it.
	 * This must be loaded after AbstractPlayerClass
	 */
	public static class ArucasOtherPlayerClass extends ArucasClassExtension {
		public ArucasOtherPlayerClass() {
			super("OtherPlayer");
		}

		@Override
		public Class<OtherPlayerValue> getValueClass() {
			return OtherPlayerValue.class;
		}
	}

	public static class ArucasAbstractPlayerClass extends ArucasClassExtension {
		public ArucasAbstractPlayerClass() {
			super("OtherPlayer");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getCurrentSlot", this::getCurrentSlot),
				new MemberFunction("getHeldItem", this::getHeldItem),
				new MemberFunction("isInventoryFull", this::isInventoryFull),
				new MemberFunction("getPlayerName", this::getPlayerName),
				new MemberFunction("getGamemode", this::getGamemode),
				new MemberFunction("getTotalSlots", this::getTotalSlots),
				new MemberFunction("getItemForSlot", "slot", this::getItemForSlot),
				new MemberFunction("getItemForPlayerSlot", "slot", this::getItemForPlayerSlot),
				new MemberFunction("getSlotFor", "itemStack", this::getSlotFor),
				new MemberFunction("getAllSlotsFor", "itemStack", this::getAllSlotsFor),
				new MemberFunction("getAbilities", this::getAbilities),
				new MemberFunction("getLevels", this::getLevels),
				new MemberFunction("getHunger", this::getHunger),
				new MemberFunction("getSaturation", this::getSaturation)
			);
		}

		private Value<?> getCurrentSlot(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getOtherPlayer(context, function).getInventory().selectedSlot);
		}

		private Value<?> getHeldItem(Context context, MemberFunction function) throws CodeError {
			return new ItemStackValue(this.getOtherPlayer(context, function).getInventory().getMainHandStack());
		}

		private Value<?> isInventoryFull(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getOtherPlayer(context, function).getInventory().getEmptySlot() == -1);
		}

		private Value<?> getPlayerName(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getOtherPlayer(context, function).getEntityName());
		}

		private Value<?> getGamemode(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			PlayerListEntry playerInfo = ArucasMinecraftExtension.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
			if (playerInfo == null || playerInfo.getGameMode() == null) {
				return NullValue.NULL;
			}
			return StringValue.of(playerInfo.getGameMode().getName());
		}

		private Value<?> getTotalSlots(Context context, MemberFunction function) throws CodeError {
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			return NumberValue.of(screenHandler.slots.size());
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

		private Value<?> getItemForPlayerSlot(Context context, MemberFunction function) throws CodeError {
			// This gets the item for a slot in the player's inventory (no screen inventories)
			AbstractClientPlayerEntity player = this.getOtherPlayer(context, function);
			int slot = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
			if (slot < 0 || slot > player.getInventory().main.size()) {
				throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
			}
			ItemStack itemStack = player.getInventory().main.get(slot);
			return new ItemStackValue(itemStack);
		}

		private Value<?> getSlotFor(Context context, MemberFunction function) throws CodeError {
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == itemStackValue.value.getItem()) {
					return NumberValue.of(slot.id);
				}
			}
			return NullValue.NULL;
		}

		private Value<?> getAllSlotsFor(Context context, MemberFunction function) throws CodeError {
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			ArucasList slotList = new ArucasList();
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == itemStackValue.value.getItem()) {
					slotList.add(NumberValue.of(slot.id));
				}
			}
			return new ListValue(slotList);
		}

		private Value<?> getAbilities(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			PlayerAbilities playerAbilities = playerEntity.getAbilities();
			ArucasMap map = new ArucasMap() {{
				this.put(context, StringValue.of("invulnerable"), BooleanValue.of(playerAbilities.invulnerable));
				this.put(context, StringValue.of("canFly"), BooleanValue.of(playerAbilities.allowFlying));
				this.put(context, StringValue.of("canBreakBlocks"), BooleanValue.of(playerAbilities.allowModifyWorld));
				this.put(context, StringValue.of("isCreative"), BooleanValue.of(playerAbilities.creativeMode));
				this.put(context, StringValue.of("walkSpeed"), NumberValue.of(playerAbilities.getWalkSpeed()));
				this.put(context, StringValue.of("flySpeed"), NumberValue.of(playerAbilities.getFlySpeed()));
			}};
			return new MapValue(map);
		}

		private Value<?> getLevels(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return NumberValue.of(playerEntity.experienceLevel);
		}

		private Value<?> getHunger(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return NumberValue.of(playerEntity.getHungerManager().getFoodLevel());
		}

		private Value<?> getSaturation(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return NumberValue.of(playerEntity.getHungerManager().getSaturationLevel());
		}

		private AbstractClientPlayerEntity getOtherPlayer(Context context, MemberFunction function) throws CodeError {
			AbstractPlayerValue<?> player = function.getParameterValueOfType(context, AbstractPlayerValue.class, 0);
			if (player == null) {
				throw new RuntimeError("OtherPlayer was null", function.syntaxPosition, context);
			}
			return player.value;
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return AbstractPlayerValue.class;
		}
	}
}
