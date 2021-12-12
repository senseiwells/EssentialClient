package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerValue extends AbstractPlayerValue<ClientPlayerEntity> {
	public PlayerValue(ClientPlayerEntity player) {
		super(player);
	}

	@Override
	public Value<ClientPlayerEntity> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "Player{name=%s}".formatted(this.value.getEntityName());
	}
}
