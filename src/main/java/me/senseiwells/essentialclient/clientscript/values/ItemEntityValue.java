package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.clientscript.ThreadSafeUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemEntityValue extends EntityValue<ItemEntity> {
	public ItemEntityValue(ItemEntity value) {
		super(value);
	}

	@Override
	public ItemEntityValue copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		ItemStack stack = this.value.getStack();
		return "ItemEntity{id=" + stack.getItem().toString() + ", count=" + stack.getCount() + "}";
	}

	@Override
	public String getTypeName() {
		return "ItemEntity";
	}

	/**
	 * ItemEntity class for Arucas. This class extends Entity and so inherits all of
	 * their methods too, ItemEntities are entities that are dropped items. <br>
	 * Import the class with <code>import ItemEntity from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasItemEntityClass extends ArucasClassExtension {
		public ArucasItemEntityClass() {
			super("ItemEntity");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getItemStack", this::getItemStack),
				new MemberFunction("getCustomName", this::getCustomName),
				new MemberFunction("getItemAge", this::getItemAge),
				new MemberFunction("getThrower", this::getThrower)
			);
		}

		/**
		 * Name: <code>&lt;ItemEntity>.getItemStack()</code> <br>
		 * Description: This method returns the ItemStack that is held in the ItemEntity <br>
		 * Returns - ItemStack: the ItemStack that the entity holds <br>
		 * Example: <code>livingEntity.getItemStack();</code>
		 */
		private Value<?> getItemStack(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			return new ItemStackValue(itemEntityValue.value.getStack());
		}

		/**
		 * Name: <code>&lt;ItemEntity>.getCustomName()</code> <br>
		 * Description: This method returns the custom name of the ItemEntity <br>
		 * Returns - String: the custom name of the entity <br>
		 * Example: <code>livingEntity.getCustomName();</code>
		 */
		private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			return StringValue.of(itemEntityValue.value.getName().asString());
		}

		/**
		 * Name: <code>&lt;ItemEntity>.getItemAge()</code> <br>
		 * Description: This method returns the age of the ItemEntity, this is increased
		 * every tick and the item entity despawns after 6000 ticks <br>
		 * Returns - Number: the age of the entity <br>
		 * Example: <code>livingEntity.getItemAge();</code>
		 */
		private Value<?> getItemAge(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			return NumberValue.of(itemEntityValue.value.getItemAge());
		}

		/**
		 * Name: <code>&lt;ItemEntity>.getThrower()</code> <br>
		 * Description: This method returns the player that threw the ItemEntity <br>
		 * Returns - Player/Null: the player that threw the entity, null if not thrown by a player <br>
		 * Example: <code>livingEntity.getThrower();</code>
		 */
		private Value<?> getThrower(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			UUID throwerUuid = itemEntityValue.value.getThrower();
			if (throwerUuid == null) {
				return NullValue.NULL;
			}
			return context.convertValue(ThreadSafeUtils.getPlayerByUuid(ArucasMinecraftExtension.getWorld(), throwerUuid));
		}

		@Override
		public Class<ItemEntityValue> getValueClass() {
			return ItemEntityValue.class;
		}
	}
}