package net.cydhra.technocracy.foundation.conduitnet.transit

import net.cydhra.technocracy.foundation.conduitnet.NetworkChunk
import net.minecraft.util.math.ChunkPos

/**
 * The global network of all pipe connections assembled from chunk-based graph partitions.
 */
class DimensionNetwork {
    /**
     * A map of all active chunks accessible by their chunk position
     */
    private val activeChunks = mutableMapOf<ChunkPos, NetworkChunk>()

    /**
     * The transit graph as a list of nodes
     */
    private val transitNodes = mutableListOf<TransitNode>()

    /**
     * Connect a chunk to the transit network.
     */
    fun loadNetworkChunk(chunk: NetworkChunk) {
        activeChunks[chunk.chunkPos] = chunk
    }

    /**
     * Disconnect a chunk from the transit network.
     */
    fun unloadNetworkChunk(chunkPos: ChunkPos) {
        activeChunks.remove(chunkPos)
    }

    /**
     * @param chunkPos a chunk coordinate pair
     *
     * @return true, if the network chunk at the given location is currently loaded.
     */
    fun isChunkLoaded(chunkPos: ChunkPos): Boolean {
        return activeChunks.containsKey(chunkPos)
    }

    /**
     * Get the [NetworkChunk] at the given [ChunkPos] or null if no chunk is loaded at that location.
     *
     * @see isChunkLoaded
     */
    fun getChunk(chunkPos: ChunkPos): NetworkChunk? {
        return activeChunks[chunkPos]
    }

    /**
     * @return all currently loaded [NetworkChunk]s of the dimension
     */
    fun getChunks(): List<NetworkChunk> {
        return activeChunks.values.toList()
    }
}