package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.mixins.clientScript.ItemEntityAccessor;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ThreadSafeUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_ENTITY;

@ClassDoc(
	name = ITEM_ENTITY,
	desc = {
		"This class extends Entity and so inherits all of their methods too,",
		"ItemEntities are entities that are dropped items."
	},
	superclass = EntityDef.class,
	language = Language.Java
)
public class ItemEntityDef extends CreatableDefinition<ItemEntity> {
	public ItemEntityDef(Interpreter interpreter) {
		super(MinecraftAPI.ITEM_ENTITY, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ItemEntity> superclass() {
		return this.getPrimitiveDef(EntityDef.class);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		ItemStack stack = instance.asPrimitive(this).getStack();
		return "ItemEntity{id=" + stack.getItem().toString() + ", count=" + stack.getCount() + "}";
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getItemStack", this::getItemStack),
			MemberFunction.of("getCustomName", this::getCustomName),
			MemberFunction.of("getItemAge", this::getItemAge),
			MemberFunction.of("getThrower", this::getThrower)
		);
	}

	@FunctionDoc(
		name = "getItemStack",
		desc = "This method returns the ItemStack that is held in the ItemEntity",
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack that the entity holds"),
		examples = "itemEntity.getItemStack();"
	)
	private ScriptItemStack getItemStack(Arguments arguments) {
		return new ScriptItemStack(arguments.nextPrimitive(this).getStack());
	}

	@FunctionDoc(
		name = "getCustomName",
		desc = "This method returns the custom name of the ItemEntity",
		returns = @ReturnDoc(type = StringDef.class, desc = "the custom name of the entity"),
		examples = "itemEntity.getCustomName();"
	)
	private String getCustomName(Arguments arguments) {
		return arguments.nextPrimitive(this).getName().getString();
	}

	@FunctionDoc(
		name = "getItemAge",
		desc = {
			"This method returns the age of the ItemEntity",
			"this is increased every tick and the item entity despawns after 6000 ticks"
		},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the age of the entity"),
		examples = "itemEntity.getItemAge();"
	)
	private double getItemAge(Arguments arguments) {
		return arguments.nextPrimitive(this).getItemAge();
	}

	@FunctionDoc(
		name = "getThrower",
		desc = "This method returns the player that threw the ItemEntity, null if not thrown by a player or player not found",
		returns = @ReturnDoc(type = PlayerDef.class, desc = "the player that threw the entity"),
		examples = "itemEntity.getThrower();"
	)
	private Object getThrower(Arguments arguments) {
		UUID throwerUuid = ((ItemEntityAccessor) arguments.nextPrimitive(this)).getThrower();
		return throwerUuid == null ? null : ThreadSafeUtils.getPlayerByUuid(EssentialUtils.getWorld(), throwerUuid);
	}
}
