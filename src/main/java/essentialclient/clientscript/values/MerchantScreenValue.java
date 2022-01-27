package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.item.ItemStack;

public class MerchantScreenValue extends ScreenValue<MerchantScreen> {
	public MerchantScreenValue(MerchantScreen screen) {
		super(screen);
	}

	@Override
	public String getAsString(Context context) {
		return "MerchantScreen@" + this.getHashCode(context);
	}

	public static class ArucasMerchantScreenClass extends ArucasClassExtension {
		public ArucasMerchantScreenClass() {
			super("MerchantScreen");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getTradeList", this::getTradeList),
				new MemberFunction("getTradeListSize", this::getTradeListSize),
				new MemberFunction("getVillagerJobLevel", this::getVillagerJobLevel),
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
		private Value<?> getTradeListSize(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			Screen screen = ArucasMinecraftExtension.getClient().currentScreen;
			if (screen instanceof MerchantScreen){
				TradeOfferList tradeOfferList = ((MerchantScreen)screen).getScreenHandler().getRecipes();
				return NumberValue.of(tradeOfferList.size());
			}
			return NumberValue.of(-1);
		}
		private Value<?> getTradeList(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			Screen screen = ArucasMinecraftExtension.getClient().currentScreen;
			ArucasList valueList = new ArucasList();
			if (screen instanceof MerchantScreen){
				TradeOfferList tradeOfferList = ((MerchantScreen)screen).getScreenHandler().getRecipes();
				for (TradeOffer tradeOffers : tradeOfferList){
					ItemStack sellItem = tradeOffers.getSellItem();
					ItemStack buyItem1 = tradeOffers.getAdjustedFirstBuyItem();
					ItemStack buyItem2 = tradeOffers.getSecondBuyItem();
					ItemStackValue sellItemStackValue = new ItemStackValue(sellItem);
					ItemStackValue buyItemStackValue1 = new ItemStackValue(buyItem1);
					ItemStackValue buyItemStackValue2 = new ItemStackValue(buyItem2.isEmpty()? Items.AIR.getDefaultStack() : buyItem2);
					ArucasMap map = new ArucasMap();
					map.put(context, StringValue.of("sell"), sellItemStackValue);
					map.put(context, StringValue.of("buy1"), buyItemStackValue1);
					map.put(context, StringValue.of("buy2"), buyItemStackValue2);
					valueList.add(new MapValue(map));
				}
				return new ListValue(valueList);
			}
			else {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
		}
		
		private Value<?> getVillagerJobLevel(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			Screen screen = ArucasMinecraftExtension.getClient().currentScreen;
			if (screen instanceof MerchantScreen){
				return NumberValue.of(((MerchantScreen)screen).getScreenHandler().getLevelProgress());
			}
			throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
		}
		
		private Value<?> tradeIndex(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			if (!InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

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

		private Value<?> doesVillagerHaveTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, function, context));
		}

		private Value<?> selectTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			if (!InventoryUtils.selectTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue())) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> clearTrade(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (!InventoryUtils.clearTrade(ArucasMinecraftExtension.getClient())) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> tradeSelected(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (!InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), false)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> tradeSelectedAndThrow(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (!InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), true)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> isTradeSelected(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			return BooleanValue.of(InventoryUtils.isTradeSelected(ArucasMinecraftExtension.getClient()));
		}

		private Value<?> isTradeDisabled(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, function, context));
		}

		private Value<?> getPriceForIndex(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			this.checkVillagerValid(price, function, context);
			return NumberValue.of(price);
		}

		public void checkIsCurrentScreen(Context context, MemberFunction function) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ScreenValue<?> screenValue = function.getThis(context, ScreenValue.class);
			if (client.currentScreen != screenValue.value) {
				throw new RuntimeError("Currently not in %s".formatted(screenValue.getAsString(context)), function.syntaxPosition, context);
			}
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
		public Class<?> getValueClass() {
			return MerchantScreenValue.class;
		}
	}
}
