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

		private Value<?> getTradeListSize(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			return NumberValue.of(tradeOfferList.size());
		}

		private Value<?> getTradeList(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			ArucasList valueList = new ArucasList();
			TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
			for (TradeOffer tradeOffers : tradeOfferList) {
				valueList.add(new TradeValue(tradeOffers));
			}
			return new ListValue(valueList);
		}

		private Value<?> getVillagerLevel(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return NumberValue.of(villagerEntity.getVillagerData().getLevel());
			}
			throw new RuntimeError("Merchant isn't a villager", function.syntaxPosition, context);
		}

		private Value<?> getVillagerProfession(Context context, MemberFunction function) throws CodeError {
			MerchantScreen merchantScreen = this.checkIsCurrentScreen(context, function);
			Merchant merchant = ((MerchantScreenHandlerMixin) merchantScreen.getScreenHandler()).getMerchant();
			if (merchant instanceof VillagerEntity villagerEntity) {
				return StringValue.of(villagerEntity.getVillagerData().getProfession().toString());
			}
			throw new RuntimeError("Merchant isn't a villager", function.syntaxPosition, context);
		}

		private Value<?> tradeIndex(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			if (InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
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
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), false)) {
				throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
			}
			return NullValue.NULL;
		}

		private Value<?> tradeSelectedAndThrow(Context context, MemberFunction function) throws CodeError {
			this.checkIsCurrentScreen(context, function);
			if (InventoryUtils.tradeSelectedRecipe(ArucasMinecraftExtension.getClient(), true)) {
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
