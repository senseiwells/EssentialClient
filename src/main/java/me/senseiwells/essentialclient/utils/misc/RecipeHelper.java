package me.senseiwells.essentialclient.utils.misc;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public class RecipeHelper {
	public static ItemStack getOutput(Recipe<?> recipe) {
		//#if MC >= 11904
		return recipe.getOutput(EssentialUtils.getRegistryManager());
		//#else
		//$$return recipe.getOutput();
		//#endif
	}
}
