package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
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

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

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
		return ITEM_ENTITY;
	}

	@ClassDoc(
		name = ITEM_ENTITY,
		desc = {
			"This class extends Entity and so inherits all of their methods too,",
			"ItemEntities are entities that are dropped items."
		},
		importPath = "Minecraft"
	)
	public static class ArucasItemEntityClass extends ArucasClassExtension {
		public ArucasItemEntityClass() {
			super(ITEM_ENTITY);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getItemStack", this::getItemStack),
				MemberFunction.of("getCustomName", this::getCustomName),
				MemberFunction.of("getItemAge", this::getItemAge),
				MemberFunction.of("getThrower", this::getThrower)
			);
		}

		@FunctionDoc(
			name = "getItemStack",
			desc = "This method returns the ItemStack that is held in the ItemEntity",
			returns = {ITEM_STACK, "the ItemStack that the entity holds"},
			example = "itemEntity.getItemStack();"
		)
		private Value getItemStack(Arguments arguments) {
			ItemEntityValue itemEntityValue = arguments.getNext(ItemEntityValue.class);
			return new ItemStackValue(itemEntityValue.value.getStack());
		}

		@FunctionDoc(
			name = "getCustomName",
			desc = "This method returns the custom name of the ItemEntity",
			returns = {STRING, "the custom name of the entity"},
			example = "itemEntity.getCustomName();"
		)
		private Value getCustomName(Arguments arguments) {
			ItemEntityValue itemEntityValue = arguments.getNext(ItemEntityValue.class);
			return StringValue.of(itemEntityValue.value.getName().getString());
		}

		@FunctionDoc(
			name = "getItemAge",
			desc = {
				"This method returns the age of the ItemEntity",
				"this is increased every tick and the item entity despawns after 6000 ticks"
			},
			returns = {NUMBER, "the age of the entity"},
			example = "itemEntity.getItemAge();"
		)
		private Value getItemAge(Arguments arguments) {
			ItemEntityValue itemEntityValue = arguments.getNext(ItemEntityValue.class);
			return NumberValue.of(itemEntityValue.value.getItemAge());
		}

		@FunctionDoc(
			name = "getThrower",
			desc = "This method returns the player that threw the ItemEntity",
			returns = {PLAYER, "the player that threw the entity", "null if not thrown by a player"},
			example = "itemEntity.getThrower();"
		)
		private Value getThrower(Arguments arguments) {
			ItemEntityValue itemEntityValue = arguments.getNext(ItemEntityValue.class);
			UUID throwerUuid = itemEntityValue.value.getThrower();
			if (throwerUuid == null) {
				return NullValue.NULL;
			}
			return arguments.getContext().convertValue(ThreadSafeUtils.getPlayerByUuid(ArucasMinecraftExtension.getWorld(), throwerUuid));
		}

		@Override
		public Class<ItemEntityValue> getValueClass() {
			return ItemEntityValue.class;
		}
	}
}
