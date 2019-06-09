package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.math.BlockPos

/**
 * A passive node in the conduit network that just exists to connect different gateway nodes. It does not do anything
 * on its own.
 */
class ConduitNetworkPassiveNode() : ConduitNetworkNode() {

    companion object {
        private const val NBT_KEY_POS = "pos"
        private const val NBT_KEY_TYPE = "type"
    }

    constructor(pos: BlockPos, type: PipeType) : this() {
        this.pos = pos
        this.type = type
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.pos = NBTUtil.getPosFromTag(nbt.getCompoundTag(NBT_KEY_POS))
        this.type = PipeType.values()[nbt.getInteger(NBT_KEY_TYPE)]
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setTag(NBT_KEY_POS, NBTUtil.createPosTag(pos))
            setInteger(NBT_KEY_TYPE, type.ordinal)
        }
    }
}