package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.network.componentsync.guiInfoPacketSubscribers
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MachineBlock(name: String, private val tileEntityConstructor: () -> TileEntity)
    : BaseRotateableTileEntityBlock(name, material = Material.ROCK) {
    init {
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return tileEntityConstructor()
    }

    @Suppress("OverridingDeprecatedMember")
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
                guiInfoPacketSubscribers[playerIn as EntityPlayerMP] =  Pair(pos, worldIn.provider.dimension)
            }

            return true
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }
}