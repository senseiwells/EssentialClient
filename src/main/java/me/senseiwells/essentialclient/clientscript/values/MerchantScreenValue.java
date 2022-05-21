package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
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

	/**
	 * MerchantScreen class for Arucas. This class extends Screen and so inherits all
	 * of their methods too, this class is used to add functionality to trading screens. <br>
	 * Import the class with <code>import MerchantScreen from Minecraft;</code> <br>
	 * Fully Documented.
	 *
	 * @author senseiwells
	 */
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

		/**
		 * Name: <code>&lt;MerchantScreen>.getTradeListSize()</code> <br>
		 * Description: This gets the size of all the trades available <br>
		 * Returns - Number: the size of the trade list <br>
		 * Example: <code>screen.getTradeListSize();</code>
		 */
		private Value getTradeListSize(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			return NumberValue.of(tradeOfferList.size());
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getTradeList()</code> <br>
		 * Description: This gets a list of all the merchant's trades <br>
		 * Returns - List: the list of all the Trades <br>
		 * Example: <code>screen.getTradeList();</code>
		 */
		private Value getTradeList(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			ArucasList valueList = new ArucasList();
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			for (TradeOffer tradeOffers : tradeOfferList) {
				valueList.add(new TradeValue(tradeOffers));
			}
			return new ListValue(valueList);
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getVillagerLevel()</code> <br>
		 * Description: This gets the level of the villager <br>
		 * Returns - Number: the level of the villager <br>
		 * Throws - Error: <code>"Merchant isn't a villager"</code> <br>
		 * Example: <code>screen.getVillagerLevel();</code>
		 */
		private Value getVillagerLevel(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return NumberValue.of(villagerEntity.getVillagerData().getLevel());
			}
			throw arguments.getError("Merchant isn't a villager");
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getVillagerProfession()</code> <br>
		 * Description: This gets the profession of the villager <br>
		 * Returns - String: the profession of the villager, for example:
		 * <code>"armorer", "mason", "weaponsmith"</code> <br>
		 * Throws - Error: <code>"Merchant isn't a villager"</code> <br>
		 * Example: <code>screen.getVillagerProfession();</code>
		 */
		private Value getVillagerProfession(Arguments arguments) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return StringValue.of(villagerEntity.getVillagerData().getProfession().toString());
			}
			throw arguments.getError("Merchant isn't a villager");
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.tradeIndex(index)</code> <br>
		 * Description: This makes your player trade with the merchant at a certain index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Throws - Error: <code>NOT_IN_GUI</code> <br>
		 * Example: <code>screen.tradeIndex(0);</code>
		 */
		private Value tradeIndex(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			if (InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getIndexOfTrade(itemStack)</code> <br>
		 * Description: This gets the index of a trade for a certain item <br>
		 * Parameter - ItemStack: the item to get the index of <br>
		 * Returns - Number: the index of the trade <br>
		 * Throws - Error: <code>NOT_IN_GUI</code> <br>
		 * Example: <code>screen.getIndexOfTrade(Material.DIAMOND_PICKAXE.asItemStack());</code>
		 */
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

		/**
		 * Name: <code>&lt;MerchantScreen>.getTradeItemForIndex(index)</code> <br>
		 * Description: This gets the item stack of a trade at a certain index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Returns - ItemStack: the item stack of the trade <br>
		 * Throws - Error: <code>NOT_IN_GUI</code>, <code>"That trade is out of bounds"</code> <br>
		 * Example: <code>screen.getTradeItemForIndex(0);</code>
		 */
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

		/**
		 * Name: <code>&lt;MerchantScreen>.doesVillagerHaveTrade(itemStack)</code> <br>
		 * Description: This checks if the villager has a trade for a certain item <br>
		 * Parameter - ItemStack: the item to check for <br>
		 * Returns - Boolean: true if the villager has a trade for the item, false otherwise <br>
		 * Throws - Error: <code>NOT_IN_GUI</code>, <code>"That trade is out of bounds"</code> <br>
		 * Example: <code>screen.doesVillagerHaveTrade(Material.DIAMOND_PICKAXE.asItemStack());</code>
		 */
		private Value doesVillagerHaveTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.selectTrade()</code> <br>
		 * Description: This selects the currently selected trade, as if you were to click it <br>
		 * Parameter - Number: the index of the trade <br>
		 * Throw - Error: <code>NOT_IN_GUI</code> <br>
		 * Example: <code>screen.selectTrade(0);</code>
		 */
		private Value selectTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			if (!InventoryUtils.selectTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue())) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.clearTrade()</code> <br>
		 * Description: This clears the currently selected trade <br>
		 * Throw - Error: <code>NOT_IN_GUI</code> <br>
		 * Example: <code>screen.clearTrade();</code>
		 */
		private Value clearTrade(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			if (!InventoryUtils.clearTrade(ArucasMinecraftExtension.getClient())) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.tradeSelected()</code> <br>
		 * Description: This trades the currently selected trade <br>
		 * Throw - Error: <code>NOT_IN_GUI</code> <br>
		 * Example: <code>screen.tradeSelected();</code>
		 */
		private Value tradeSelected(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), false)) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.tradeSelectedAndThrow()</code> <br>
		 * Description: This trades the currently selected trade and throws the items that were traded <br>
		 * Throw - Error: <code>NOT_IN_GUI</code> <br>
		 * Example: <code>screen.tradeSelectedAndThrow();</code>
		 */
		private Value tradeSelectedAndThrow(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), true)) {
				throw arguments.getError(NOT_IN_GUI);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.isTradeSelected()</code> <br>
		 * Description: This returns true if a trade is selected <br>
		 * Return - Boolean: true if a trade is selected <br>
		 * Example: <code>screen.isTradeSelected();</code>
		 */
		private Value isTradeSelected(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			return BooleanValue.of(InventoryUtils.isTradeSelected(ArucasMinecraftExtension.getClient()));
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.isTradeDisabled(index)</code> <br>
		 * Description: This returns true if a trade is disabled at an index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Return - Boolean: true if a trade is disabled <br>
		 * Example: <code>screen.isTradeDisabled(1);</code>
		 */
		private Value isTradeDisabled(Arguments arguments) throws CodeError {
			this.checkIsCurrentScreen(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getPriceForIndex(index)</code> <br>
		 * Description: This gets the price of a trade at a certain index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Returns - Number: the price of the trade <br>
		 * Throws - Error: <code>NOT_IN_GUI</code>, <code>"That trade is out of bounds"</code> <br>
		 * Example: <code>screen.getPriceForIndex(0);</code>
		 */
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
