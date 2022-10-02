package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ArucasObfuscator;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.MinecraftDeobfuscator;

import java.util.Objects;

public class ClientScriptObfuscator implements ArucasObfuscator {
	@Override
	public boolean shouldObfuscate() {
		return !EssentialUtils.isDev();
	}

	@Override
	public String obfuscateClassName(String s) {
		return this.shouldObfuscate() ? MinecraftDeobfuscator.obfuscate(s) : s;
	}

	@Override
	public String obfuscateMethodName(Class<?> clazz, String name) {
		if (this.shouldObfuscate()) {
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
		}
		return name;
	}

	@Override
	public String obfuscateFieldName(Class<?> clazz, String name) {
		if (this.shouldObfuscate()) {
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
		}
		return name;
	}

	@Override
	public String deobfuscateClass(Class<?> aClass) {
		return this.shouldObfuscate() ? MinecraftDeobfuscator.deobfuscateClass(aClass.getName()) : aClass.getName();
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
