package me.senseiwells.essential_client.mixins.remove_warn_invalid_chunk;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.multiplayer.ClientChunkCache;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientChunkCache.class)
public class ClientChunkCacheMixin {
    @WrapWithCondition(
        method = {"replaceBiomes", "replaceWithPacketData"},
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
            remap = false
        )
    )
    private boolean shouldWarnInvalidChunk(Logger instance, String s, Object a, Object b) {
        return !EssentialClientConfig.getInstance().getRemoveWarnInvalidChunk();
    }
}
