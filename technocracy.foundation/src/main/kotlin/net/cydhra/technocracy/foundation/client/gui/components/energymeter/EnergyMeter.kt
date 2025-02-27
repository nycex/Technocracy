package net.cydhra.technocracy.foundation.client.gui.components.energymeter

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.util.math.MathHelper

abstract class EnergyMeter(val posX: Int, val posY: Int) : TCComponent {

    /**
     * energy level from 0.0 to 1.0
     */
    var level = 0.0f

    val width = 10
    val height = 50

    override fun update() {
        this.level += 0.01f
        if (level > 1.1f)
            this.level = 0f

    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {

    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }
}