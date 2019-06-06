package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.PipeType
import net.minecraft.util.math.BlockPos

/**
 * The conduit network is a one-to-one representation of a pipe network structure as given by the placement of pipes.
 *
 * @param pos the block position of the pipe tile entity that this node is a model of
 */
abstract class ConduitNetworkNode(val pos: BlockPos, val type: PipeType)