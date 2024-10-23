package me.senseiwells.essential_client.mixins.yacl;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import me.senseiwells.essential_client.utils.yacl.ReloadChunks;
import me.senseiwells.essential_client.utils.yacl.ReloadResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = SimpleOptionFactory.class, remap = false)
public class SimpleOptionFactoryMixin<A extends Annotation, T> {
	@ModifyReturnValue(
		method = "flags",
		at = @At("RETURN")
	)
	private Set<OptionFlag> onGetFlags(
		Set<OptionFlag> original,
		A annotation,
		ConfigField<T> field
	) {
		Set<OptionFlag> additional = new HashSet<>();
		if (field.access().getAnnotation(ReloadChunks.class).isPresent()) {
			additional.add(OptionFlag.RELOAD_CHUNKS);
		}
		if (field.access().getAnnotation(ReloadResources.class).isPresent()) {
			additional.add(OptionFlag.ASSET_RELOAD);
		}
		return Sets.union(original, additional);
	}
}
