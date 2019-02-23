package net.cydhra.technocracy.foundation.materials

import net.cydhra.technocracy.foundation.blocks.OreBlock
import net.cydhra.technocracy.foundation.items.IngotItem
import net.cydhra.technocracy.foundation.items.ItemManager

/**
 * A material system consists of an ore type, an ingot and all stages of ore processing necessary.
 */
class MaterialSystem(materialName: String) {

    /**
     * The ingot of this material
     */
    val ingot = IngotItem(materialName)

    /**
     * The ore of this material
     */
    val ore = OreBlock()

    init {
        ItemManager.prepareItemForRegistration(this.ingot)
    }
}