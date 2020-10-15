package jake.kitchenmod.recipes;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.tiles.OvenTile;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class OvenRecipe extends KMRecipe {

  private ItemStack input;
  private ItemStack output;

  public OvenRecipe(ResourceLocation id, ItemStack input, ItemStack output) {
    super(id, ModRecipes.OVEN_RT);
    this.input = input;
    this.output = output;
  }

  public boolean doesMatch(KMItemHandler inv, World worldIn) {
    ItemStack check = OvenTile.SLOTS.getStackInSlot("INPUT", inv);
    return check.getItem() == this.input.getItem();
  }

  public boolean matches(IInventory inv, World worldIn) {
    return true;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.output.copy();
  }

  @Override
  public IRecipeSerializer<OvenRecipe> getSerializer() {
    return new OvenRecipeFactory();
  }

  public ItemStack getInput() {
    return input;
  }

  public ItemStack getOutput() {
    return output;
  }

  @Override
  public String formatBody() {
    return String.format("[%s] -> [%s]", input.toString(), output.toString());
  }
}
