package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.pipes.types.PipeType

/**
 * @param a first node of the edge
 * @param b second node of the edge
 * @param capacity optional flow capacity for graph flow algorithms. Defaults to -1
 * @param type type of pipe
 */
class ConduitNetworkEdge(val a: ConduitNetworkNode, val b: ConduitNetworkNode, val capacity: Int = Integer.MAX_VALUE,
                         val type: PipeType) {

    /**
     * Offer the first edge for destructuring
     */
    operator fun component0(): ConduitNetworkNode = a

    /**
     * Offer the second edge for destructuring
     */
    operator fun component1(): ConduitNetworkNode = b
}