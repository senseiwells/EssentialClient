package me.senseiwells.essentialclient.utils.misc;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EssentialMixinConfig implements IMixinConfigPlugin {
	private final Pattern mixinPattern = Pattern.compile("([a-zA-Z]+\\.[a-zA-Z]+$)");

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return switch (this.getSimpleMixinClass(mixinClassName)) {
			case "keyboard.KeyboardMixin", "disableNarrator.KeyboardMixin", "disableHotbarScrolling.MouseMixin" -> !this.isModLoaded("rebind_all_the_keys");
			default -> true;
		};
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void onLoad(String mixinPackage) { }

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

	@SuppressWarnings("SameParameterValue")
	private boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	private String getSimpleMixinClass(String mixinClassName) {
		Matcher matcher = this.mixinPattern.matcher(mixinClassName);
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}
}
