package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.MinecraftDeobfuscator;

import java.nio.file.Path;
import java.util.Objects;

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
	public String obfuscateClassName(String name) {
		return MinecraftDeobfuscator.obfuscate(name);
	}

	@Override
	public String obfuscateMethodName(Class<?> clazz, String name) {
		for (Class<?> k = clazz; k != Object.class; k = k.getSuperclass()) {
			String result = obfuscateMethod(k, name);
			if (result != null) {
				return result;
			}
		}
		for (Class<?> k : clazz.getInterfaces()) {
			String result = obfuscateMethod(k, name);
			if (result != null) {
				return result;
			}
		}
		return IArucasAPI.super.obfuscateMethodName(clazz, name);
	}

	@Override
	public String obfuscateFieldName(Class<?> clazz, String name) {
		for (Class<?> k = clazz; k != Object.class; k = k.getSuperclass()) {
			String result = obfuscateField(k, name);
			if (result != null) {
				return result;
			}
		}
		for (Class<?> k : clazz.getInterfaces()) {
			String result = obfuscateField(k, name);
			if (result != null) {
				return result;
			}
		}
		return IArucasAPI.super.obfuscateFieldName(clazz, name);
	}

	private static String obfuscateMethod(Class<?> clazz, String name) {
		String deobfuscatedClass = MinecraftDeobfuscator.deobfuscateClass(clazz.getName());
		String deobfuscatedMethod = deobfuscatedClass + "#" + name + "()";
		String obfuscatedMethod = MinecraftDeobfuscator.obfuscate(deobfuscatedMethod);
		if (!Objects.equals(obfuscatedMethod, deobfuscatedMethod)) {
			return obfuscatedMethod.substring(obfuscatedMethod.lastIndexOf('#') + 1, obfuscatedMethod.length() - 2);
		}
		return null;
	}

	private static String obfuscateField(Class<?> clazz, String name) {
		String deobfuscatedClass = MinecraftDeobfuscator.deobfuscateClass(clazz.getName());
		String deobfuscatedMethod = deobfuscatedClass + "#" + name;
		String obfuscatedMethod = MinecraftDeobfuscator.obfuscate(deobfuscatedMethod);
		if (!Objects.equals(obfuscatedMethod, deobfuscatedMethod)) {
			return obfuscatedMethod.substring(obfuscatedMethod.lastIndexOf('#') + 1);
		}
		return null;
	}
}
