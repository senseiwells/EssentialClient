package me.senseiwells.essential_client.features.carpet_client

import io.netty.buffer.ByteBuf
import me.senseiwells.essential_client.EssentialClient
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

object SpoofedCarpetClientNetworkHandler {
    internal fun load() {
        if (!CarpetClient.hasLocalCarpet) {
            PayloadTypeRegistry.playC2S().register(CarpetPayload.TYPE, CarpetPayload.STREAM_CODEC)
            PayloadTypeRegistry.playS2C().register(CarpetPayload.TYPE, CarpetPayload.STREAM_CODEC)
            ClientPlayNetworking.registerGlobalReceiver(CarpetPayload.TYPE, ::handleCarpetPacket)
        }
    }

    private fun handleCarpetPacket(payload: CarpetPayload, context: ClientPlayNetworking.Context) {
        for (key in payload.data.allKeys) {
            when (key) {
                "69" -> this.handleCarpetHello(context)
                "Rules" -> this.handleCarpetRules(payload.data.getCompound(key))
            }
        }
    }

    private fun handleCarpetHello(context: ClientPlayNetworking.Context) {
        val data = CompoundTag()
        data.putString("420", "essential-client-spoofed")
        context.responseSender().sendPacket(CarpetPayload(data))
        EssentialClient.setMultiplayerCarpet(context.player().connection)
    }

    private fun handleCarpetRules(tag: CompoundTag) {
        EssentialClient.synchronizeCarpetRules(tag)
    }

    private class CarpetPayload(val data: CompoundTag): CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
            return TYPE
        }

        companion object {
            val TYPE = CustomPacketPayload.Type<CarpetPayload>(ResourceLocation.parse("carpet:hello"))

            val STREAM_CODEC: StreamCodec<ByteBuf, CarpetPayload> = ByteBufCodecs.COMPOUND_TAG.map(::CarpetPayload, CarpetPayload::data)
        }
    }
}