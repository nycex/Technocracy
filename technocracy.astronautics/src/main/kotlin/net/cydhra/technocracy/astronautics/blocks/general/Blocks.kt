package net.cydhra.technocracy.astronautics.blocks.general

import net.cydhra.technocracy.astronautics.blocks.*
import net.cydhra.technocracy.foundation.blocks.ColoredPlainBlock
import net.cydhra.technocracy.foundation.blocks.PlainBlock
import net.minecraft.block.material.Material

val scaffoldBlock = ScaffoldBlock()
val reinforcedConcreteBlock = ReinforcedConcreteBlock()
val wetConcreteBlock = WetConcreteBlock()
val wetReinforcedConcreteBlock = WetReinforcedConcreteBlock()
val rocketControllerBlock = RocketControllerBlock()

val rocketHullBlock = ColoredPlainBlock("rocket_hull", Material.IRON)
val rocketDriveBlock = PlainBlock("rocket_drive", Material.IRON)
val rocketTipBlock = PlainBlock("rocket_tip", Material.IRON)