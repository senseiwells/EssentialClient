package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.client.world.ClientWorld;

public class WorldValue extends Value<ClientWorld> {
	public WorldValue(ClientWorld world) {
		super(world);
	}

	@Override
	public Value<ClientWorld> copy() {
		return this;
	}
}
