package net.cydhra.technocracy.foundation.tileentity.api

import net.cydhra.technocracy.foundation.tileentity.AbstractTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Aggregation of [AbstractComponent] implementations used for tile entities. Note, that this interface is not necessarily
 * implemented by a tile entity, but can be implemented as a delegate instead.
 */
interface TCAggregatable {

    var tile: TileEntity

    fun getComponents(): MutableList<Pair<String, AbstractComponent>>

    fun registerComponent(component: AbstractComponent, name: String)

    fun serializeNBT(compound: NBTTagCompound): NBTTagCompound

    fun deserializeNBT(compound: NBTTagCompound)

    fun supportsCapability(capability: Capability<*>, facing: EnumFacing?): Boolean

    fun <T : Any?> castCapability(capability: Capability<T>, facing: EnumFacing?): T?

    /**
     * Called whenever syncing requires an NBT data structure to send to the client. Contains all state information
     * about all registered components
     *
     * @param player player this update is intended for. Can be used to reduce amount of accessible information
     * @param tag the NBT tag compound to write data into
     */
    fun generateNbtUpdateCompound(player: EntityPlayerMP, tag: NBTTagCompound): NBTTagCompound
}