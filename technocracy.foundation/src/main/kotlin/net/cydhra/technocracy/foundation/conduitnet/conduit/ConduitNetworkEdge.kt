package net.cydhra.technocracy.foundation.conduitnet.conduit

import net.cydhra.technocracy.foundation.conduitnet.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

/**
 * An edge between to nodes of the conduit network. Each edge is of one common pipe type of the two nodes. Two nodes
 * can have multiple edges of different types. Flow pipes have a capacity on their edges.
 */
class ConduitNetworkEdge() : INBTSerializable<NBTTagCompound> {

    private var _capacity: Int? = null

    lateinit var a: ConduitNetworkNode
        private set
    lateinit var b: ConduitNetworkNode
        private set
    lateinit var type: PipeType
        private set

    val capacity: Int
        get() {
            return if (_capacity == null)
                throw IllegalStateException("cannot access property before assignment")
            else _capacity!!
        }

    /**
     * @param a first node of the edge
     * @param b second node of the edge
     * @param capacity optional flow capacity for graph flow algorithms. Defaults to -1
     * @param type type of pipe
     */
    constructor(a: ConduitNetworkNode, b: ConduitNetworkNode, capacity: Int = Integer.MAX_VALUE, type: PipeType) : this() {
        this.a = a
        this.b = b
        this._capacity = capacity
        this.type = type
    }

    companion object {
        private const val NBT_KEY_NODE_A = "a"
        private const val NBT_KEY_NODE_B = "b"
        private const val NBT_KEY_CAPACITY = "capacity"
        private const val NBT_KEY_TYPE = "type"
    }

    /**
     * Offer the first edge for destructuring
     */
    operator fun component1(): ConduitNetworkNode = a

    /**
     * Offer the second edge for destructuring
     */
    operator fun component2(): ConduitNetworkNode = b

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.a = ConduitNetworkNode.deserializeNode(nbt.getCompoundTag(NBT_KEY_NODE_A))
        this.b = ConduitNetworkNode.deserializeNode(nbt.getCompoundTag(NBT_KEY_NODE_B))
        this._capacity = nbt.getInteger(NBT_KEY_CAPACITY)
        this.type = PipeType.values()[nbt.getInteger(NBT_KEY_TYPE)]
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setTag(NBT_KEY_NODE_A, ConduitNetworkNode.serializeNode(a))
            setTag(NBT_KEY_NODE_B, ConduitNetworkNode.serializeNode(b))
            setInteger(NBT_KEY_CAPACITY, capacity)
            setInteger(NBT_KEY_TYPE, type.ordinal)
        }
    }
}