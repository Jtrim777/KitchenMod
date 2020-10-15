package jake.kitchenmod.recipes;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.capabilities.KMItemHandler.IInventoryWrapper;
import jake.kitchenmod.tiles.MillTile;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MillRecipe extends KMRecipe {

  private ItemStack input;
  private ItemStack output;

  public MillRecipe(ResourceLocation id, ItemStack input, ItemStack output) {
    super(id, ModRecipes.MILL_RT);
    this.input = input;
    this.output = output;
  }

  public boolean doesMatch(KMItemHandler inv, World worldIn) {
    ItemStack check = MillTile.SLOTS.getStackInSlot("INPUT", inv);
    return check.getItem() == this.input.getItem();
  }

  public boolean matches(IInventory inv, World worldIn) {
    return this.doesMatch(((IInventoryWrapper) inv).unwrap(), worldIn);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.output.copy();
  }

  @Override
  public IRecipeSerializer<MillRecipe> getSerializer() {
    return new MillRecipeFactory();
  }

  public ItemStack getInput() {
    return input;
  }

  public ItemStack getOutput() {
    return output;
  }

  public int getOutputQuantity() {
    return output.getCount();
  }

  @Override
  public String formatBody() {
    return String.format("%s -> %s", input.toString(),
        output.toString());
  }
}
