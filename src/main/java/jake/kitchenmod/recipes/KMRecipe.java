package jake.kitchenmod.recipes;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.capabilities.KMItemHandler.IInventoryWrapper;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class KMRecipe implements IRecipe<IInventory> {

  protected ResourceLocation id;
  protected IRecipeType<?> recipeType;

  public KMRecipe(ResourceLocation name, IRecipeType<?> rtyp) {
    this.id = name;
    this.recipeType = rtyp;
  }

  @Nullable
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public IRecipeType<?> getType() {
    return this.recipeType;
  }

  @Override
  public ItemStack getCraftingResult(IInventory inv) {
    return this.getRecipeOutput().copy();
  }

  @Override
  public boolean canFit(int width, int height) {
    return width == 1 && height == 1;
  }

  @Override
  public boolean matches(IInventory inv, World worldIn) {
    return this.doesMatch(((IInventoryWrapper) inv).unwrap(), worldIn);
  }

  public abstract boolean doesMatch(KMItemHandler inv, World world);

  @Override
  public String toString() {
    return String.format("{%s | %s}", id.toString(), this.formatBody());
  }

  public abstract String formatBody();
}

