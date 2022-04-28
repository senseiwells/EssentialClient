package me.senseiwells.essentialclient.utils.interfaces;

import net.minecraft.entity.Entity;

/**
 * Thread safe accessor for Entities
 */
public interface IEntityList {
	Entity[] getAllEntities();
}
