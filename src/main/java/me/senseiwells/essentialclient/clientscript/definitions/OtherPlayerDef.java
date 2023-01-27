package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.LocatableTrace;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.mapping.PlayerHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = OTHER_PLAYER,
	desc = {
		"This class is used to represent all players, mainly other players,",
		"this class extends LivingEntity and so inherits all of their methods too"
	},
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class OtherPlayerDef extends PrimitiveDefinition<AbstractClientPlayerEntity> {
	public OtherPlayerDef(Interpreter interpreter) {
		super(OTHER_PLAYER, interpreter);
	}

	@Deprecated
	@NotNull
	@Override
	public ClassInstance create(@NotNull AbstractClientPlayerEntity value) {
		return super.create(value);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super AbstractClientPlayerEntity> superclass() {
		return this.getPrimitiveDef(LivingEntityDef.class);
	}

	@NotNull
	@Override
	public String toString$Arucas(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return "OtherPlayer{name=%s}".formatted(instance.asPrimitive(this).getEntityName());
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getCurrentSlot", this::getCurrentSlot),
			MemberFunction.of("getHeldItem", this::getHeldItem),
			MemberFunction.of("isInventoryFull", this::isInventoryFull),
			MemberFunction.of("getEmptySlots", this::getEmptySlots),
			MemberFunction.of("getPlayerName", this::getPlayerName),
			MemberFunction.of("getGamemode", this::getGamemode),
			MemberFunction.of("getTotalSlots", this::getTotalSlots),
			MemberFunction.of("isPlayerSlot", 1, this::isPlayerSlot),
			MemberFunction.of("getItemForSlot", 1, this::getItemForSlot),
			MemberFunction.of("getItemForPlayerSlot", 1, this::getItemForPlayerSlot),
			MemberFunction.of("getSlotFor", 1, this::getSlotFor),
			MemberFunction.of("getAllSlotsFor", 1, this::getAllSlotsFor),
			MemberFunction.of("getAllSlotsFor", 2, this::getAllSlotsForWithOption),
			MemberFunction.of("getAbilities", this::getAbilities),
			MemberFunction.of("getLevels", this::getLevels),
			MemberFunction.of("getXpProgress", this::getXpProgress),
			MemberFunction.of("getNextLevelExperience", this::getNextLevelExperience),
			MemberFunction.of("getHunger", this::getHunger),
			MemberFunction.of("getSaturation", this::getSaturation),
			MemberFunction.of("getFishingBobber", this::getFishingBobber)
		);
	}

	@FunctionDoc(
		name = "getCurrentSlot",
		desc = "This gets the players currently selected slot",
		returns = {NUMBER, "the currently selected slot number"},
		examples = "otherPlayer.getCurrentSlot();"
	)
	private double getCurrentSlot(Arguments arguments) {
		return PlayerHelper.getPlayerInventory(arguments.nextPrimitive(this)).selectedSlot;
	}

	@FunctionDoc(
		name = "getHeldItem",
		desc = "This gets the players currently selected item, in their main hand",
		returns = {ITEM_STACK, "the currently selected item"},
		examples = "otherPlayer.getHeldItem();"
	)
	private ItemStack getHeldItem(Arguments arguments) {
		return PlayerHelper.getPlayerInventory(arguments.nextPrimitive(this)).getMainHandStack();
	}

	@FunctionDoc(
		name = "isInventoryFull",
		desc = "This gets whether the players inventory is full",
		returns = {BOOLEAN, "whether the inventory is full"},
		examples = "otherPlayer.isInventoryFull();"
	)
	private boolean isInventoryFull(Arguments arguments) {
		return PlayerHelper.getPlayerInventory(arguments.nextPrimitive(this)).getEmptySlot() == -1;
	}

	@FunctionDoc(
		name = "getEmptySlots",
		desc = "This gets all the empty slots in the player inventory",
		returns = {LIST, "a list of all the slot numbers that are empty"},
		examples = "otherPlayer.getEmptySlots();"
	)
	private ArucasList getEmptySlots(Arguments arguments) {
		PlayerInventory inventory = PlayerHelper.getPlayerInventory(arguments.nextPrimitive(this));
		ArucasList list = new ArucasList();
		for (int i = 0; i < inventory.main.size(); ++i) {
			if (inventory.main.get(i).isEmpty()) {
				list.add(arguments.getInterpreter().convertValue(i));
			}
		}
		return list;
	}

	@FunctionDoc(
		name = "getPlayerName",
		desc = "This gets the players name",
		returns = {STRING, "the players name"},
		examples = "otherPlayer.getPlayerName();"
	)
	private String getPlayerName(Arguments arguments) {
		return arguments.nextPrimitive(this).getEntityName();
	}

	@FunctionDoc(
		name = "getGamemode",
		desc = "This gets the players gamemode, may be null if not known",
		returns = {STRING, "the players gamemode as a string, for example 'creative', 'survival', 'spectator'"},
		examples = "otherPlayer.getGamemode();"
	)
	private String getGamemode(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		PlayerListEntry playerInfo = EssentialUtils.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
		if (playerInfo == null || playerInfo.getGameMode() == null) {
			return null;
		}
		return playerInfo.getGameMode().getName();
	}

	@FunctionDoc(
		name = "getTotalSlots",
		desc = "This gets the players total inventory slots",
		returns = {NUMBER, "the players total inventory slots"},
		examples = "otherPlayer.getTotalSlots();"
	)
	private double getTotalSlots(Arguments arguments) {
		ScreenHandler screenHandler = arguments.nextPrimitive(this).currentScreenHandler;
		return screenHandler.slots.size();
	}

	@FunctionDoc(
		name = "getItemForSlot",
		desc = {
			"This gets the item in the specified slot, in the total players inventory, including inventories of open containers.",
			"This will throw an error if the index is out of bounds"
		},
		params = {NUMBER, "slotNum", "the slot number you want to get"},
		returns = {ITEM_STACK, "the item in the specified slot"},
		examples = "otherPlayer.getItemForSlot(0);"
	)
	private ItemStack getItemForSlot(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		int index = arguments.nextPrimitive(NumberDef.class).intValue();
		ScreenHandler screenHandler = playerEntity.currentScreenHandler;
		if (index > screenHandler.slots.size() || index < 0) {
			throw new RuntimeError("That slot is out of bounds");
		}
		return screenHandler.slots.get(index).getStack();
	}

	@FunctionDoc(
		name = "isPlayerSlot",
		desc = {
			"This gets inventory type (player / other) for given slot numbers.",
			"This will throw an error if the index is out of bounds"
		},
		params = {NUMBER, "slotNum", "the slot number you want to get"},
		returns = {BOOLEAN, "whether slot was player inventory or not"},
		examples = "otherPlayer.isPlayerSlot(0);"
	)
	private boolean isPlayerSlot(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		int index = arguments.nextPrimitive(NumberDef.class).intValue();
		ScreenHandler screenHandler = playerEntity.currentScreenHandler;
		if (index > screenHandler.slots.size() || index < 0) {
			throw new RuntimeError("That slot is out of bounds");
		}
		Slot slot = screenHandler.slots.get(index);
		return slot.inventory instanceof PlayerInventory;
	}

	@FunctionDoc(
		name = "getItemForPlayerSlot",
		desc = {
			"This gets the item in the specified slot, in the players inventory, not including inventories of open containers.",
			"This will throw an error if the slot is out of bounds"
		},
		params = {NUMBER, "slotNum", "the slot number you want to get"},
		returns = {ITEM_STACK, "the item in the specified slot"},
		examples = "otherPlayer.getItemForPlayerSlot(0);"
	)
	private ItemStack getItemForPlayerSlot(Arguments arguments) {
		// This gets the item for a slot in the player's inventory (no screen inventories)
		AbstractClientPlayerEntity player = arguments.nextPrimitive(this);
		PlayerInventory inventory = PlayerHelper.getPlayerInventory(player);
		int slot = arguments.nextPrimitive(NumberDef.class).intValue();
		if (slot < 0 || slot > inventory.main.size()) {
			throw new RuntimeError("That slot is out of bounds");
		}
		return inventory.main.get(slot);
	}

	@FunctionDoc(
		name = "getSlotFor",
		desc = "This gets the slot number of the specified item in the players combined inventory",
		params = {MATERIAL, "materialLike", "the item or material you want to get the slot of"},
		returns = {NUMBER, "the slot number of the item, null if not found"},
		examples = "otherPlayer.getSlotFor(Material.DIAMOND.asItemStack());"
	)
	private Double getSlotFor(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		ScriptMaterial materialLike = arguments.nextPrimitive(MaterialDef.class);
		ScreenHandler screenHandler = playerEntity.currentScreenHandler;
		for (Slot slot : screenHandler.slots) {
			if (slot.getStack().getItem() == materialLike.asItem()) {
				return (double) slot.id;
			}
		}
		return null;
	}

	@FunctionDoc(
		name = "getAllSlotsFor",
		desc = "This gets all the slot numbers of the specified item in the players combined inventory",
		params = {MATERIAL, "materialLike", "the item or material you want to get the slot of"},
		returns = {LIST, "the slot numbers of the item, empty list if not found"},
		examples = "otherPlayer.getAllSlotsFor(Material.DIAMOND);"
	)
	private ArucasList getAllSlotsFor(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		ScriptMaterial materialLike = arguments.nextPrimitive(MaterialDef.class);
		ScreenHandler screenHandler = playerEntity.currentScreenHandler;
		ArucasList slotList = new ArucasList();
		for (Slot slot : screenHandler.slots) {
			if (slot.getStack().getItem() == materialLike.asItem()) {
				slotList.add(arguments.getInterpreter().create(NumberDef.class, (double) slot.id));
			}
		}
		return slotList;
	}

	@FunctionDoc(
		name = "getAllSlotsFor",
		desc = "This gets all the slot numbers of the specified item in the players combined inventory",
		params = {MATERIAL, "materialLike", "the item or material you want to get the slot of",
			STRING, "inventoryType", "all/combined -> includes external, player/main -> player slots, external/other -> excludes player inventory"},
		returns = {LIST, "the slot numbers of the item, empty list if not found"},
		examples = "otherPlayer.getAllSlotsFor(Material.DIAMOND, 'player');"
	)
	private ArucasList getAllSlotsForWithOption(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		ScriptMaterial materialLike = arguments.nextPrimitive(MaterialDef.class);
		String inventoryArgument = arguments.nextPrimitive(StringDef.class);
		ScreenHandler screenHandler = playerEntity.currentScreenHandler;
		ArucasList slotList = new ArucasList();
		for (Slot slot : screenHandler.slots) {
			switch (inventoryArgument) {
				case "all", "combined" -> {
				}
				case "player", "main" -> {
					if (!(slot.inventory instanceof PlayerInventory)) {
						continue;
					}
				}
				case "external", "other" -> {
					if (slot.inventory instanceof PlayerInventory) {
						continue;
					}
				}
				default -> throw new RuntimeError("String argument was not either one of combined / player / other");
			}
			if (slot.getStack().getItem() == materialLike.asItem()) {
				slotList.add(arguments.getInterpreter().create(NumberDef.class, (double) slot.id));
			}
		}
		return slotList;
	}

	@FunctionDoc(
		name = "getAbilities",
		desc = {
			"This gets the abilities of the player in a map",
			"For example:",
			"`{\"invulnerable\": false, \"canFly\": true, \"canBreakBlocks\": true, \"isCreative\": true, \"walkSpeed\": 1.0, \"flySpeed\": 1.2}`"
		},
		returns = {MAP, "the abilities of the player"},
		examples = "otherPlayer.getAbilities();"
	)
	private ArucasMap getAbilities(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		PlayerAbilities playerAbilities = PlayerHelper.getPlayerAbilities(playerEntity);
		Interpreter interpreter = arguments.getInterpreter();
		ArucasMap map = new ArucasMap();
		map.put(interpreter, interpreter.create(StringDef.class, "invulnerable"), interpreter.createBool(playerAbilities.invulnerable));
		map.put(interpreter, interpreter.create(StringDef.class, "canFly"), interpreter.createBool(playerAbilities.allowFlying));
		map.put(interpreter, interpreter.create(StringDef.class, "canBreakBlocks"), interpreter.createBool(playerAbilities.allowModifyWorld));
		map.put(interpreter, interpreter.create(StringDef.class, "isCreative"), interpreter.createBool(playerAbilities.creativeMode));
		map.put(interpreter, interpreter.create(StringDef.class, "walkSpeed"), interpreter.create(NumberDef.class, (double) playerAbilities.getWalkSpeed()));
		map.put(interpreter, interpreter.create(StringDef.class, "flySpeed"), interpreter.create(NumberDef.class, (double) playerAbilities.getFlySpeed()));
		return map;
	}

	@FunctionDoc(
		name = "getLevels",
		desc = "This gets the number of experience levels the player has",
		returns = {NUMBER, "the number of experience levels"},
		examples = "otherPlayer.getLevels();"
	)
	private double getLevels(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		return playerEntity.experienceLevel;
	}

	@FunctionDoc(
		name = "getXpProgress",
		desc = "This gets the number of experience progress the player has",
		returns = {NUMBER, "the number of experience progress"},
		examples = "otherPlayer.getXpProgress();"
	)
	private double getXpProgress(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		return playerEntity.experienceProgress;
	}

	@FunctionDoc(
		name = "getNextLevelExperience",
		desc = "This gets the number of experience required to level up for the player",
		returns = {NUMBER, "the number required to next level"},
		examples = "otherPlayer.getNextLevelExperience();"
	)
	private double getNextLevelExperience(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		return playerEntity.getNextLevelExperience();
	}

	@FunctionDoc(
		name = "getHunger",
		desc = "This gets the hunger level of the player",
		returns = {NUMBER, "the hunger level"},
		examples = "otherPlayer.getHunger();"
	)
	private double getHunger(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		return playerEntity.getHungerManager().getFoodLevel();
	}

	@FunctionDoc(
		name = "getSaturation",
		desc = "This gets the saturation level of the player",
		returns = {NUMBER, "the saturation level"},
		examples = "otherPlayer.getSaturation();"
	)
	private double getSaturation(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		return playerEntity.getHungerManager().getSaturationLevel();
	}

	@FunctionDoc(
		name = "getFishingBobber",
		desc = "This gets the fishing bobber that the player has",
		returns = {ENTITY, "the fishing bobber entity, null if the player isn't fishing"},
		examples = "otherPlayer.getFishingBobber();"
	)
	private FishingBobberEntity getFishingBobber(Arguments arguments) {
		AbstractClientPlayerEntity playerEntity = arguments.nextPrimitive(this);
		return playerEntity.fishHook;
	}
}
