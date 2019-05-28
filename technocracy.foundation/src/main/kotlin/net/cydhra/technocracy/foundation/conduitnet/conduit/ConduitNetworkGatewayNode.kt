package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.transit.TransitEdge
import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * @param networkId a unique id of the network this node belongs to
 * @param pos a block position
 */
open class ConduitNetworkGatewayNode(networkId: UUID, pos: BlockPos) : ConduitNetworkNode(networkId, pos) {

    /**
     * A list of transit edges (path-compressed routes) to other transit nodes (like machines and chunk exit
     * nodes) with their travel cost and flow capacity
     */
    val transitEdges = mutableListOf<TransitEdge>()
}