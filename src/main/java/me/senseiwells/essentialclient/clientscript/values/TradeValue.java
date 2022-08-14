package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.TRADE;

public class TradeValue extends GenericValue<TradeOffer> {
	public TradeValue(TradeOffer value) {
		super(value);
	}

	private boolean allMatch(TradeOffer otherTrade) {
		if (otherTrade == null) {
			return this.value == null;
		}
		return areItemStacksEqual(otherTrade.getOriginalFirstBuyItem(), this.value.getOriginalFirstBuyItem())
			&& areItemStacksEqual(otherTrade.getSellItem(), this.value.getSellItem())
			&& areItemStacksEqual(otherTrade.getSecondBuyItem(), this.value.getSecondBuyItem());
	}

	private static boolean areItemStacksEqual(ItemStack itemStack, ItemStack otherStack) {
		return itemStack.getCount() == otherStack.getCount()
			&& ItemStack.areItemsEqual(itemStack, otherStack)
			&& ItemStack.areNbtEqual(itemStack, otherStack);
	}

	@Override
	public GenericValue<TradeOffer> copy(Context context) {
		return new TradeValue(new TradeOffer(this.value.toNbt()));
	}

	@Override
	public String getAsString(Context context) {
		return "Trade@" + this.getHashCode(context);
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		return value instanceof TradeValue tradeValue && this.allMatch(tradeValue.value);
	}

	@Override
	public String getTypeName() {
		return TRADE;
	}

	@ClassDoc(
		name = TRADE,
		desc = "This class represents a trade offer, and allows you to get information about it.",
		importPath = "Minecraft"
	)
	public static class ArucasTradeOfferClass extends ArucasClassExtension {
		public ArucasTradeOfferClass() {
			super(TRADE);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getSellItem", this::getSellItem),
				MemberFunction.of("getFirstBuyItem", this::getFirstBuyItem),
				MemberFunction.of("getAdjustedFirstBuyItem", this::getAdjustedFirstBuyItem),
				MemberFunction.of("getSecondBuyItem", this::getSecondBuyItem),
				MemberFunction.of("getMaxUses", this::getMaxUses),
				MemberFunction.of("getUses", this::getUses),
				MemberFunction.of("getSpecialPrice", this::getSpecialPrice),
				MemberFunction.of("getPriceMultiplier", this::getPriceMultiplier)
			);
		}

		@FunctionDoc(
			name = "getSellItem",
			desc = "Gets the item that is being sold by the merchant",
			returns = {ITEM_STACK, "the item for sale"},
			example = "trade.getSellItem();"
		)
		public Value getSellItem(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getSellItem());
		}

		@FunctionDoc(
			name = "getFirstBuyItem",
			desc = "Gets the first item that the merchant will buy",
			returns = {ITEM_STACK, "the first item to buy"},
			example = "trade.getFirstBuyItem();"
		)
		public Value getFirstBuyItem(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getOriginalFirstBuyItem());
		}

		@FunctionDoc(
			name = "getAdjustedFirstBuyItem",
			desc = "Gets the first item that the merchant will buy, adjusted by the price multiplier",
			returns = {ITEM_STACK, "the first item to buy"},
			example = "trade.getAdjustedFirstBuyItem();"
		)
		public Value getAdjustedFirstBuyItem(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getAdjustedFirstBuyItem());
		}

		@FunctionDoc(
			name = "getSecondBuyItem",
			desc = "Gets the second item that the merchant will buy",
			returns = {ITEM_STACK, "the second item to buy"},
			example = "trade.getSecondBuyItem();"
		)
		public Value getSecondBuyItem(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getSecondBuyItem());
		}

		@FunctionDoc(
			name = "getMaxUses",
			desc = "Gets the maximum number of times the trade can be used",
			returns = {NUMBER, "the maximum number of uses"},
			example = "trade.getMaxUses();"
		)
		public Value getMaxUses(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getMaxUses());
		}

		@FunctionDoc(
			name = "getUses",
			desc = "Gets the number of times the trade has been used",
			returns = {NUMBER, "the number of uses"},
			example = "trade.getUses();"
		)
		public Value getUses(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getUses());
		}

		@FunctionDoc(
			name = "getSpecialPrice",
			desc = "This gets the special price which is used to adjust the price of the first buy item",
			returns = {NUMBER, "the special price"},
			example = "trade.getSpecialPrice();"
		)
		public Value getSpecialPrice(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getSpecialPrice());
		}

		@FunctionDoc(
			name = "getPriceMultiplier",
			desc = "Gets the price multiplier which is used to adjust the price of the first buy item",
			returns = {NUMBER, "the price multiplier"},
			example = "trade.getPriceMultiplier();"
		)
		public Value getPriceMultiplier(Arguments arguments) {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getPriceMultiplier());
		}

		@Override
		public Class<TradeValue> getValueClass() {
			return TradeValue.class;
		}
	}
}
