package net.cydhra.technocracy.foundation.conduitnet.transit

import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkGatewayNode

class TransitNode(val conduitNode: ConduitNetworkGatewayNode) {

    /**
     * A map where all reachable transit nodes map to the respective path cost to reach them.
     */
    val pathCosts = mutableMapOf<TransitNode, Int>()
}