package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.conduitnet.transit.GlobalTransitNetworks
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ConduitNetworkManager {

    private const val NBT_KEY_NETWORK = "network"

    @SubscribeEvent
    fun chunkDataLoad(event: ChunkDataEvent.Load) {
        if (event.world.isRemote)
            return

        val networkChunk = NetworkChunk(event.chunk)

        if (event.data.hasKey(NBT_KEY_NETWORK)) {
            networkChunk.deserialize(event.data.getCompoundTag(NBT_KEY_NETWORK))
        }

        GlobalTransitNetworks.getNetwork(event.world.provider.dimension).loadNetworkChunk(networkChunk)
    }

    @SubscribeEvent
    fun chunkDataSave(event: ChunkDataEvent.Save) {
        if (event.world.isRemote)
            return

        event.data.setTag(NBT_KEY_NETWORK,
                GlobalTransitNetworks.getNetwork(event.world.provider.dimension).getChunk(event.chunk.pos).serialize())
    }

    @SubscribeEvent
    fun chunkUnload(event: ChunkEvent.Unload) {
        if (event.world.isRemote)
            return

        GlobalTransitNetworks.getNetwork(event.world.provider.dimension).unloadNetworkChunk(event.chunk.pos)
    }
}