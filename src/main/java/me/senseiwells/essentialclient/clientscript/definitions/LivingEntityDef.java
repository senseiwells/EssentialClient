package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import net.minecraft.entity.LivingEntity;

public class LivingEntityDef extends PrimitiveDefinition<LivingEntity> {
	public LivingEntityDef(Interpreter interpreter) {
		super(MinecraftAPI.LIVING_ENTITY, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super LivingEntity> superclass() {
		return this.getInterpreter().getPrimitive(EntityDef.class);
	}
}
