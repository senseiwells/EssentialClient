package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.LocatableTrace;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.village.TradeOffer;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.TRADE;

@ClassDoc(
	name = TRADE,
	desc = "This class represents a trade offer, and allows you to get information about it.",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class TradeDef extends CreatableDefinition<TradeOffer> {
	public TradeDef(Interpreter interpreter) {
		super(MinecraftAPI.TRADE, interpreter);
	}

	@Override
	public boolean equals$Arucas(ClassInstance instance, Interpreter interpreter, ClassInstance other, LocatableTrace trace) {
		TradeOffer otherOffer = other.getPrimitive(this);
		return otherOffer != null && InventoryUtils.areTradesEqual(instance.getPrimitive(this), otherOffer);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getSellItem", this::getSellItem),
			MemberFunction.of("getFirstBuyItem", this::getFirstBuyItem),
			MemberFunction.of("getAdjustedFirstBuyItem", this::getAdjustedFirstBuyItem),
			MemberFunction.of("getSecondBuyItem", this::getSecondBuyItem),
			MemberFunction.of("getMaxUses", this::getMaxUses),
			MemberFunction.of("getUses", this::getUses),
			MemberFunction.of("getSpecialPrice", this::getSpecialPrice),
			MemberFunction.of("getPriceMultiplier", this::getPriceMultiplier),
			MemberFunction.of("getXpReward", this::getXpReward)
		);
	}

	@FunctionDoc(
		name = "getSellItem",
		desc = "Gets the item that is being sold by the merchant",
		returns = {ITEM_STACK, "the item for sale"},
		examples = "trade.getSellItem();"
	)
	public ScriptItemStack getSellItem(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return new ScriptItemStack(offer.getSellItem());
	}

	@FunctionDoc(
		name = "getFirstBuyItem",
		desc = "Gets the first item that the merchant will buy",
		returns = {ITEM_STACK, "the first item to buy"},
		examples = "trade.getFirstBuyItem();"
	)
	public ScriptItemStack getFirstBuyItem(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return new ScriptItemStack(offer.getOriginalFirstBuyItem());
	}

	@FunctionDoc(
		name = "getAdjustedFirstBuyItem",
		desc = "Gets the first item that the merchant will buy, adjusted by the price multiplier",
		returns = {ITEM_STACK, "the first item to buy"},
		examples = "trade.getAdjustedFirstBuyItem();"
	)
	public ScriptItemStack getAdjustedFirstBuyItem(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return new ScriptItemStack(offer.getAdjustedFirstBuyItem());
	}

	@FunctionDoc(
		name = "getSecondBuyItem",
		desc = "Gets the second item that the merchant will buy",
		returns = {ITEM_STACK, "the second item to buy"},
		examples = "trade.getSecondBuyItem();"
	)
	public ScriptItemStack getSecondBuyItem(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return new ScriptItemStack(offer.getSecondBuyItem());
	}

	@FunctionDoc(
		name = "getMaxUses",
		desc = "Gets the maximum number of times the trade can be used",
		returns = {NUMBER, "the maximum number of uses"},
		examples = "trade.getMaxUses();"
	)
	public int getMaxUses(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return offer.getMaxUses();
	}

	@FunctionDoc(
		name = "getUses",
		desc = "Gets the number of times the trade has been used",
		returns = {NUMBER, "the number of uses"},
		examples = "trade.getUses();"
	)
	public int getUses(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return offer.getUses();
	}

	@FunctionDoc(
		name = "getSpecialPrice",
		desc = "This gets the special price which is used to adjust the price of the first buy item",
		returns = {NUMBER, "the special price"},
		examples = "trade.getSpecialPrice();"
	)
	public int getSpecialPrice(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return offer.getSpecialPrice();
	}

	@FunctionDoc(
		name = "getPriceMultiplier",
		desc = "Gets the price multiplier which is used to adjust the price of the first buy item",
		returns = {NUMBER, "the price multiplier"},
		examples = "trade.getPriceMultiplier();"
	)
	public float getPriceMultiplier(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return offer.getPriceMultiplier();
	}

	@FunctionDoc(
		name = "getXpReward",
		desc = {
			"Returns the amount of xp the villager will get, which",
			"goes towards them levelling up, from trading this offer"
		},
		returns = {NUMBER, "the amount of xp"},
		examples = "trade.getXpReward"
	)
	private int getXpReward(Arguments arguments) {
		TradeOffer offer = arguments.nextPrimitive(this);
		return offer.getMerchantExperience();
	}
}
