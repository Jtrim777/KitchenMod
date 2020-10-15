package jake.kitchenmod.recipes;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.capabilities.KMItemHandler.IInventoryWrapper;
import jake.kitchenmod.tiles.MixingBowlTile;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MixingRecipe extends KMRecipe {

  private ItemStack[] inputs;
  private Item[] tools;
  private ItemStack[] outputs;

  public MixingRecipe(ResourceLocation name, ItemStack[] inputs, Item[] tools,
      ItemStack[] outputs) {
    super(name, ModRecipes.MIXING_RT);
    this.inputs = inputs;
    this.tools = tools;
    this.outputs = outputs;
  }

  public boolean doesMatch(KMItemHandler inv, World worldIn) {
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<Integer> counts = new ArrayList<>();

    for (Integer i : MixingBowlTile.SLOTS.getRangeIndices("INPUT")) {
      ItemStack inSlot = inv.getStackInSlot(i);

      if (inSlot.isEmpty()) {
        continue;
      }

      if (items.contains(inSlot.getItem())) {
        int j = items.indexOf(inSlot.getItem());
        counts.set(j, counts.get(j) + inSlot.getCount());
      } else {
        items.add(inSlot.getItem());
        counts.add(inSlot.getCount());
      }
    }

    if (items.size() != this.inputs.length) {
      return false;
    }

    for (ItemStack is : this.inputs) {
      int ind = items.indexOf(is.getItem());

      if (ind == -1) {
        return false;
      }

      int c = counts.get(ind);

      if (c < is.getCount()) {
        return false;
      }
    }

    for (Item ti : this.tools) {
      boolean someWorked = false;
      for (Integer i :MixingBowlTile.SLOTS.getRangeIndices("TOOLS")) {
        someWorked = (inv.getStackInSlot(i).getItem() == ti) || someWorked;
      }

      if (!someWorked) {
        return false;
      }
    }

    return true;
  }

  public boolean matches(IInventory inv, World worldIn) {
    return this.doesMatch(((IInventoryWrapper) inv).unwrap(), worldIn);
  }

  @Override
  public String formatBody() {
    String ingredients = Arrays.toString(inputs);

    String toolsS = Arrays.toString(tools);

    String outs = Arrays.toString(outputs);

    return String.format("%s + %s -> %s", ingredients, toolsS, outs);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.outputs[0].copy();
  }

  public ItemStack[] getAllOutputs() {
    ItemStack[] outs = new ItemStack[this.outputs.length];

    for (int i = 0; i < outs.length; i++) {
      outs[i] = this.outputs[i].copy();
    }

    return outs;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return new MixingRecipeFactory();
  }

  public ItemStack[] getInputs() {
    return inputs;
  }

  public Item[] getTools() {
    return tools;
  }

  public ItemStack[] getOutputs() {
    return outputs;
  }
}
