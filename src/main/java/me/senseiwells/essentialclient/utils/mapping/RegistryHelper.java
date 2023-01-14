package me.senseiwells.essentialclient.utils.mapping;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.Biome;

//#if MC >= 11903
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
//#else
//$$import net.minecraft.util.registry.Registry;
//#endif

public class RegistryHelper {
	public static Registry<EntityType<?>> getEntityTypeRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.ENTITY_TYPE);
		//#else
		//$$return Registry.ENTITY_TYPE;
		//#endif
	}

	public static Registry<Block> getBlockRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.BLOCK);
		//#else
		//$$return Registry.BLOCK;
		//#endif
	}

	public static Registry<Item> getItemRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.ITEM);
		//#else
		//$$return Registry.ITEM;
		//#endif
	}

	public static Registry<Enchantment> getEnchantmentRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
		//#else
		//$$return Registry.ENCHANTMENT;
		//#endif
	}

	public static Registry<StatusEffect> getStatusEffectRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.STATUS_EFFECT);
		//#else
		//$$return Registry.STATUS_EFFECT;
		//#endif
	}

	public static Registry<SoundEvent> getSoundEventRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.SOUND_EVENT);
		//#else
		//$$return Registry.SOUND_EVENT;
		//#endif
	}

	public static Registry<RecipeType<?>> getRecipeTypeRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.RECIPE_TYPE);
		//#else
		//$$return Registry.RECIPE_TYPE;
		//#endif
	}

	public static Registry<Biome> getBiomeRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.BIOME);
		//#else
		//$$return EssentialUtils.getRegistryManager().get(Registry.BIOME_KEY);
		//#endif
	}

	public static Registry<ParticleType<?>> getParticleTypeRegistry() {
		//#if MC >= 11903
		return EssentialUtils.getRegistryManager().get(RegistryKeys.PARTICLE_TYPE);
		//#else
		//$$return Registry.PARTICLE_TYPE;
		//#endif
	}
}
