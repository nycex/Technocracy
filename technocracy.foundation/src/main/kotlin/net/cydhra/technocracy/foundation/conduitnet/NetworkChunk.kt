package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode
import net.cydhra.technocracy.foundation.conduitnet.transit.TransitNode
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.chunk.Chunk

/**
 * A chunk of the network structure that is placed inside a chunk (who would have thought)
 *
 * @param chunk the chunk this network partition is associated to
 */
class NetworkChunk(private val chunk: Chunk) {

    private val nodes = mutableListOf<ConduitNetworkNode>()

    private val transitNodes = mutableListOf<TransitNode>()

    /**
     * Incremented each time a modification of the internal data is occurring, so cached routes know when they have
     * to be re-evaluated
     */
    var cacheValidationCounter = 0
        private set

    fun insertNode(node: ConduitNetworkNode) {
        TODO()

        cacheValidationCounter++
    }

    fun removeNode(node: ConduitNetworkNode) {
        TODO()

        cacheValidationCounter++
    }

    fun loadNeighborChunk(chunk: NetworkChunk) {
        TODO()

        cacheValidationCounter++
    }

    fun unloadNeighborChunk(chunk: NetworkChunk) {
        TODO()

        cacheValidationCounter++
    }

    fun unload() {
        TODO()
    }

    fun serialize(): NBTTagCompound {
        TODO("not implemented")
    }

    fun deserialize(compound: NBTTagCompound) {
        TODO()
    }
}