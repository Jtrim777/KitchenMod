package jake.kitchenmod.recipes;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.tiles.PressTile;
import jake.kitchenmod.util.ModUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class PressRecipe extends KMRecipe {

  private ItemStack input;
  private FluidStack output;
  private ItemStack byproduct;
  private float byproductChance;

  public PressRecipe(ResourceLocation id, ItemStack input, FluidStack output,
      ItemStack byproduct, float byproductChance) {
    super(id, ModRecipes.PRESS_RT);

    this.input = input;
    this.output = output;
    this.byproduct = byproduct;
    this.byproductChance = byproductChance;
  }

  public boolean doesMatch(KMItemHandler inv, World worldIn) {
    ItemStack check = PressTile.SLOTS.getStackInSlot("INPUT", inv);
    return check.getItem() == this.input.getItem();
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  public FluidStack getRecipeOutputFluid() {
    return output.copy();
  }

  @Override
  public IRecipeSerializer<PressRecipe> getSerializer() {
    return new PressRecipeFactory();
  }

  public ItemStack getInput() {
    return input;
  }

  public FluidStack getOutput() {
    return output;
  }

  public ItemStack getByproduct() {
    return byproduct;
  }

  public float getByproductChance() {
    return byproductChance;
  }

  public ItemStack requestByproduct() {
    if (ModUtil.chance(this.byproductChance)) {
      return this.byproduct.copy();
    } else {
      return ItemStack.EMPTY;
    }
  }

  @Override
  public String formatBody() {
    String byString = "";
    if (byproduct != null) {
      byString = String.format(" + [%s](%f%%)", byproduct.toString(),
          (double) byproductChance);
    }

    return String.format("[%s] -> %s[%d mB]%s", input.toString(),
        output.getFluid().getRegistryName().toString(), output.getAmount(), byString);
  }
}
