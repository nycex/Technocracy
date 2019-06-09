package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.INBTSerializable

/**
 * The conduit network is a one-to-one representation of a pipe network structure as given by the placement of pipes.
 */
abstract class ConduitNetworkNode : INBTSerializable<NBTTagCompound> {
    lateinit var pos: BlockPos
    lateinit var type: PipeType
}