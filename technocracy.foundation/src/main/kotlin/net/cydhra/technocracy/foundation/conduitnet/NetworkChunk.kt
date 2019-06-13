package net.cydhra.technocracy.foundation.conduitnet

import com.google.common.collect.Lists
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkEdge
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkGatewayNode
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkPassiveNode
import net.cydhra.technocracy.foundation.conduitnet.transit.TransitNode
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * A chunk of the network structure that is placed inside a chunk (who would have thought)
 *
 * @param chunk the chunk this network partition is associated to
 */
class NetworkChunk(private val chunk: Chunk) {

    companion object {
        const val NBT_KEY_NODE_LIST = "nodes"
        const val NBT_KEY_EDGE_MAP_POSITIONS = "pos"
        const val NBT_KEY_EDGE_MAP_EDGES = "edge"
        const val NBT_KEY_EDGE_MAP = "edges"
        const val NBT_KEY_TRANIST_LIST = "transit"
    }

    private val nodes = mutableListOf<ConduitNetworkNode>()

    private val edges = mutableMapOf<BlockPos, MutableList<ConduitNetworkEdge>>()

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

    fun insertNode(pos: BlockPos, world: World, pipeType: PipeType) {
        // to make it easier to recursively call this method on the same and on other chunks, check for existance of
        // pipes here and not at callsite
        if (nodes.any { it.pos == pos && it.type == pipeType })
            return

        val tileEntity = world.getTileEntity(pos) ?: error("cannot insert normal block into network")

        val newNode = if (tileEntity is TileEntityPipe) {
            if (pos.atChunkEdge()) {
                ConduitNetworkGatewayNode(pos, true, pipeType)
            } else {
                ConduitNetworkPassiveNode(pos, pipeType)
            }
        } else {
            // else if sided inventory capability
            // else if fluid inventory capability
            // else if energy storage capability
            ConduitNetworkGatewayNode(pos, false, PipeType.NONE)
        }

        nodes += newNode

        for (face in EnumFacing.values()) {
            val offPos = newNode.pos.offset(face)

            world.getTileEntity(offPos) ?: continue
            insertNode(offPos, world, pipeType)
        }

        edges.putIfAbsent(pos, mutableListOf())

        for (face in EnumFacing.values()) {
            val offPos = newNode.pos.offset(face)
            if (edges[pos]?.any { (a, b) -> (if (a == newNode) b else a).pos == offPos } != null)
                continue
            val neighborNode = nodes.firstOrNull { it.pos == offPos } ?: continue

            val edge = ConduitNetworkEdge(newNode, neighborNode, type = pipeType)
            edges[pos]!!.add(edge)
            edges[offPos]!!.add(edge)
        }

        markDirty()
    }

    fun removeNode(pos: BlockPos, world: World, pipeType: PipeType) {
        // remove edges
        val iter = edges[pos]!!.iterator()
        for (edge in iter) {
            if (edge.type != pipeType)
                continue

            val otherNode = if (edge.a.pos == pos) edge.b else edge.a
            edges[otherNode.pos]!!.removeIf { it.type == pipeType && (it.a == pos || it.b == pos) }
            iter.remove()
        }

        // remove node
        nodes.removeIf { it.pos == pos && it.type == pipeType }

        markDirty()
    }

    fun removeEdge(pos: BlockPos, face: EnumFacing) {
        TODO()

        markDirty()
    }

    fun insertEdge(pos: BlockPos, face: EnumFacing) {
        TODO()

        markDirty()
    }

    private fun markDirty() {
        cacheValidationCounter++

        recalculateInternalTransitNetwork()
    }

