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

import java.lang.reflect.Method;

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

	/**
	 * OtherPlayer class for Arucas. This class extends LivingEntity and so inherits all of
	 * their methods too, OtherPlayer are any player entities, including the main player <br>
	 * Import the class with <code>import OtherPlayer from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
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
				new MemberFunction("getSaturation", this::getSaturation),
				new MemberFunction("getFishingBobber", this::getFishingBobber)
			);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getCurrentSlot()</code> <br>
		 * Description: This gets the players currently selected slot <br>
		 * Returns - Number: the currently selected slot number <br>
		 * Example: <code>otherPlayer.getCurrentSlot();</code>
		 */
		private Value<?> getCurrentSlot(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getOtherPlayer(context, function).getInventory().selectedSlot);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getHeldItem()</code> <br>
		 * Description: This gets the players currently selected item, in their main hand <br>
		 * Returns - ItemStack: the currently selected item <br>
		 * Example: <code>otherPlayer.getHeldItem();</code>
		 */
		private Value<?> getHeldItem(Context context, MemberFunction function) throws CodeError {
			return new ItemStackValue(this.getOtherPlayer(context, function).getInventory().getMainHandStack());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.isInventoryFull()</code> <br>
		 * Description: This gets whether the players inventory is full <br>
		 * Returns - Boolean: whether the inventory is full <br>
		 * Example: <code>otherPlayer.isInventoryFull();</code>
		 */
		private Value<?> isInventoryFull(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getOtherPlayer(context, function).getInventory().getEmptySlot() == -1);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getPlayerName()</code> <br>
		 * Description: This gets the players name <br>
		 * Returns - String: the players name <br>
		 * Example: <code>otherPlayer.getPlayerName();</code>
		 */
		private Value<?> getPlayerName(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(this.getOtherPlayer(context, function).getEntityName());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getGamemode()</code> <br>
		 * Description: This gets the players gamemode <br>
		 * Returns - String/Null: the players gamemode as a string, null if not known,
		 * for example <code>'creative', 'survival', 'spectator'</code> <br>
		 * Example: <code>otherPlayer.getGamemode();</code>
		 */
		private Value<?> getGamemode(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			PlayerListEntry playerInfo = ArucasMinecraftExtension.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
			if (playerInfo == null || playerInfo.getGameMode() == null) {
				return NullValue.NULL;
			}
			return StringValue.of(playerInfo.getGameMode().getName());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getTotalSlots()</code> <br>
		 * Description: This gets the players total inventory slots <br>
		 * Returns - Number: the players total inventory slots <br>
		 * Example: <code>otherPlayer.getTotalSlots();</code>
		 */
		private Value<?> getTotalSlots(Context context, MemberFunction function) throws CodeError {
			ScreenHandler screenHandler = this.getOtherPlayer(context, function).currentScreenHandler;
			return NumberValue.of(screenHandler.slots.size());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getItemForSlot(slotNum)</code> <br>
		 * Description: This gets the item in the specified slot, in the
		 * total players inventory, including inventories of open containers <br>
		 * Parameter - Number: the slot number you want to get <br>
		 * Returns - ItemStack: the item in the specified slot <br>
		 * Throws - Error: <code>"That slot is out of bounds"</code> if the slot is out of bounds <br>
		 * Example: <code>otherPlayer.getItemForSlot(0);</code>
		 */
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

		/**
		 * Name: <code>&lt;OtherPlayer>.getItemForPlayerSlot(slotNum)</code> <br>
		 * Description: This gets the item in the specified slot, in the
		 * players inventory, not including inventories of open containers <br>
		 * Parameter - Number: the slot number you want to get <br>
		 * Returns - ItemStack: the item in the specified slot <br>
		 * Throws - Error: <code>"That slot is out of bounds"</code> if the slot is out of bounds <br>
		 * Example: <code>otherPlayer.getItemForPlayerSlot(0);</code>
		 */
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

		/**
		 * Name: <code>&lt;OtherPlayer>.getSlotFor(itemStack)</code> <br>
		 * Description: This gets the slot number of the specified item in the players combined inventory <br>
		 * Parameter - ItemStack: the item you want to get the slot of <br>
		 * Returns - Number/Null: the slot number of the item, null if not found <br>
		 * Example: <code>otherPlayer.getSlotFor(Material.DIAMOND.asItemStack());</code>
		 */
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

		/**
		 * Name: <code>&lt;OtherPlayer>.getAllSlotsFor(itemStack)</code> <br>
		 * Description: This gets all the slot numbers of the specified item in the players combined inventory <br>
		 * Parameter - ItemStack: the item you want to get the slot of <br>
		 * Returns - List: the slot numbers of the item, empty list if not found <br>
		 * Example: <code>otherPlayer.getAllSlotsFor(Material.DIAMOND.asItemStack());</code>
		 */
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

		/**
		 * Name: <code>&lt;OtherPlayer>.getAbilities()</code> <br>
		 * Description: This gets the abilities of the player in a map <br>
		 * Returns - Map: the abilities of the player, for example:
		 * <code>{"invulnerable": false, "canFly": true, "canBreakBlocks": true, "isCreative": true, "walkSpeed": 1.0, "flySpeed": 1.2}</code> <br>
		 * Example: <code>otherPlayer.getAbilities();</code>
		 */
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

		/**
		 * Name: <code>&lt;OtherPlayer>.getLevels()</code> <br>
		 * Description: This gets the number of experience levels the player has <br>
		 * Returns - Number: the number of experience levels <br>
		 * Example: <code>otherPlayer.getLevels();</code>
		 */
		private Value<?> getLevels(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return NumberValue.of(playerEntity.experienceLevel);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getHunger()</code> <br>
		 * Description: This gets the hunger level of the player <br>
		 * Returns - Number: the hunger level <br>
		 * Example: <code>otherPlayer.getHunger();</code>
		 */
		private Value<?> getHunger(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return NumberValue.of(playerEntity.getHungerManager().getFoodLevel());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getSaturation()</code> <br>
		 * Description: This gets the saturation level of the player <br>
		 * Returns - Number: the saturation level <br>
		 * Example: <code>otherPlayer.getSaturation();</code>
		 */
		private Value<?> getSaturation(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return NumberValue.of(playerEntity.getHungerManager().getSaturationLevel());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getFishingBobber()</code> <br>
		 * Description: This gets the fishing bobber that the player has <br>
		 * Returns - Entity/Null: the fishing bobber entity, null if the player isn't fishing <br>
		 * Example: <code>otherPlayer.getFishingBobber();</code>
		 */
		private Value<?> getFishingBobber(Context context, MemberFunction function) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(context, function);
			return context.convertValue(playerEntity.fishHook);
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
