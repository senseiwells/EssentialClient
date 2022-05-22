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
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.mixins.clientScript.MerchantScreenHandlerMixin;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.MERCHANT_SCREEN;

public class MerchantScreenValue extends ScreenValue<MerchantScreen> {
	public MerchantScreenValue(MerchantScreen screen) {
		super(screen);
	}

	@Override
	public String getAsString(Context context) {
		return "MerchantScreen@" + this.getHashCode(context);
	}

	@Override
	public String getTypeName() {
		return MERCHANT_SCREEN;
	}

	@ClassDoc(
		name = MERCHANT_SCREEN,
		desc = {
			"This class extends Screen and so inherits all of their methods too,",
			"this class is used to add functionality to trading screens.",
		},
		importPath = "Minecraft"
	)
	public static class ArucasMerchantScreenClass extends ArucasClassExtension {
		private static final String NOT_IN_GUI = "Not in merchant gui";

		public ArucasMerchantScreenClass() {
			super(MERCHANT_SCREEN);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getTradeList", this::getTradeList),
				MemberFunction.of("getTradeListSize", this::getTradeListSize),
				MemberFunction.of("getVillagerLevel", this::getVillagerLevel),
				MemberFunction.of("getVillagerProfession", this::getVillagerProfession),
				MemberFunction.of("tradeIndex", 1, this::tradeIndex),
				MemberFunction.of("getIndexOfTradeItem", 1, this::getIndexOfTrade),
				MemberFunction.of("getTradeItemForIndex", 1, this::getTradeItemForIndex),
				MemberFunction.of("doesVillagerHaveTrade", 1, this::doesVillagerHaveTrade),
				MemberFunction.of("isTradeDisabled", 1, this::isTradeDisabled),
				MemberFunction.of("getPriceForIndex", 1, this::getPriceForIndex),
				MemberFunction.of("selectTrade", 1, this::selectTrade),
				MemberFunction.of("clearTrade", this::clearTrade),
				MemberFunction.of("isTradeSelected", this::isTradeSelected),
				MemberFunction.of("tradeSelected", this::tradeSelected),
				MemberFunction.of("tradeSelectedAndThrow", this::tradeSelectedAndThrow)
			);
		}

		@FunctionDoc(
			name = "getTradeListSize",
			desc = "This gets the size of all the trades available",
			returns = {NUMBER, "the size of the trade list"},
			example = "screen.getTradeListSize();"
		)
		private Value getTradeListSize(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			return NumberValue.of(tradeOfferList.size());
		}

		@FunctionDoc(
			name = "getTradeList",
			desc = "This gets a list of all the merchant's trades",
			returns = {LIST, "the list of all the Trades"},
			example = "screen.getTradeList();"
		)
		private Value getTradeList(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			ArucasList valueList = new ArucasList();
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			for (TradeOffer tradeOffers : tradeOfferList) {
				valueList.add(new TradeValue(tradeOffers));
			}
			return new ListValue(valueList);
		}

