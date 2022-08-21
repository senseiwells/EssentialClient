package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.LocatableTrace;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.LIVING_ENTITY;

@ClassDoc(
	name = LIVING_ENTITY,
	desc = {
		"This class extends Entity and so inherits all of their methods too,",
		"LivingEntities are any entities that are alive, so all mobs"
	},
	importPath = "Minecraft",
	superclass = EntityDef.class,
	language = Util.Language.Java
)
public class LivingEntityDef extends PrimitiveDefinition<LivingEntity> {
	public LivingEntityDef(Interpreter interpreter) {
		super(MinecraftAPI.LIVING_ENTITY, interpreter);
	}

	@Deprecated
	@Override
	public ClassInstance create(LivingEntity value) {
		return super.create(value);
	}

	@Override
	public PrimitiveDefinition<? super LivingEntity> superclass() {
		return this.getInterpreter().getPrimitive(EntityDef.class);
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return "LivingEntity{id=%s}".formatted(Registry.ENTITY_TYPE.getId(instance.asPrimitive(this).getType()).getPath());
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
			"[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Effects)"
		},
		returns = {LIST, "a list of status effects, may be empty"},
		examples = "livingEntity.getStatusEffects();"
	)
	private ArucasList getStatusEffects(Arguments arguments) {
		LivingEntity livingEntity = arguments.nextPrimitive(this);
		ArucasList potionList = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		livingEntity.getStatusEffects().forEach(s -> {
			Identifier effectId = Registry.STATUS_EFFECT.getId(s.getEffectType());
			potionList.add(effectId == null ? interpreter.getNull() : interpreter.convertValue(effectId.getPath()));
		});
		return potionList;
	}

	@FunctionDoc(
		name = "getHealth",
		desc = "This gets the LivingEntity's current health",
		returns = {NUMBER, "the LivingEntity's health"},
		examples = "livingEntity.getHealth();"
	)
	private double getHealth(Arguments arguments) {
		return arguments.nextPrimitive(this).getHealth();
	}

	@FunctionDoc(
		name = "isFlyFalling",
		desc = "This checks if the LivingEntity is fly falling (gliding with elytra)",
		returns = {BOOLEAN, "true if the LivingEntity is fly falling"},
		examples = "livingEntity.isFlyFalling();"
	)
	private boolean isFlyFalling(Arguments arguments) {
		return arguments.nextPrimitive(this).isFallFlying();
	}
}
