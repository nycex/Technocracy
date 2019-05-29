package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode
import net.cydhra.technocracy.foundation.conduitnet.transit.TransitNode
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import java.util.*

/**
 * A chunk of the network structure that is placed inside a chunk (who would have thought)
 *
 * @param chunk the chunk this network partition is associated to
 */
class NetworkChunk(private val chunk: Chunk) {

    private val nodes = mutableListOf<ConduitNetworkNode>()

    /**
     * The internal network of transit nodes and their edges. Updated whenever the network changes.
     */
    private val internalTransitNetwork = mutableListOf<TransitNode>()

    /**
     * Cached set of network UUIDs that are available within this chunk
     */
    private var networkIdCache: Set<UUID>? = null

    /**
     * Set of networks residing in this chunk. Networks may not be accessible through the transit network.
     */
    val networks: Set<UUID>
        get() {
            if (networkIdCache == null) {
                networkIdCache = nodes.map { it.networkId }.toSet()
            }

            return networkIdCache!!
        }

    /**
     * [ChunkPos] of the network chunk
     */
    val chunkPos: ChunkPos
        get() = chunk.pos

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

    private fun markDirty() {
        networkIdCache = null
        cacheValidationCounter++

        TODO("recalculate internal transit network")
    }

    fun serialize(): NBTTagCompound {
        TODO("not implemented")
    }

    fun deserialize(compound: NBTTagCompound) {
        TODO()
    }
}