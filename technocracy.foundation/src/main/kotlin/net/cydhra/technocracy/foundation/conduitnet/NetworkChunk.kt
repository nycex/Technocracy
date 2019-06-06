package net.cydhra.technocracy.foundation.conduitnet

import com.google.common.collect.Lists
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkEdge
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkGatewayNode
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkPassiveNode
import net.cydhra.technocracy.foundation.conduitnet.transit.TransitNode
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.nbt.NBTTagCompound
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

    fun insertNode(pos: BlockPos, world: World) {
        val tileEntity = world.getTileEntity(pos) ?: error("cannot insert normal block into network")

        val newNode = if (tileEntity is TileEntityPipe) {
            if (pos.atChunkEdge()) {
                ConduitNetworkGatewayNode(pos, true)
            } else {
                ConduitNetworkPassiveNode(pos)
            }
        } else {
            // else if sided inventory capability
            // else if fluid inventory capability
            // else if energy storage capability
            ConduitNetworkGatewayNode(pos, false)
        }

        nodes += newNode

        for (face in EnumFacing.values()) {
            val offPos = newNode.pos.offset(face)

            if (nodes.any { it.pos == offPos })
                continue

            world.getTileEntity(offPos) ?: continue
            insertNode(offPos, world)
        }

        for (face in EnumFacing.values()) {
            val offPos = newNode.pos.offset(face)
            if (edges[pos]?.any { (a, b) -> (if (a == newNode) b else a).pos == offPos } != null)
                continue
            val neighborNode = nodes.firstOrNull { it.pos == offPos } ?: continue

            edges.putIfAbsent(pos, mutableListOf())
            // TODO correct pipe type
            edges[pos]!!.add(ConduitNetworkEdge(newNode, neighborNode, type = PipeType.ENERGY))
        }

        markDirty()
    }

    fun removeNode(pos: BlockPos, world: World) {
        TODO()

        markDirty()
    }

    fun removeEdge(pos: BlockPos, face: EnumFacing) {
        TODO()

        markDirty()
    }

    fun insertEdge(pos: BlockPos, face: EnumFacing) {

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
        }
    }

    fun serialize(): NBTTagCompound {
        TODO("not implemented")
    }

    fun deserialize(compound: NBTTagCompound) {
        TODO()
    }

    /**
     * Extension utility function to test, whether a block position is at the edge of a chunk
     */
    private fun BlockPos.atChunkEdge(): Boolean {
        return (this.x % 16 == 0 || this.x % 15 == 0) && (this.z % 16 == 0 || this.z % 15 == 0)
    }
}