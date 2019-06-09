package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.math.BlockPos

/**
 * A conduit network model node that is part of the transit network in some way: It can be a node at a chunk edge
 * that must be connected with other transit gateways in the transit network or it can be a machine that accepts some
 * input or generates output for the network. It can also be both: A machine placed at a chunk border must also be a
 * transit node, because it must be connected to pipes in the chunk next to it.
 */
class ConduitNetworkGatewayNode() : ConduitNetworkNode() {

    companion object {
        private const val NBT_KEY_POS = "pos"
        private const val NBT_KEY_TYPE = "type"
        private const val NBT_KEY_TRANSIT = "transit"
    }

    private var _eligibleForTransit: Boolean? = null

    val eligibleForTransit: Boolean
        get() {
            return if (_eligibleForTransit == null)
                throw IllegalStateException("property cannot be accessed before assignment")
            else _eligibleForTransit!!
        }

    /**
     * @param pos a block position where this node is located
     * @param eligibleForTransit whether this node can be used to route contents through.
     * @param type the pipe type of the node
     */
    constructor(pos: BlockPos, eligibleForTransit: Boolean, type: PipeType) : this() {
        this.pos = pos
        this.type = type
        this._eligibleForTransit = eligibleForTransit
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.pos = NBTUtil.getPosFromTag(nbt.getCompoundTag(NBT_KEY_POS))
        this.type = PipeType.values()[nbt.getInteger(NBT_KEY_TYPE)]
        this._eligibleForTransit = nbt.getBoolean(NBT_KEY_TRANSIT)
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setTag(NBT_KEY_POS, NBTUtil.createPosTag(pos))
            setInteger(NBT_KEY_TYPE, type.ordinal)
            setBoolean(NBT_KEY_TRANSIT, eligibleForTransit)
        }
    }
}
