package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.client.events.KeyEventHandler
import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.tileentity.management.TileEntityManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.gameevent.TickEvent


/**
 * Client side implementation of sided proxy. Calls Common proxy and adds client-side-only behaviour like rendering
 * and animations.
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
class ClientProxy : CommonProxy() {

    /**
     * Loads an [IAnimationStateMachine] from a model file at [location] on client side.
     */
    override fun loadAnimationStateMachine(location: ResourceLocation, parameters: ImmutableMap<String, ITimeValue>):
            IAnimationStateMachine? {
        return ModelLoaderRegistry.loadASM(location, parameters)
    }

    override fun preInit() {
        super.preInit()
        TextureAtlasManager()
        MinecraftForge.EVENT_BUS.register(RenderEventListener())
        MinecraftForge.EVENT_BUS.register(KeyEventHandler)
    }

    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
        tileEntityManager.onClientInitialize()
    }
}