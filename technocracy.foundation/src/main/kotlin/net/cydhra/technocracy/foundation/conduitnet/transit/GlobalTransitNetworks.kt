package net.cydhra.technocracy.foundation.conduitnet.transit

object GlobalTransitNetworks {

    private val networks = mutableMapOf<Int, DimensionNetwork>()

    /**
     * Get the dimension-global transit network by the [dimensionId]
     */
    fun getNetwork(dimensionId: Int): DimensionNetwork {
        return this.networks.getOrPut(dimensionId, { DimensionNetwork() })
    }
}