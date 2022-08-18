package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

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

	@ClassDoc(
		name = OTHER_PLAYER,
		desc = {
			"This class is used to represent all players, mainly other players,",
			"this class extends LivingEntity and so inherits all of their methods too"
		},
		importPath = "Minecraft"
	)
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
				MemberFunction.of("isPlayerSlot", 1, this::isPlayerSlot),
				MemberFunction.of("getItemForSlot", 1, this::getItemForSlot),
				MemberFunction.of("getItemForPlayerSlot", 1, this::getItemForPlayerSlot),
				MemberFunction.of("getSlotFor", 1, this::getSlotFor),
				MemberFunction.of("getAllSlotsFor", 1, this::getAllSlotsFor),
				MemberFunction.of("getAllSlotsFor", 2, this::getAllSlotsForBoolean),
				MemberFunction.of("getAbilities", this::getAbilities),
				MemberFunction.of("getLevels", this::getLevels),
				MemberFunction.of("getHunger", this::getHunger),
				MemberFunction.of("getSaturation", this::getSaturation),
				MemberFunction.of("getFishingBobber", this::getFishingBobber)
			);
		}

		@FunctionDoc(
			name = "getCurrentSlot",
			desc = "This gets the players currently selected slot",
			returns = {NUMBER, "the currently selected slot number"},
			example = "otherPlayer.getCurrentSlot();"
		)
		private Value getCurrentSlot(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getOtherPlayer(arguments).getInventory().selectedSlot);
		}

		@FunctionDoc(
			name = "getHeldItem",
			desc = "This gets the players currently selected item, in their main hand",
			returns = {ITEM_STACK, "the currently selected item"},
			example = "otherPlayer.getHeldItem();"
		)
		private Value getHeldItem(Arguments arguments) throws CodeError {
			return new ItemStackValue(this.getOtherPlayer(arguments).getInventory().getMainHandStack());
		}

		@FunctionDoc(
			name = "isInventoryFull",
			desc = "This gets whether the players inventory is full",
			returns = {BOOLEAN, "whether the inventory is full"},
			example = "otherPlayer.isInventoryFull();"
		)
		private Value isInventoryFull(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getOtherPlayer(arguments).getInventory().getEmptySlot() == -1);
		}

		@FunctionDoc(
			name = "getPlayerName",
			desc = "This gets the players name",
			returns = {STRING, "the players name"},
			example = "otherPlayer.getPlayerName();"
		)
		private Value getPlayerName(Arguments arguments) throws CodeError {
			return StringValue.of(this.getOtherPlayer(arguments).getEntityName());
		}

		@FunctionDoc(
			name = "getGamemode",
			desc = "This gets the players gamemode",
			returns = {STRING, "the players gamemode as a string, null if not known, for example 'creative', 'survival', 'spectator'"},
			example = "otherPlayer.getGamemode();"
		)
		private Value getGamemode(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			PlayerListEntry playerInfo = ArucasMinecraftExtension.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
			if (playerInfo == null || playerInfo.getGameMode() == null) {
				return NullValue.NULL;
			}
			return StringValue.of(playerInfo.getGameMode().getName());
		}

		@FunctionDoc(
			name = "getTotalSlots",
			desc = "This gets the players total inventory slots",
			returns = {NUMBER, "the players total inventory slots"},
			example = "otherPlayer.getTotalSlots();"
		)
		private Value getTotalSlots(Arguments arguments) throws CodeError {
			ScreenHandler screenHandler = this.getOtherPlayer(arguments).currentScreenHandler;
			return NumberValue.of(screenHandler.slots.size());
		}

		@FunctionDoc(
			name = "getItemForSlot",
			desc = "This gets the item in the specified slot, in the total players inventory, including inventories of open containers",
			params = {NUMBER, "slotNum", "the slot number you want to get"},
			returns = {ITEM_STACK, "the item in the specified slot"},
			throwMsgs = "That slot is out of bounds",
			example = "otherPlayer.getItemForSlot(0);"
		)
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

		@FunctionDoc(
			name = "isPlayerSlot",
			desc = "This gets inventory type (player / other) for given slot numbers",
			params = {NUMBER, "slotNum", "the slot number you want to get"},
			returns = {BOOLEAN, "whether slot was player inventory or not"},
			throwMsgs = "That slot is out of bounds",
			example = "otherPlayer.isPlayerSlot(0);"
		)
		private Value isPlayerSlot(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			ScreenHandler screenHandler = playerEntity.currentScreenHandler;
			int index = numberValue.value.intValue();
			if (index > screenHandler.slots.size() || index < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			Slot slot = screenHandler.slots.get(index);
			boolean retVal = slot.inventory instanceof PlayerInventory;
			return BooleanValue.of(retVal);
		}

		@FunctionDoc(
			name = "getItemForPlayerSlot",
			desc = "This gets the item in the specified slot, in the players inventory, not including inventories of open containers",
			params = {NUMBER, "slotNum", "the slot number you want to get"},
			returns = {ITEM_STACK, "the item in the specified slot"},
			throwMsgs = "That slot is out of bounds",
			example = "otherPlayer.getItemForPlayerSlot(0);"
		)
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

		@FunctionDoc(
			name = "getSlotFor",
			desc = "This gets the slot number of the specified item in the players combined inventory",
			params = {MATERIAL_LIKE, "materialLike", "the item or material you want to get the slot of"},
			returns = {NUMBER, "the slot number of the item, null if not found"},
			example = "otherPlayer.getSlotFor(Material.DIAMOND.asItemStack());"
		)
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

		@FunctionDoc(
			name = "getAllSlotsFor",
			desc = "This gets all the slot numbers of the specified item in the players combined inventory",
			params = {MATERIAL_LIKE, "materialLike", "the item or material you want to get the slot of"},
			returns = {LIST, "the slot numbers of the item, empty list if not found"},
			example = "otherPlayer.getAllSlotsFor(Material.DIAMOND);"
		)
		private Value getAllSlotsFor(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			MaterialLike materialLike = arguments.getAnyNext(MaterialLike.class);
			ScreenHandler screenHandler = playerEntity.currentScreenHandler;
			ArucasList slotList = new ArucasList();
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == materialLike.asItem()) {
					slotList.add(NumberValue.of(slot.id));
				}
			}
			return new ListValue(slotList);
		}

		@FunctionDoc(
			name = "getAllSlotsFor",
			desc = "This gets all the slot numbers of the specified item in the players combined or player inventory",
			params = {
				MATERIAL_LIKE, "materialLike", "the item or material you want to get the slot of",
				BOOLEAN, "includeExternalInventory", "whether search should include external inventory"
			},
			returns = {LIST, "the slot numbers of the item, empty list if not found"},
			example = "otherPlayer.getAllSlotsFor(Material.DIAMOND, false);"
		)
		private Value getAllSlotsForBoolean(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			MaterialLike materialLike = arguments.getAnyNext(MaterialLike.class);
			BooleanValue booleanValue = arguments.getNextBoolean();
			ScreenHandler screenHandler = booleanValue.value ? playerEntity.currentScreenHandler : playerEntity.playerScreenHandler;
			ArucasList slotList = new ArucasList();
			for (Slot slot : screenHandler.slots) {
				if (slot.getStack().getItem() == materialLike.asItem()) {
					slotList.add(NumberValue.of(slot.id));
				}
			}
			return new ListValue(slotList);
		}

		@FunctionDoc(
			name = "getAbilities",
			desc = {
				"This gets the abilities of the player in a map",
				"For example:",
				"`{\"invulnerable\": false, \"canFly\": true, \"canBreakBlocks\": true, \"isCreative\": true, \"walkSpeed\": 1.0, \"flySpeed\": 1.2}`"
			},
			returns = {MAP, "the abilities of the player"},
			example = "otherPlayer.getAbilities();"
		)
		private Value getAbilities(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			PlayerAbilities playerAbilities = playerEntity.getAbilities();
			Context context = arguments.getContext();
			ArucasMap map = new ArucasMap();
			map.put(context, StringValue.of("invulnerable"), BooleanValue.of(playerAbilities.invulnerable));
			map.put(context, StringValue.of("canFly"), BooleanValue.of(playerAbilities.allowFlying));
			map.put(context, StringValue.of("canBreakBlocks"), BooleanValue.of(playerAbilities.allowModifyWorld));
			map.put(context, StringValue.of("isCreative"), BooleanValue.of(playerAbilities.creativeMode));
			map.put(context, StringValue.of("walkSpeed"), NumberValue.of(playerAbilities.getWalkSpeed()));
			map.put(context, StringValue.of("flySpeed"), NumberValue.of(playerAbilities.getFlySpeed()));
			return new MapValue(map);
		}

		@FunctionDoc(
			name = "getLevels",
			desc = "This gets the number of experience levels the player has",
			returns = {NUMBER, "the number of experience levels"},
			example = "otherPlayer.getLevels();"
		)
		private Value getLevels(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return NumberValue.of(playerEntity.experienceLevel);
		}

		@FunctionDoc(
			name = "getHunger",
			desc = "This gets the hunger level of the player",
			returns = {NUMBER, "the hunger level"},
			example = "otherPlayer.getHunger();"
		)
		private Value getHunger(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return NumberValue.of(playerEntity.getHungerManager().getFoodLevel());
		}

		@FunctionDoc(
			name = "getSaturation",
			desc = "This gets the saturation level of the player",
			returns = {NUMBER, "the saturation level"},
			example = "otherPlayer.getSaturation();"
		)
		private Value getSaturation(Arguments arguments) throws CodeError {
			AbstractClientPlayerEntity playerEntity = this.getOtherPlayer(arguments);
			return NumberValue.of(playerEntity.getHungerManager().getSaturationLevel());
		}

		@FunctionDoc(
			name = "getFishingBobber",
			desc = "This gets the fishing bobber that the player has",
			returns = {ENTITY, "the fishing bobber entity, null if the player isn't fishing"},
			example = "otherPlayer.getFishingBobber();"
		)
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
