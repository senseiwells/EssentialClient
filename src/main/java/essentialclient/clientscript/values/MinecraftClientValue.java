package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.client.MinecraftClient;

public class MinecraftClientValue extends Value<MinecraftClient> {
	public MinecraftClientValue(MinecraftClient client) {
		super(client);
	}

	@Override
	public Value<MinecraftClient> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "MinecraftClient";
	}
}
