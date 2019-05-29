package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode
import net.cydhra.technocracy.foundation.conduitnet.transit.TransitNode
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk

/**
 * A chunk of the network structure that is placed inside a chunk (who would have thought)
 *
 * @param chunk the chunk this network partition is associated to
 */
class NetworkChunk(private val chunk: Chunk) {

    private val nodes = mutableListOf<ConduitNetworkNode>()

    /**
     * The internal network of transit nodes and their edges. Updated whenever the network changes.
     */
    private val internalTransitNetwork = mutableListOf<TransitNode>()

    /**
     * [ChunkPos] of the network chunk
     */
    val chunkPos: ChunkPos
        get() = chunk.pos

    /**
     * Incremented each time a modification of the internal data is occurring, so cached routes know when they have
     * to be re-evaluated
     */
    var cacheValidationCounter = 0
        private set

    fun insertNode(pos: BlockPos, world: World) {


        markDirty()
    }

    fun removeNode(pos: BlockPos, world: World) {
        TODO()

        markDirty()
    }

    private fun markDirty() {
        cacheValidationCounter++

        TODO("recalculate internal transit network")
    }

    fun serialize(): NBTTagCompound {
        TODO("not implemented")
    }

    fun deserialize(compound: NBTTagCompound) {
        TODO()
    }
}