		@FunctionDoc(
			name = "getVillagerLevel",
			desc = "This gets the level of the villager",
			returns = {NUMBER, "the level of the villager"},
			throwMsgs = "Merchant isn't a villager",
			example = "screen.getVillagerLevel();"
		)
		private Value getVillagerLevel(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return NumberValue.of(villagerEntity.getVillagerData().getLevel());
			}
			throw arguments.getError("Merchant isn't a villager");
		}

		@FunctionDoc(
			name = "getVillagerProfession",
			desc = "This gets the profession of the villager",
			returns = {STRING, "the profession of the villager, for example: 'armorer', 'mason', 'weaponsmith'"},
			throwMsgs = "Merchant isn't a villager",
			example = "screen.getVillagerProfession();"
		)
		private Value getVillagerProfession(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return StringValue.of(villagerEntity.getVillagerData().getProfession().toString());
			}
			throw arguments.getError("Merchant isn't a villager");
		}

		@FunctionDoc(
			name = "tradeIndex",
			desc = "This makes your player trade with the merchant at a certain index",
			params = {NUMBER, "index", "the index of the trade"},
			throwMsgs = "Not in merchant gui",
			example = "screen.tradeIndex(0);"
		)
		private Value tradeIndex(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			if (InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getIndexOfTrade",
			desc = "This gets the index of a trade for a certain item",
			params = {ITEM_STACK, "itemStack", "the item to get the index of"},
			returns = {NUMBER, "the index of the trade"},
			throwMsgs = "Not in merchant gui",
			example = "screen.getIndexOfTrade(Material.DIAMOND_PICKAXE.asItemStack());"
		)
		private Value getIndexOfTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			if (index == -1) {
				return NullValue.NULL;
			}
			this.checkVillagerValid(index, arguments);
			return NumberValue.of(index);
		}

		@FunctionDoc(
			name = "getTradeItemForIndex",
			desc = "This gets the item stack of a trade at a certain index",
			params = {NUMBER, "index", "the index of the trade"},
			returns = {ITEM_STACK, "the item stack of the trade"},
			throwMsgs = {
				"Not in merchant gui",
				"That trade is out of bounds"
			},
			example = "screen.getTradeItemForIndex(0);"
		)
		private Value getTradeItemForIndex(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			try {
				ItemStack itemStack = InventoryUtils.getTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
				if (itemStack == null) {
					throw arguments.getError("That trade is out of bounds");
				}
				return new ItemStackValue(itemStack);
			}
			catch (RuntimeException e) {
				throw arguments.getError(NOT_IN_GUI);
			}
		}

		@FunctionDoc(
			name = "doesVillagerHaveTrade",
			desc = "This checks if the villager has a trade for a certain item",
			params = {ITEM_STACK, "itemStack", "the item to check for"},
			returns = {BOOLEAN, "true if the villager has a trade for the item, false otherwise"},
			throwMsgs = {
				"Not in merchant gui",
				"That trade is out of bounds"
			},
			example = "screen.doesVillagerHaveTrade(Material.DIAMOND_PICKAXE.asItemStack());"
		)
		private Value doesVillagerHaveTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		@FunctionDoc(
			name = "selectTrade",
			desc = "This selects the currently selected trade, as if you were to click it",
			params = {NUMBER, "index", "the index of the trade"},
			throwMsgs = "Not in merchant gui",
			example = "screen.selectTrade(0);"
		)
		private Value selectTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			if (!InventoryUtils.selectTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue())) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "clearTrade",
			desc = "This clears the currently selected trade",
			throwMsgs = "Not in merchant gui",
			example = "screen.clearTrade();"
		)
		private Value clearTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			if (!InventoryUtils.clearTrade(ArucasMinecraftExtension.getClient())) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "tradeSelected",
			desc = "This trades the currently selected trade",
			throwMsgs = "Not in merchant gui",
			example = "screen.tradeSelected();"
		)
		private Value tradeSelected(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), false)) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "tradeSelectedAndThrow",
			desc = "This trades the currently selected trade and throws the items that were traded",
			throwMsgs = "Not in merchant gui",
			example = "screen.tradeSelectedAndThrow();"
		)
		private Value tradeSelectedAndThrow(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), true)) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "isTradeSelected",
			desc = "This returns true if a trade is selected",
			returns = {BOOLEAN, "true if a trade is selected"},
			example = "screen.isTradeSelected();"
		)
		private Value isTradeSelected(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			return BooleanValue.of(InventoryUtils.isTradeSelected(ArucasMinecraftExtension.getClient()));
		}

		@FunctionDoc(
			name = "isTradeDisabled",
			desc = "This returns true if a trade is disabled at an index",
			params = {NUMBER, "index", "the index of the trade"},
			returns = {BOOLEAN, "true if a trade is disabled"},
			example = "screen.isTradeDisabled(1);"
		)
		private Value isTradeDisabled(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		@FunctionDoc(
			name = "getPriceForIndex",
			desc = "This gets the price of a trade at a certain index",
			params = {NUMBER, "index", "the index of the trade"},
			returns = {NUMBER, "the price of the trade"},
			throwMsgs = {
				"Not in merchant gui",
				"That trade is out of bounds"
			},
			example = "screen.getPriceForIndex(0);"
		)
		private Value getPriceForIndex(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			this.checkVillagerValid(price, arguments);
			return NumberValue.of(price);
		}

		public MerchantScreen checkIsCurrentScreen(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			MerchantScreenValue screenValue = arguments.getNext(MerchantScreenValue.class);
			if (client.currentScreen != screenValue.value) {
				throw arguments.getError("Currently not in %s", screenValue);
			}
			return screenValue.value;
		}

		public boolean checkVillagerValid(int code, Arguments arguments) throws RuntimeError {
			boolean bool = false;
			switch (code) {
				case -2 -> throw arguments.getError(NOT_IN_GUI);
				case -1 -> throw arguments.getError("That trade is out of bounds");
				case 1 -> bool = true;
			}
			return bool;
		}

		@Override
		public Class<MerchantScreenValue> getValueClass() {
			return MerchantScreenValue.class;
		}
	}
}
