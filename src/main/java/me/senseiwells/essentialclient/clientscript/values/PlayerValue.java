package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.MaterialLike;
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
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class PlayerValue extends AbstractPlayerValue<ClientPlayerEntity> {
	public PlayerValue(ClientPlayerEntity player) {
		super(player);
	}

	@Override
	public EntityValue<ClientPlayerEntity> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "Player{name=%s}".formatted(this.value.getEntityName());
	}

	@Override
	public String getTypeName() {
		return PLAYER;
	}

	@ClassDoc(
		name = PLAYER,
		desc = {
			"This class is used to interact with the main player, this extends OtherPlayer",
			"and so inherits all methods from that class."
		},
		importPath = "Minecraft"
	)
	public static class ArucasPlayerClass extends ArucasClassExtension {
		public ArucasPlayerClass() {
			super(PLAYER);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("get", this::get)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "get",
			desc = "This gets the main player",
			returns = {PLAYER, "The main player"},
			example = "player = Player.get();"
		)
		private Value get(Arguments arguments) throws CodeError {
			return new PlayerValue(ArucasMinecraftExtension.getPlayer());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("use", 1, this::use),
				MemberFunction.of("attack", 1, this::attack),
				MemberFunction.of("setSelectedSlot", 1, this::setSelectedSlot),
				MemberFunction.of("say", 1, this::say),
				MemberFunction.of("message", 1, this::message),
				MemberFunction.of("messageActionBar", 1, this::messageActionBar),
				MemberFunction.of("showTitle", 1, this::showTitle),
				MemberFunction.of("openInventory", this::openInventory),
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
				MemberFunction.of("logout", 1, this::logout),
				MemberFunction.of("attackEntity", 1, this::attackEntity),
				MemberFunction.of("interactWithEntity", 1, this::interactWithEntity),
				MemberFunction.of("anvil", 2, this::anvilDrop),
				MemberFunction.of("anvil", 3, this::anvil),
				MemberFunction.of("anvilRename", 2, this::anvilRename),
				MemberFunction.of("stonecutter", 2, this::stonecutter),
				MemberFunction.of("fakeLook", 4, this::fakeLook),
				MemberFunction.of("swapPlayerSlotWithHotbar", 1, this::swapPlayerSlotWithHotbar),
				MemberFunction.of("breakBlock", 1, this::breakBlock),
				MemberFunction.of("breakBlock", 2, this::breakBlockMore),
				MemberFunction.of("updateBreakingBlock", 3, this::updateBreakingBlock),
				MemberFunction.of("updateBreakingBlock", 1, this::updateBreakingBlockPos),
				MemberFunction.of("attackBlock", 4, this::attackBlock),
				MemberFunction.of("attackBlock", 2, this::attackBlockPos),
				MemberFunction.of("interactBlock", 4, this::interactBlock),
				MemberFunction.of("interactBlock", 2, this::interactBlockPos),
				MemberFunction.of("interactBlock", 3, this::interactBlockPosHand),
				MemberFunction.of("interactBlock", 8, this::interactBlockFull),
				MemberFunction.of("interactBlock", 4, this::interactBlockFullPos),
				MemberFunction.of("interactBlock", 5, this::interactBlockFullPosHand),
				MemberFunction.of("getBlockBreakingSpeed", 1, this::getBlockBreakingSpeed),
				MemberFunction.of("swapHands", this::swapHands),
				MemberFunction.of("swingHand", 1, this::swingHand),
				MemberFunction.of("clickSlot", 3, this::clickSlot),
				MemberFunction.of("getSwappableHotbarSlot", this::getSwappableHotbarSlot),
				MemberFunction.of("spectatorTeleport", 1, this::spectatorTeleport),
				MemberFunction.of("canPlaceBlockAt", 2, this::canPlaceBlockAtPos),
				MemberFunction.of("canPlaceBlockAt", 4, this::canPlaceBlockAtPos1),

				// Villager Stuff
				MemberFunction.of("tradeIndex", 1, this::tradeIndex, "Use '<MerchantScreen>.tradeIndex(index)'"),
				MemberFunction.of("getIndexOfTradeItem", 1, this::getIndexOfTrade, "Use '<MerchantScreen>.getIndexOfTradeItem(itemStack)'"),
				MemberFunction.of("getTradeItemForIndex", 1, this::getTradeItemForIndex, "Use '<MerchantScreen>.getTradeItemForIndex(index)'"),
				MemberFunction.of("doesVillagerHaveTrade", 1, this::doesVillagerHaveTrade, "Use '<MerchantScreen>.doesVillagerHaveTrade(itemStack)'"),
				MemberFunction.of("isTradeDisabled", 1, this::isTradeDisabled, "Use '<MerchantScreen>.isTradeDisabled(index)'"),
				MemberFunction.of("getPriceForIndex", 1, this::getPriceForIndex, "Use '<MerchantScreen>.getPriceForIndex(index)'")
			);
		}

		@FunctionDoc(
			name = "use",
			desc = "This allows you to make your player use",
			params = {STRING, "action", "the type of action, either 'hold', 'stop', or 'once'"},
			throwMsgs = "Must pass 'hold', 'stop', or 'once' into use()",
			example = "player.use('hold');"
		)
		private Value use(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			switch (stringValue.value.toLowerCase()) {
				case "hold" -> client.execute(() -> client.options.useKey.setPressed(true));
				case "stop" -> client.execute(() -> client.options.useKey.setPressed(false));
				case "once" -> ((MinecraftClientInvoker) client).rightClickMouseAccessor();
				default -> throw arguments.getError("Must pass 'hold', 'stop' or 'once' into use()");
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "attack",
			desc = "This allows you to make your player attack",
			params = {STRING, "action", "the type of action, either 'hold', 'stop', or 'once'"},
			throwMsgs = "Must pass 'hold', 'stop', or 'once' into attack()",
			example = "player.attack('once');"
		)
		private Value attack(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			switch (stringValue.value.toLowerCase()) {
				case "hold" -> client.execute(() -> client.options.attackKey.setPressed(true));
				case "stop" -> client.execute(() -> client.options.attackKey.setPressed(false));
				case "once" -> ((MinecraftClientInvoker) client).leftClickMouseAccessor();
				default -> throw arguments.getError("Must pass 'hold', 'stop' or 'once' into attack()");
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "setSelectedSlot",
			desc = "This allows you to set the slot number your player is holding",
			params = {NUMBER, "slot", "the slot number, must be between 0 - 8"},
			throwMsgs = "Number must be between 0 - 8",
			example = "player.setSelectedSlot(0);"
		)
		private Value setSelectedSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			if (numberValue.value < 0 || numberValue.value > 8) {
				throw arguments.getError("Number must be between 0 - 8");
			}
			int selectedSlot = numberValue.value.intValue();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			client.execute(() -> {
				networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
				player.getInventory().selectedSlot = selectedSlot;
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "say",
			desc = "This allows you to make your player send a message in chat, this includes commands",
			params = {STRING, "message", "the message to send"},
			example = "player.say('/help');"
		)
		private Value say(Arguments arguments) throws CodeError {
			EssentialUtils.sendChatMessage(arguments.skip().getNext().getAsString(arguments.getContext()));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "message",
			desc = "This allows you to send a message to your player, only they will see this, purely client side",
			params = {TEXT, "message", "the message to send, can also be string"},
			example = "player.message('Hello World!');"
		)
		private Value message(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			Value value = arguments.getNext();
			Text text = value instanceof TextValue textValue ? textValue.value : Texts.literal(value.getAsString(arguments.getContext()));
			ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, false));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "messageActionBar",
			desc = "This allows you to set the current memssage displaying on the action bar",
			params = {TEXT, "message", "the message to send, can also be string"},
			example = "player.messageActionBar('Hello World!');"
		)
		private Value messageActionBar(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			Value value = arguments.getNext();
			Text text = value instanceof TextValue textValue ? textValue.value : Texts.literal(value.getAsString(arguments.getContext()));
			ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, true));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "showTitle",
			desc = "THis allows you to show a title and subtitle to the player",
			params = {
				TEXT, "title", "the title to show, can be string or null",
				TEXT, "subtitle", "the subtitle to show, can be string or null"
			},
			example = "player.showTitle('Title!', 'Subtitle!');"
		)
		private Value showTitle(Arguments arguments) throws CodeError {
			Value value = arguments.skip().getNext();
			Value subValue = arguments.getNext();
			Context context = arguments.getContext();
			Text text = value instanceof TextValue textValue ? textValue.value : value.getValue() == null ? null : Texts.literal(value.getAsString(context));
			Text subText = subValue instanceof TextValue textValue ? textValue.value : subValue.getValue() == null ? null : Texts.literal(subValue.getAsString(context));
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> {
				client.inGameHud.setTitle(text);
				client.inGameHud.setSubtitle(subText);
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "openInventory",
			desc = "This opens the player's inventory",
			example = "player.openInventory();"
		)
		private Value openInventory(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> client.setScreen(new InventoryScreen(player)));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "openScreen",
			desc = "This opens a screen for the player, this cannot open server side screens",
			params = {SCREEN, "screen", "the screen to open"},
			throwMsgs = "Opening handled screens is unsafe",
			example = "player.openScreen(new FakeScreen('MyScreen', 4));"
		)
		private Value openScreen(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ScreenValue<?> screenValue = arguments.skip().getNext(ScreenValue.class);
			if (screenValue.value instanceof HandledScreen && !(screenValue.value instanceof FakeInventoryScreen)) {
				throw arguments.getError("Opening handled screens is unsafe");
			}
			client.execute(() -> client.setScreen(screenValue.value));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "closeScreen",
			desc = "This closes the current screen",
			example = "player.closeScreen();"
		)
		private Value closeScreen(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(player::closeHandledScreen);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "setWalking",
			desc = "This sets the player's walking state",
			params = {BOOLEAN, "walking", "the walking state"},
			example = "player.setWalking(true);"
		)
		private Value setWalking(Arguments arguments) throws CodeError {
			return this.setKey(arguments, ArucasMinecraftExtension.getClient().options.forwardKey);
		}

		@FunctionDoc(
			name = "setSneaking",
			desc = "This sets the player's sneaking state",
			params = {BOOLEAN, "sneaking", "the sneaking state"},
			example = "player.setSneaking(true);"
		)
		private Value setSneaking(Arguments arguments) throws CodeError {
			return this.setKey(arguments, ArucasMinecraftExtension.getClient().options.sneakKey);
		}

		@FunctionDoc(
			name = "setSprinting",
			desc = "This sets the player's sprinting state",
			params = {BOOLEAN, "sprinting", "the sprinting state"},
			example = "player.setSprinting(true);"
		)
		private Value setSprinting(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BooleanValue booleanValue = arguments.getNextBoolean();
			ArucasMinecraftExtension.getClient().execute(() -> player.setSprinting(booleanValue.value));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "dropItemInHand",
			desc = "This drops the item(s) in the player's main hand",
			params = {BOOLEAN, "dropAll", "if true, all items in the player's main hand will be dropped"},
			example = "player.dropItemInHand(true);"
		)
		private Value dropItemInHand(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BooleanValue booleanValue = arguments.getNextBoolean();
			ArucasMinecraftExtension.getClient().execute(() -> player.dropSelectedItem(booleanValue.value));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "dropAll",
			desc = "This drops all items of a given type in the player's inventory",
			params = {MATERIAL_LIKE, "materialLike", "the item stack, or material type to drop"},
			example = "player.dropAll(Material.DIRT.asItemStack());"
		)
		private Value dropAll(Arguments arguments) throws CodeError {
			MaterialLike materialLike = arguments.skip().getAnyNext(MaterialLike.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> InventoryUtils.dropAllItemType(client.player, materialLike.asItem()));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "dropAllExact",
			desc = "This drops all the items that have the same nbt as a given stack",
			params = {ITEM_STACK, "itemStack", "the stack with nbt to drop"},
			example = "player.dropAllExact(Material.GOLD_INGOT.asItemStack());"
		)
		private Value dropAllExact(Arguments arguments) throws CodeError {
			ItemStack stack = arguments.skip().getNextGeneric(ItemStackValue.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> InventoryUtils.dropAllItemExact(stack));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "look",
			desc = "This sets the player's look direction",
			params = {
				NUMBER, "yaw", "the yaw of the player's look direction",
				NUMBER, "pitch", "the pitch of the player's look direction"
			},
			example = "player.look(0, 0);"
		)
		private Value look(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			NumberValue numberValue2 = arguments.getNextNumber();
			ArucasMinecraftExtension.getClient().execute(() -> {
				player.setYaw(numberValue.value.floatValue());
				player.setPitch(numberValue2.value.floatValue());
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "lookAtPos",
			desc = "This makes your player look towards a position",
			params = {
				NUMBER, "x", "the x coordinate of the position",
				NUMBER, "y", "the y coordinate of the position",
				NUMBER, "z", "the z coordinate of the position"
			},
			example = "player.lookAtPos(0, 0, 0);"
		)
		private Value lookAtPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			ArucasMinecraftExtension.getClient().execute(() -> player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(x, y, z)));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "lookAtPos",
			desc = "This makes your player look towards a position",
			params = {POS, "pos", "the position to look at"},
			example = "player.lookAtPos(pos);"
		)
		private Value lookAtPosPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			ArucasMinecraftExtension.getClient().execute(() -> player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, posValue.value));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "canPlaceBlockAt",
			desc = "Checks block can be placed at given position",
			params = {POS, "pos", "the position to check"},
			example = "player.canPlaceBlockAt(block, pos);"
		)
		private Value canPlaceBlockAtPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BlockState blockState = arguments.getNext(BlockValue.class).value;
			PosValue posValue = arguments.getNext(PosValue.class);
			boolean canPlace = blockState.canPlaceAt(player.clientWorld, posValue.toBlockPos());
			canPlace = canPlace && player.clientWorld.canPlace(blockState, posValue.toBlockPos(), ShapeContext.of(player));
			return BooleanValue.of(canPlace);
		}

		@FunctionDoc(
			name = "canPlaceBlockAt",
			desc = "Checks block can be placed at given position",
			params = {
				NUMBER, "x", "the x coordinate of the position",
				NUMBER, "y", "the y coordinate of the position",
				NUMBER, "z", "the z coordinate of the position"
			},
			example = "player.canPlaceBlockAt(block, 0, 0, 0);"
		)
		private Value canPlaceBlockAtPos1(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BlockState blockState = arguments.getNextGeneric(BlockValue.class);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			BlockPos pos = new BlockPos(x, y, z);
			boolean canPlace = blockState.canPlaceAt(player.clientWorld, pos);
			canPlace = canPlace && player.clientWorld.canPlace(blockState, pos, ShapeContext.of(player));
			return BooleanValue.of(canPlace);
		}

		@FunctionDoc(
			name = "jump",
			desc = "This will make the player jump if they are on the ground",
			example = "player.jump();"
		)
		private Value jump(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			ArucasMinecraftExtension.getClient().execute(() -> {
				if (player.isOnGround()) {
					player.jump();
					player.setOnGround(false);
				}
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getLookingAtEntity",
			desc = "This gets the entity that the player is currently looking at",
			returns = {ENTITY, "the entity that the player is looking at"},
			example = "player.getLookingAtEntity();"
		)
		private Value getLookingAtEntity(Arguments arguments) throws CodeError {
			if (ArucasMinecraftExtension.getClient().crosshairTarget instanceof EntityHitResult hitResult) {
				return arguments.getContext().convertValue(hitResult.getEntity());
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "swapSlots",
			desc = {
				"The allows you to swap two slots with one another",
				"A note about slot order is that slots go from top to bottom"
			},
			params = {
				NUMBER, "slot1", "the slot to swap with slot2",
				NUMBER, "slot2", "the slot to swap with slot1"
			},
			throwMsgs = "That slot is out of bounds",
			example = "player.swapSlots(13, 14);"
		)
		private Value swapSlots(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			int slot1 = arguments.getNextGeneric(NumberValue.class).intValue();
			int slot2 = arguments.getNextGeneric(NumberValue.class).intValue();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot1 >= size || slot1 < 0 || slot2 >= size || slot2 < 0) {
				throw arguments.getError("That slot is out of bounds");
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
			example = "player.getSwappableHotbarSlot();"
		)
		private Value getSwappableHotbarSlot(Arguments arguments) throws CodeError {
			// Return predicted current swappable hotbar slot
			ClientPlayerEntity player = this.getPlayer(arguments);
			return NumberValue.of(player.getInventory().getSwappableHotbarSlot());
		}

		@FunctionDoc(
			name = "spectatorTeleport",
			desc = "This allows you to teleport to any entity as long as you are in spectator mode",
			params = {ENTITY, "entity", "the entity to teleport to"},
			example = "player.spectatorTeleport(player.getLookingAtEntity());"
		)
		private Value spectatorTeleport(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			if (!player.isSpectator()) {
				return BooleanValue.FALSE;
			}
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			networkHandler.sendPacket(new SpectatorTeleportC2SPacket(entityValue.value.getUuid()));
			return BooleanValue.TRUE;
		}

		@FunctionDoc(
			name = "swapHands",
			desc = "This will swap the player's main hand with the off hand",
			example = "player.swapHands();"
		)
		private Value swapHands(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			if (player.isSpectator()) {
				return BooleanValue.FALSE;
			}
			ArucasMinecraftExtension.getClient().execute(() ->
				networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN))
			);
			return BooleanValue.TRUE;
		}

		@FunctionDoc(
			name = "swingHand",
			desc = "This will play the player's hand swing animation for a given hand",
			params = {STRING, "hand", "the hand to swing, this should be either 'main_hand' or 'off_hand'"},
			example = "player.swingHand('main_hand');"
		)
		private Value swingHand(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			StringValue handAsString = arguments.getNextString();
			Hand hand = switch (handAsString.value.toLowerCase()) {
				case "main", "main_hand" -> Hand.MAIN_HAND;
				case "off", "off_hand" -> Hand.OFF_HAND;
				default -> throw arguments.getError("Invalid hand '%s'", handAsString);
			};
			ArucasMinecraftExtension.getClient().execute(() -> player.swingHand(hand));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "clickSlot",
			desc = {
				"This allows you to click a slot with either right or left click",
				"and a slot action, the click must be either 'left' or 'right' or a number (for swap).",
				"The action must be either 'click', 'shift_click', 'swap', 'middle_click',",
				"'throw', 'drag', or 'double_click'"
			},
			params = {
				NUMBER, "slot", "the slot to click",
				STRING, "click", "the click type, this should be either 'left' or 'right'",
				STRING, "action", "the action to perform"
			},
			throwMsgs = {
				"Invalid clickData must be 'left' or 'right' or a number",
				"Invalid slotActionType, see Wiki",
				"That slot is out of bounds"
			},
			example = "player.clickSlot(9, 'left', 'double_click');"
		)
		private Value clickSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			int slot = arguments.getNextGeneric(NumberValue.class).intValue();
			Value clickDataValue = arguments.getNext();
			int clickData;
			if (clickDataValue instanceof StringValue clickDataString) {
				clickData = switch (clickDataString.value) {
					case "right" -> 1;
					case "left" -> 0;
					default -> throw arguments.getError("Invalid clickData must be 'left' or 'right' or a number");
				};
			}
			else if (clickDataValue instanceof NumberValue clickDataNumber) {
				clickData = clickDataNumber.value.intValue();
			}
			else {
				throw arguments.getError("Invalid clickData must be 'left' or 'right' or a number");
			}
			String stringValue = arguments.getNextGeneric(StringValue.class).toLowerCase();
			SlotActionType slotActionType = switch (stringValue) {
				case "click", "pickup" -> SlotActionType.PICKUP;
				case "shift_click", "quick_move" -> SlotActionType.QUICK_MOVE;
				case "swap" -> SlotActionType.SWAP;
				case "middle_click", "clone" -> SlotActionType.CLONE;
				case "throw" -> SlotActionType.THROW;
				case "drag", "quick_craft" -> SlotActionType.QUICK_CRAFT;
				case "double_click", "pickup_all" -> SlotActionType.PICKUP_ALL;
				default -> throw arguments.getError("Invalid slotActionType, see Wiki");
			};
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot != -999 && slot >= size || slot < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			int finalClickData = clickData;
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(screenHandler.syncId, slot, finalClickData, slotActionType, player));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "shiftClickSlot",
			desc = "This allows you to shift click a slot",
			params = {NUMBER, "slot", "the slot to click"},
			throwMsgs = "That slot is out of bounds",
			example = "player.shiftClickSlot(9);"
		)
		private Value shiftClickSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue number = arguments.getNextNumber();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (number.value >= size || number.value < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(screenHandler.syncId, number.value.intValue(), 0, SlotActionType.QUICK_MOVE, player));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "dropSlot",
			desc = "This allows you to drop the items in a slot",
			params = {NUMBER, "slot", "the slot to drop"},
			throwMsgs = "That slot is out of bounds",
			example = "player.dropSlot(9);"
		)
		private Value dropSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (numberValue.value >= size || numberValue.value < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			final ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(player.currentScreenHandler.syncId, numberValue.value.intValue(), 1, SlotActionType.THROW, player));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getCurrentScreen",
			desc = "This gets the current screen the player is in",
			returns = {SCREEN, "the screen the player is in, if the player is not in a screen it will return null"},
			example = "screen = player.getCurrentScreen();"
		)
		private Value getCurrentScreen(Arguments arguments) throws CodeError {
			Screen currentScreen = ArucasMinecraftExtension.getClient().currentScreen;
			return arguments.getContext().convertValue(currentScreen);
		}

		@FunctionDoc(
			name = "craft",
			desc = {
				"This allows you to craft a recipe, this can be 2x2 or 3x3",
				"The list you pass in must contain Materials or ItemStacks",
				"Most of the time you should use craftRecipe instead"
			},
			params = {LIST, "recipe", "a list of materials making up the recipe you want to craft including air"},
			throwMsgs = {
				"Must be in a crafting GUI",
				"You must be in a crafting table to craft a 3x3",
				"Recipe must either be 3x3 or 2x2",
				"The recipe must only include items or materials"
			},
			example = """
				chestRecipe = [
					Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS,
					Material.OAK_PLANKS,    Material.AIR    , Material.OAK_PLANKS,
					Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS
				];
				player.craft(chestRecipe);
				"""
		)
		private Value craft(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ListValue listValue = arguments.skip().getNextList();
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw arguments.getError("Must be in a crafting GUI");
			}

			int listSize = listValue.value.size();
			if (listSize == 9) {
				if (!(handledScreen instanceof CraftingScreen)) {
					throw arguments.getError("You must be in a crafting table to craft a 3x3");
				}
			}
			else if (listSize != 4) {
				throw arguments.getError("Recipe must either be 3x3 or 2x2");
			}

			ItemStack[] itemStacks = new ItemStack[listSize];
			for (int i = 0; i < listSize; i++) {
				Value value = listValue.value.get(i);
				if (value instanceof MaterialLike materialLike) {
					itemStacks[i] = materialLike.asItemStack();
					continue;
				}
				throw arguments.getError("The recipe must only include items or materials");
			}
			client.execute(() -> {
				InventoryUtils.tryMoveItemsToCraftingGridSlots(client, itemStacks, handledScreen);
				InventoryUtils.shiftClickSlot(client, handledScreen, 0);
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "craftRecipe",
			desc = "This allows you to craft a predefined recipe",
			params = {RECIPE, "recipe", "the recipe you want to craft"},
			throwMsgs = "Must be in a crafting GUI",
			example = "player.craftRecipe(Recipe.CHEST);"
		)
		private Value craftRecipe(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw arguments.getError("Must be in a crafting GUI");
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			RecipeValue recipeValue = arguments.skip().getNext(RecipeValue.class);
			client.execute(() -> {
				CraftingSharedConstants.IS_SCRIPT_CLICK.set(true);
				interactionManager.clickRecipe(handledScreen.getScreenHandler().syncId, recipeValue.value, true);
				InventoryUtils.shiftClickSlot(client, handledScreen, 0);
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "logout",
			desc = "This forces the player to leave the world",
			params = {STRING, "message", "the message to display to the player on the logout screen"},
			example = "player.logout('You've been lazy!');"
		)
		private Value logout(Arguments arguments) throws CodeError {
			String reason = arguments.skip().getNextGeneric(StringValue.class);
			ArucasMinecraftExtension.getNetworkHandler().onDisconnected(Texts.literal(reason));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "attackEntity",
			desc = {
				"This makes your player attack an entity without",
				"having to be looking at it or clicking on the entity"
			},
			params = {ENTITY, "entity", "the entity to attack"},
			example = """
				allEntities = client.getWorld().getAllEntities();
				foreach (entity : allEntities) {
					if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
						player.attackEntity(entity);
						break;
					}
				}
				"""
		)
		private Value attackEntity(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			EntityValue<?> entity = arguments.getNext(EntityValue.class);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackEntity(player, entity.value));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "interactWithEntity",
			desc = {
				"This allows your player to interact with an entity without",
				"having to be looking at it or clicking on the entity"
			},
			params = {ENTITY, "entity", "the entity to interact with"},
			example = """
				allEntities = client.getWorld().getAllEntities();
				foreach (entity : allEntities) {
					if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
						player.interactWithEntity(entity);
						break;
					}
				}
				"""
		)
		private Value interactWithEntity(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			EntityValue<?> entity = arguments.getNext(EntityValue.class);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactEntity(player, entity.value, Hand.MAIN_HAND));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "anvil",
			desc = "This allows you to combine two items in an anvil",
			params = {
				FUNCTION, "predicate1", "a function determining whether the first ItemStack meets a criteria",
				FUNCTION, "predicate2", "a function determining whether the second ItemStack meets a criteria",
				BOOLEAN, "take", "whether you should take the item after putting items in the anvil"
			},
			returns = {BOOLEAN, "whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost"},
			throwMsgs = {
				"Not in anvil gui",
				"Invalid function parameter"
			},
			example = """
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
		)
		private Value anvil(Arguments arguments) throws CodeError {
			return this.anvilInternal(arguments, true);
		}

		@FunctionDoc(
			name = "anvil",
			desc = "This allows you to combine two items in an anvil",
			params = {
				FUNCTION, "predicate1", "a function determining whether the first ItemStack meets a criteria",
				FUNCTION, "predicate2", "a function determining whether the second ItemStack meets a criteria"
			},
			returns = {BOOLEAN, "whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost"},
			throwMsgs = {
				"Not in anvil gui",
				"Invalid function parameter"
			},
			example = """
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
		private Value anvilDrop(Arguments arguments) throws CodeError {
			return this.anvilInternal(arguments, false);
		}

		@FunctionDoc(
			name = "anvilRename",
			desc = "This allows you to name an item in an anvil",
			params = {
				STRING, "name", "the name you want to give the item",
				FUNCTION, "predicate", "whether the ItemStack meets a certain criteria"
			},
			returns = {BOOLEAN, "whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost"},
			throwMsgs = {
				"Not in anvil gui",
				"Invalid function parameter"
			},
			example = """
				// Rename any shulker box
				player.anvilRename("Rocket Box",
					fun(item) {
						isShulker = item.getItemId().containsString("shulker_box"));
						return isShulker;
					}
				);
				"""
		)
		private Value anvilRename(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return NullValue.NULL;
			}
			ClientPlayerEntity player = this.getPlayer(arguments);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
				throw arguments.getError("Not in anvil gui");
			}
			String newName = arguments.getNextGeneric(StringValue.class);
			FunctionValue functionValue = arguments.getNextFunction();
			boolean valid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : handler.slots) {
				ItemStack itemStack = slot.getStack();
				if (!itemStack.getName().getString().equals(newName)) {
					Value predicateReturn = functionValue.call(arguments.getContext().createBranch(), ArucasList.arrayListOf(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						valid = true;
						client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player));
						break;
					}
				}
			}
			anvilHandler.updateResult();
			anvilHandler.setNewItemName(newName);
			client.execute(() -> EssentialUtils.getNetworkHandler().sendPacket(new RenameItemC2SPacket(newName)));
			if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
				return NumberValue.of(anvilHandler.getLevelCost());
			}
			client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player));
			return BooleanValue.of(valid);
		}

		@FunctionDoc(
			name = "stonecutter",
			desc = "This allows you to use the stonecutter",
			params = {
				MATERIAL_LIKE, "itemInput", "the item or material you want to input",
				MATERIAL_LIKE, "itemOutput", "the item or material you want to craft"
			},
			returns = {BOOLEAN, "whether the result was successful"},
			throwMsgs = {
				"Not in stonecutter gui",
				"Recipe does not exist"
			},
			example = "player.stonecutter(Material.STONE.asItemstack(), Material.STONE_BRICKS.asItemStack());"
		)
		private Value stonecutter(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return BooleanValue.FALSE;
			}
			ClientPlayerEntity player = this.getPlayer(arguments);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof StonecutterScreenHandler cutterHandler)) {
				throw arguments.getError("Not in stonecutter gui");
			}
			Item itemInput = arguments.getAnyNext(MaterialLike.class).asItem();
			Item itemOutput = arguments.getAnyNext(MaterialLike.class).asItem();
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
			throw arguments.getError("Recipe does not exist");
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
			example = "player.fakeLook(90, 0, 'up', 100);"
		)
		private Value fakeLook(Arguments arguments) throws CodeError {
			NumberValue yaw = arguments.skip().getNextNumber();
			NumberValue pitch = arguments.getNextNumber();
			StringValue directionValue = arguments.getNextString();
			NumberValue durationValue = arguments.getNextNumber();
			Direction direction = ClientScriptUtils.stringToDirection(directionValue.value, Direction.DOWN);
			int duration = MathHelper.ceil(durationValue.value);
			duration = duration > 0 ? duration : 20;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			int finalDuration = duration;
			client.execute(() -> {
				BetterAccurateBlockPlacement.fakeYaw = yaw.value.floatValue();
				BetterAccurateBlockPlacement.fakePitch = pitch.value.floatValue();
				BetterAccurateBlockPlacement.fakeDirection = direction;
				BetterAccurateBlockPlacement.requestedTicks = finalDuration;
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "swapPlayerSlotWithHotbar",
			desc = "This allows you to swap a slot in the player's inventory with the hotbar",
			params = {NUMBER, "slot", "the slot to swap"},
			throwMsgs = "That slot is out of bounds",
			example = "player.swapPlayerSlotWithHotbar(15);"
		)
		private Value swapPlayerSlotWithHotbar(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue slotToSwap = arguments.getNextNumber();
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			if (slotToSwap.value < 0 || slotToSwap.value > player.getInventory().main.size()) {
				throw arguments.getError("That slot is out of bounds");
			}
			int prepareSlot = player.getInventory().getSwappableHotbarSlot();
			ArucasMinecraftExtension.getClient().execute(() -> {
				networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prepareSlot));
				player.getInventory().swapSlotWithHotbar(slotToSwap.value.intValue());
				networkHandler.sendPacket(new PickFromInventoryC2SPacket(slotToSwap.value.intValue()));
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "breakBlock",
			desc = "This breaks a block at a given position, if it is able to be broken",
			params = {POS, "pos", "the position of the block"},
			returns = {BOOLEAN, "whether the block can be broken"},
			example = "player.breakBlock(new Pos(0, 0, 0));"
		)
		private Value breakBlock(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ClientPlayerEntity player = this.getPlayer(arguments);
			BlockPos pos = arguments.getNext(PosValue.class).toBlockPos();
			if (!EssentialUtils.canMineBlock(player, pos)) {
				return BooleanValue.FALSE;
			}

			arguments.getContext().getThreadHandler().loopAsyncFunctionInThreadPool(
				50, TimeUnit.MILLISECONDS,
				() -> EssentialUtils.canMineBlock(player, pos), () -> { },
				null, c -> client.execute(() -> interactionManager.updateBlockBreakingProgress(pos, Direction.DOWN))
			);

			return BooleanValue.TRUE;
		}

		@FunctionDoc(
			name = "breakBlock",
			desc = {
				"This breaks a block at a given position, if it is able to be broken",
				"and runs a function when the block is broken, or when the block cannot be broken"
			},
			params = {
				POS, "pos", "the position of the block",
				FUNCTION, "function", "the function to run when the block is broken"
			},
			returns = {BOOLEAN, "whether the block can be broken"},
			example = "player.breakBlock(new Pos(0, 0, 0), fun() { print('broken'); });"
		)
		private Value breakBlockMore(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ClientPlayerEntity player = this.getPlayer(arguments);
			BlockPos pos = arguments.getNext(PosValue.class).toBlockPos();
			FunctionValue function = arguments.getNext(FunctionValue.class);
			if (!EssentialUtils.canMineBlock(player, pos)) {
				return BooleanValue.FALSE;
			}

			Context branchContext = arguments.getContext().createBranch();
			branchContext.getThreadHandler().loopAsyncFunctionInThreadPool(
				50, TimeUnit.MILLISECONDS,
				() -> EssentialUtils.canMineBlock(player, pos),
				() -> function.call(branchContext.createBranch(), new ArrayList<>()),
				null, c -> client.execute(() -> {
					interactionManager.updateBlockBreakingProgress(pos, Direction.DOWN);
					player.swingHand(Hand.MAIN_HAND);
				})
			);
			return BooleanValue.TRUE;
		}

		@FunctionDoc(
			name = "updateBreakingBlock",
			desc = "This allows you to update your block breaking progress at a position",
			params = {
				NUMBER, "x", "the x position",
				NUMBER, "y", "the y position",
				NUMBER, "z", "the z position"
			},
			example = "player.updateBreakingBlock(0, 0, 0);"
		)
		private Value updateBreakingBlock(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(arguments);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			BlockPos blockPos = new BlockPos(x, y, z);
			if (ArucasMinecraftExtension.getWorld().isAir(blockPos)) {
				return NullValue.NULL;
			}
			ArucasMinecraftExtension.getClient().execute(() -> {
				interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP);
				player.swingHand(Hand.MAIN_HAND);
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "updateBreakingBlock",
			desc = "This allows you to update your block breaking progress at a position",
			params = {POS, "pos", "the position of the block"},
			example = "player.updateBreakingBlock(new Pos(0, 0, 0));"
		)
		private Value updateBreakingBlockPos(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			if (ArucasMinecraftExtension.getWorld().isAir(blockPos)) {
				return NullValue.NULL;
			}
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP));
			player.swingHand(Hand.MAIN_HAND);
			return NullValue.NULL;
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
			example = "player.attackBlock(0, 0, 0, 'up');"
		)
		private Value attackBlock(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			double x = arguments.skip().getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = ClientScriptUtils.stringToDirection(stringValue.value, Direction.DOWN);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackBlock(new BlockPos(x, y, z), direction));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "attackBlock",
			desc = "This allows you to attack a block at a position and direction",
			params = {
				POS, "pos", "the position of the block",
				STRING, "direction", "the direction of the attack, e.g. 'up', 'north', 'east', etc."
			},
			example = "player.attackBlock(new Pos(0, 0, 0), 'up');"
		)
		private Value attackBlockPos(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			PosValue posValue = arguments.skip().getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = ClientScriptUtils.stringToDirection(stringValue.value, Direction.DOWN);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackBlock(new BlockPos(posValue.value), direction));
			return NullValue.NULL;
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
			example = "player.interactBlock(0, 100, 0, 'up');"
		)
		private Value interactBlock(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = ClientScriptUtils.stringToDirection(stringValue.value, Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(new Vec3d(x, y, z), direction, new BlockPos(x, y, z), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "interactBlock",
			desc = "This allows you to interact with a block at a position and direction",
			params = {
				POS, "pos", "the position of the block",
				STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc."
			},
			example = "player.interactBlock(new Pos(0, 0, 0), 'up');"
		)
		private Value interactBlockPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			return this.interactInternal(player, posValue, stringValue, posValue, Hand.MAIN_HAND);
		}

		@FunctionDoc(
			name = "interactBlock",
			desc = "This allows you to interact with a block at a position, direction, and hand",
			params = {
				POS, "pos", "the position of the block",
				STRING, "direction", "the direction of the interaction, e.g. 'up', 'north', 'east', etc.",
				STRING, "hand", "the hand to use, e.g. 'main_hand', 'off_hand'"
			},
			example = "player.interactBlock(new Pos(0, 0, 0), 'up', 'off_hand');"
		)
		private Value interactBlockPosHand(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			StringValue handValue = arguments.getNextString();
			Hand hand = switch (handValue.value.toLowerCase()) {
				case "main_hand", "main" -> Hand.MAIN_HAND;
				case "off_hand", "off" -> Hand.OFF_HAND;
				default -> throw arguments.getError("Invalid hand: " + handValue.value);
			};
			return this.interactInternal(player, posValue, stringValue, posValue, hand);
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
			example = "player.interactBlock(0, 100.5, 0, 'up', 0, 100, 0, true);"
		)
		private Value interactBlockFull(Arguments arguments) throws CodeError {
			//carpet protocol support but why not client side?
			ClientPlayerEntity player = this.getPlayer(arguments);
			double px = arguments.getNextGeneric(NumberValue.class);
			double py = arguments.getNextGeneric(NumberValue.class);
			double pz = arguments.getNextGeneric(NumberValue.class);
			Direction direction = ClientScriptUtils.stringToDirection(arguments.getNextGeneric(StringValue.class), Direction.DOWN);
			double bx = arguments.getNextGeneric(NumberValue.class);
			double by = arguments.getNextGeneric(NumberValue.class);
			double bz = arguments.getNextGeneric(NumberValue.class);
			boolean bool = arguments.getNextGeneric(BooleanValue.class);
			BlockHitResult hitResult = new BlockHitResult(new Vec3d(px, py, pz), direction, new BlockPos(bx, by, bz), bool);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
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
			example = "player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true);"
		)
		private Value interactBlockFullPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			PosValue blockPosValue = arguments.getNext(PosValue.class);
			return this.interactInternal(player, posValue, stringValue, blockPosValue, Hand.MAIN_HAND);
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
			example = "player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true, 'off_hand');"
		)
		private Value interactBlockFullPosHand(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			StringValue handValue = arguments.getNextString();
			PosValue blockPosValue = arguments.getNext(PosValue.class);
			Hand hand = switch (handValue.value.toLowerCase()) {
				case "main_hand", "main" -> Hand.MAIN_HAND;
				case "off_hand", "off" -> Hand.OFF_HAND;
				default -> throw arguments.getError("Invalid hand: " + handValue.value);
			};
			return this.interactInternal(player, posValue, stringValue, blockPosValue, hand);
		}

		@FunctionDoc(
			name = "getBlockBreakingSpeed",
			desc = "This returns the block breaking speed of the player on a block including enchanements and effects",
			params = {ITEM_STACK, "itemStack", "item to test with", BLOCK, "block", "the block to get the speed of"},
			example = "speed = player.getBlockBreakingSpeed(Material.NETHERITE_PICKAXE.asItem(), Material.GOLD_BLOCK.asBlock());"
		)
		private Value getBlockBreakingSpeed(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			ItemStack itemStack = arguments.getNext(ItemStackValue.class).value;
			BlockValue blockStateValue = arguments.getNext(BlockValue.class);
			float multiplier = EssentialUtils.getBlockBreakingSpeed(itemStack, blockStateValue.value, player);
			return NumberValue.of(multiplier);
		}

		private Value tradeIndex(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			if (InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw arguments.getError("Not in merchant gui");
			}
			return NullValue.NULL;
		}

		private Value getIndexOfTrade(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.skip().getNext(ItemStackValue.class);
			int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			if (index == -1) {
				return NullValue.NULL;
			}
			this.checkVillagerValid(index, arguments);
			return NumberValue.of(index);
		}

		private Value getTradeItemForIndex(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			try {
				ItemStack itemStack = InventoryUtils.getTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
				if (itemStack == null) {
					throw arguments.getError("That trade is out of bounds");
				}
				return new ItemStackValue(itemStack);
			}
			catch (RuntimeException e) {
				throw arguments.getError("Not in merchant gui");
			}
		}

		private Value doesVillagerHaveTrade(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.skip().getNext(ItemStackValue.class);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		private Value isTradeDisabled(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		private Value getPriceForIndex(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			this.checkVillagerValid(price, arguments);
			return NumberValue.of(price);
		}

		private Value anvilInternal(Arguments arguments, boolean hasArg) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return BooleanValue.FALSE;
			}
			ClientPlayerEntity player = this.getPlayer(arguments);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
				throw arguments.getError("Not in anvil gui");
			}
			FunctionValue predicate1 = arguments.getNextFunction();
			FunctionValue predicate2 = arguments.getNextFunction();
			hasArg = hasArg && arguments.getNextBoolean().value;
			boolean firstValid = false;
			boolean secondValid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : anvilHandler.slots) {
				if (firstValid && secondValid) {
					break;
				}
				ItemStack itemStack = slot.getStack();
				if (!firstValid) {
					Value predicateReturn = predicate1.call(arguments.getContext().createBranch(), ArucasList.arrayListOf(new ItemStackValue(itemStack)));
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
					Value predicateReturn = predicate2.call(arguments.getContext().createBranch(), ArucasList.arrayListOf(new ItemStackValue(itemStack)));
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
			if (hasArg) {
				client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player));
			}
			return BooleanValue.of(firstValid && secondValid);
		}

		private Value interactInternal(ClientPlayerEntity player, PosValue posValue, StringValue stringValue, PosValue blockPosValue, Hand hand) throws CodeError {
			Direction direction = ClientScriptUtils.stringToDirection(stringValue.value, Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(posValue.value, direction, new BlockPos(blockPosValue.value), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, hand, hitResult));
			return NullValue.NULL;
		}

		private boolean checkVillagerValid(int code, Arguments arguments) throws RuntimeError {
			boolean bool = false;
			switch (code) {
				case -2 -> throw arguments.getError("Not in merchant gui");
				case -1 -> throw arguments.getError("That trade is out of bounds");
				case 1 -> bool = true;
			}
			return bool;
		}

		private NullValue setKey(Arguments arguments, KeyBinding keyBinding) throws CodeError {
			BooleanValue booleanValue = arguments.skip().getNextBoolean();
			ArucasMinecraftExtension.getClient().execute(() -> keyBinding.setPressed(booleanValue.value));
			return NullValue.NULL;
		}

		private ClientPlayerEntity getPlayer(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = arguments.getNextGeneric(PlayerValue.class);
			if (player == null) {
				throw arguments.getError("Player was null");
			}
			return player;
		}

		@Override
		public Class<PlayerValue> getValueClass() {
			return PlayerValue.class;
		}
	}
}
