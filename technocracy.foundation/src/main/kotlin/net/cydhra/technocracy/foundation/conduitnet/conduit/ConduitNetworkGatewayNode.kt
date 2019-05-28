package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * @param networkId a unique id of the network this node belongs to
 * @param pos a block position
 */
open class ConduitNetworkGatewayNode(networkId: UUID, pos: BlockPos) : ConduitNetworkNode(networkId, pos)