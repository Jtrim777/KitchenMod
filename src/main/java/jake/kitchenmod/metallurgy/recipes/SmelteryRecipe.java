package jake.kitchenmod.metallurgy.recipes;

import com.google.gson.JsonObject;
import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.recipes.KMRecipe;
import javax.annotation.Nullable;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class SmelteryRecipe extends KMRecipe {

  Item input1;
  Item input2;

  FluidStack moltenOutput;
  ItemStack byproduct;

  public SmelteryRecipe(ResourceLocation name, Item input1, Item input2,
      FluidStack moltenOutput, ItemStack byproduct) {
    super(name, MllgyRecipes.SMELT_RT);
    this.input1 = input1;
    this.input2 = input2;
    this.moltenOutput = moltenOutput;
    this.byproduct = byproduct;
  }

  public SmelteryRecipe(ResourceLocation name, Item input,
      FluidStack moltenOutput, ItemStack byproduct) {
    super(name, MllgyRecipes.SMELT_RT);
    this.input1 = input;
    this.input2 = null;
    this.moltenOutput = moltenOutput;
    this.byproduct = byproduct;
  }

  public SmelteryRecipe(ResourceLocation name, Item input1, Item input2,
      FluidStack moltenOutput) {
    super(name, MllgyRecipes.SMELT_RT);
    this.input1 = input1;
    this.input2 = input2;
    this.moltenOutput = moltenOutput;
    this.byproduct = null;
  }

  public SmelteryRecipe(ResourceLocation name, Item input,
      FluidStack moltenOutput) {
    super(name, MllgyRecipes.SMELT_RT);
    this.input1 = input;
    this.input2 = null;
    this.moltenOutput = moltenOutput;
    this.byproduct = null;
  }

  public boolean doesMatch(KMItemHandler inv, World worldIn) {
    ItemStack checkI1 = inv.getStackInSlot(0);
    ItemStack checkI2 = inv.getStackInSlot(1);

    int matched = 0;
    if (checkI1.getItem() == input1) {
      matched = 1;
    } else if (checkI2.getItem() == input1) {
      matched = 2;
    } else {
      return false;
    }

    if (input2 == null) {
      return true;
    } else if (matched == 1) {
      return checkI2.getItem() == input2;
    } else {
      return checkI1.getItem() == input2;
    }
  }

  @Override
  public String formatBody() {
    String i1 = input1.toString();
    String i2 = input2 == null ? "--" : input2.toString();

    String bpStr = byproduct == null ? "--" : byproduct.toString();

    return String.format("[%s + %s] -> [%s & %s]",
        i1, i2, moltenOutput.toString(), bpStr);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return MllgyRecipes.SMELT_FACTORY;
  }

  public FluidStack getMoltenOutput() {
    return moltenOutput.copy();
  }

  public ItemStack getByproduct() {
    return byproduct.copy();
  }

  public boolean hasByproduct() {
    return byproduct != null;
  }

  public FinishedForm finish() {
    return new FinishedForm(this);
  }

  public static class FinishedForm implements IFinishedRecipe {
    private SmelteryRecipe recipe;

    private FinishedForm(SmelteryRecipe recipe) {
      this.recipe = recipe;
    }

    @Override
    public void serialize(JsonObject json) {
      SmelteryRecipeFactory.write(recipe, json);
    }

    @Override
    public ResourceLocation getID() {
      return recipe.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return recipe.getSerializer();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return null;
    }
  }
}
