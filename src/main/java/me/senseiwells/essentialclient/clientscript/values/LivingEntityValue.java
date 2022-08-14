package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.LIVING_ENTITY;

public class LivingEntityValue<T extends LivingEntity> extends EntityValue<T> {
	public LivingEntityValue(T value) {
		super(value);
	}

	@Override
	public EntityValue<T> copy(Context context) {
		return new LivingEntityValue<>(this.value);
	}

	@Override
	public String getAsString(Context context) {
		return "LivingEntity{id=%s}".formatted(Registry.ENTITY_TYPE.getId(this.value.getType()).getPath());
	}

	@Override
	public String getTypeName() {
		return LIVING_ENTITY;
	}

	@ClassDoc(
		name = LIVING_ENTITY,
		desc = {
			"This class extends Entity and so inherits all of their methods too,",
			"LivingEntities are any entities that are alive, so all mobs"
		},
		importPath = "Minecraft"
	)
	public static class ArucasLivingEntityClass extends ArucasClassExtension {
		public ArucasLivingEntityClass() {
			super(LIVING_ENTITY);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
			example = "livingEntity.getStatusEffects();"
		)
		private Value getStatusEffects(Arguments arguments) {
			LivingEntity livingEntity = this.getLivingEntity(arguments);
			ArucasList potionList = new ArucasList();
			livingEntity.getStatusEffects().forEach(s -> {
				Identifier effectId = Registry.STATUS_EFFECT.getId(s.getEffectType());
				potionList.add(effectId == null ? NullValue.NULL : StringValue.of(effectId.getPath()));
			});
			return new ListValue(potionList);
		}

		@FunctionDoc(
			name = "getHealth",
			desc = "This gets the LivingEntity's current health",
			returns = {NUMBER, "the LivingEntity's health"},
			example = "livingEntity.getHealth();"
		)
		private Value getHealth(Arguments arguments) {
			return NumberValue.of(this.getLivingEntity(arguments).getHealth());
		}

		@FunctionDoc(
			name = "isFlyFalling",
			desc = "This checks if the LivingEntity is fly falling (gliding with elytra)",
			returns = {BOOLEAN, "true if the LivingEntity is fly falling"},
			example = "livingEntity.isFlyFalling();"
		)
		private Value isFlyFalling(Arguments arguments) {
			return BooleanValue.of(this.getLivingEntity(arguments).isFallFlying());
		}

		private LivingEntity getLivingEntity(Arguments arguments) {
			LivingEntityValue<?> livingEntity = arguments.getNext(LivingEntityValue.class);
			if (livingEntity.value == null) {
				throw arguments.getError("LivingEntity was null");
			}
			return livingEntity.value;
		}

		@Override
		public Class<? extends Value> getValueClass() {
			return LivingEntityValue.class;
		}
	}
}
