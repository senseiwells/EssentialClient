package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.client.network.OtherClientPlayerEntity;

public class OtherPlayerValue extends AbstractPlayerValue<OtherClientPlayerEntity> {
	public OtherPlayerValue(OtherClientPlayerEntity player) {
		super(player);
	}

	@Override
	public Value<?> copy() {
		return new OtherPlayerValue(this.value);
	}
}
