package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.client.network.OtherClientPlayerEntity;

public class OtherPlayerValue extends AbstractPlayerValue<OtherClientPlayerEntity> {
	public OtherPlayerValue(OtherClientPlayerEntity player) {
		super(player);
	}

	@Override
	public Value<OtherClientPlayerEntity> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "OtherPlayer{name=%s}".formatted(this.value.getEntityName());
	}
}
