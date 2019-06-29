package net.cydhra.technocracy.foundation.conduitnet

import net.cydhra.technocracy.foundation.conduitnet.transit.GlobalTransitNetworks
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

object ConduitNetworkManager {

    private const val NBT_KEY_NETWORK = "network"

    @Suppress("unused")
    @SubscribeEvent
    fun chunkDataLoad(event: ChunkDataEvent.Load) {
        if (event.world.isRemote)
            return

        val networkChunk = NetworkChunk(event.chunk)

        if (event.data.hasKey(NBT_KEY_NETWORK)) {
            networkChunk.deserialize(event.data.getCompoundTag(NBT_KEY_NETWORK))
        }

        GlobalTransitNetworks.getNetwork(event.world.provider.dimension).loadNetworkChunk(networkChunk)
    }

    @Suppress("unused")
    @SubscribeEvent
    fun chunkDataSave(event: ChunkDataEvent.Save) {
        if (event.world.isRemote)
            return

        var chunk = GlobalTransitNetworks.getNetwork(event.world.provider.dimension).getChunk(event.chunk.pos)

        // when a chunk is first created, no load event is fired, instead it is immediately saved
        if (chunk == null) {
            chunk = NetworkChunk(event.chunk)
            GlobalTransitNetworks.getNetwork(event.world.provider.dimension).loadNetworkChunk(chunk)
        }

        event.data.setTag(NBT_KEY_NETWORK, chunk.serialize())
    }

    @Suppress("unused")
    @SubscribeEvent
    fun chunkUnload(event: ChunkEvent.Unload) {
        if (event.world.isRemote)
            return

        GlobalTransitNetworks.getNetwork(event.world.provider.dimension).unloadNetworkChunk(event.chunk.pos)
    }

    @SubscribeEvent
    fun renderDebugEvent(event: RenderWorldLastEvent) {
        val mc = Minecraft.getMinecraft()
        val doubleX = mc.player.posX
        val doubleY = mc.player.posY
        val doubleZ = mc.player.posZ

        GL11.glPushMatrix()
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ)


        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_LIGHTING)

        GL11.glDepthMask(false)

        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        GL11.glLineWidth(0.5f)

        GL11.glBegin(GL11.GL_LINES)
        GlobalTransitNetworks.getNetwork(0).getChunks().forEach { nChunk ->
            nChunk.transitNodes.forEach { tNode ->
                tNode.pathCosts.keys.forEach { tPathNode ->
                    with(tNode.conduitNode.pos) {
                        GL11.glVertex3d(x.toDouble(), y.toDouble(), z.toDouble())
                    }
                    with(tPathNode.conduitNode.pos) {
                        GL11.glVertex3d(x.toDouble(), y.toDouble(), z.toDouble())
                    }
                }
            }
        }
        GL11.glEnd()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        GL11.glDepthMask(true)
        GL11.glPopMatrix()
    }
}