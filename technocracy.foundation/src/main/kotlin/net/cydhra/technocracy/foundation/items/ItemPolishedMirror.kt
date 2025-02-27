package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemPolishedMirror : BaseItem("polished_mirror") {

    @SideOnly(Side.CLIENT)
    override fun hasEffect(stack: ItemStack): Boolean {
        return true
    }
}