    /**
     * The connections and path costs inside the chunk can be calculated and stored in an internal transit structure
     * to save on performance on large networks. The calculation is done once every change in the network. All
     * connected components of the graph are evaluated and their path costs are calculated.
     */
    private fun recalculateInternalTransitNetwork() {
        val unvisitedNodes = LinkedBlockingQueue<ConduitNetworkNode>(this.nodes.size)
        unvisitedNodes.addAll(this.nodes)

        /**
         * Perform a depth first search for all gateway nodes that are reachable from the given start node. While
         * doing that, remove any visited nodes from the queue that is used in the main loop.
         */
        fun depthFirstDiscoverNode(node: ConduitNetworkNode, transitList: MutableList<TransitNode>) {
            if (node is ConduitNetworkGatewayNode) {
                transitList += TransitNode(node)

                if (!node.eligibleForTransit) {
                    return
                }
            }

            this.edges[node.pos]?.forEach { edge ->
                val nextNode =
                        if (edge.a == node)
                            edge.b
                        else
                            edge.a

                if (unvisitedNodes.remove(nextNode)) {
                    depthFirstDiscoverNode(nextNode, transitList)
                }
            }
        }

        /**
         * Calculate the minimum number of nodes that need to be traversed to get from [origin] to [target] using an
         * A* algorithm. The algorithm uses [manhattanDistance] as a heuristic to choose depth-first paths for
         * traversal.
         */
        fun calculateAStarPathCost(origin: TransitNode, target: TransitNode, transitNet: List<TransitNode>): Int {
            /**
             * Extension utility function to calculate the Manhattan-distance of two block positions.
             */
            fun BlockPos.manhattanDistance(other: BlockPos): Int {
                return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z)
            }

            /**
             * State for the algorithm. Saves for each node how to get there and its respective transit node, if it
             * is a gateway node.
             */
            data class DijkstraState(val node: ConduitNetworkNode, val comeFrom: DijkstraState?,
                                     var transitNode: TransitNode? = null) {
                fun heuristic(target: TransitNode): Int {
                    if (transitNode != null) {
                        return transitNode!!.pathCosts[target]
                                ?: this.node.pos.manhattanDistance(target.conduitNode.pos)
                    } else if (node is ConduitNetworkGatewayNode) {
                        this.transitNode = transitNet.find { it.conduitNode == node }
                        return Math.max(transitNode!!.pathCosts[target]!!,
                                this.node.pos.manhattanDistance(target.conduitNode.pos))
                    }

                    return this.node.pos.manhattanDistance(target.conduitNode.pos)
                }

                fun calculatePath(): Int {
                    return if (comeFrom == null)
                        0
                    else
                        1 + comeFrom.calculatePath()
                }
            }

            // a comparator using the node's heuristics to compare them.
            val comparator = kotlin.Comparator<DijkstraState> { state1, state2 ->
                state1.heuristic(target) - state2.heuristic(target)
            }

            // a priority queue to select nodes to try next
            val priorityQueue = PriorityQueue<DijkstraState>(comparator)

            val originState = DijkstraState(origin.conduitNode, null)

            var candidate = originState
            while (priorityQueue.isNotEmpty()) {
                when {
                    candidate.node == target.conduitNode ->
                        return candidate.calculatePath()
                    candidate.transitNode?.pathCosts?.get(target) ?: -1 > 0 ->
                        return candidate.calculatePath() + candidate.transitNode!!.pathCosts[target]!!
                    else -> {
                        val reachable = this.edges[candidate.node.pos]
                                ?.filter { (a, b) -> a == candidate.node || b == candidate.node }
                                ?.map { (a, b) ->
                                    if (a == candidate.node) DijkstraState(b, candidate) else DijkstraState(a, candidate)
                                }
                                ?: emptyList()

                        priorityQueue.addAll(reachable)
                    }
                }

                candidate = priorityQueue.poll()
            }

            throw IllegalArgumentException("there is no way from ${origin.conduitNode.pos} to ${target.conduitNode.pos}")
        }

        while (unvisitedNodes.isNotEmpty()) {
            // take any node out of the node list of the chunk
            val current = unvisitedNodes.poll()

            // and find the graph component of the node
            val connectedTransitComponent = mutableListOf<TransitNode>()
            depthFirstDiscoverNode(current, connectedTransitComponent)

            // then calculate all paths between the connected transit nodes
            Lists.cartesianProduct(connectedTransitComponent, connectedTransitComponent)
                    .filter { (i, j) -> i != j }
                    .forEach { (i, j) -> i.pathCosts[j] = -1 }

            connectedTransitComponent.forEach { transitNode ->
                transitNode.pathCosts.clear()

                connectedTransitComponent.forEach { target ->
                    if (target != transitNode) {
                        transitNode.pathCosts[target] = calculateAStarPathCost(transitNode, target, connectedTransitComponent)
                    }
                }
            }

            this.internalTransitNetwork.addAll(connectedTransitComponent)
        }
    }

    fun serialize(): NBTTagCompound {
        val compound = NBTTagCompound()

        val nodesList = NBTTagList()
        this.nodes.map(ConduitNetworkNode.Companion::serializeNode).forEach(nodesList::appendTag)
        compound.setTag(NBT_KEY_NODE_LIST, nodesList)

        val posList = NBTTagList()
        this.edges
                .map { (pos, edges) ->
                    val nbtEdgesList = NBTTagList()
                    edges.map(ConduitNetworkEdge::serializeNBT).map(nbtEdgesList::appendTag)
                    NBTTagCompound().apply {
                        setTag(NBT_KEY_EDGE_MAP_POSITIONS, NBTUtil.createPosTag(pos))
                        setTag(NBT_KEY_EDGE_MAP_EDGES, nbtEdgesList)
                    }
                }
                .forEach(posList::appendTag)
        compound.setTag(NBT_KEY_EDGE_MAP, posList)

        // TODO transit network serialization

        return compound
    }

    fun deserialize(compound: NBTTagCompound) {
        this.nodes.addAll(compound.getTagList(NBT_KEY_NODE_LIST, 10)
                .filterIsInstance<NBTTagCompound>().map(ConduitNetworkNode.Companion::deserializeNode))

        compound.getTagList(NBT_KEY_EDGE_MAP, 10)
                .filterIsInstance<NBTTagCompound>()
                .map { compound ->
                    val pos = NBTUtil.getPosFromTag(compound.getCompoundTag(NBT_KEY_EDGE_MAP_POSITIONS))
                    val edgeList = compound.getTagList(NBT_KEY_EDGE_MAP_EDGES, 10)
                    this.edges[pos] = edgeList
                            .filterIsInstance<NBTTagCompound>()
                            .map { nbt -> ConduitNetworkEdge().apply { deserializeNBT(nbt) } }
                            .toMutableList()
                }

        // TODO transit network deserialization
    }

    /**
     * Extension utility function to test, whether a block position is at the edge of a chunk
     */
    private fun BlockPos.atChunkEdge(): Boolean {
        return (this.x % 16 == 0 || this.x % 15 == 0) || (this.z % 16 == 0 || this.z % 15 == 0)
    }
}