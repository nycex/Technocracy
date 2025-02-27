package net.cydhra.technocracy.foundation.items.general

import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item

/**
 * Base class for all items added by this modification.
 *
 * @param unlocalizedName the unlocalized name of the item that can be looked up in lang files
 * @param registryName the name this item is registered as. Per default, this is the unlocalized name
 * @param oreDictName the entry for the ore dictionary for this item. Can be null (default)
 * @param itemColor an [IItemColor] instance defining a tint for specific layers of this item
 */
open class BaseItem(unlocalizedName: String,
                    registryName: String = unlocalizedName,
                    val oreDictName: String? = null,
                    val itemColor: IItemColor? = null) : Item() {

    /**
     * A convenience property used during item registration to set the model location to the default assets path and
     * the registry name as file name. Can be overridden to use a different model
     */
    open val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}