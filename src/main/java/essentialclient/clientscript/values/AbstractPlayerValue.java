package essentialclient.clientscript.values;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public abstract class AbstractPlayerValue<T extends AbstractClientPlayerEntity> extends LivingEntityValue<T> {
	public AbstractPlayerValue(T value) {
		super(value);
	}
}
