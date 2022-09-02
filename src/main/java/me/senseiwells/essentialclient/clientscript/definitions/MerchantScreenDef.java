package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;
import static me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils.ensureMainThread;
import static me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils.warnMainThread;

@ClassDoc(
	name = MERCHANT_SCREEN,
	desc = {
		"This class extends Screen and so inherits all of their methods too,",
		"this class is used to add functionality to trading screens."
	},
	importPath = "Minecraft",
	superclass = ScreenDef.class,
	language = Util.Language.Java
)
public class MerchantScreenDef extends CreatableDefinition<MerchantScreen> {
	public MerchantScreenDef(Interpreter interpreter) {
		super(MERCHANT_SCREEN, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super MerchantScreen> superclass() {
		return this.getPrimitiveDef(ScreenDef.class);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getTradeList", this::getTradeList),
			MemberFunction.of("getTradeListSize", this::getTradeListSize),
			MemberFunction.of("getVillagerLevel", this::getVillagerLevel),
			MemberFunction.of("getVillagerXpBar", this::getVillagerXpBar),
			MemberFunction.of("tradeIndex", 1, this::tradeIndex),
			MemberFunction.of("getIndexOfTradeItem", 1, this::getIndexOfTradeItem),
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
		examples = "screen.getTradeListSize();"
	)
	private int getTradeListSize(Arguments arguments) {
		MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
		TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
		return tradeOfferList.size();
	}

	@FunctionDoc(
		name = "getTradeList",
		desc = "This gets a list of all the merchant's trades",
		returns = {LIST, "the list of all the Trades"},
		examples = "screen.getTradeList();"
	)
	private ArucasList getTradeList(Arguments arguments) {
		MerchantScreen merchantScreen = this.checkIsCurrentScreen(arguments);
		ArucasList valueList = new ArucasList();
		TradeOfferList tradeOfferList = merchantScreen.getScreenHandler().getRecipes();
		for (TradeOffer tradeOffers : tradeOfferList) {
			valueList.add(arguments.getInterpreter().convertValue(tradeOffers));
		}
		return valueList;
	}

	@FunctionDoc(
		name = "getVillagerLevel",
		desc = {
			"This gets the level of the villager, this will",
			"throw an error if you are not trading with a villager.",
			"The level can be between 1 - 5 from Novice to Master"
		},
		returns = {NUMBER, "the level of the villager"},
		examples = "screen.getVillagerLevel();"
	)
	private int getVillagerLevel(Arguments arguments) {
		MerchantScreenHandler handler = this.checkIsCurrentScreen(arguments).getScreenHandler();
		if (handler.isLeveled()) {
			return handler.getLevelProgress();
		}
		throw new RuntimeError("Merchant isn't a villager");
	}

	@FunctionDoc(
		name = "getVillagerXp",
		desc = {
			"This gets the amount of xp in the villagers xp bar,",
			"The total amount of xp is hardcoded for each level.",
			"Level 2 requires 10 xp, 3 requires 70 (60 xp from 2 -> 3),",
			"4 requires 150 (80 xp from 3 -> 4), 5 requires 250",
			"(100 xp from 4 -> 5). 250 is the max xp a villager can have"
		},
		returns = {NUMBER, "the amount of xp"},
		examples = "screen.getVillagerXpBar"
	)
	private int getVillagerXpBar(Arguments arguments) {
		MerchantScreenHandler handler = this.checkIsCurrentScreen(arguments).getScreenHandler();
		if (handler.isLeveled()) {
			return handler.getExperience();
		}
		throw new RuntimeError("Merchant isn't a villager");
	}

	@FunctionDoc(
		name = "tradeIndex",
		desc = {
			"This makes your player trade with the merchant at a certain index.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		params = {NUMBER, "index", "the index of the trade"},
		examples = "screen.tradeIndex(0);"
	)
	private Void tradeIndex(Arguments arguments) {
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		ensureMainThread("tradeIndex", arguments.getInterpreter(), () -> {
			InventoryUtils.tradeAllItems(index, false);
		});
		return null;
	}

	@FunctionDoc(
		name = "getIndexOfTradeItem",
		desc = {
			"This gets the index of a trade for a certain item.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		params = {MATERIAL, "material", "the item to get the index of"},
		returns = {NUMBER, "the index of the trade"},
		examples = "screen.getIndexOfTradeItem(Material.DIAMOND_PICKAXE);"
	)
	private int getIndexOfTradeItem(Arguments arguments) {
		warnMainThread("getIndexOfTradeItem", arguments.getInterpreter());
		ScriptMaterial material = arguments.skip().nextPrimitive(MaterialDef.class);
		return InventoryUtils.getIndexOfItemInMerchant(material.asItem());
	}

	@FunctionDoc(
		name = "getTradeItemForIndex",
		desc = {
			"This gets the item stack of a trade at a certain index.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		params = {NUMBER, "index", "the index of the trade"},
		returns = {ITEM_STACK, "the item stack of the trade"},
		examples = "screen.getTradeItemForIndex(0);"
	)
	private ScriptItemStack getTradeItemForIndex(Arguments arguments) {
		warnMainThread("getTradeItemForIndex", arguments.getInterpreter());
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		return new ScriptItemStack(InventoryUtils.getTrade(index));
	}

	@FunctionDoc(
		name = "doesVillagerHaveTrade",
		desc = {
			"This checks if the villager has a trade for a certain item.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		params = {MATERIAL, "materialLike", "the item or material to check for"},
		returns = {BOOLEAN, "true if the villager has a trade for the item, false otherwise"},
		examples = "screen.doesVillagerHaveTrade(Material.DIAMOND_PICKAXE);"
	)
	private boolean doesVillagerHaveTrade(Arguments arguments) {
		warnMainThread("doesVillagerHaveTrade", arguments.getInterpreter());
		ScriptMaterial material = arguments.skip().nextPrimitive(MaterialDef.class);
		return InventoryUtils.checkHasTrade(material.asItem());
	}

	@FunctionDoc(
		name = "selectTrade",
		desc = {
			"This selects the currently selected trade, as if you were to click it.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		params = {NUMBER, "index", "the index of the trade"},
		examples = "screen.selectTrade(0);"
	)
	private Void selectTrade(Arguments arguments) {
		int index = arguments.skip().nextPrimitive(NumberDef.class).intValue();
		ensureMainThread("selectTrade", arguments.getInterpreter(), () -> {
			InventoryUtils.selectTrade(index);
		});
		return null;
	}

	@FunctionDoc(
		name = "clearTrade",
		desc = {
			"This clears the currently selected trade.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		examples = "screen.clearTrade();"
	)
	private Void clearTrade(Arguments arguments) {
		ensureMainThread("clearTrade", arguments.getInterpreter(), InventoryUtils::clearTrade);
		return null;
	}

	@FunctionDoc(
		name = "tradeSelected",
		desc = {
			"This trades the currently selected trade.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		examples = "screen.tradeSelected();"
	)
	private Void tradeSelected(Arguments arguments) {
		ensureMainThread("tradeSelected", arguments.getInterpreter(), () -> InventoryUtils.tradeSelectedRecipe(false));
		return null;
	}

	@FunctionDoc(
		name = "tradeSelectedAndThrow",
		desc = {
			"This trades the currently selected trade and throws the items that were traded.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		examples = "screen.tradeSelectedAndThrow();"
	)
	private Void tradeSelectedAndThrow(Arguments arguments) {
		ensureMainThread("tradeSelectedAndThrow", arguments.getInterpreter(), () -> InventoryUtils.tradeSelectedRecipe(true));
		return null;
	}

	@FunctionDoc(
		name = "isTradeSelected",
		desc = "This returns true if a trade is selected",
		returns = {BOOLEAN, "true if a trade is selected"},
		examples = "screen.isTradeSelected();"
	)
	private boolean isTradeSelected(Arguments arguments) {
		warnMainThread("isTradeSelected", arguments.getInterpreter());
		return InventoryUtils.isTradeSelected();
	}

	@FunctionDoc(
		name = "isTradeDisabled",
		desc = "This returns true if a trade is disabled at an index",
		params = {NUMBER, "index", "the index of the trade"},
		returns = {BOOLEAN, "true if a trade is disabled"},
		examples = "screen.isTradeDisabled(1);"
	)
	private boolean isTradeDisabled(Arguments arguments) {
		warnMainThread("isTradeDisabled", arguments.getInterpreter());
		int index = arguments.nextPrimitive(NumberDef.class).intValue();
		return InventoryUtils.checkTradeDisabled(index);
	}

	@FunctionDoc(
		name = "getPriceForIndex",
		desc = {
			"This gets the price of a trade at a certain index.",
			"You must be inside the merchant GUI or an error will be thrown"
		},
		params = {NUMBER, "index", "the index of the trade"},
		returns = {NUMBER, "the price of the trade"},
		examples = "screen.getPriceForIndex(0);"
	)
	private int getPriceForIndex(Arguments arguments) {
		warnMainThread("getPriceForIndex", arguments.getInterpreter());
		int index = arguments.nextPrimitive(NumberDef.class).intValue();
		return InventoryUtils.checkPriceForTrade(index);
	}

	public MerchantScreen checkIsCurrentScreen(Arguments arguments) {
		MinecraftClient client = EssentialUtils.getClient();
		MerchantScreen screen = arguments.nextPrimitive(this);
		if (client.currentScreen != screen) {
			throw new RuntimeError("Currently not in that screen");
		}
		return screen;
	}
}
