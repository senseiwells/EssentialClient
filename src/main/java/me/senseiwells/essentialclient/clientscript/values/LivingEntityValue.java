package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LivingEntityValue<T extends LivingEntity> extends EntityValue<T> {
	public LivingEntityValue(T value) {
		super(value);
	}

	@Override
	public Value<T> copy(Context context) {
		return new LivingEntityValue<>(this.value);
	}

	@Override
	public String getAsString(Context context) {
		return "LivingEntity{id=%s}".formatted(Registry.ENTITY_TYPE.getId(this.value.getType()).getPath());
	}

	@Override
	public String getTypeName() {
		return "LivingEntity";
	}

	/**
	 * LivingEntity class for Arucas. This class extends Entity and so inherits all of
	 * their methods too, LivingEntities are any entities that are alive, so all mobs <br>
	 * Import the class with <code>import LivingEntity from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasLivingEntityClass extends ArucasClassExtension {
		public ArucasLivingEntityClass() {
			super("LivingEntity");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getStatusEffects", this::getStatusEffects),
				new MemberFunction("getHealth", this::getHealth),
				new MemberFunction("isFlyFalling", this::isFlyFalling)
			);
		}

		/**
		 * Name: <code>&lt;LivingEntity>.getStatusEffects()</code> <br>
		 * Description: This gets the LivingEntity's status effects, you can find
		 * a list of all the ids of the status effects
		 * [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Effects) <br>
		 * Returns - List: a list of status effects, may be empty <br>
		 * Example: <code>livingEntity.getStatusEffects();</code>
		 */
		private Value<?> getStatusEffects(Context context, MemberFunction function) throws CodeError {
			LivingEntity livingEntity = this.getLivingEntity(context, function);
			ArucasList potionList = new ArucasList();
			livingEntity.getStatusEffects().forEach(s -> {
				Identifier effectId = Registry.STATUS_EFFECT.getId(s.getEffectType());
				potionList.add(effectId == null ? NullValue.NULL : StringValue.of(effectId.getPath()));
			});
			return new ListValue(potionList);
		}

		/**
		 * Name: <code>&lt;LivingEntity>.getHealth()</code> <br>
		 * Description: This gets the LivingEntity's current health <br>
		 * Returns - Number: the LivingEntity's health <br>
		 * Example: <code>livingEntity.getHealth();</code>
		 */
		private Value<?> getHealth(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getLivingEntity(context, function).getHealth());
		}

		/**
		 * Name: <code>&lt;LivingEntity>.isFlyFalling()</code> <br>
		 * Description: This checks if the LivingEntity is fly falling (gliding with elytra) <br>
		 * Returns - Boolean: true if the LivingEntity is fly falling <br>
		 * Example: <code>livingEntity.isFlyFalling();</code>
		 */
		private Value<?> isFlyFalling(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getLivingEntity(context, function).isFallFlying());
		}

		private LivingEntity getLivingEntity(Context context, MemberFunction function) throws CodeError {
			LivingEntityValue<?> livingEntity = function.getParameterValueOfType(context, LivingEntityValue.class, 0);
			if (livingEntity.value == null) {
				throw new RuntimeError("LivingEntity was null", function.syntaxPosition, context);
			}
			return livingEntity.value;
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return LivingEntityValue.class;
		}
	}
}
