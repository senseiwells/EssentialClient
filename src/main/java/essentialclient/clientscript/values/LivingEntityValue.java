package essentialclient.clientscript.values;

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

	public static class ArucasLivingEntityClass extends ArucasClassExtension {
		public ArucasLivingEntityClass() {
			super("LivingEntity");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getStatusEffects", this::getStatusEffects),
				new MemberFunction("getHealth", (context, function) -> NumberValue.of(this.getLivingEntity(context, function).getHealth())),
				new MemberFunction("isFlyFalling", (context, function) -> BooleanValue.of(this.getLivingEntity(context, function).isFallFlying()))
			);
		}

		private Value<?> getStatusEffects(Context context, MemberFunction function) throws CodeError {
			LivingEntity livingEntity = this.getLivingEntity(context, function);
			ArucasList potionList = new ArucasList();
			livingEntity.getStatusEffects().forEach(s -> {
				Identifier effectId = Registry.STATUS_EFFECT.getId(s.getEffectType());
				potionList.add(effectId == null ? NullValue.NULL : StringValue.of(effectId.getPath()));
			});
			return new ListValue(potionList);
		}

		private LivingEntity getLivingEntity(Context context, MemberFunction function) throws CodeError {
			LivingEntityValue<?> livingEntity = function.getParameterValueOfType(context, LivingEntityValue.class, 0);
			if (livingEntity.value == null) {
				throw new RuntimeError("LivingEntity was null", function.syntaxPosition, context);
			}
			return livingEntity.value;
		}

		@Override
		public Class<?> getValueClass() {
			return LivingEntityValue.class;
		}
	}
}
