package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.clientscript.MaterialLike;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.OTHER_PLAYER;

public class OtherPlayerValue extends AbstractPlayerValue<OtherClientPlayerEntity> {
	public OtherPlayerValue(OtherClientPlayerEntity player) {
		super(player);
	}

	@Override
	public EntityValue<OtherClientPlayerEntity> copy(Context context) {
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
			super(OTHER_PLAYER);
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
	 *
	 * @author senseiwells
	 */
	public static class ArucasAbstractPlayerClass extends ArucasClassExtension {
		public ArucasAbstractPlayerClass() {
			super(OTHER_PLAYER);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getCurrentSlot", this::getCurrentSlot),
				MemberFunction.of("getHeldItem", this::getHeldItem),
				MemberFunction.of("isInventoryFull", this::isInventoryFull),
				MemberFunction.of("getPlayerName", this::getPlayerName),
				MemberFunction.of("getGamemode", this::getGamemode),
				MemberFunction.of("getTotalSlots", this::getTotalSlots),
				MemberFunction.of("getItemForSlot", 1, this::getItemForSlot),
				MemberFunction.of("getItemForPlayerSlot", 1, this::getItemForPlayerSlot),
				MemberFunction.of("getSlotFor", 1, this::getSlotFor),
				MemberFunction.of("getAllSlotsFor", 1, this::getAllSlotsFor),
				MemberFunction.of("getAbilities", this::getAbilities),
				MemberFunction.of("getLevels", this::getLevels),
				MemberFunction.of("getHunger", this::getHunger),
				MemberFunction.of("getSaturation", this::getSaturation),
				MemberFunction.of("getFishingBobber", this::getFishingBobber)
			);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getCurrentSlot()</code> <br>
		 * Description: This gets the players currently selected slot <br>
		 * Returns - Number: the currently selected slot number <br>
		 * Example: <code>otherPlayer.getCurrentSlot();</code>
		 */
		private Value getCurrentSlot(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getOtherPlayer(arguments).getInventory().selectedSlot);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getHeldItem()</code> <br>
		 * Description: This gets the players currently selected item, in their main hand <br>
		 * Returns - ItemStack: the currently selected item <br>
		 * Example: <code>otherPlayer.getHeldItem();</code>
		 */
		private Value getHeldItem(Arguments arguments) throws CodeError {
			return new ItemStackValue(this.getOtherPlayer(arguments).getInventory().getMainHandStack());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.isInventoryFull()</code> <br>
		 * Description: This gets whether the players inventory is full <br>
		 * Returns - Boolean: whether the inventory is full <br>
		 * Example: <code>otherPlayer.isInventoryFull();</code>
		 */
		private Value isInventoryFull(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getOtherPlayer(arguments).getInventory().getEmptySlot() == -1);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getPlayerName()</code> <br>
		 * Description: This gets the players name <br>
		 * Returns - String: the players name <br>
		 * Example: <code>otherPlayer.getPlayerName();</code>
		 */
		private Value getPlayerName(Arguments arguments) throws CodeError {
			return StringValue.of(this.getOtherPlayer(arguments).getEntityName());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getGamemode()</code> <br>
		 * Description: This gets the players gamemode <br>
		 * Returns - String/Null: the players gamemode as a string, null if not known,
		 * for example <code>'creative', 'survival', 'spectator'</code> <br>
		 * Example: <code>otherPlayer.getGamemode();</code>
		 */
		private Value getGamemode(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
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
		private Value getTotalSlots(Arguments arguments) throws CodeError {
			ScreenHandler screenHandler = this.getOtherPlayer(arguments).currentScreenHandler;
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
		private Value getItemForSlot(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			ScreenHandler screenHandler = playerEntity.currentScreenHandler;
			int index = numberValue.value.intValue();
			if (index > screenHandler.slots.size() || index < 0) {
				throw arguments.getError("That slot is out of bounds");
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
		private Value getItemForPlayerSlot(Arguments arguments) throws CodeError {
			// This gets the item for a slot in the player's inventory (no screen inventories)
			AbstractClientPlayerEntity player = this.getOtherPlayer(arguments);
			int slot = arguments.getNextGeneric(NumberValue.class).intValue();
			if (slot < 0 || slot > player.getInventory().main.size()) {
				throw arguments.getError("That slot is out of bounds");
			}
			ItemStack itemStack = player.getInventory().main.get(slot);
			return new ItemStackValue(itemStack);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getSlotFor(itemStack)</code> <br>
		 * Description: This gets the slot number of the specified item in the players combined inventory <br>
		 * Parameter - MaterialLike: the item or material you want to get the slot of <br>
		 * Returns - Number/Null: the slot number of the item, null if not found <br>
		 * Example: <code>otherPlayer.getSlotFor(Material.DIAMOND.asItemStack());</code>
		 */
		private Value getSlotFor(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			MaterialLike materialLike = arguments.getAnyNext(MaterialLike.class);
			ScreenHandler screenHandler = playerEntity.currentScreenHandler;
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == materialLike.asItem()) {
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
		private Value getAllSlotsFor(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			ScreenHandler screenHandler = playerEntity.currentScreenHandler;
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
		private Value getAbilities(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			PlayerAbilities playerAbilities = playerEntity.getAbilities();
			Context context = arguments.getContext();
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
		private Value getLevels(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return NumberValue.of(playerEntity.experienceLevel);
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getHunger()</code> <br>
		 * Description: This gets the hunger level of the player <br>
		 * Returns - Number: the hunger level <br>
		 * Example: <code>otherPlayer.getHunger();</code>
		 */
		private Value getHunger(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return NumberValue.of(playerEntity.getHungerManager().getFoodLevel());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getSaturation()</code> <br>
		 * Description: This gets the saturation level of the player <br>
		 * Returns - Number: the saturation level <br>
		 * Example: <code>otherPlayer.getSaturation();</code>
		 */
		private Value getSaturation(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return NumberValue.of(playerEntity.getHungerManager().getSaturationLevel());
		}

		/**
		 * Name: <code>&lt;OtherPlayer>.getFishingBobber()</code> <br>
		 * Description: This gets the fishing bobber that the player has <br>
		 * Returns - Entity/Null: the fishing bobber entity, null if the player isn't fishing <br>
		 * Example: <code>otherPlayer.getFishingBobber();</code>
		 */
		private Value getFishingBobber(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return arguments.getContext().convertValue(playerEntity.fishHook);
		}

		private AbstractClientPlayerEntity getOtherPlayer(Arguments arguments) throws CodeError {
			AbstractPlayerValue<?> player = arguments.getNext(AbstractPlayerValue.class);
			if (player.value == null) {
				throw arguments.getError("OtherPlayer was null");
			}
			return player.value;
		}

		@Override
		public Class<? extends Value> getValueClass() {
			return AbstractPlayerValue.class;
		}
	}
}
