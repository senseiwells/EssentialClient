package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.mapping.RegistryHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.LIVING_ENTITY;

@ClassDoc(
	name = LIVING_ENTITY,
	desc = {
		"This class extends Entity and so inherits all of their methods too,",
		"LivingEntities are any entities that are alive, so all mobs"
	},
	superclass = EntityDef.class,
	language = Language.Java
)
public class LivingEntityDef extends PrimitiveDefinition<LivingEntity> {
	public LivingEntityDef(Interpreter interpreter) {
		super(MinecraftAPI.LIVING_ENTITY, interpreter);
	}

	@Deprecated
	@NotNull
	@Override
	public ClassInstance create(@NotNull LivingEntity value) {
		return super.create(value);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super LivingEntity> superclass() {
		return this.getInterpreter().getPrimitive(EntityDef.class);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return "LivingEntity{id=%s}".formatted(RegistryHelper.getEntityTypeRegistry().getId(instance.asPrimitive(this).getType()).getPath());
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getStatusEffects", this::getStatusEffects),
			MemberFunction.of("getHealth", this::getHealth),
			MemberFunction.of("isFlyFalling", this::isFlyFalling)
		);
	}

	@FunctionDoc(
		name = "getStatusEffects",
		desc = {
			"This gets the LivingEntity's status effects, you can find",
			"a list of all the ids of the status effects",
			"[here](https://minecraft.wiki/w/Java_Edition_data_values#Effects)"
		},
		returns = @ReturnDoc(type = ListDef.class, desc = "a list of status effects, may be empty"),
		examples = "livingEntity.getStatusEffects();"
	)
	private ArucasList getStatusEffects(Arguments arguments) {
		LivingEntity livingEntity = arguments.nextPrimitive(this);
		ArucasList potionList = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		livingEntity.getStatusEffects().forEach(s -> {
			Identifier effectId = RegistryHelper.getStatusEffectRegistry().getId(s.getEffectType());
			potionList.add(effectId == null ? interpreter.getNull() : interpreter.convertValue(effectId.getPath()));
		});
		return potionList;
	}

	@FunctionDoc(
		name = "getHealth",
		desc = "This gets the LivingEntity's current health",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the LivingEntity's health"),
		examples = "livingEntity.getHealth();"
	)
	private double getHealth(Arguments arguments) {
		return arguments.nextPrimitive(this).getHealth();
	}

	@FunctionDoc(
		name = "isFlyFalling",
		desc = "This checks if the LivingEntity is fly falling (gliding with elytra)",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the LivingEntity is fly falling"),
		examples = "livingEntity.isFlyFalling();"
	)
	private boolean isFlyFalling(Arguments arguments) {
		return arguments.nextPrimitive(this).isFallFlying();
	}
}
