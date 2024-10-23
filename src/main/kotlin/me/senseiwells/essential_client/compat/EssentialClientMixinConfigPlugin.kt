package me.senseiwells.essential_client.compat

import me.senseiwells.essential_client.features.carpet_client.CarpetClient
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class EssentialClientMixinConfigPlugin: IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) {

    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if (mixinClassName == "me.senseiwells.essential_client.mixins.carpet_client.ClientNetworkHandlerMixin") {
            return CarpetClient.hasLocalCarpet
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