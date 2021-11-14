package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class EntityValue<T extends Entity> extends Value<T> {
	public EntityValue(T value) {
		super(value);
	}

	@Override
	public Value<T> copy() {
		return this;
	}

	public static Value<?> getEntityValue(Entity entity) {
		if (entity != null) {
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				return new PlayerValue(clientPlayerEntity);
			}
			if (entity instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
				return new OtherPlayerValue(otherClientPlayerEntity);
			}
			if (entity instanceof LivingEntity livingEntity) {
				return new LivingEntityValue<>(livingEntity);
			}
			return new EntityValue<>(entity);
		}
		return new NullValue();
	}
}
