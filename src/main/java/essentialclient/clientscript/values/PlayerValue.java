package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import essentialclient.feature.BetterAccurateBlockPlacement;
import essentialclient.feature.CraftingSharedConstants;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
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
import net.minecraft.network.packet.c2s.play.*;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;

public class PlayerValue extends AbstractPlayerValue<ClientPlayerEntity> {
	public PlayerValue(ClientPlayerEntity player) {
		super(player);
	}

	@Override
	public Value<ClientPlayerEntity> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "Player{name=%s}".formatted(this.value.getEntityName());
	}

	public static class ArucasPlayerClass extends ArucasClassExtension {
		public ArucasPlayerClass() {
			super("Player");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("get", this::get)
			);
		}

		private Value<?> get(Context context, BuiltInFunction function) throws CodeError {
			return new PlayerValue(ArucasMinecraftExtension.getPlayer());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
				new MemberFunction("lookAtPos", List.of("x", "y", "z"), this::lookAtPos),
				new MemberFunction("lookAtPos", "pos", this::lookAtPosPos),
				new MemberFunction("jump", this::jump),
				new MemberFunction("getLookingAtEntity", this::getLookingAtEntity),
				new MemberFunction("swapSlots", List.of("slot1", "slot2"), this::swapSlots),
				new MemberFunction("shiftClickSlot", "slot", this::shiftClickSlot),
				new MemberFunction("dropSlot", "slot", this::dropSlot),
				new MemberFunction("getCurrentScreen", this::getCurrentScreen),
				new MemberFunction("craft", "recipe", this::craft),
				new MemberFunction("craftRecipe", List.of("recipe"), this::craftRecipe),
				new MemberFunction("logout", "message", this::logout),
				new MemberFunction("attackEntity", "entity", this::attackEntity),
				new MemberFunction("interactWithEntity", "entity", this::interactWithEntity),
				new MemberFunction("anvil", List.of("predicate1", "predicate2"), this::anvil),
				new MemberFunction("anvilRename", List.of("name", "predicate"), this::anvilRename),
				new MemberFunction("stonecutter", List.of("itemInput", "itemOutput"), this::stonecutter),
				new MemberFunction("fakeLook", List.of("yaw", "pitch", "direction", "duration"), this::fakeLook),
				new MemberFunction("swapPlayerSlotWithHotbar", "slot1", this::swapPlayerSlotWithHotbar),
				new MemberFunction("updateBreakingBlock", List.of("x", "y", "z"), this::updateBreakingBlock),
				new MemberFunction("updateBreakingBlock", "pos", this::updateBreakingBlockPos),
				new MemberFunction("attackBlock", List.of("x", "y", "z", "direction"), this::attackBlock),
				new MemberFunction("attackBlock", List.of("pos", "direction"), this::attackBlockPos),
				new MemberFunction("interactBlock", List.of("x", "y", "z", "face"), this::interactBlock),
				new MemberFunction("interactBlock", List.of("pos", "face"), this::interactBlockPos),
				new MemberFunction("interactBlock", List.of("px", "py", "pz", "face", "bx", "by", "bz", "insideBlock"), this::interactBlockFull),
				new MemberFunction("interactBlock", List.of("pos", "face", "pos", "insideBlock"), this::interactBlockFullPos),
				new MemberFunction("getBlockBreakingSpeed", "blockState", this::getBlockBreakingSpeed),
				new MemberFunction("swapHands", this::swapHands),
				new MemberFunction("swingHand", "isOffHand", this::swingHand),
				new MemberFunction("clickSlot", List.of("slot", "clickData", "slotActionType"), this::clickSlot),
				new MemberFunction("getSwappableHotbarSlot", this::getSwappableHotbarSlot),
				new MemberFunction("spectatorTeleport", "entity", this::spectatorTeleport),

				// Villager Stuff
				new MemberFunction("tradeIndex", "index", this::tradeIndex, "Use '<MerchantScreen>.tradeIndex(index)'"),
				new MemberFunction("getIndexOfTradeItem", "itemStack", this::getIndexOfTrade, "Use '<MerchantScreen>.getIndexOfTradeItem(itemStack)'"),
				new MemberFunction("getTradeItemForIndex", "index", this::getTradeItemForIndex, "Use '<MerchantScreen>.getTradeItemForIndex(index)'"),
				new MemberFunction("doesVillagerHaveTrade", "itemStack", this::doesVillagerHaveTrade, "Use '<MerchantScreen>.doesVillagerHaveTrade(itemStack)'"),
				new MemberFunction("isTradeDisabled", "index", this::isTradeDisabled, "Use '<MerchantScreen>.isTradeDisabled(index)'"),
				new MemberFunction("getPriceForIndex", "index", this::getPriceForIndex, "Use '<MerchantScreen>.getPriceForIndex(index)'")
			);
		}

		private Value<?> use(Context context, MemberFunction function) throws CodeError {
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
			final String error = "Must pass \"hold\", \"stop\" or \"once\" into attack()";
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
			int selectedSlot = numberValue.value.intValue();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ClientPlayNetworkHandler networkhandler = ArucasMinecraftExtension.getNetworkHandler();
			final ClientPlayerEntity player = this.getPlayer(context, function);
			client.execute(() -> {
				networkhandler.sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
				player.getInventory().selectedSlot = selectedSlot;
			});
			return NullValue.NULL;
		}

		private Value<?> say(Context context, MemberFunction function) throws CodeError {
			this.getPlayer(context, function).sendChatMessage(function.getParameterValue(context, 1).getAsString(context));
			return NullValue.NULL;
		}

		private Value<?> message(Context context, MemberFunction function) throws CodeError {
			Value<?> value = function.getParameterValue(context, 1);
			final ClientPlayerEntity player = this.getPlayer(context, function);
			Text text = value instanceof TextValue textValue ? textValue.value : new LiteralText(value.getAsString(context));
			ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, false));
			return NullValue.NULL;
		}

		private Value<?> messageActionBar(Context context, MemberFunction function) throws CodeError {
			Value<?> value = function.getParameterValue(context, 1);
			final ClientPlayerEntity player = this.getPlayer(context, function);
			Text text = value instanceof TextValue textValue ? textValue.value : new LiteralText(value.getAsString(context));
			ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, true));
			return NullValue.NULL;
		}

		private Value<?> showTitle(Context context, MemberFunction function) throws CodeError {
			Value<?> value = function.getParameterValue(context, 1);
			Value<?> subValue = function.getParameterValue(context, 2);
			Text text = value instanceof TextValue textValue ? textValue.value : value.value == null ? null : new LiteralText(value.getAsString(context));
			Text subText = subValue instanceof TextValue textValue ? textValue.value : subValue.value == null ? null : new LiteralText(subValue.getAsString(context));
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> {
				client.inGameHud.setTitle(text);
				client.inGameHud.setSubtitle(subText);
			});
			return NullValue.NULL;
		}

		private Value<?> openInventory(Context context, MemberFunction function) throws CodeError {
			final ClientPlayerEntity player = this.getPlayer(context, function);
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> client.setScreen(new InventoryScreen(player)));
			return NullValue.NULL;
		}

		private Value<?> openScreen(Context context, MemberFunction function) throws CodeError {
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			ScreenValue<?> screenValue = function.getParameterValueOfType(context, ScreenValue.class, 1);
			if (screenValue.value instanceof HandledScreen && !(screenValue.value instanceof FakeInventoryScreen)) {
				throw new RuntimeError("Opening handled screens is unsafe", function.syntaxPosition, context);
			}
			client.execute(() -> client.setScreen(screenValue.value));
			return NullValue.NULL;
		}

		private Value<?> closeScreen(Context context, MemberFunction function) throws CodeError {
			final ClientPlayerEntity player = this.getPlayer(context, function);
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(player::closeHandledScreen);
			return NullValue.NULL;
		}

		private Value<?> setSprinting(Context context, MemberFunction function) throws CodeError {
			BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
			final ClientPlayerEntity player = this.getPlayer(context, function);
			ArucasMinecraftExtension.getClient().execute(() -> player.setSprinting(booleanValue.value));
			return NullValue.NULL;
		}

		private Value<?> dropItemInHand(Context context, MemberFunction function) throws CodeError {
			BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
			ClientPlayerEntity player = this.getPlayer(context, function);
			ArucasMinecraftExtension.getClient().execute(() -> player.dropSelectedItem(booleanValue.value));
			return NullValue.NULL;
		}

		private Value<?> dropAll(Context context, MemberFunction function) throws CodeError {
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> InventoryUtils.dropAllItemType(client.player, itemStackValue.value.getItem()));
			return NullValue.NULL;
		}

		private Value<?> look(Context context, MemberFunction function) throws CodeError {
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 2);
			ClientPlayerEntity player = this.getPlayer(context, function);
			player.setYaw(numberValue.value.floatValue());
			player.setPitch(numberValue2.value.floatValue());
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

		private Value<?> lookAtPosPos(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(context, function);
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, posValue.value);
			return NullValue.NULL;
		}

		private Value<?> jump(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(context, function);
			ArucasMinecraftExtension.getClient().execute(() -> {
				if (player.isOnGround()) {
					player.jump();
					player.setOnGround(false);
				}
			});
			return NullValue.NULL;
		}

		private Value<?> getLookingAtEntity(Context context, MemberFunction function) throws CodeError {
			Entity targetedEntity = ArucasMinecraftExtension.getClient().targetedEntity;
			return of(targetedEntity);
		}

		private Value<?> swapSlots(Context context, MemberFunction function) throws CodeError {
			int slot1 = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
			int slot2 = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			ClientPlayerEntity player = this.getPlayer(context, function);
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot1 >= size || slot1 < 0 || slot2 >= size || slot2 < 0) {
				throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
			}
			int firstMapped = InventoryUtils.isSlotInHotbar(screenHandler, slot1);
			int secondMapped = InventoryUtils.isSlotInHotbar(screenHandler, slot2);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (firstMapped == -1) {
				if (secondMapped == -1) {
					client.execute(() -> {
						interactionManager.clickSlot(screenHandler.syncId, slot1, 0, SlotActionType.SWAP, player);
						interactionManager.clickSlot(screenHandler.syncId, slot2, 0, SlotActionType.SWAP, player);
						interactionManager.clickSlot(screenHandler.syncId, slot1, 0, SlotActionType.SWAP, player);
						player.getInventory().updateItems();
					});
				}
				else {
					InventoryUtils.swapSlot(client, screenHandler, slot1, secondMapped);
				}
			}
			else {
				InventoryUtils.swapSlot(client, screenHandler, slot2, firstMapped);
			}
			return NullValue.NULL;
		}

		private Value<?> getSwappableHotbarSlot(Context context, MemberFunction function) throws CodeError {
			// Return predicted current swappable hotbar slot
			ClientPlayerEntity player = this.getPlayer(context, function);
			return NumberValue.of(player.getInventory().getSwappableHotbarSlot());
		}

		private Value<?> spectatorTeleport(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(context, function);
			if (!player.isSpectator()) {
				return BooleanValue.FALSE;
			}
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 1);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			networkHandler.sendPacket(new SpectatorTeleportC2SPacket(entityValue.value.getUuid()));
			return BooleanValue.TRUE;
		}

		private Value<?> swapHands(Context context, MemberFunction function) throws CodeError {
			final ClientPlayerEntity player = this.getPlayer(context, function);
			final ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			if (player.isSpectator()) {
				return BooleanValue.FALSE;
			}
			ArucasMinecraftExtension.getClient().execute(() -> networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN)));
			return BooleanValue.TRUE;
		}

		private Value<?> swingHand(Context context, MemberFunction function) throws CodeError {
			final ClientPlayerEntity player = this.getPlayer(context, function);
			StringValue handAsString = function.getParameterValueOfType(context, StringValue.class, 1);
			Hand hand = switch (handAsString.value.toLowerCase()) {
				case "main", "main_hand" -> Hand.MAIN_HAND;
				case "off", "off_hand" -> Hand.OFF_HAND;
				default -> throw new RuntimeError("Invalid hand '%s'".formatted(handAsString.value), function.syntaxPosition, context);
			};
			player.swingHand(hand);
			return NullValue.NULL;
		}

		private Value<?> clickSlot(Context context, MemberFunction function) throws CodeError {
			int slot = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
			Value<?> clickDataValue = function.getParameterValue(context, 2);
			int clickData;
			if (clickDataValue instanceof StringValue clickDataString) {
				clickData = switch (clickDataString.value) {
					case "right" -> 1;
					case "left" -> 0;
					default -> throw new RuntimeError("Invalid clickData must be \"left\" or \"right\" or a number", function.syntaxPosition, context);
				};
			}
			else if (clickDataValue instanceof NumberValue clickDataNumber) {
				clickData = clickDataNumber.value.intValue();
			}
			else {
				throw new RuntimeError("Invalid clickData must be \"left\" or \"right\" or a number", function.syntaxPosition, context);
			}
			String stringValue = function.getParameterValueOfType(context, StringValue.class, 3).value.toLowerCase();
			SlotActionType slotActionType = switch (stringValue) {
				case "click", "pickup" -> SlotActionType.PICKUP;
				case "shift_click", "quick_move" -> SlotActionType.QUICK_MOVE;
				case "swap" -> SlotActionType.SWAP;
				case "middle_click", "clone" -> SlotActionType.CLONE;
				case "throw" -> SlotActionType.THROW;
				case "drag", "quick_craft" -> SlotActionType.QUICK_CRAFT;
				case "double_click", "pickup_all" -> SlotActionType.PICKUP_ALL;
				default -> throw new RuntimeError("Invalid slotActionType, see Wiki", function.syntaxPosition, context);
			};
			ScreenHandler screenHandler = this.getPlayer(context, function).currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot != -999 && slot >= size || slot < 0) {
				throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(context, function);
			int finalClickData = clickData;
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(screenHandler.syncId, slot, finalClickData, slotActionType, player));
			return NullValue.NULL;
		}

		private Value<?> shiftClickSlot(Context context, MemberFunction function) throws CodeError {
			NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1);
			ScreenHandler screenHandler = this.getPlayer(context, function).currentScreenHandler;
			int size = screenHandler.slots.size();
			if (numberValue1.value >= size || numberValue1.value < 0) {
				throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(context, function);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue(), 0, SlotActionType.QUICK_MOVE, player));
			return NullValue.NULL;
		}

		private Value<?> dropSlot(Context context, MemberFunction function) throws CodeError {
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			ArucasMinecraftExtension.getInteractionManager()
				.clickSlot(this.getPlayer(context, function).currentScreenHandler.syncId, numberValue.value.intValue(), 1, SlotActionType.THROW, ArucasMinecraftExtension.getPlayer());
			return NullValue.NULL;
		}

		private Value<?> getCurrentScreen(Context context, MemberFunction function) throws CodeError {
			Screen currentScreen = ArucasMinecraftExtension.getClient().currentScreen;
			return ScreenValue.of(currentScreen);
		}

		private Value<?> craft(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 1);
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw new RuntimeError("Must be in a crafting GUI", function.syntaxPosition, context);
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
			client.execute(() -> InventoryUtils.tryMoveItemsToCraftingGridSlots(client, itemStacks, handledScreen));
			if (handledScreen.getScreenHandler().slots.get(0).getStack() == ItemStack.EMPTY) {
				return BooleanValue.FALSE;
			}
			client.execute(() -> InventoryUtils.shiftClickSlot(client, handledScreen, 0));
			return BooleanValue.TRUE;
		}

		private Value<?> craftRecipe(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw new RuntimeError("Must be in a crafting GUI", function.syntaxPosition, context);
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			RecipeValue recipeValue = function.getParameterValueOfType(context, RecipeValue.class, 1);
			client.execute(() -> {
				CraftingSharedConstants.IS_SCRIPT_CLICK.set(true);
				interactionManager.clickRecipe(handledScreen.getScreenHandler().syncId, recipeValue.value, true);
				InventoryUtils.shiftClickSlot(client, handledScreen, 0);
			});
			return NullValue.NULL;
		}

		private Value<?> logout(Context context, MemberFunction function) throws CodeError {
			String reason = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ArucasMinecraftExtension.getNetworkHandler().onDisconnected(new LiteralText(reason));
			return NullValue.NULL;
		}

		private Value<?> attackEntity(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(context, function);
			EntityValue<?> entity = function.getParameterValueOfType(context, EntityValue.class, 1);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> {
				interactionManager.attackEntity(player, entity.value);
			});
			return NullValue.NULL;
		}

		private Value<?> interactWithEntity(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(context, function);
			EntityValue<?> entity = function.getParameterValueOfType(context, EntityValue.class, 1);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> {
				interactionManager.interactEntity(player, entity.value, Hand.MAIN_HAND);
			});
			return NullValue.NULL;
		}

		private Value<?> anvil(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return BooleanValue.FALSE;
			}
			ClientPlayerEntity player = this.getPlayer(context, function);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
				throw new RuntimeError("Not in anvil gui", function.syntaxPosition, context);
			}
			FunctionValue predicate1 = function.getParameterValueOfType(context, FunctionValue.class, 1);
			FunctionValue predicate2 = function.getParameterValueOfType(context, FunctionValue.class, 2);
			boolean firstValid = false;
			boolean secondValid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : anvilHandler.slots) {
				if (firstValid && secondValid) {
					break;
				}
				ItemStack itemStack = slot.getStack();
				if (!firstValid) {
					Value<?> predicateReturn = predicate1.call(context.createBranch(), List.of(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						firstValid = true;
						client.execute(() -> {
							interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
							interactionManager.clickSlot(anvilHandler.syncId, 0, 0, SlotActionType.PICKUP, player);
						});
						continue;
					}
				}
				if (!secondValid) {
					Value<?> predicateReturn = predicate2.call(context.createBranch(), List.of(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						secondValid = true;
						client.execute(() -> {
							interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
							interactionManager.clickSlot(anvilHandler.syncId, 1, 0, SlotActionType.PICKUP, player);
						});
					}
				}
			}
			anvilHandler.updateResult();
			if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
				return NumberValue.of(anvilHandler.getLevelCost());
			}
			interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player);
			return BooleanValue.of(firstValid && secondValid);
		}

		private Value<?> anvilRename(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return NullValue.NULL;
			}
			ClientPlayerEntity player = this.getPlayer(context, function);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
				throw new RuntimeError("Not in anvil gui", function.syntaxPosition, context);
			}
			String newName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 2);
			boolean valid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : handler.slots) {
				ItemStack itemStack = slot.getStack();
				if (!itemStack.getName().asString().equals(newName)) {
					Value<?> predicateReturn = functionValue.call(context.createBranch(), List.of(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						valid = true;
						client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player));
						break;
					}
				}
			}
			anvilHandler.updateResult();
			anvilHandler.setNewItemName(newName);
			EssentialUtils.getNetworkHandler().sendPacket(new RenameItemC2SPacket(newName));
			if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
				return NumberValue.of(anvilHandler.getLevelCost());
			}
			client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player));
			return BooleanValue.of(valid);
		}

		private Value<?> stonecutter(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return BooleanValue.FALSE;
			}
			ClientPlayerEntity player = this.getPlayer(context, function);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof StonecutterScreenHandler cutterHandler)) {
				throw new RuntimeError("Not in stonecutter gui", function.syntaxPosition, context);
			}
			Item itemInput = function.getParameterValueOfType(context, ItemStackValue.class, 1).value.getItem();
			Item itemOutput = function.getParameterValueOfType(context, ItemStackValue.class, 2).value.getItem();
			boolean valid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : cutterHandler.slots) {
				if (slot.getStack().getItem() == itemInput) {
					client.execute(() -> interactionManager.clickSlot(cutterHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player));
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
					int iFinal = i;
					client.execute(() -> {
						interactionManager.clickButton(cutterHandler.syncId, iFinal);
						interactionManager.clickSlot(cutterHandler.syncId, 1, 0, SlotActionType.QUICK_MOVE, player);
					});
					return BooleanValue.TRUE;
				}
			}
			throw new RuntimeError("Recipe does not exist", function.syntaxPosition, context);
		}

		private Value<?> fakeLook(Context context, MemberFunction function) throws CodeError {
			NumberValue yaw = function.getParameterValueOfType(context, NumberValue.class, 1);
			NumberValue pitch = function.getParameterValueOfType(context, NumberValue.class, 2);
			StringValue directionValue = function.getParameterValueOfType(context, StringValue.class, 3);
			NumberValue durationValue = function.getParameterValueOfType(context, NumberValue.class, 4);
			Direction direction = Objects.requireNonNullElse(Direction.byName(directionValue.value), Direction.DOWN);
			int duration = MathHelper.ceil(durationValue.value);
			duration = duration > 0 ? duration : 20;
			BetterAccurateBlockPlacement.fakeYaw = yaw.value.floatValue();
			BetterAccurateBlockPlacement.fakePitch = pitch.value.floatValue();
			BetterAccurateBlockPlacement.fakeDirection = direction;
			BetterAccurateBlockPlacement.requestedTicks = duration;
			return NullValue.NULL;
		}

		private Value<?> swapPlayerSlotWithHotbar(Context context, MemberFunction function) throws CodeError {
			NumberValue slotToSwap = function.getParameterValueOfType(context, NumberValue.class, 1);
			ClientPlayerEntity player = this.getPlayer(context, function);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			if (slotToSwap.value < 0 || slotToSwap.value > player.getInventory().main.size()) {
				throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
			}
			int prepareSlot = player.getInventory().getSwappableHotbarSlot();
			ArucasMinecraftExtension.getClient().execute(() -> {
				networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prepareSlot));
				player.getInventory().swapSlotWithHotbar(slotToSwap.value.intValue());
				networkHandler.sendPacket(new PickFromInventoryC2SPacket(slotToSwap.value.intValue()));
			});
			return NullValue.NULL;
		}

		private Value<?> updateBreakingBlock(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			double x = function.getParameterValueOfType(context, NumberValue.class, 1).value;
			double y = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			double z = function.getParameterValueOfType(context, NumberValue.class, 3).value;
			BlockPos blockPos = new BlockPos(x, y, z);
			if (ArucasMinecraftExtension.getWorld().isAir(blockPos)) {
				return NullValue.NULL;
			}
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP));
			this.getPlayer(context, function).swingHand(Hand.MAIN_HAND);
			return NullValue.NULL;
		}

		private Value<?> updateBreakingBlockPos(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			BlockPos blockPos = new BlockPos(posValue.value);
			if (ArucasMinecraftExtension.getWorld().isAir(blockPos)) {
				return NullValue.NULL;
			}
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP));
			this.getPlayer(context, function).swingHand(Hand.MAIN_HAND);
			return NullValue.NULL;
		}

		private Value<?> attackBlock(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			double x = function.getParameterValueOfType(context, NumberValue.class, 1).value;
			double y = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			double z = function.getParameterValueOfType(context, NumberValue.class, 3).value;
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 4);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackBlock(new BlockPos(x, y, z), direction));
			return NullValue.NULL;
		}

		private Value<?> attackBlockPos(Context context, MemberFunction function) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 2);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackBlock(new BlockPos(posValue.value), direction));
			return NullValue.NULL;
		}

		private Value<?> interactBlock(Context context, MemberFunction function) throws CodeError {
			double x = function.getParameterValueOfType(context, NumberValue.class, 1).value;
			double y = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			double z = function.getParameterValueOfType(context, NumberValue.class, 3).value;
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 4);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(new Vec3d(x, y, z), direction, new BlockPos(x, y, z), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(context, function);
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value<?> interactBlockPos(Context context, MemberFunction function) throws CodeError {
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 2);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(posValue.value, direction, new BlockPos(posValue.value), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(context, function);
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value<?> interactBlockFull(Context context, MemberFunction function) throws CodeError {
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
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value<?> interactBlockFullPos(Context context, MemberFunction function) throws CodeError {
			PosValue posValue = function.getParameterValueOfType(context, PosValue.class, 1);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 2);
			PosValue blockPosValue = function.getParameterValueOfType(context, PosValue.class, 3);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(posValue.value, direction, new BlockPos(blockPosValue.value), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(context, function);
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value<?> getBlockBreakingSpeed(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(context, function);
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 1);
			float breakingSpeed = player.getBlockBreakingSpeed(blockStateValue.value);
			return NumberValue.of(breakingSpeed);
		}

		private Value<?> tradeIndex(Context context, MemberFunction function) throws CodeError {
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			if (!InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> getIndexOfTrade(Context context, MemberFunction function) throws CodeError {
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			if (index == -1) {
				return NullValue.NULL;
			}
			this.checkVillagerValid(index, function, context);
			return NumberValue.of(index);
		}

		private Value<?> getTradeItemForIndex(Context context, MemberFunction function) throws CodeError {
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
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, function, context));
		}

		private Value<?> isTradeDisabled(Context context, MemberFunction function) throws CodeError {
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, function, context));
		}

		private Value<?> getPriceForIndex(Context context, MemberFunction function) throws CodeError {
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			this.checkVillagerValid(price, function, context);
			return NumberValue.of(price);
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

			BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
			keyBinding.setPressed(booleanValue.value);
			return NullValue.NULL;
		}

		private ClientPlayerEntity getPlayer(Context context, MemberFunction function) throws CodeError {
			ClientPlayerEntity player = function.getParameterValueOfType(context, PlayerValue.class, 0).value;
			if (player == null) {
				throw new RuntimeError("Player was null", function.syntaxPosition, context);
			}
			return player;
		}

		@Override
		public Class<?> getValueClass() {
			return PlayerValue.class;
		}
	}
}
