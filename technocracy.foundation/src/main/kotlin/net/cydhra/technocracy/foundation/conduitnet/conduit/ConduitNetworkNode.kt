package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * @param networkId a unique id of the network this node belongs to
 * @param pos a block position
 */
abstract class ConduitNetworkNode(val networkId: UUID, private val pos: BlockPos)