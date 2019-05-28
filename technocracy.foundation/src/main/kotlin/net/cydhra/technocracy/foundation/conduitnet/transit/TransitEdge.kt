package net.cydhra.technocracy.foundation.conduitnet.transit

import net.cydhra.technocracy.foundation.conduitnet.PipeType


/**
 * @param a first node of the transit edge
 * @param b second node of the transit edge
 * @param types all pipe types on the path between a and b
 * @param capacities the minimum capacity of the path between a and b
 * @param cost the amount of nodes between a and b; a inclusive, b exclusive
 */
class TransitEdge(val a: TransitNode, val b: TransitNode, val types: Array<PipeType>,
                  val capacities: Array<Int> = Array(types.size) { Integer.MAX_VALUE }, val cost: Int) {

    init {
        check(types.size == capacities.size) { "less capacity information than types available" }
    }

    /**
     * Offer the first edge for destructuring
     */
    operator fun component0(): TransitNode = a

    /**
     * Offer the second edge for destructuring
     */
    operator fun component1(): TransitNode = b
}