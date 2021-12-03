package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.entity.LivingEntity;

public class LivingEntityValue<T extends LivingEntity> extends EntityValue<T> {
	public LivingEntityValue(T value) {
		super(value);
	}

	@Override
	public Value<T> copy() {
		return new LivingEntityValue<>(this.value);
	}
}