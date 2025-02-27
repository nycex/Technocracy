package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart

import net.minecraft.util.ResourceLocation

abstract class BaseMultiblockTab(val controller: TileEntityMultiBlockPart<*>, parent: TCGui, icon: ResourceLocation) : TCTab(name = controller.blockType.localizedName, parent = parent, icon = icon) {

}