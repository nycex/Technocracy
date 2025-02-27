package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractRotateableBlock
import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material


abstract class BaseRotateableTileEntityBlock(unlocalizedName: String,
                                             registryName: String = unlocalizedName,
                                             colorMultiplier: ConstantBlockColor? = null,
                                             material: Material)
    : AbstractRotateableBlock(unlocalizedName, material, registryName, colorMultiplier), ITileEntityProvider