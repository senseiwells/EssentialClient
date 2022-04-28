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

		private Value<?> getItemStack(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			return new ItemStackValue(itemEntityValue.value.getStack());
		}

		private Value<?> getCustomName(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			return StringValue.of(itemEntityValue.value.getName().asString());
		}

		private Value<?> getItemAge(Context context, MemberFunction function) throws CodeError {
			ItemEntityValue itemEntityValue = function.getThis(context, ItemEntityValue.class);
			return NumberValue.of(itemEntityValue.value.getItemAge());
		}

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
