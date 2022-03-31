package essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;

public class TradeValue extends Value<TradeOffer> {
	public TradeValue(TradeOffer value) {
		super(value);
	}

	private boolean allMatch(TradeOffer otherTrade){
		if (otherTrade == null){
			return this.value == null;
		}
		return areItemStacksEqual(otherTrade.getOriginalFirstBuyItem(), this.value.getOriginalFirstBuyItem())
			&& areItemStacksEqual(otherTrade.getSellItem(), this.value.getSellItem())
			&& areItemStacksEqual(otherTrade.getSecondBuyItem(), this.value.getSecondBuyItem());
	}

	private static boolean areItemStacksEqual(ItemStack itemStack, ItemStack otherStack){
		return itemStack.getCount() == otherStack.getCount()
			&& ItemStack.areItemsEqual(itemStack, otherStack)
			&& ItemStack.areNbtEqual(itemStack, otherStack);
	}

	@Override
	public Value<TradeOffer> copy(Context context) throws CodeError {
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
	public boolean isEquals(Context context, Value<?> value) throws CodeError {
		return value instanceof TradeValue && this.allMatch((TradeOffer) value.value);
	}

	@Override
	public String getTypeName() {
		return "Trade";
	}

	public static class ArucasTradeOfferClass extends ArucasClassExtension{

		public ArucasTradeOfferClass() {
			super("Trade");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getSellItem", this::getSellItem),
				new MemberFunction("getFirstBuyItem", this::getFirstBuyItem),
				new MemberFunction("getAdjustedFirstBuyItem", this::getAdjustedFirstBuyItem),
				new MemberFunction("getSecondBuyItem", this::getSecondBuyItem),
				new MemberFunction("getMaxUses", this::getMaxUses),
				new MemberFunction("getUses", this::getUses),
				new MemberFunction("getSpecialPrice", this::getSpecialPrice),
				new MemberFunction("getPriceMultiplier", this::getPriceMultiplier)
			);
		}

		public Value<?> getSellItem(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return new ItemStackValue(thisValue.value.getSellItem());
		}

		public Value<?> getFirstBuyItem(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return new ItemStackValue(thisValue.value.getOriginalFirstBuyItem());
		}

		public Value<?> getAdjustedFirstBuyItem(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return new ItemStackValue(thisValue.value.getAdjustedFirstBuyItem());
		}

		public Value<?> getSecondBuyItem(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return new ItemStackValue(thisValue.value.getSecondBuyItem());
		}

		public Value<?> getMaxUses(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return NumberValue.of(thisValue.value.getMaxUses());
		}

		public Value<?> getUses(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return NumberValue.of(thisValue.value.getUses());
		}

		public Value<?> getSpecialPrice(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return NumberValue.of(thisValue.value.getSpecialPrice());
		}

		public Value<?> getPriceMultiplier(Context context, MemberFunction function) throws CodeError {
			TradeValue thisValue = function.getThis(context, TradeValue.class);
			return NumberValue.of(thisValue.value.getPriceMultiplier());
		}

		@Override
		public Class<TradeValue> getValueClass() {
			return TradeValue.class;
		}
	}
}