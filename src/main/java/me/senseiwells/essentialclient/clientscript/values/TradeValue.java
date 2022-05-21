package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
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
	public GenericValue<TradeOffer> copy(Context context) throws CodeError {
		return new TradeValue(new TradeOffer(this.value.toNbt()));
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "Trade@" + this.getHashCode(context);
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) throws CodeError {
		return value instanceof TradeValue tradeValue && this.allMatch(tradeValue.value);
	}

	@Override
	public String getTypeName() {
		return TRADE;
	}

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

		public Value getSellItem(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getSellItem());
		}

		public Value getFirstBuyItem(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getOriginalFirstBuyItem());
		}

		public Value getAdjustedFirstBuyItem(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getAdjustedFirstBuyItem());
		}

		public Value getSecondBuyItem(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return new ItemStackValue(thisValue.value.getSecondBuyItem());
		}

		public Value getMaxUses(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getMaxUses());
		}

		public Value getUses(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getUses());
		}

		public Value getSpecialPrice(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getSpecialPrice());
		}

		public Value getPriceMultiplier(Arguments arguments) throws CodeError {
			TradeValue thisValue = arguments.getNext(TradeValue.class);
			return NumberValue.of(thisValue.value.getPriceMultiplier());
		}

		@Override
		public Class<TradeValue> getValueClass() {
			return TradeValue.class;
		}
	}
}