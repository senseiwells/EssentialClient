package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.MinecraftDeobfuscator;

import java.nio.file.Path;

public class ImplArucasAPI implements IArucasAPI {
	private final Path IMPORT_PATH = ClientScript.INSTANCE.getScriptDirectory().resolve("libs");

	@Override
	public IArucasOutput getOutput() {
		return ArucasMinecraftOutput.INSTANCE;
	}

	@Override
	public Path getImportPath() {
		return this.IMPORT_PATH;
	}

	@Override
	public boolean shouldObfuscate() {
		return !EssentialUtils.isDev();
	}

	@Override
	public String obfuscate(String name) {
		return MinecraftDeobfuscator.obfuscate(name);
	}

	@Override
	public String deobfuscate(String name) {
		return MinecraftDeobfuscator.deobfuscateClass(name);
	}
}
