package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.LivingEntityValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Set;

public class ArucasLivingEntityMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.livingEntityFunctions;
	}

	@Override
	public String getName() {
		return "LivingEntityMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> livingEntityFunctions = Set.of(
		new MemberFunction("getStatusEffects", this::getStatusEffects),
		new MemberFunction("getHealth", (context, function) -> new NumberValue(this.getLivingEntity(context, function).getHealth()))
	);

	private Value<?> getStatusEffects(Context context, MemberFunction function) throws CodeError {
		LivingEntity playerEntity = this.getLivingEntity(context, function);
		ArucasValueList potionList = new ArucasValueList();
		playerEntity.getStatusEffects().forEach(s -> {
			Identifier effectId = Registry.STATUS_EFFECT.getId(s.getEffectType());
			potionList.add(effectId == null ? new NullValue() : new StringValue(effectId.getPath()));
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
}
