package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [ChemicalProcessingRecipe]
 */
object DissolutionRecipeParser : RecipeParser<DissolutionRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_INPUT_FLUID = "fluid"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): DissolutionRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input item parameter" }
        check(json.has(JSON_KEY_INPUT_FLUID)) { "recipe is missing input fluid parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val input = CraftingHelper.getIngredient(json.get(JSON_KEY_INPUT), context)
        val fluid = getFluidStack(json.get(JSON_KEY_INPUT_FLUID).asJsonObject)
        val outputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT).asJsonObject)

        return DissolutionRecipe(input, fluid, outputFluid, json.get(JSON_KEY_TICK_COST).asInt)
    }

}