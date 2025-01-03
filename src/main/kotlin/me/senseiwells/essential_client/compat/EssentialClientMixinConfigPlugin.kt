package me.senseiwells.essential_client.compat

import com.google.common.collect.HashMultimap
import me.senseiwells.essential_client.features.carpet_client.CarpetClient
import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class EssentialClientMixinConfigPlugin: IMixinConfigPlugin {
    companion object {
        private const val MIXIN_COMPAT = "me.senseiwells.essential_client.mixins.compat."

        private val incompatible = HashMultimap.create<String, String>()

        init {
            this.incompatible.put("me.senseiwells.essential_client.mixins.highlight_liquids.LiquidBlockRendererMixin", "sodium")
            this.incompatible.put("me.senseiwells.essential_client.mixins.lava_opacity.BlockRenderDispatcherMixin", "sodium")
            this.incompatible.put("me.senseiwells.essential_client.mixins.lava_opacity.LiquidBlockRendererMixin", "sodium")
            this.incompatible.put("me.senseiwells.essential_client.mixins.lava_opacity.SectionCompilerMixin", "sodium")
        }
    }

    override fun onLoad(mixinPackage: String) {

    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if (mixinClassName.startsWith(MIXIN_COMPAT)) {
            val modId = mixinClassName.removePrefix(MIXIN_COMPAT).substringBefore('.')
            return FabricLoader.getInstance().isModLoaded(modId)
        }
        for (modId in incompatible.get(mixinClassName)) {
            if (FabricLoader.getInstance().isModLoaded(modId)) {
                return false
            }
        }
        return true
    }

    override fun acceptTargets(myTargets: Set<String>, otherTargets: Set<String>) {

    }

    override fun getMixins(): List<String>? {
        return null
    }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {

    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {

    }
}