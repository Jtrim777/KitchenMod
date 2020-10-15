package jake.kitchenmod.metallurgy.recipes;

import jake.kitchenmod.recipes.ModRecipes;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

public class MllgyRecipes {
  public static final IRecipeType<SmelteryRecipe> SMELT_RT = ModRecipes.registerRT("smeltery");

  public static final IRecipeSerializer<SmelteryRecipe> SMELT_FACTORY = new SmelteryRecipeFactory();
}
