package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode
import net.cydhra.technocracy.foundation.conduitnet.transit.TransitNode

/**
 * A chunk of the network structure that is placed inside a chunk (who would have thought)
 */
class NetworkChunk {

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
}