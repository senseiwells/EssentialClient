package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.interfaces.MinecraftClientInvoker;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = PLAYER,
	desc = {
		"This class is used to interact with the main player, this extends OtherPlayer",
		"and so inherits all methods from that class."
	},
	importPath = "Minecraft",
	superclass = OtherPlayerDef.class,
	language = Util.Language.Java
)
public class PlayerDef extends CreatableDefinition<ClientPlayerEntity> {
	public PlayerDef(Interpreter interpreter) {
		super(PLAYER, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ClientPlayerEntity> superclass() {
		return this.getPrimitiveDef(OtherPlayerDef.class);
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return "Player{name=" + instance.asPrimitive(this).getEntityName() + "}";
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("get", this::get)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "get",
		desc = "This gets the main player",
		returns = {PLAYER, "The main player"},
		examples = "player = Player.get();"
	)
	private ClassInstance get(Arguments arguments) {
		return this.create(EssentialUtils.getPlayer());
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("use", 1, this::use),
			MemberFunction.of("attack", 1, this::attack),
			MemberFunction.of("setSelectedSlot", 1, this::setSelectedSlot),
			MemberFunction.of("say", 1, this::say),
			MemberFunction.of("message", 1, this::message),
			MemberFunction.of("messageActionBar", 1, this::messageActionBar),
			MemberFunction.of("showTitle", 1, this::showTitle),
			MemberFunction.of("openInventory", this::openInventory),
			MemberFunction.of("selectTrade", 1, this::selectTrade, "Use <MerchantScreen>.selectTrade(int) for normal uses"),
			MemberFunction.of("openScreen", 1, this::openScreen),
			MemberFunction.of("closeScreen", this::closeScreen),
			MemberFunction.of("setWalking", 1, this::setWalking),
			MemberFunction.of("setSneaking", 1, this::setSneaking),
			MemberFunction.of("setSprinting", 1, this::setSprinting),
			MemberFunction.of("dropItemInHand", 1, this::dropItemInHand),
			MemberFunction.of("dropAll", 1, this::dropAll),
			MemberFunction.of("dropAllExact", 1, this::dropAllExact),
			MemberFunction.of("look", 2, this::look),
			MemberFunction.of("lookAtPos", 3, this::lookAtPos),
			MemberFunction.of("lookAtPos", 1, this::lookAtPosPos),
			MemberFunction.of("jump", this::jump),
			MemberFunction.of("getLookingAtEntity", this::getLookingAtEntity),
			MemberFunction.of("swapSlots", 2, this::swapSlots),
			MemberFunction.of("shiftClickSlot", 1, this::shiftClickSlot),
			MemberFunction.of("dropSlot", 1, this::dropSlot),
			MemberFunction.of("getCurrentScreen", this::getCurrentScreen),
			MemberFunction.of("craft", 1, this::craft),
			MemberFunction.of("craftRecipe", 1, this::craftRecipe),
			MemberFunction.of("craftRecipe", 2, this::craftRecipeDrop),
			MemberFunction.of("clickRecipe", 1, this::clickRecipe1),
			MemberFunction.of("clickRecipe", 2, this::clickRecipe2),
			MemberFunction.of("logout", 1, this::logout),
			MemberFunction.of("attackEntity", 1, this::attackEntity),
			MemberFunction.of("interactWithEntity", 1, this::interactWithEntity),
			MemberFunction.of("anvil", 2, this::anvil2),
			MemberFunction.of("anvil", 3, this::anvil3),
			MemberFunction.of("anvilRename", 2, this::anvilRename),
			MemberFunction.of("stonecutter", 2, this::stonecutter),
			MemberFunction.of("fakeLook", 4, this::fakeLook),
			MemberFunction.of("swapPlayerSlotWithHotbar", 1, this::swapPlayerSlotWithHotbar),
			MemberFunction.of("breakBlock", 1, this::breakBlock),
			MemberFunction.of("updateBreakingBlock", 3, this::updateBreakingBlock, "Use <Player>.breakBlock(pos)"),
			MemberFunction.of("updateBreakingBlock", 1, this::updateBreakingBlockPos, "Use <Player>.breakBlock(pos)"),
			MemberFunction.of("attackBlock", 4, this::attackBlock),
			MemberFunction.of("attackBlock", 2, this::attackBlockPos),
			MemberFunction.of("interactItem", 2, this::interactItem),
			MemberFunction.of("interactBlock", 4, this::interactBlock),
			MemberFunction.of("interactBlock", 2, this::interactBlockPos),
			MemberFunction.of("interactBlock", 3, this::interactBlockPosHand),
			MemberFunction.of("interactBlock", 8, this::interactBlockFull),
			MemberFunction.of("interactBlock", 4, this::interactBlockFullPos),
			MemberFunction.of("interactBlock", 5, this::interactBlockFullPosHand),
			MemberFunction.of("getBlockBreakingSpeed", 2, this::getBlockBreakingSpeed),
			MemberFunction.of("swapHands", this::swapHands),
			MemberFunction.of("swingHand", 1, this::swingHand),
			MemberFunction.of("clickSlot", 3, this::clickSlot),
			MemberFunction.of("clickCreativeStack", 2, this::clickCreativeStack),
			MemberFunction.of("getSwappableHotbarSlot", this::getSwappableHotbarSlot),
			MemberFunction.of("spectatorTeleport", 1, this::spectatorTeleport),
			MemberFunction.of("canPlaceBlockAt", 2, this::canPlaceBlockAtPos),
			MemberFunction.of("canPlaceBlockAt", 4, this::canPlaceBlockAtPos1)
		);
	}

	@FunctionDoc(
		name = "use",
		desc = {
			"This allows you to make your player use, you must",
			"pass 'hold', 'stop', or 'once' otherwise an error will be thrown"
		},
		params = {STRING, "action", "the type of action, either 'hold', 'stop', or 'once'"},
		examples = "player.use('hold');"
	)
	private Void use(Arguments arguments) {
		String action = arguments.skip().nextPrimitive(StringDef.class);
		MinecraftClient client = EssentialUtils.getClient();
		switch (action.toLowerCase()) {
			case "hold" -> ClientScriptUtils.holdKey(arguments.getInterpreter(), client.options.useKey);
			case "stop" -> ClientScriptUtils.releaseKey(client.options.useKey);
			case "once" -> ((MinecraftClientInvoker) client).rightClickMouseAccessor();
			default -> throw new RuntimeError("Must pass 'hold', 'stop' or 'once' into use()");
		}
		return null;
	}

	@FunctionDoc(
		name = "attack",
		desc = {
			"This allows you to make your player attack, you must",
			"pass 'hold', 'stop', or 'once' otherwise an error will be thrown"
		},
		params = {STRING, "action", "the type of action, either 'hold', 'stop', or 'once'"},
		examples = "player.attack('once');"
	)
	private Void attack(Arguments arguments) {
		String action = arguments.skip().nextPrimitive(StringDef.class);
		MinecraftClient client = EssentialUtils.getClient();
		switch (action.toLowerCase()) {
			case "hold" -> ClientScriptUtils.holdKey(arguments.getInterpreter(), client.options.attackKey);
			case "stop" -> ClientScriptUtils.releaseKey(client.options.attackKey);
			case "once" -> ((MinecraftClientInvoker) client).leftClickMouseAccessor();
			default -> throw new RuntimeError("Must pass 'hold', 'stop' or 'once' into attack()");
		}
		return null;
	}

	@FunctionDoc(
		name = "setSelectedSlot",
		desc = {
			"This allows you to set the slot number your player is holding.",
			"If the number is not between 0 and 8 an error will be thrown"
		},
		params = {NUMBER, "slot", "the slot number, must be between 0 - 8"},
		examples = "player.setSelectedSlot(0);"
	)
	private Void setSelectedSlot(Arguments arguments) {
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		if (index < 0 || index > 8) {
			throw new RuntimeError("Number must be between 0 - 8");
		}
		ClientScriptUtils.ensureMainThread("setSelectedSlot", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().getInventory().selectedSlot = index;
			EssentialUtils.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(index));
		});
		return null;
	}

	@FunctionDoc(
		name = "say",
		desc = "This allows you to make your player send a message in chat, this includes commands",
		params = {STRING, "message", "the message to send"},
		examples = "player.say('/help');"
	)
	private Void say(Arguments arguments) {
		EssentialUtils.sendChatMessage(arguments.skip().next().toString(arguments.getInterpreter()));
		return null;
	}

	@FunctionDoc(
		name = "selectTrade",
		desc = "This allows you to player send trade select packet, maybe while screen is being opened.",
		params = {NUMBER, "index", "the trade index to send"},
		examples = "player.selectTrade(0);"
	)
	private Void selectTrade(Arguments arguments) {
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		ClientScriptUtils.ensureMainThread("selectTradeUnsafe", arguments.getInterpreter(), () -> {
			InventoryUtils.selectTradeUnsafe(index);
		});
		return null;
	}

	@FunctionDoc(
		name = "message",
		desc = "This allows you to send a message to your player, only they will see this, purely client side",
		params = {TEXT, "message", "the message to send, can also be string"},
		examples = "player.message('Hello World!');"
	)
	private Void message(Arguments arguments) {
		ClassInstance value = arguments.skip().next();
		Text text = ClientScriptUtils.instanceToText(value, arguments.getInterpreter());
		ClientScriptUtils.ensureMainThread("message", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().sendMessage(text, false);
		});
		return null;
	}

	@FunctionDoc(
		name = "messageActionBar",
		desc = "This allows you to set the current memssage displaying on the action bar",
		params = {TEXT, "message", "the message to send, can also be string"},
		examples = "player.messageActionBar('Hello World!');"
	)
	private Void messageActionBar(Arguments arguments) {
		ClassInstance value = arguments.skip().next();
		Text text = ClientScriptUtils.instanceToText(value, arguments.getInterpreter());
		ClientScriptUtils.ensureMainThread("messageActionBar", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().sendMessage(text, true);
		});
		return null;
	}

	@FunctionDoc(
		name = "showTitle",
		desc = "THis allows you to show a title and subtitle to the player",
		params = {
			TEXT, "title", "the title to show, can be string or null",
			TEXT, "subtitle", "the subtitle to show, can be string or null"
		},
		examples = "player.showTitle('Title!', 'Subtitle!');"
	)
	private Void showTitle(Arguments arguments) {
		ClassInstance titleInstance = arguments.skip().next();
		ClassInstance subTitleInstance = arguments.next();
		Text title = ClientScriptUtils.instanceToText(titleInstance, arguments.getInterpreter());
		Text subTitle = ClientScriptUtils.instanceToText(subTitleInstance, arguments.getInterpreter());
		ClientScriptUtils.ensureMainThread("showTitle", arguments.getInterpreter(), () -> {
			MinecraftClient client = EssentialUtils.getClient();
			client.inGameHud.setTitle(title);
			client.inGameHud.setSubtitle(subTitle);
		});
		return null;
	}

	@FunctionDoc(
		name = "openInventory",
		desc = "This opens the player's inventory",
		examples = "player.openInventory();"
	)
	private Void openInventory(Arguments arguments) {
		ClientScriptUtils.ensureMainThread("openInventory", arguments.getInterpreter(), () -> {
			EssentialUtils.getClient().setScreen(new InventoryScreen(EssentialUtils.getPlayer()));
		});
		return null;
	}

	@FunctionDoc(
		name = "openScreen",
		desc = {
			"This opens a screen for the player, this cannot open server side screens.",
			"This will throw an error if you are trying to open a handled screen"
		},
		params = {SCREEN, "screen", "the screen to open"},
		examples = "player.openScreen(new FakeScreen('MyScreen', 4));"
	)
	private Void openScreen(Arguments arguments) {
		Screen screen = arguments.skip().nextPrimitive(ScreenDef.class);
		if (screen instanceof HandledScreen && !(screen instanceof FakeInventoryScreen)) {
			throw new RuntimeError("Opening handled screens is unsafe");
		}
		ClientScriptUtils.ensureMainThread("openScreen", arguments.getInterpreter(), () -> {
			EssentialUtils.getClient().setScreen(screen);
		});
		return null;
	}

	@FunctionDoc(
		name = "closeScreen",
		desc = "This closes the current screen",
		examples = "player.closeScreen();"
	)
	private Void closeScreen(Arguments arguments) {
		ClientScriptUtils.ensureMainThread("closeScreen", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().closeHandledScreen();
		});
		return null;
	}

	@FunctionDoc(
		name = "setWalking",
		desc = "This sets the player's walking state",
		params = {BOOLEAN, "walking", "the walking state"},
		examples = "player.setWalking(true);"
	)
	private Void setWalking(Arguments arguments) {
		ClientScriptUtils.modifyKey(arguments.getInterpreter(), arguments.skip().nextPrimitive(BooleanDef.class), EssentialUtils.getClient().options.forwardKey);
		return null;
	}

	@FunctionDoc(
		name = "setSneaking",
		desc = "This sets the player's sneaking state",
		params = {BOOLEAN, "sneaking", "the sneaking state"},
		examples = "player.setSneaking(true);"
	)
	private Void setSneaking(Arguments arguments) {
		ClientScriptUtils.modifyKey(arguments.getInterpreter(), arguments.skip().nextPrimitive(BooleanDef.class), EssentialUtils.getClient().options.sneakKey);
		return null;
	}

	@FunctionDoc(
		name = "setSprinting",
		desc = "This sets the player's sprinting state",
		params = {BOOLEAN, "sprinting", "the sprinting state"},
		examples = "player.setSprinting(true);"
	)
	private Void setSprinting(Arguments arguments) {
		boolean shouldSprint = arguments.skip().nextPrimitive(BooleanDef.class);
		ClientScriptUtils.ensureMainThread("setSprinting", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().setSprinting(shouldSprint);
		});
		return null;
	}

	@FunctionDoc(
		name = "dropItemInHand",
		desc = "This drops the item(s) in the player's main hand",
		params = {BOOLEAN, "dropAll", "if true, all items in the player's main hand will be dropped"},
		examples = "player.dropItemInHand(true);"
	)
	private Void dropItemInHand(Arguments arguments) {
		boolean dropAll = arguments.skip().nextPrimitive(BooleanDef.class);
		ClientScriptUtils.ensureMainThread("dropItemInHand", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().dropSelectedItem(dropAll);
		});
		return null;
	}

	@FunctionDoc(
		name = "dropAll",
		desc = "This drops all items of a given type in the player's inventory",
		params = {MATERIAL, "material", "the item stack, or material type to drop"},
		examples = "player.dropAll(Material.DIRT.asItemStack());"
	)
	private Void dropAll(Arguments arguments) {
		Item item = arguments.skip().nextPrimitive(MaterialDef.class).asItem();
		ClientScriptUtils.ensureMainThread("dropAll", arguments.getInterpreter(), () -> {
			InventoryUtils.dropAllItemType(item);
		});
		return null;
	}

	@FunctionDoc(
		name = "dropAllExact",
		desc = "This drops all the items that have the same nbt as a given stack",
		params = {ITEM_STACK, "itemStack", "the stack with nbt to drop"},
		examples = "player.dropAllExact(Material.GOLD_INGOT.asItemStack());"
	)
	private Void dropAllExact(Arguments arguments) {
		ItemStack stack = arguments.skip().nextPrimitive(ItemStackDef.class).stack;
		ClientScriptUtils.ensureMainThread("dropAllExact", arguments.getInterpreter(), () -> {
			InventoryUtils.dropAllItemExact(stack);
		});
		return null;
	}

	@FunctionDoc(
		name = "look",
		desc = "This sets the player's look direction",
		params = {
			NUMBER, "yaw", "the yaw of the player's look direction",
			NUMBER, "pitch", "the pitch of the player's look direction"
		},
		examples = "player.look(0, 0);"
	)
	private Void look(Arguments arguments) {
		float yaw = arguments.skip().nextPrimitive(NumberDef.class).floatValue();
		float pitch = arguments.nextPrimitive(NumberDef.class).floatValue();
		ClientScriptUtils.ensureMainThread("look", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().setYaw(yaw);
			EssentialUtils.getPlayer().setPitch(pitch);
		});
		return null;
	}

	@FunctionDoc(
		name = "lookAtPos",
		desc = "This makes your player look towards a position",
		params = {
			NUMBER, "x", "the x coordinate of the position",
			NUMBER, "y", "the y coordinate of the position",
			NUMBER, "z", "the z coordinate of the position"
		},
		examples = "player.lookAtPos(0, 0, 0);"
	)
	private Void lookAtPos(Arguments arguments) {
		double x = arguments.skip().nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		ClientScriptUtils.ensureMainThread("lookAtPos", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(x, y, z));
		});
		return null;
	}

	@FunctionDoc(
		name = "lookAtPos",
		desc = "This makes your player look towards a position",
		params = {POS, "pos", "the position to look at"},
		examples = "player.lookAtPos(pos);"
	)
	private Void lookAtPosPos(Arguments arguments) {
		Vec3d pos = arguments.skip().nextPrimitive(PosDef.class).getVec3d();
		ClientScriptUtils.ensureMainThread("lookAtPos", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, pos);
		});
		return null;
	}

	@FunctionDoc(
		name = "canPlaceBlockAt",
		desc = "Checks block can be placed at given position",
		params = {POS, "pos", "the position to check"},
		examples = "player.canPlaceBlockAt(block, pos);"
	)
	private boolean canPlaceBlockAtPos(Arguments arguments) {
		ClientScriptUtils.warnMainThread("canPlaceBlockAt", arguments.getInterpreter());
		BlockState state = arguments.skip().nextPrimitive(BlockDef.class).state;
		BlockPos pos = arguments.nextPrimitive(PosDef.class).getBlockPos();
		boolean canPlace = state.canPlaceAt(EssentialUtils.getWorld(), pos);
		canPlace = canPlace && EssentialUtils.getWorld().canPlace(state, pos, ShapeContext.of(EssentialUtils.getPlayer()));
		return canPlace;
	}

	@FunctionDoc(
		name = "canPlaceBlockAt",
		desc = "Checks block can be placed at given position",
		params = {
			BLOCK, "block", "the block to check for",
			NUMBER, "x", "the x coordinate of the position",
			NUMBER, "y", "the y coordinate of the position",
			NUMBER, "z", "the z coordinate of the position"
		},
		examples = "player.canPlaceBlockAt(block, 0, 0, 0);"
	)
	private boolean canPlaceBlockAtPos1(Arguments arguments) {
		ClientScriptUtils.warnMainThread("canPlaceBlockAt", arguments.getInterpreter());
		BlockState state = arguments.skip().nextPrimitive(BlockDef.class).state;
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos pos = new BlockPos(x, y, z);
		boolean canPlace = state.canPlaceAt(EssentialUtils.getWorld(), pos);
		canPlace = canPlace && EssentialUtils.getWorld().canPlace(state, pos, ShapeContext.of(EssentialUtils.getPlayer()));
		return canPlace;
	}

	@FunctionDoc(
		name = "jump",
		desc = "This will make the player jump if they are on the ground",
		examples = "player.jump();"
	)
	private Void jump(Arguments arguments) {
		ClientScriptUtils.ensureMainThread("jump", arguments.getInterpreter(), () -> {
			ClientPlayerEntity player = EssentialUtils.getPlayer();
			if (player.isOnGround()) {
				player.jump();
				player.setOnGround(false);
			}
		});
		return null;
	}

	@FunctionDoc(
		name = "getLookingAtEntity",
		desc = "This gets the entity that the player is currently looking at",
		returns = {ENTITY, "the entity that the player is looking at"},
		examples = "player.getLookingAtEntity();"
	)
	private Entity getLookingAtEntity(Arguments arguments) {
		ClientScriptUtils.warnMainThread("getLookingAtEntity", arguments.getInterpreter());
		if (EssentialUtils.getClient().crosshairTarget instanceof EntityHitResult hitResult) {
			return hitResult.getEntity();
		}
		return null;
	}

	@FunctionDoc(
		name = "swapSlots",
		desc = {
			"The allows you to swap two slots with one another.",
			"A note about slot order is that slots go from top to bottom.",
			"This will throw an errof if the slots are out of bounds"
		},
		params = {
			NUMBER, "slot1", "the slot to swap with slot2",
			NUMBER, "slot2", "the slot to swap with slot1"
		},
		examples = "player.swapSlots(13, 14);"
	)
	private Void swapSlots(Arguments arguments) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		int slot1 = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		int slot2 = arguments.nextPrimitive(NumberDef.class).intValue();
		ClientScriptUtils.ensureMainThread("swapSlots", arguments.getInterpreter(), () -> {
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot1 >= size || slot1 < 0 || slot2 >= size || slot2 < 0) {
				throw new RuntimeError("That slot is out of bounds");
			}
			int firstMapped = InventoryUtils.isSlotInHotbar(screenHandler, slot1);
			int secondMapped = InventoryUtils.isSlotInHotbar(screenHandler, slot2);
			if (firstMapped == -1) {
				if (secondMapped == -1) {
					ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
					interactionManager.clickSlot(screenHandler.syncId, slot1, 0, SlotActionType.SWAP, player);
					interactionManager.clickSlot(screenHandler.syncId, slot2, 0, SlotActionType.SWAP, player);
					interactionManager.clickSlot(screenHandler.syncId, slot1, 0, SlotActionType.SWAP, player);
					player.getInventory().updateItems();
				} else {
					InventoryUtils.swapSlot(screenHandler, slot1, secondMapped);
				}
			} else {
				InventoryUtils.swapSlot(screenHandler, slot2, firstMapped);
			}
		});
		return null;
	}

	@FunctionDoc(
		name = "getSwappableHotbarSlot",
		desc = {
			"This will get the next empty slot in the hotbar starting from the current slot",
			"going right, and if it reaches the end of the hotbar it will start from the beginning.",
			"If there is no empty slot it will return any slot that doesn't have an item with",
			"an enchantment that is in the hotbar, again going from the current slot",
			"if there is no such slot it will return the current selected slot"
		},
		returns = {NUMBER, "the slot that is swappable"},
		examples = "player.getSwappableHotbarSlot();"
	)
	private int getSwappableHotbarSlot(Arguments arguments) {
		// Return predicted current swappable hotbar slot
		return EssentialUtils.getPlayer().getInventory().getSwappableHotbarSlot();
	}

	@FunctionDoc(
		name = "spectatorTeleport",
		desc = "This allows you to teleport to any entity as long as you are in spectator mode",
		params = {ENTITY, "entity", "the entity to teleport to"},
		examples = "player.spectatorTeleport(player.getLookingAtEntity());"
	)
	private boolean spectatorTeleport(Arguments arguments) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		if (!player.isSpectator()) {
			return false;
		}
		Entity entity = arguments.skip().nextPrimitive(EntityDef.class);
		EssentialUtils.getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(entity.getUuid()));
		return true;
	}

	@FunctionDoc(
		name = "swapHands",
		desc = "This will swap the player's main hand with the off hand",
		examples = "player.swapHands();"
	)
	private boolean swapHands(Arguments arguments) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		if (player.isSpectator()) {
			return false;
		}
		ClientScriptUtils.ensureMainThread("swapHands", arguments.getInterpreter(), () -> {
			EssentialUtils.getNetworkHandler().sendPacket(
				new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN)
			);
		});
		return true;
	}

	@FunctionDoc(
		name = "swingHand",
		desc = "This will play the player's hand swing animation for a given hand",
		params = {STRING, "hand", "the hand to swing, this should be either 'main_hand' or 'off_hand'"},
		examples = "player.swingHand('main_hand');"
	)
	private Void swingHand(Arguments arguments) {
		String handAsString = arguments.skip().nextPrimitive(StringDef.class);
		Hand hand = ClientScriptUtils.stringToHand(handAsString);
		ClientScriptUtils.ensureMainThread("swingHand", arguments.getInterpreter(), () -> {
			EssentialUtils.getPlayer().swingHand(hand);
		});
		return null;
	}

	@FunctionDoc(
		name = "clickSlot",
		desc = {
			"This allows you to click a slot with either right or left click",
			"and a slot action, the click must be either 'left' or 'right' or a number (for swap).",
			"The action must be either 'click', 'shift_click', 'swap', 'middle_click',",
			"'throw', 'drag', or 'double_click' or an error will be thrown"
		},
		params = {
			NUMBER, "slot", "the slot to click",
			STRING, "click", "the click type, this should be either 'left' or 'right'",
			STRING, "action", "the action to perform"
		},
		examples = "player.clickSlot(9, 'left', 'double_click');"
	)
	private Void clickSlot(Arguments arguments) {
		int slot = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		int clickData;
		if (arguments.isNext(StringDef.class)) {
			clickData = switch (arguments.nextPrimitive(StringDef.class).toLowerCase()) {
				case "right" -> 1;
				case "left" -> 0;
				default -> throw new RuntimeError("Invalid clickData must be 'left' or 'right' or a number");
			};
		} else if (arguments.isNext(NumberDef.class)) {
			clickData = arguments.nextPrimitive(NumberDef.class).intValue();
		} else {
			throw new RuntimeError("Invalid clickData must be 'left' or 'right' or a number");
		}
		String action = arguments.nextPrimitive(StringDef.class);
		SlotActionType slotActionType = ClientScriptUtils.stringToSlotActionType(action);
		ClientScriptUtils.ensureMainThread("clickSlot", arguments.getInterpreter(), () -> {
			ClientPlayerEntity player = EssentialUtils.getPlayer();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot != -999 && slot >= size || slot < 0) {
				throw new RuntimeError("That slot is out of bounds");
			}
			EssentialUtils.getInteractionManager().clickSlot(screenHandler.syncId, slot, clickData, slotActionType, player);
		});
		return null;
	}

	@FunctionDoc(
		name = "clickCreativeStack",
		desc = "This allows you to click Creative stack, but requires sync with server",
		params = {
			ITEM_STACK, "itemStack", "Stack to click",
			NUMBER, "slot", "the slot to click"},
		examples = "player.clickCreativeStack(Material.DIAMOND_SWORD.asItemStack(), 9);"
	)
	private Void clickCreativeStack(Arguments arguments) {
		ScriptItemStack stackValue = arguments.skip().nextPrimitive(ItemStackDef.class);
		int index = arguments.nextPrimitive(NumberDef.class).intValue();
		ClientScriptUtils.ensureMainThread("clickCreativeStack", arguments.getInterpreter(), () -> {
			ClientPlayerEntity player = EssentialUtils.getPlayer();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (index >= size || index < 0) {
				throw new RuntimeError("That slot is out of bounds");
			}
			EssentialUtils.getInteractionManager().clickCreativeStack(stackValue.stack, index);
		});
		return null;
	}

	@FunctionDoc(
		name = "shiftClickSlot",
		desc = "This allows you to shift click a slot",
		params = {NUMBER, "slot", "the slot to click"},
		examples = "player.shiftClickSlot(9);"
	)
	private Void shiftClickSlot(Arguments arguments) {
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		ClientScriptUtils.ensureMainThread("shiftClickSlot", arguments.getInterpreter(), () -> {
			ClientPlayerEntity player = EssentialUtils.getPlayer();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (index >= size || index < 0) {
				throw new RuntimeError("That slot is out of bounds");
			}
			InventoryUtils.shiftClickSlot(screenHandler, index);
		});
		return null;
	}

	@FunctionDoc(
		name = "dropSlot",
		desc = "This allows you to drop the items in a slot",
		params = {NUMBER, "slot", "the slot to drop"},
		examples = "player.dropSlot(9);"
	)
	private Void dropSlot(Arguments arguments) {
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		ClientScriptUtils.ensureMainThread("shiftClickSlot", arguments.getInterpreter(), () -> {
			ClientPlayerEntity player = EssentialUtils.getPlayer();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (index >= size || index < 0) {
				throw new RuntimeError("That slot is out of bounds");
			}
			EssentialUtils.getInteractionManager().clickSlot(screenHandler.syncId, index, 1, SlotActionType.THROW, player);
		});
		return null;
	}

	@FunctionDoc(
		name = "getCurrentScreen",
		desc = "This gets the current screen the player is in",
		returns = {SCREEN, "the screen the player is in, if the player is not in a screen it will return null"},
		examples = "screen = player.getCurrentScreen();"
	)
	private Screen getCurrentScreen(Arguments arguments) {
		ClientScriptUtils.warnMainThread("getCurrentScreen", arguments.getInterpreter());
		return EssentialUtils.getClient().currentScreen;
	}

	@FunctionDoc(
		name = "craft",
		desc = {
			"This allows you to craft a recipe, this can be 2x2 or 3x3",
			"The list you pass in must contain Materials or ItemStacks",
			"Most of the time you should use craftRecipe instead. You must",
			"be in an appropriate gui for the crafting recipe or an error will be thrown"
		},
		params = {LIST, "recipe", "a list of materials making up the recipe you want to craft including air"},
		examples = {
			"""
				chestRecipe = [
					Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS,
					Material.OAK_PLANKS,    Material.AIR    , Material.OAK_PLANKS,
					Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS
				];
				player.craft(chestRecipe);
				"""
		}
	)
	private Void craft(Arguments arguments) {
		ArucasList listValue = arguments.skip().nextPrimitive(ListDef.class);
		if (listValue.size() != 4 && listValue.size() != 9) {
			throw new RuntimeError("Recipe must either be 3x3 or 2x2");
		}
		ItemStack[] itemStacks = new ItemStack[listValue.size()];
		for (int i = 0; i < listValue.size(); i++) {
			ScriptMaterial value = listValue.get(i).getPrimitive(MaterialDef.class);
			if (value != null) {
				itemStacks[i] = value.asItemStack();
				continue;
			}
			throw new RuntimeError("The recipe must only include items or materials");
		}

		ClientScriptUtils.ensureMainThread("craft", arguments.getInterpreter(), () -> {
			MinecraftClient client = EssentialUtils.getClient();
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw new RuntimeError("Must be in a crafting GUI");
			}
			if (itemStacks.length == 9 && !(handledScreen instanceof CraftingScreen)) {
				throw new RuntimeError("3x3 recipes require crafting table GUI");
			}
			ScreenHandler handler = handledScreen.getScreenHandler();
			InventoryUtils.tryMoveItemsToCraftingGridSlots(itemStacks, handler);
			InventoryUtils.shiftClickSlot(handler, 0);
		});
		return null;
	}

	@FunctionDoc(
		name = "craftRecipe",
		desc = "This allows you to craft a predefined recipe",
		params = {RECIPE, "recipe", "the recipe you want to craft"},
		examples = "player.craftRecipe(Recipe.CHEST);"
	)
	private Void craftRecipe(Arguments arguments) {
		Recipe<?> recipe = arguments.skip().nextPrimitive(RecipeDef.class);
		ClientScriptUtils.ensureMainThread("craftRecipe", arguments.getInterpreter(), () -> {
			InventoryUtils.craftRecipe(recipe, false);
		});
		return null;
	}

	@FunctionDoc(
		name = "craftRecipe",
		desc = "This allows you to craft a predefined recipe",
		params = {
			RECIPE, "recipe", "the recipe you want to craft",
			BOOLEAN, "boolean", "whether result should be dropped or not"
		},
		examples = "player.craftRecipe(Recipe.CHEST, true);"
	)
	private Void craftRecipeDrop(Arguments arguments) {
		Recipe<?> recipe = arguments.skip().nextPrimitive(RecipeDef.class);
		boolean shouldDrop = arguments.nextPrimitive(BooleanDef.class);
		ClientScriptUtils.ensureMainThread("craftRecipe", arguments.getInterpreter(), () -> {
			InventoryUtils.craftRecipe(recipe, shouldDrop);
		});
		return null;
	}

	@FunctionDoc(
		name = "clickRecipe",
		desc = "This allows you to click a predefined recipe",
		params = {RECIPE, "recipe", "the recipe you want to select"},
		examples = "player.clickRecipe(Recipe.CHEST);"
	)
	private Void clickRecipe1(Arguments arguments) {
		Recipe<?> recipe = arguments.skip().nextPrimitive(RecipeDef.class);
		ClientScriptUtils.ensureMainThread("clickRecipe", arguments.getInterpreter(), () -> {
			InventoryUtils.clickRecipe(recipe, false);
		});
		return null;
	}

	@FunctionDoc(
		name = "clickRecipe",
		desc = "This allows you to click a predefined recipe",
		params = {
			RECIPE, "recipe", "the recipe you want to select",
			BOOLEAN, "boolean", "whether to shift click the recipe"
		},
		examples = "player.clickRecipe(Recipe.CHEST, true);"
	)
	private Void clickRecipe2(Arguments arguments) {
		Recipe<?> recipe = arguments.skip().nextPrimitive(RecipeDef.class);
		boolean craftAll = arguments.nextPrimitive(BooleanDef.class);
		ClientScriptUtils.ensureMainThread("clickRecipe", arguments.getInterpreter(), () -> {
			InventoryUtils.clickRecipe(recipe, craftAll);
		});
		return null;
	}

	@FunctionDoc(
		name = "logout",
		desc = "This forces the player to leave the world",
		params = {STRING, "message", "the message to display to the player on the logout screen"},
		examples = "player.logout('You've been lazy!');"
	)
	private Void logout(Arguments arguments) {
		String reason = arguments.skip().nextPrimitive(StringDef.class);
		ClientScriptUtils.ensureMainThread("logout", arguments.getInterpreter(), () -> {
			EssentialUtils.getNetworkHandler().onDisconnected(Texts.literal(reason));
		});
		return null;
	}

	@FunctionDoc(
		name = "attackEntity",
		desc = {
			"This makes your player attack an entity without",
			"having to be looking at it or clicking on the entity"
		},
		params = {ENTITY, "entity", "the entity to attack"},
		examples = """
			allEntities = client.getWorld().getAllEntities();
			foreach (entity : allEntities) {
				if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
					player.attackEntity(entity);
					break;
				}
			}
			"""
	)
	private Void attackEntity(Arguments arguments) {
		Entity entity = arguments.skip().nextPrimitive(EntityDef.class);
		ClientScriptUtils.ensureMainThread("interactWithEntity", arguments.getInterpreter(), () -> {
			EssentialUtils.getInteractionManager().attackEntity(EssentialUtils.getPlayer(), entity);
		});
		return null;
	}

	@FunctionDoc(
		name = "interactWithEntity",
		desc = {
			"This allows your player to interact with an entity without",
			"having to be looking at it or clicking on the entity"
		},
		params = {ENTITY, "entity", "the entity to interact with"},
		examples = """
			allEntities = client.getWorld().getAllEntities();
			foreach (entity : allEntities) {
				if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
					player.interactWithEntity(entity);
					break;
				}
			}
			"""
	)
	private Void interactWithEntity(Arguments arguments) {
		Entity entity = arguments.skip().nextPrimitive(EntityDef.class);
		ClientScriptUtils.ensureMainThread("interactWithEntity", arguments.getInterpreter(), () -> {
			EssentialUtils.getInteractionManager().interactEntity(EssentialUtils.getPlayer(), entity, Hand.MAIN_HAND);
		});
		return null;
	}

	@FunctionDoc(
		name = "anvil",
		desc = "This allows you to combine two items in an anvil",
		params = {
			FUNCTION, "predicate1", "a function determining whether the first ItemStack meets a criteria",
			FUNCTION, "predicate2", "a function determining whether the second ItemStack meets a criteria"
		},
		returns = {FUTURE, "whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost"},
		examples = """
			// Enchant a pickaxe with mending
			player.anvil(
				// Predicate for pick
				fun(item) {
					// We want a netherite pickaxe without mending
					if (item.getItemId() == "netherite_pickaxe") {
						hasMending = item.getEnchantments().getKeys().contains("mending");
						return !hasMending;
					}
					return false;
				},
				// Predicate for book
				fun(item) {
					// We want a book with mending
					if (item.getItemId() == "enchanted_book") {
						hasMending = item.getEnchantments().getKeys().contains("mending");
						return hasMending;
					}
					return false;
				}
			);
			"""
	)
	private Future<Object> anvil2(Arguments arguments) {
		ArucasFunction predicate1 = arguments.skip().nextPrimitive(FunctionDef.class);
		ArucasFunction predicate2 = arguments.nextPrimitive(FunctionDef.class);
		Interpreter interpreter = arguments.getInterpreter();
		return ClientScriptUtils.ensureMainThread("anvil", interpreter, () -> {
			return InventoryUtils.anvil(interpreter, predicate1, predicate2, false);
		});
	}

	@FunctionDoc(
		name = "anvil",
		desc = "This allows you to combine two items in an anvil",
		params = {
			FUNCTION, "predicate1", "a function determining whether the first ItemStack meets a criteria",
			FUNCTION, "predicate2", "a function determining whether the second ItemStack meets a criteria",
			BOOLEAN, "take", "whether you should take the item after putting items in the anvil"
		},
		returns = {FUTURE, "whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost"},
		examples = {
			"""
				// Enchant a pickaxe with mending
				player.anvil(
					// Predicate for pick
					fun(item) {
						// We want a netherite pickaxe without mending
						if (item.getItemId() == "netherite_pickaxe") {
							hasMending = item.getEnchantments().getKeys().contains("mending");
							return !hasMending;
						}
						return false;
					},
					// Predicate for book
					fun(item) {
						// We want a book with mending
						if (item.getItemId() == "enchanted_book") {
							hasMending = item.getEnchantments().getKeys().contains("mending");
							return hasMending;
						}
						return false;
					},
					false
				);
				"""
		}
	)
	private Future<Object> anvil3(Arguments arguments) {
		ArucasFunction predicate1 = arguments.skip().nextPrimitive(FunctionDef.class);
		ArucasFunction predicate2 = arguments.nextPrimitive(FunctionDef.class);
		boolean take = arguments.nextPrimitive(BooleanDef.class);
		Interpreter interpreter = arguments.getInterpreter();
		return ClientScriptUtils.ensureMainThread("anvil", interpreter, () -> {
			return InventoryUtils.anvil(interpreter, predicate1, predicate2, take);
		});
	}

	@FunctionDoc(
		name = "anvilRename",
		desc = "This allows you to name an item in an anvil",
		params = {
			STRING, "name", "the name you want to give the item",
			FUNCTION, "predicate", "whether the ItemStack meets a certain criteria"
		},
		returns = {FUTURE, "whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost"},
		examples = {
			"""
				// Rename any shulker box
				player.anvilRename("Rocket Box",
					fun(item) {
						isShulker = item.getItemId().containsString("shulker_box"));
						return isShulker;
					}
				);
				"""
		}
	)
	private Future<Object> anvilRename(Arguments arguments) {
		String newName = arguments.skip().nextPrimitive(StringDef.class);
		ArucasFunction function = arguments.nextPrimitive(FunctionDef.class);
		Interpreter interpreter = arguments.getInterpreter();
		return ClientScriptUtils.ensureMainThread("anvilRename", interpreter, () -> {
			return InventoryUtils.anvilRename(interpreter, newName, function);
		});
	}

	@FunctionDoc(
		name = "stonecutter",
		desc = "This allows you to use the stonecutter",
		params = {
			MATERIAL, "itemInput", "the item or material you want to input",
			MATERIAL, "itemOutput", "the item or material you want to craft"
		},
		returns = {FUTURE, "whether the result was successful"},
		examples = "player.stonecutter(Material.STONE.asItemstack(), Material.STONE_BRICKS.asItemStack());"
	)
	private Future<Boolean> stonecutter(Arguments arguments) {
		Item itemInput = arguments.skip().nextPrimitive(MaterialDef.class).asItem();
		Item itemOutput = arguments.nextPrimitive(MaterialDef.class).asItem();
		return ClientScriptUtils.ensureMainThread("stonecutter", arguments.getInterpreter(), () -> {
			return InventoryUtils.stonecutter(itemInput, itemOutput);
		});
	}

	@FunctionDoc(
		name = "fakeLook",
		desc = {
			"This makes the player 'fake' looking in a direction, this can be",
			"used to place blocks in unusual orientations without moving the camera"
		},
		params = {
			NUMBER, "yaw", "the yaw to look at",
			NUMBER, "pitch", "the pitch to look at",
			STRING, "direction", "the direction to look at",
			NUMBER, "duration", "the duration of the look in ticks"
		},
		examples = "player.fakeLook(90, 0, 'up', 100);"
	)
	private Void fakeLook(Arguments arguments) {
		float yaw = arguments.skip().nextPrimitive(NumberDef.class).floatValue();
		float pitch = arguments.nextPrimitive(NumberDef.class).floatValue();
		String directionAsString = arguments.nextPrimitive(StringDef.class);
		int ticks = arguments.nextPrimitive(NumberDef.class).intValue();
		Direction direction = ClientScriptUtils.stringToDirection(directionAsString, Direction.DOWN);
		ClientScriptUtils.ensureMainThread("fakeLook", arguments.getInterpreter(), () -> {
			BetterAccurateBlockPlacement.fakeYaw = yaw;
			BetterAccurateBlockPlacement.fakePitch = pitch;
			BetterAccurateBlockPlacement.fakeDirection = direction;
			BetterAccurateBlockPlacement.requestedTicks = Math.max(20, ticks);
			BetterAccurateBlockPlacement.sendLookPacket(EssentialUtils.getPlayer());
		});
		return null;
	}

	@FunctionDoc(
		name = "swapPlayerSlotWithHotbar",
		desc = "This allows you to swap a slot in the player's inventory with the hotbar",
		params = {NUMBER, "slot", "the slot to swap"},
		examples = "player.swapPlayerSlotWithHotbar(15);"
	)
	private Void swapPlayerSlotWithHotbar(Arguments arguments) {
		int slotToSwap = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		ClientScriptUtils.ensureMainThread("swapPlayerSlotWithHotbar", arguments.getInterpreter(), () -> {
			ClientPlayerEntity player = EssentialUtils.getPlayer();
			if (slotToSwap < 0 || slotToSwap > player.getInventory().main.size()) {
				throw new RuntimeError("That slot is out of bounds");
			}
			ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
			int prepareSlot = player.getInventory().getSwappableHotbarSlot();
			networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prepareSlot));
			player.getInventory().swapSlotWithHotbar(slotToSwap);
			networkHandler.sendPacket(new PickFromInventoryC2SPacket(slotToSwap));
		});
		return null;
	}

	@FunctionDoc(
		name = "breakBlock",
		desc = "This breaks a block at a given position, if it is able to be broken",
		params = {POS, "pos", "the position of the block"},
		returns = {FUTURE, "the future will be completed when the block is broken"},
		examples = "player.breakBlock(new Pos(0, 0, 0));"
	)
	private Future<Void> breakBlock(Arguments arguments) {
		BlockPos blockPos = arguments.skip().nextPrimitive(PosDef.class).getBlockPos();
		CompletableFuture<Void> future = new CompletableFuture<>();
		ClientScriptUtils.ensureMainThread("breakBlock", arguments.getInterpreter(), () -> {
			EssentialUtils.mineBlock(blockPos, () -> arguments.getInterpreter().getThreadHandler().getRunning(), future);
		});
		return future;
	}

	@FunctionDoc(
		deprecated = "Consider using other alternatives for breaking blocks, e.g. <Player>.breakBlock",
		name = "updateBreakingBlock",
		desc = "This allows you to update your block breaking progress at a position",
		params = {
			NUMBER, "x", "the x position",
			NUMBER, "y", "the y position",
			NUMBER, "z", "the z position"
		},
		examples = "player.updateBreakingBlock(0, 0, 0);"
	)
	private Void updateBreakingBlock(Arguments arguments) {
		double x = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		double y = arguments.nextPrimitive(NumberDef.class).intValue();
		double z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos blockPos = new BlockPos(x, y, z);
		ClientScriptUtils.ensureMainThread("updateBreakingBlock", arguments.getInterpreter(), () -> {
			EssentialUtils.getInteractionManager().updateBlockBreakingProgress(blockPos, Direction.UP);
			EssentialUtils.getPlayer().swingHand(Hand.MAIN_HAND);
		});
		return null;
	}

	@FunctionDoc(
		deprecated = "Consider using other alternatives for breaking blocks, e.g. <Player>.breakBlock",
		name = "updateBreakingBlock",
		desc = "This allows you to update your block breaking progress at a position",
		params = {POS, "pos", "the position of the block"},
		examples = "player.updateBreakingBlock(new Pos(0, 0, 0));"
	)
	private Void updateBreakingBlockPos(Arguments arguments) {
		BlockPos blockPos = arguments.skip().nextPrimitive(PosDef.class).getBlockPos();
		ClientScriptUtils.ensureMainThread("updateBreakingBlock", arguments.getInterpreter(), () -> {
			EssentialUtils.getInteractionManager().updateBlockBreakingProgress(blockPos, Direction.UP);
			EssentialUtils.getPlayer().swingHand(Hand.MAIN_HAND);
		});
		return null;
	}

	@FunctionDoc(
		name = "attackBlock",
		desc = "This allows you to attack a block at a position and direction",
		params = {
			NUMBER, "x", "the x position",
			NUMBER, "y", "the y position",
			NUMBER, "z", "the z position",
			STRING, "direction", "the direction of the attack, e.g. 'up', 'north', 'east', etc."
		},
		examples = "player.attackBlock(0, 0, 0, 'up');"
	)
	private Void attackBlock(Arguments arguments) {
		double x = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		double y = arguments.nextPrimitive(NumberDef.class).intValue();
		double z = arguments.nextPrimitive(NumberDef.class).intValue();
		String stringDirection = arguments.nextPrimitive(StringDef.class);
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		ClientScriptUtils.ensureMainThread("attackBlock", arguments.getInterpreter(), () -> {
			EssentialUtils.getInteractionManager().attackBlock(new BlockPos(x, y, z), direction);
		});
		return null;
	}

	@FunctionDoc(
		name = "attackBlock",
		desc = "This allows you to attack a block at a position and direction",
		params = {
			POS, "pos", "the position of the block",
			STRING, "direction", "the direction of the attack, e.g. 'up', 'north', 'east', etc."
		},
		examples = "player.attackBlock(new Pos(0, 0, 0), 'up');"
	)
	private Void attackBlockPos(Arguments arguments) {
		BlockPos pos = arguments.skip().nextPrimitive(PosDef.class).getBlockPos();
		String stringDirection = arguments.nextPrimitive(StringDef.class);
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		ClientScriptUtils.ensureMainThread("attackBlock", arguments.getInterpreter(), () -> {
			EssentialUtils.getInteractionManager().attackBlock(pos, direction);
		});
		return null;
	}

	@FunctionDoc(
		name = "interactItem",
		desc = "This allows you to interact item with given Hand",
		params = {STRING, "hand", " Hand to use, either 'main' or 'offhand'"},
		examples = "player.interactItem('main');"
	)
	private Void interactItem(Arguments arguments) {
		String handAsString = arguments.skip().nextPrimitive(StringDef.class);
		Hand hand = ClientScriptUtils.stringToHand(handAsString);
		ClientScriptUtils.ensureMainThread("interactItem", arguments.getInterpreter(), () -> {
			//#if MC >= 11900
			EssentialUtils.getInteractionManager().interactItem(EssentialUtils.getPlayer(), hand);
			//#else
			//$$EssentialUtils.getInteractionManager().interactItem(EssentialUtils.getPlayer(), EssentialUtils.getWorld(), hand);
			//#endif
		});
		return null;
	}

	@FunctionDoc(
		name = "interactBlock",
		desc = "This allows you to interact with a block at a position and direction",
		params = {
			NUMBER, "x", "the x position",
			NUMBER, "y", "the y position",
			NUMBER, "z", "the z position",
			STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc."
		},
		examples = "player.interactBlock(0, 100, 0, 'up');"
	)
	private Void interactBlock(Arguments arguments) {
		double x = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		double y = arguments.nextPrimitive(NumberDef.class).intValue();
		double z = arguments.nextPrimitive(NumberDef.class).intValue();
		String directionAsString = arguments.nextPrimitive(StringDef.class);
		this.interactInternal(
			arguments.getInterpreter(),
			directionAsString,
			EssentialUtils.getPlayer().getActiveHand(),
			new Vec3d(x, y, z),
			new BlockPos(x, y, z),
			false
		);
		return null;
	}

	@FunctionDoc(
		name = "interactBlock",
		desc = "This allows you to interact with a block at a position and direction",
		params = {
			POS, "pos", "the position of the block",
			STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc."
		},
		examples = "player.interactBlock(new Pos(0, 0, 0), 'up');"
	)
	private Void interactBlockPos(Arguments arguments) {
		ScriptPos pos = arguments.skip().nextPrimitive(PosDef.class);
		String direction = arguments.nextPrimitive(StringDef.class);
		this.interactInternal(
			arguments.getInterpreter(),
			direction,
			EssentialUtils.getPlayer().getActiveHand(),
			pos.getVec3d(),
			pos.getBlockPos(),
			false
		);
		return null;
	}

	@FunctionDoc(
		name = "interactBlock",
		desc = "This allows you to interact with a block at a position, direction, and hand",
		params = {
			POS, "pos", "the position of the block",
			STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc.",
			STRING, "hand", "the hand to use, e.g. 'main_hand', 'off_hand'"
		},
		examples = "player.interactBlock(new Pos(0, 0, 0), 'up', 'off_hand');"
	)
	private Void interactBlockPosHand(Arguments arguments) {
		ScriptPos pos = arguments.skip().nextPrimitive(PosDef.class);
		String direction = arguments.nextPrimitive(StringDef.class);
		String handAsString = arguments.nextPrimitive(StringDef.class);
		Hand hand = ClientScriptUtils.stringToHand(handAsString);
		this.interactInternal(
			arguments.getInterpreter(),
			direction,
			hand,
			pos.getVec3d(),
			pos.getBlockPos(),
			false
		);
		return null;
	}

	@FunctionDoc(
		name = "interactBlock",
		desc = {
			"This allows you to interact with a block at a position and direction",
			"This function is for very specific cases where there needs to be extra precision",
			"like when placing stairs or slabs in certain directions, so the first set of",
			"coords is the exact position of the block, and the second set of coords is the position"
		},
		params = {
			NUMBER, "x", "the exact x position",
			NUMBER, "y", "the exact y position",
			NUMBER, "z", "the exact z position",
			STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc.",
			NUMBER, "blockX", "the x position of the block",
			NUMBER, "blockY", "the y position of the block",
			NUMBER, "blockZ", "the z position of the block",
			BOOLEAN, "insideBlock", "whether the player is inside the block"
		},
		examples = "player.interactBlock(0, 100.5, 0, 'up', 0, 100, 0, true);"
	)
	private Void interactBlockFull(Arguments arguments) {
		double px = arguments.skip().nextPrimitive(NumberDef.class);
		double py = arguments.nextPrimitive(NumberDef.class);
		double pz = arguments.nextPrimitive(NumberDef.class);
		String direction = arguments.nextPrimitive(StringDef.class);
		double bx = arguments.nextPrimitive(NumberDef.class);
		double by = arguments.nextPrimitive(NumberDef.class);
		double bz = arguments.nextPrimitive(NumberDef.class);
		boolean bool = arguments.nextPrimitive(BooleanDef.class);
		this.interactInternal(
			arguments.getInterpreter(),
			direction,
			EssentialUtils.getPlayer().getActiveHand(),
			new Vec3d(px, py, pz),
			new BlockPos(bx, by, bz),
			bool
		);
		return null;
	}

	@FunctionDoc(
		name = "interactBlock",
		desc = {
			"This allows you to interact with a block at a position and direction",
			"This function is for very specific cases where there needs to be extra precision",
			"like when placing stairs or slabs in certain directions, so the first set of",
			"coords is the exact position of the block, and the second set of coords is the position"
		},
		params = {
			POS, "pos", "the exact position of the block",
			STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc.",
			POS, "blockPos", "the position of the block",
			BOOLEAN, "insideBlock", "whether the player is inside the block"
		},
		examples = "player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true);"
	)
	private Void interactBlockFullPos(Arguments arguments) {
		Vec3d pos = arguments.skip().nextPrimitive(PosDef.class).getVec3d();
		String direction = arguments.nextPrimitive(StringDef.class);
		BlockPos blockPos = arguments.nextPrimitive(PosDef.class).getBlockPos();
		this.interactInternal(arguments.getInterpreter(), direction, EssentialUtils.getPlayer().getActiveHand(), pos, blockPos, false);
		return null;
	}

	@FunctionDoc(
		name = "interactBlock",
		desc = {
			"This allows you to interact with a block at a position and direction",
			"This function is for very specific cases where there needs to be extra precision",
			"like when placing stairs or slabs in certain directions, so the first set of",
			"coords is the exact position of the block, and the second set of coords is the position"
		},
		params = {
			POS, "pos", "the exact position of the block",
			STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc.",
			STRING, "hand", "the hand to use, e.g. 'main_hand', 'off_hand'",
			POS, "blockPos", "the position of the block",
			BOOLEAN, "insideBlock", "whether the player is inside the block"
		},
		examples = "player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true, 'off_hand');"
	)
	private Void interactBlockFullPosHand(Arguments arguments) {
		Vec3d pos = arguments.nextPrimitive(PosDef.class).getVec3d();
		String direction = arguments.nextPrimitive(StringDef.class);
		String handAsString = arguments.nextPrimitive(StringDef.class);
		BlockPos blockPos = arguments.nextPrimitive(PosDef.class).getBlockPos();
		Hand hand = ClientScriptUtils.stringToHand(handAsString);
		this.interactInternal(arguments.getInterpreter(), direction, hand, pos, blockPos, false);
		return null;
	}

	@FunctionDoc(
		name = "getBlockBreakingSpeed",
		desc = "This returns the block breaking speed of the player on a block including enchanements and effects",
		params = {ITEM_STACK, "itemStack", "item to test with", BLOCK, "block", "the block to get the speed of"},
		examples = "speed = player.getBlockBreakingSpeed(Material.NETHERITE_PICKAXE.asItem(), Material.GOLD_BLOCK.asBlock());"
	)
	private float getBlockBreakingSpeed(Arguments arguments) {
		ClientPlayerEntity player = EssentialUtils.getPlayer();
		ItemStack itemStack = arguments.skip().nextPrimitive(ItemStackDef.class).stack;
		BlockState state = arguments.nextPrimitive(BlockDef.class).state;
		return EssentialUtils.getBlockBreakingSpeed(itemStack, state, player);
	}

	private void interactInternal(Interpreter interpreter, String directionAsString, Hand hand, Vec3d pos, BlockPos blockPos, boolean insideBlock) {
		Hand finalHand = hand == null ? Hand.MAIN_HAND : hand;
		Direction direction = ClientScriptUtils.stringToDirection(directionAsString, Direction.DOWN);
		BlockHitResult hitResult = new BlockHitResult(pos, direction, blockPos, insideBlock);
		ClientScriptUtils.ensureMainThread("interactBlock", interpreter, () -> {
			//#if MC >= 11900
			EssentialUtils.getInteractionManager().interactBlock(EssentialUtils.getPlayer(), finalHand, hitResult);
			//#else
			//$$EssentialUtils.getInteractionManager().interactBlock(EssentialUtils.getPlayer(), EssentialUtils.getWorld(), finalHand, hitResult);
			//#endif
		});
	}
}
