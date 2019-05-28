package net.cydhra.technocracy.foundation.conduitnet.routing

import net.cydhra.technocracy.foundation.conduitnet.NetworkChunk
import net.cydhra.technocracy.foundation.conduitnet.conduit.ConduitNetworkNode

/**
 * A route or a flow (combined from routes) abstracted to its input and output and a list of traversed chunks and
 * their cached validity counters. The actual traversed nodes and edges aren't cached, as those are not required for
 * content transfer. Clients should call [isStillValid] to check whether the route is still valid or might have
 * changed and should be recalculated.
 *
 * @param input entry node of the conduit network, supplies something
 * @param output exit node of the conduit network, consumes the supplied good
 * @param capacity how much of the supplied good can be transferred. MAX_INT by default indicating limitless transfer.
 * @param cost path cost for the route. Undefined for flows, as there cannot be two flows between the same in/out nodes
 * @param cacheValidityCounts an array of pairs of saved validity counts and the respective chunk this route traverses
 */
class CachedRoute(val input: ConduitNetworkNode, val output: ConduitNetworkNode, val capacity: Int = Integer.MAX_VALUE,
                  val cost: Int, private val cacheValidityCounts: Array<Pair<Int, NetworkChunk>>) {

    /**
     * @return true, if the route is still unchanged and can be used safely, false if any of the traversed chunks
     * changed thus requiring an update of the route.
     */
    fun isStillValid(): Boolean {
        return cacheValidityCounts.all { (count, chunk) -> count == chunk.cacheValidationCounter }
    }
}