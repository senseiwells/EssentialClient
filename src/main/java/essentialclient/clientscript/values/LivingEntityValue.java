package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

public class LivingEntityValue<T extends LivingEntity> extends EntityValue<T> {
	public LivingEntityValue(T value) {
		super(value);
	}

	@Override
	public Value<T> copy() {
		return new LivingEntityValue<>(this.value);
	}

	@Override
	public String toString() {
		return "LivingEntity{id=%s}".formatted(Registry.ENTITY_TYPE.getId(this.value.getType()).getPath());
	}
}
