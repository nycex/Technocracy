package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ConduitNetworkManager {

    private const val NBT_KEY_NETWORK = "network"

    private val networkChunks = HashMap<ChunkPos, NetworkChunk>()

    @SubscribeEvent
    fun chunkDataLoad(event: ChunkDataEvent.Load) {
        if (event.world.isRemote)
            return

        val networkChunk = NetworkChunk(event.chunk)
        networkChunks[event.chunk.pos] = networkChunk

        if (event.data.hasKey(NBT_KEY_NETWORK)) {
            networkChunk.deserialize(event.data.getCompoundTag(NBT_KEY_NETWORK))
        }
    }

    @SubscribeEvent
    fun chunkDataSave(event: ChunkDataEvent.Save) {
        if (event.world.isRemote)
            return

        // save networks
        event.data.setTag(NBT_KEY_NETWORK, (networkChunks[event.chunk.pos]
                ?: error("chunk had no network assigned")).serialize())
    }

    @SubscribeEvent
    fun chunkUnload(event: ChunkEvent.Unload) {
        if (event.world.isRemote)
            return

        networkChunks.remove(event.chunk.pos)?.unload() ?: TCFoundation.logger
                .warn("unloaded chunk (${event.chunk.pos}) that had no network assigned")
    }
}