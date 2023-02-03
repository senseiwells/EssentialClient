package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ArucasObfuscator;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.MinecraftDeobfuscator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClientScriptObfuscator implements ArucasObfuscator {
	@Override
	public boolean shouldObfuscate() {
		return !EssentialUtils.isDev();
	}

	@NotNull
	@Override
	public String obfuscateClassName(@NotNull String s) {
		return this.shouldObfuscate() ? MinecraftDeobfuscator.obfuscate(s) : s;
	}

	@NotNull
	@Override
	public String obfuscateMethodName(@NotNull Class<?> clazz, @NotNull String name) {
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

	@NotNull
	@Override
	public String obfuscateFieldName(@NotNull Class<?> clazz, @NotNull String name) {
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

	@NotNull
	@Override
	public String deobfuscateClass(@NotNull Class<?> aClass) {
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
