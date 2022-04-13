package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.IArucasOutput;

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
		return true;
	}

	// Obviously this is just for testing, don't have a re-mapper yet...
	@Override
	public String obfuscate(String name) {
		if (name.equals("net.minecraft.client.MinecraftClient")) {
			return "net.minecraft.class_310";
		}
		if (name.equals("net.minecraft.client.MinecraftClient#getInstance()")) {
			return "net.minecraft.class_310#method_1551()";
		}
		if (name.equals("net.minecraft.client.MinecraftClient#IS_SYSTEM_MAC")) {
			return "net.minecraft.class_310#field_1703";
		}
		return name;
	}

	@Override
	public String deobfuscate(String name) {
		if (name.equals("net.minecraft.class_310")) {
			return "net.minecraft.client.MinecraftClient";
		}
		return name;
	}
}
