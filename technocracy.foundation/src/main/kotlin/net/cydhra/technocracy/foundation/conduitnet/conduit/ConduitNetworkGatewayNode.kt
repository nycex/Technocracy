package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.PipeType
import net.minecraft.util.math.BlockPos

/**
 * A conduit network model node that is part of the transit network in some way: It can be a node at a chunk edge
 * that must be connected with other transit gateways in the transit network or it can be a machine that accepts some
 * input or generates output for the network. It can also be both: A machine placed at a chunk border must also be a
 * transit node, because it must be connected to pipes in the chunk next to it.
 *
 * @param pos a block position where this node is located
 * @param eligibleForTransit whether this node can be used to route contents through.
 * @param type the pipe type of the node
 */
open class ConduitNetworkGatewayNode(pos: BlockPos, val eligibleForTransit: Boolean, type: PipeType) :
        ConduitNetworkNode(pos, type)