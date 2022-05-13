package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
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
		return "MerchantScreen";
	}

	/**
	 * MerchantScreen class for Arucas. This class extends Screen and so inherits all
	 * of their methods too, this class is used to add functionality to trading screens. <br>
	 * Import the class with <code>import MerchantScreen from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasMerchantScreenClass extends ArucasClassExtension {
		public ArucasMerchantScreenClass() {
			super("MerchantScreen");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getTradeList", this::getTradeList),
				new MemberFunction("getTradeListSize", this::getTradeListSize),
				new MemberFunction("getVillagerLevel", this::getVillagerLevel),
				new MemberFunction("getVillagerProfession", this::getVillagerProfession),
				new MemberFunction("tradeIndex", "index", this::tradeIndex),
				new MemberFunction("getIndexOfTradeItem", "itemStack", this::getIndexOfTrade),
				new MemberFunction("getTradeItemForIndex", "index", this::getTradeItemForIndex),
				new MemberFunction("doesVillagerHaveTrade", "itemStack", this::doesVillagerHaveTrade),
				new MemberFunction("isTradeDisabled", "index", this::isTradeDisabled),
				new MemberFunction("getPriceForIndex", "index", this::getPriceForIndex),
				new MemberFunction("selectTrade", "index", this::selectTrade),
				new MemberFunction("clearTrade", this::clearTrade),
				new MemberFunction("isTradeSelected", this::isTradeSelected),
				new MemberFunction("tradeSelected", this::tradeSelected),
				new MemberFunction("tradeSelectedAndThrow", this::tradeSelectedAndThrow)
			);
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getTradeListSize()</code> <br>
		 * Description: This gets the size of all the trades available <br>
		 * Returns - Number: the size of the trade list <br>
		 * Example: <code>screen.getTradeListSize();</code>
		 */
		private Value<?> getTradeListSize(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			return NumberValue.of(tradeOfferList.size());
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getTradeList()</code> <br>
		 * Description: This gets a list of all the merchant's trades <br>
		 * Returns - List: the list of all the Trades <br>
		 * Example: <code>screen.getTradeList();</code>
		 */
		private Value<?> getTradeList(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
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
		private Value<?> getVillagerLevel(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return NumberValue.of(villagerEntity.getVillagerData().getLevel());
			}
			throw new RuntimeError("Merchant isn't a villager", function.syntaxPosition, context);
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getVillagerProfession()</code> <br>
		 * Description: This gets the profession of the villager <br>
		 * Returns - String: the profession of the villager, for example:
		 * <code>"armorer", "mason", "weaponsmith"</code> <br>
		 * Throws - Error: <code>"Merchant isn't a villager"</code> <br>
		 * Example: <code>screen.getVillagerProfession();</code>
		 */
		private Value<?> getVillagerProfession(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return StringValue.of(villagerEntity.getVillagerData().getProfession().toString());
			}
			throw new RuntimeError("Merchant isn't a villager", function.syntaxPosition, context);
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.tradeIndex(index)</code> <br>
		 * Description: This makes your player trade with the merchant at a certain index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Throws - Error: <code>"Not in merchant gui"</code> <br>
		 * Example: <code>screen.tradeIndex(0);</code>
		 */
		private Value<?> tradeIndex(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			if (InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getIndexOfTrade(itemStack)</code> <br>
		 * Description: This gets the index of a trade for a certain item <br>
		 * Parameter - ItemStack: the item to get the index of <br>
		 * Returns - Number: the index of the trade <br>
		 * Throws - Error: <code>"Not in merchant gui"</code> <br>
		 * Example: <code>screen.getIndexOfTrade(Material.DIAMOND_PICKAXE.asItemStack());</code>
		 */
		private Value<?> getIndexOfTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			if (index == -1) {
				return NullValue.NULL;
			}
			this.checkVillagerValid(index, function, context);
			return NumberValue.of(index);
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getTradeItemForIndex(index)</code> <br>
		 * Description: This gets the item stack of a trade at a certain index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Returns - ItemStack: the item stack of the trade <br>
		 * Throws - Error: <code>"Not in merchant gui"</code>, <code>"That trade is out of bounds"</code> <br>
		 * Example: <code>screen.getTradeItemForIndex(0);</code>
		 */
		private Value<?> getTradeItemForIndex(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
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

		/**
		 * Name: <code>&lt;MerchantScreen>.doesVillagerHaveTrade(itemStack)</code> <br>
		 * Description: This checks if the villager has a trade for a certain item <br>
		 * Parameter - ItemStack: the item to check for <br>
		 * Returns - Boolean: true if the villager has a trade for the item, false otherwise <br>
		 * Throws - Error: <code>"Not in merchant gui"</code>, <code>"That trade is out of bounds"</code> <br>
		 * Example: <code>screen.doesVillagerHaveTrade(Material.DIAMOND_PICKAXE.asItemStack());</code>
		 */
		private Value<?> doesVillagerHaveTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, function, context));
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.selectTrade()</code> <br>
		 * Description: This selects the currently selected trade, as if you were to click it <br>
		 * Parameter - Number: the index of the trade <br>
		 * Throw - Error: <code>"Not in merchant gui"</code> <br>
		 * Example: <code>screen.selectTrade(0);</code>
		 */
		private Value<?> selectTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			if (!InventoryUtils.selectTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue())) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.clearTrade()</code> <br>
		 * Description: This clears the currently selected trade <br>
		 * Throw - Error: <code>"Not in merchant gui"</code> <br>
		 * Example: <code>screen.clearTrade();</code>
		 */
		private Value<?> clearTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (!InventoryUtils.clearTrade(ArucasMinecraftExtension.getClient())) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.tradeSelected()</code> <br>
		 * Description: This trades the currently selected trade <br>
		 * Throw - Error: <code>"Not in merchant gui"</code> <br>
		 * Example: <code>screen.tradeSelected();</code>
		 */
		private Value<?> tradeSelected(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), false)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.tradeSelectedAndThrow()</code> <br>
		 * Description: This trades the currently selected trade and throws the items that were traded <br>
		 * Throw - Error: <code>"Not in merchant gui"</code> <br>
		 * Example: <code>screen.tradeSelectedAndThrow();</code>
		 */
		private Value<?> tradeSelectedAndThrow(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), true)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.isTradeSelected()</code> <br>
		 * Description: This returns true if a trade is selected <br>
		 * Return - Boolean: true if a trade is selected <br>
		 * Example: <code>screen.isTradeSelected();</code>
		 */
		private Value<?> isTradeSelected(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			return BooleanValue.of(InventoryUtils.isTradeSelected(ArucasMinecraftExtension.getClient()));
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.isTradeDisabled(index)</code> <br>
		 * Description: This returns true if a trade is disabled at an index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Return - Boolean: true if a trade is disabled <br>
		 * Example: <code>screen.isTradeDisabled(1);</code>
		 */
		private Value<?> isTradeDisabled(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, function, context));
		}

		/**
		 * Name: <code>&lt;MerchantScreen>.getPriceForIndex(index)</code> <br>
		 * Description: This gets the price of a trade at a certain index <br>
		 * Parameter - Number: the index of the trade <br>
		 * Returns - Number: the price of the trade <br>
		 * Throws - Error: <code>"Not in merchant gui"</code>, <code>"That trade is out of bounds"</code> <br>
		 * Example: <code>screen.getPriceForIndex(0);</code>
		 */
		private Value<?> getPriceForIndex(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			this.checkVillagerValid(price, function, context);
			return NumberValue.of(price);
		}

		public MerchantScreen checkIsCurrentScreen(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			MerchantScreenValue screenValue = function.getThis(context, MerchantScreenValue.class);
			if (client.currentScreen != screenValue.value) {
				throw new RuntimeError("Currently not in %s".formatted(screenValue.getAsString(context)), function.syntaxPosition, context);
			}
			return screenValue.value;
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

		@Override
		public Class<MerchantScreenValue> getValueClass() {
			return MerchantScreenValue.class;
		}
	}
}
