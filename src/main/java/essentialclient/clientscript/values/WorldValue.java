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

	@Override
	public String toString() {
		return "World{level=%s, dimension=%s}".formatted(this.value.toString(), this.value.getRegistryKey().getValue().getPath());
	}
}
