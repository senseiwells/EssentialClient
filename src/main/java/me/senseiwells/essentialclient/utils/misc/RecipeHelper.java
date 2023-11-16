package me.senseiwells.essentialclient.utils.misc;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public class RecipeHelper {
	@Deprecated
	public static ItemStack getOutput(Recipe<?> recipe) {
		return recipe.getResult(EssentialUtils.getRegistryManager());
	}
}
