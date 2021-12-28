package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.*;
import essentialclient.feature.BetterAccurateBlockPlacement;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Set;

public class ArucasPlayerMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.playerFunctions;
	}

	@Override
	public Class<PlayerValue> getValueType() {
		return PlayerValue.class;
	}

	@Override
	public String getName() {
		return "PlayerMemberFunctions";
	}

	private final Set<MemberFunction> playerFunctions = Set.of(
		new MemberFunction("use", "type", this::use),
		new MemberFunction("attack", "type", this::attack),
		new MemberFunction("setSelectedSlot", "slotNum", this::setSelectedSlot),
		new MemberFunction("say", "text", this::say),
		new MemberFunction("message", "text", this::message),
		new MemberFunction("messageActionBar", "text", this::messageActionBar),
		new MemberFunction("showTitle", "text", this::showTitle),
		new MemberFunction("openInventory", this::openInventory),
		new MemberFunction("openScreen", "screen", this::openScreen),
		new MemberFunction("closeScreen", this::closeScreen),
		new MemberFunction("setWalking", "boolean", (context, function) -> this.setKey(context, function, ArucasMinecraftExtension.getClient().options.keyForward)),
		new MemberFunction("setSneaking", "boolean", (context, function) -> this.setKey(context, function, ArucasMinecraftExtension.getClient().options.keySneak)),
		new MemberFunction("setSprinting", "boolean", this::setSprinting),
		new MemberFunction("dropItemInHand", "boolean", this::dropItemInHand),
		new MemberFunction("dropAll", "itemStack", this::dropAll),
		new MemberFunction("look", List.of("yaw", "pitch"), this::look),
		new MemberFunction("fakeLook", List.of("yaw", "pitch", "direction"), this::fakeLook),
		new MemberFunction("lookAtPos", List.of("x", "y", "z"), this::lookAtPos),
		new MemberFunction("jump", this::jump),
		new MemberFunction("getLookingAtEntity", this::getLookingAtEntity),
		new MemberFunction("swapSlots", List.of("slot1", "slot2"), this::swapSlots),
		new MemberFunction("shiftClickSlot", "slot", this::shiftClickSlot),
		new MemberFunction("dropSlot", "slot", this::dropSlot),
		new MemberFunction("getCurrentScreen", this::getCurrentScreen),
		new MemberFunction("craft", "recipe", this::craft),
		new MemberFunction("logout", "message", this::logout),
		new MemberFunction("interactWithEntity", "entity", this::interactWithEntity),
		new MemberFunction("anvil", List.of("predicate1", "predicate2"), this::anvil),
		new MemberFunction("anvilRename", List.of("name", "predicate"), this::anvilRename),
		new MemberFunction("stonecutter", List.of("itemInput", "itemOutput"), this::stonecutter),
		new MemberFunction("updateBreakingBlock", List.of("x", "y", "z"), this::updateBreakingBlock),
		new MemberFunction("interactBlock", List.of("px", "py", "pz", "face", "bx", "by", "bz", "insideBlock"), this::interactBlock),
		new MemberFunction("getItemForSlot","slot", this::getitemForSlot),
		new MemberFunction("swapSlotWithHotbar","slot1", this::swapSlotWithHotbar),
		// Villager Stuff
		new MemberFunction("tradeIndex", "index", this::tradeIndex),
		new MemberFunction("getIndexOfTradeItem", "itemStack", this::getIndexOfTrade),
		new MemberFunction("getTradeItemForIndex", "index", this::getTradeItemForIndex),
		new MemberFunction("doesVillagerHaveTrade", "itemStack", this::doesVillagerHaveTrade),
		new MemberFunction("isTradeDisabled", "index", this::isTradeDisabled),
		new MemberFunction("getPriceForIndex", "index", this::getPriceForIndex)
	);

	private Value<?> use(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		final String error = "Must pass \"hold\", \"stop\" or \"once\" into use()";
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1, error);
		switch (stringValue.value.toLowerCase()) {
			case "hold" -> ArucasMinecraftExtension.getClient().options.keyUse.setPressed(true);
			case "stop" -> ArucasMinecraftExtension.getClient().options.keyUse.setPressed(false);
			case "once" -> ((MinecraftClientInvoker) ArucasMinecraftExtension.getClient()).rightClickMouseAccessor();
			default -> throw function.throwInvalidParameterError(error, context);
		}
		return NullValue.NULL;
	}

	private Value<?> attack(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		final String error = "Must pass \"hold\", 'stop\" or \"once\" into attack()";
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1, error);
		switch (stringValue.value.toLowerCase()) {
			case "hold" -> ArucasMinecraftExtension.getClient().options.keyAttack.setPressed(true);
			case "stop" -> ArucasMinecraftExtension.getClient().options.keyAttack.setPressed(false);
			case "once" -> ((MinecraftClientInvoker) ArucasMinecraftExtension.getClient()).leftClickMouseAccessor();
			default -> throw function.throwInvalidParameterError(error, context);
		}
		return NullValue.NULL;
	}

	private Value<?> setSelectedSlot(Context context, MemberFunction function) throws CodeError {
		final String error = "Number must be between 0 - 8";
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1, error);
		if (numberValue.value < 0 || numberValue.value > 8) {
			throw function.throwInvalidParameterError(error, context);
		}
		this.getPlayer(context, function).inventory.selectedSlot = numberValue.value.intValue();
		ClientPlayerEntity player = this.getPlayer(context, function);
		player.inventory.selectedSlot = numberValue.value.intValue();
		ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
		ArucasMinecraftExtension.getClient().execute(()->{player.inventory.selectedSlot = numberValue.value.intValue();
			networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(numberValue.value.intValue()));});
		return NullValue.NULL;
	}

	private Value<?> say(Context context, MemberFunction function) throws CodeError {
		this.getPlayer(context, function).sendChatMessage(function.getParameterValue(context, 1).toString());
		return NullValue.NULL;
	}

	private Value<?> message(Context context, MemberFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 1);
		final ClientPlayerEntity player = this.getPlayer(context, function);
		Text text = value instanceof TextValue textValue ? textValue.value : new LiteralText(value.toString());
		ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, false));
		return NullValue.NULL;
	}

	private Value<?> messageActionBar(Context context, MemberFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 1);
		final ClientPlayerEntity player = this.getPlayer(context, function);
		Text text = value instanceof TextValue textValue ? textValue.value : new LiteralText(value.toString());
		ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, true));
		return NullValue.NULL;
	}

	private Value<?> showTitle(Context context, MemberFunction function) throws CodeError {
		this.getPlayer(context, function);
		Value<?> value = function.getParameterValue(context, 1);
		Value<?> subValue = function.getParameterValue(context, 2);
		Text text = value instanceof TextValue textValue ? textValue.value : value.value == null ? null : new LiteralText(value.toString());
		Text subText = subValue instanceof TextValue textValue ? textValue.value : subValue.value == null ? null : new LiteralText(subValue.toString());
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> client.inGameHud.setTitles(text, subText, -1, -1, -1));
		return NullValue.NULL;
	}

	private Value<?> openInventory(Context context, MemberFunction function) throws CodeError {
		final ClientPlayerEntity player = this.getPlayer(context, function);
		final MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> client.openScreen(new InventoryScreen(player)));
		return NullValue.NULL;
	}

	private Value<?> openScreen(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		final MinecraftClient client = ArucasMinecraftExtension.getClient();
		ScreenValue screenValue = function.getParameterValueOfType(context, ScreenValue.class, 1);
		if (screenValue.value instanceof HandledScreen && !(screenValue.value instanceof FakeInventoryScreen)) {
			throw new RuntimeError("Opening handled screens is unsafe", function.syntaxPosition, context);
		}
		client.execute(() -> client.openScreen(screenValue.value));
		return NullValue.NULL;
	}

	private Value<?> closeScreen(Context context, MemberFunction function) throws CodeError {
		final ClientPlayerEntity player = this.getPlayer(context, function);
		final MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(player::closeHandledScreen);
		return NullValue.NULL;
	}

	private Value<?> setSprinting(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		final ClientPlayerEntity player = this.getPlayer(context, function);
		ArucasMinecraftExtension.getClient().execute(() -> player.setSprinting(booleanValue.value));
		return NullValue.NULL;
	}

	private Value<?> dropItemInHand(Context context, MemberFunction function) throws CodeError {
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		this.getPlayer(context, function).dropSelectedItem(booleanValue.value);
		return NullValue.NULL;
	}

	private Value<?> dropAll(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> InventoryUtils.dropAllItemType(client.player, itemStackValue.value.getItem()));
		return NullValue.NULL;
	}

	private Value<?> look(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 2);
		ClientPlayerEntity player = this.getPlayer(context, function);
		player.yaw = numberValue.value.floatValue();
		player.pitch = numberValue2.value.floatValue();
		return NullValue.NULL;
	}
	private Value<?> fakeLook(Context context, MemberFunction function) throws CodeError {
		//registers fake looking request at endtick event
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 2);
		Direction direction = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 3).value);
		BetterAccurateBlockPlacement.onRequest = true;
		BetterAccurateBlockPlacement.fakeRequestYaw = numberValue.value.floatValue();
		BetterAccurateBlockPlacement.fakeRequestPitch = numberValue2.value.floatValue();
		BetterAccurateBlockPlacement.fakeDirection = direction;
		return NullValue.NULL;
	}
	private Value<?> lookAtPos(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = this.getPlayer(context, function);
		double x = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double y = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double z = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(x, y, z));
		return NullValue.NULL;
	}

	private Value<?> jump(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = this.getPlayer(context, function);
		ArucasMinecraftExtension.getClient().execute(() -> {
			if (player.isOnGround()) {
				player.jump();
			}
		});
		return NullValue.NULL;
	}

	private Value<?> getLookingAtEntity(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		Entity targetedEntity = ArucasMinecraftExtension.getClient().targetedEntity;
		return EntityValue.getEntityValue(targetedEntity);
	}

	private Value<?> swapSlots(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1);
		NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 2);
		ClientPlayerEntity player = this.getPlayer(context, function);
		ScreenHandler screenHandler = player.currentScreenHandler;
		int size = screenHandler.slots.size();
		if (numberValue1.value > size || numberValue1.value < 0 || numberValue2.value > size || numberValue2.value < 0) {
			throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
		}
		ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
		ArucasMinecraftExtension.getClient().execute(() -> {
			interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue(), 0, SlotActionType.SWAP, player);
			interactionManager.clickSlot(screenHandler.syncId, numberValue2.value.intValue(), 0, SlotActionType.SWAP, player);
			interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue(), 0, SlotActionType.SWAP, player);
		});
		return NullValue.NULL;
	}
	private Value<?> swapSlotWithHotbar(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1);
		ClientPlayerEntity player = this.getPlayer(context, function);
		ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
		if (numberValue1.value < 0 || numberValue1.value > 40) {
			throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
		}
		ArucasMinecraftExtension.getClient().execute(()->{player.inventory.swapSlotWithHotbar(numberValue1.value.intValue());
			networkHandler.sendPacket(new PickFromInventoryC2SPacket(numberValue1.value.intValue()));});
		return NullValue.NULL;
	}
	private Value<?> shiftClickSlot(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1);
		ScreenHandler screenHandler = this.getPlayer(context, function).currentScreenHandler;
		int size = screenHandler.slots.size();
		if (numberValue1.value > size || numberValue1.value < 0) {
			throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
		}
		ArucasMinecraftExtension.getInteractionManager().clickSlot(screenHandler.syncId, numberValue1.value.intValue(), 0, SlotActionType.QUICK_MOVE, this.getPlayer(context, function));
		return NullValue.NULL;
	}

	private Value<?> dropSlot(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		ArucasMinecraftExtension.getInteractionManager().clickSlot(this.getPlayer(context, function).currentScreenHandler.syncId, numberValue.value.intValue(), 1, SlotActionType.THROW, ArucasMinecraftExtension.getClient().player);
		return NullValue.NULL;
	}

	private Value<?> getCurrentScreen(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		Screen currentScreen = ArucasMinecraftExtension.getClient().currentScreen;
		if (currentScreen == null) {
			return NullValue.NULL;
		}
		return new ScreenValue(currentScreen);
	}

	private Value<?> craft(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 1);
		if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
			return NullValue.NULL;
		}
		int listSize = listValue.value.size();
		if (listSize == 9) {
			if (!(handledScreen instanceof CraftingScreen)) {
				throw new RuntimeError("You must be in a crafting table to craft a 3x3", function.syntaxPosition, context);
			}
		}
		else if (listSize != 4) {
			throw new RuntimeError("Recipe must either be 3x3 or 2x2", function.syntaxPosition, context);
		}
		ItemStack[] itemStacks = new ItemStack[listSize];
		for (int i = 0; i < listSize; i++) {
			if (!(listValue.value.get(i) instanceof ItemStackValue itemStackValue)) {
				throw new RuntimeError("The recipe must only include items", function.syntaxPosition, context);
			}
			itemStacks[i] = itemStackValue.value;
		}
		InventoryUtils.tryMoveItemsToCraftingGridSlots(client, itemStacks, handledScreen);
		if (handledScreen.getScreenHandler().slots.get(0).getStack() == ItemStack.EMPTY) {
			return BooleanValue.FALSE;
		}
		InventoryUtils.shiftClickSlot(client, handledScreen, 0);
		return BooleanValue.TRUE;
	}

	private Value<?> logout(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		String reason = function.getParameterValueOfType(context, StringValue.class, 1).value;
		ArucasMinecraftExtension.getNetworkHandler().onDisconnected(new LiteralText(reason));
		return NullValue.NULL;
	}

	private Value<?> interactWithEntity(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player =  this.getPlayer(context, function);
		EntityValue<?> entity  = function.getParameterValueOfType(context, EntityValue.class, 1);
		ArucasMinecraftExtension.getInteractionManager().interactEntity(player, entity.value, Hand.MAIN_HAND);
		return NullValue.NULL;
	}

	private Value<?> anvil(Context context, MemberFunction function) throws CodeError {
		ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
		if (interactionManager == null) {
			return BooleanValue.FALSE;
		}
		ClientPlayerEntity player =  this.getPlayer(context, function);
		ScreenHandler handler = player.currentScreenHandler;
		if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
			throw new RuntimeError("Not in anvil gui", function.syntaxPosition, context);
		}
		FunctionValue predicate1 = function.getParameterValueOfType(context, FunctionValue.class, 1);
		FunctionValue predicate2 = function.getParameterValueOfType(context, FunctionValue.class, 2);
		boolean firstValid = false;
		boolean secondValid = false;
		for (Slot slot : anvilHandler.slots) {
			if (firstValid && secondValid) {
				break;
			}
			ItemStack itemStack = slot.getStack();
			try {
				if (!firstValid) {
					Value<?> predicateReturn = predicate1.call(context.createBranch(), List.of(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						firstValid = true;
						interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
						interactionManager.clickSlot(anvilHandler.syncId, 0, 0, SlotActionType.PICKUP, player);
						continue;
					}
				}
				if (!secondValid) {
					Value<?> predicateReturn = predicate2.call(context.createBranch(), List.of(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						secondValid = true;
						interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
						interactionManager.clickSlot(anvilHandler.syncId, 1, 0, SlotActionType.PICKUP, player);
					}
				}
			}
			catch (ThrowValue throwValue) {
				throw new RuntimeError("Invalid function parameter", function.syntaxPosition, context);
			}
		}
		anvilHandler.updateResult();
		if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
			return new NumberValue(anvilHandler.getLevelCost());
		}
		interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player);
		return BooleanValue.of(firstValid && secondValid);
	}

	private Value<?> anvilRename(Context context, MemberFunction function) throws CodeError {
		ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
		if (interactionManager == null) {
			return NullValue.NULL;
		}
		ClientPlayerEntity player =  this.getPlayer(context, function);
		ScreenHandler handler = player.currentScreenHandler;
		if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
			throw new RuntimeError("Not in anvil gui", function.syntaxPosition, context);
		}
		String newName = function.getParameterValueOfType(context, StringValue.class, 1).value;
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 2);
		boolean valid = false;
		for (Slot slot : handler.slots) {
			ItemStack itemStack = slot.getStack();
			try {
				if (!itemStack.getName().asString().equals(newName)) {
					Value<?> predicateReturn = functionValue.call(context.createBranch(), List.of(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						valid = true;
						interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player);
						break;
					}
				}
			}
			catch (ThrowValue throwValue) {
				throw new RuntimeError("Invalid function parameter", function.syntaxPosition, context);
			}
		}
		anvilHandler.updateResult();
		anvilHandler.setNewItemName(newName);
		EssentialUtils.getNetworkHandler().sendPacket(new RenameItemC2SPacket(newName));
		if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
			return new NumberValue(anvilHandler.getLevelCost());
		}
		interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player);
		return BooleanValue.of(valid);
	}

	private Value<?> stonecutter(Context context, MemberFunction function) throws CodeError {
		ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
		if (interactionManager == null) {
			return BooleanValue.FALSE;
		}
		ClientPlayerEntity player =  this.getPlayer(context, function);
		ScreenHandler handler = player.currentScreenHandler;
		if (!(handler instanceof StonecutterScreenHandler cutterHandler)) {
			throw new RuntimeError("Not in stonecutter gui", function.syntaxPosition, context);
		}
		Item itemInput = function.getParameterValueOfType(context, ItemStackValue.class, 1).value.getItem();
		Item itemOutput = function.getParameterValueOfType(context, ItemStackValue.class, 2).value.getItem();
		boolean valid = false;
		for (Slot slot : cutterHandler.slots) {
			if (slot.getStack().getItem() == itemInput) {
				interactionManager.clickSlot(cutterHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player);
				valid = true;
				break;
			}
		}
		if (!valid) {
			return BooleanValue.FALSE;
		}
		List<StonecuttingRecipe> stonecuttingRecipes = cutterHandler.getAvailableRecipes();
		for (int i = 0; i < stonecuttingRecipes.size(); i++) {
			if (stonecuttingRecipes.get(i).getOutput().getItem() == itemOutput) {
				interactionManager.clickButton(cutterHandler.syncId, i);
				interactionManager.clickSlot(cutterHandler.syncId, 1, 0, SlotActionType.QUICK_MOVE, player);
				return BooleanValue.TRUE;
			}
		}
		throw new RuntimeError("Recipe does not exist", function.syntaxPosition, context);
	}
	private Value<?> updateBreakingBlock(Context context, MemberFunction function) throws CodeError{
		ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
		double x = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double y = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double z = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		if (ArucasMinecraftExtension.getWorld().isAir(new BlockPos(x,y,z))) {
			return NullValue.NULL;
		}
		ArucasMinecraftExtension.getClient().execute(()-> interactionManager.updateBlockBreakingProgress(new BlockPos(x,y,z), Direction.UP));
		this.getPlayer(context, function).swingHand(Hand.MAIN_HAND);
		return NullValue.NULL;
	}

	private Value<?> interactBlock(Context context, MemberFunction function) throws CodeError {
		//carpet protocol support but why not client side?
		double px = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double py = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double pz = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		Direction direction = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 4).value);
		double bx = function.getParameterValueOfType(context, NumberValue.class, 5).value;
		double by = function.getParameterValueOfType(context, NumberValue.class, 6).value;
		double bz = function.getParameterValueOfType(context, NumberValue.class, 7).value;
		boolean bool = function.getParameterValueOfType(context, BooleanValue.class, 8).value;
		BlockHitResult hitResult = new BlockHitResult(new Vec3d(px, py, pz), direction, new BlockPos(bx, by, bz), bool);
		ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
		ClientPlayerEntity player = this.getPlayer(context, function);
		ClientWorld world = ArucasMinecraftExtension.getWorld();
		ArucasMinecraftExtension.getClient().execute(()->interactionManager.interactBlock(player,world, Hand.MAIN_HAND, hitResult));
		return NullValue.NULL;
	}
	private Value<?> tradeIndex(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		if (!InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
			throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
		}
		return NullValue.NULL;
	}
	private Value<?> getitemForSlot(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = this.getPlayer(context, function);
		int slot = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
		if (slot < 0 || slot > 40){throw new RuntimeError("slot number is not valid", function.syntaxPosition, context); }
		ItemStack itemStack = player.inventory.main.get(slot);
		return new ItemStackValue(itemStack);
	}
	private Value<?> getIndexOfTrade(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
		if (index == -1) {
			return NullValue.NULL;
		}
		this.checkVillagerValid(index, function, context);
		return new NumberValue(index);
	}

	private Value<?> getTradeItemForIndex(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		try {
			ItemStack itemStack = InventoryUtils.getTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			if (itemStack == null) {
				throw new RuntimeError("That trade is out of bounds", function.syntaxPosition, context);
			}
			return new ItemStackValue(itemStack);
		}
		catch (RuntimeException e) {
			throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
		}
	}

	private Value<?> doesVillagerHaveTrade(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
		return BooleanValue.of(this.checkVillagerValid(code, function, context));
	}

	private Value<?> isTradeDisabled(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
		return BooleanValue.of(this.checkVillagerValid(code, function, context));
	}

	private Value<?> getPriceForIndex(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
		this.checkVillagerValid(price, function, context);
		return new NumberValue(price);
	}

	public boolean checkVillagerValid(int code, AbstractBuiltInFunction<?> function, Context context) throws RuntimeError {
		boolean bool = false;
		switch (code) {
			case -2 -> throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			case -1 -> throw new RuntimeError("That trade is out of bounds", function.syntaxPosition, context);
			case 1 -> bool = true;
		}
		return bool;
	}

	private NullValue setKey(Context context, MemberFunction function, KeyBinding keyBinding) throws CodeError {
		this.checkMainPlayer(context, function);
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		keyBinding.setPressed(booleanValue.value);
		return NullValue.NULL;
	}

	private void checkMainPlayer(Context context, MemberFunction function) throws CodeError {
		function.getParameterValueOfType(context, PlayerValue.class, 0);
	}

	private ClientPlayerEntity getPlayer(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = function.getParameterValueOfType(context, PlayerValue.class, 0).value;
		if (player == null) {
			throw new RuntimeError("Player was null", function.syntaxPosition, context);
		}
		return player;
	}
}
