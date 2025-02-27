package net.cydhra.technocracy.astronautics.proxy


class ClientProxy : CommonProxy() {
    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
    }

    override fun preInit() {
        super.preInit()
        entityManager.registerRenderer()
    }
}