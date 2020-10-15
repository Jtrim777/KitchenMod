package jake.kitchenmod.tiles;

import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.containers.MixingBowlContainer;
import jake.kitchenmod.containers.ModContainers;
import jake.kitchenmod.inventory.KMSlot.Types;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.recipes.MixingRecipe;
import jake.kitchenmod.recipes.ModRecipes;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MixingBowlTile extends KMTileBase<MixingRecipe> {

  public static KMSlotMap SLOTS;
  private static KMSlotMap initSlots() {
    SLOTS = new KMSlotMap()
        .addRange("INPUT", Types.GENERIC_INGREDIENT, 0, 11)
        .addRange("TOOLS", Types.COOKING_TOOL, 12, 14)
        .addRange("OUTPUT", Types.OUTPUT, 15, 18);

    return SLOTS;
  }

  public MixingBowlTile() {
    super(ModTileEntities.MIXING_BOWL, 19, ModRecipes.MIXING_RT, initSlots());
  }

  @Override
  public void innerTick() {
    boolean shouldSendUpdates = false;

    if (inventoryHasContent(inventory)) {
      MixingRecipe validRecipe = checkForRecipeAvailable(inventory);

      if (validRecipe == null || !spaceInOutput(inventory, validRecipe)) {
        return;
      }

      cook(inventory, validRecipe);
      shouldSendUpdates = true;
      markDirty();
    }

    if (KMTileConvenience.removeDestroyedItems(inventory, 19, "Mixing Bowl")) {
      shouldSendUpdates = true;
    }

    if (shouldSendUpdates) {
      sendUpdates();
    }
  }

  private boolean inventoryHasContent(KMItemHandler handler) {
    for (Integer i : slots.getRangeIndices("INPUT")) {
      if (handler.getStackInSlot(i) != ItemStack.EMPTY) {
        return true;
      }
    }
    return false;
  }

  private MixingRecipe checkForRecipeAvailable(KMItemHandler handler) {
    List<MixingRecipe> recipes = this.world.getRecipeManager().getRecipes(
        ModRecipes.MIXING_RT, inventory.wrap(), this.world);

    for (MixingRecipe r : recipes) {
      if (r.doesMatch(handler, this.world)) {
        return r;
      }
    }

    return null;
  }

  private boolean spaceInOutput(KMItemHandler handler, MixingRecipe recipe) {
    int[] outRange = slots.getRangeIndices("OUTPUT");
    int ur = outRange[outRange.length - 1];
    int lr = outRange[0];

    for (ItemStack ois : recipe.getOutputs()) {
      if (handler.insertItemInRange(lr, ur, ois, true, false).slot == -1) {
        return false;
      }
    }
    return true;
  }

  private void cook(KMItemHandler handler, MixingRecipe recipe) {
    for (ItemStack ing : recipe.getInputs()) {
      removeIngredient(ing.getItem(), ing.getCount(), handler);
    }

    for (Integer toolSlot : slots.getRangeIndices("TOOLS")) {
      if (!handler.getStackInSlot(toolSlot).isEmpty()) {
        ItemStack stack = handler.extractItem(toolSlot, 1, false);

        Item tool = stack.getItem();

        int damage = tool.getDamage(stack);

        tool.setDamage(stack, damage + 1);

        handler.insertItem(toolSlot, stack, false);
      }
    }

    int[] outRange = slots.getRangeIndices("OUTPUT");
    int ur = outRange[outRange.length - 1];
    int lr = outRange[0];

    for (ItemStack nstack : recipe.getOutputs()) {
      handler.insertItemInRange(lr, ur, nstack, false, false);
    }
  }

  private void removeIngredient(Item type, int count, KMItemHandler handler) {
    for (Integer i : slots.getRangeIndices("INPUT")) {
      ItemStack sis = handler.getStackInSlot(i);

      if (sis.getItem() == type) {
        if (sis.getCount() >= count) {
          handler.extractItem(i, count, false);
          return;
        } else {
          int oc = sis.getCount();
          handler.extractItem(i, oc, false);
          int nc = count - oc;
          this.removeIngredient(type, nc, handler);
          return;
        }
      }
    }
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("block.kitchenmod.mixing_bowl");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new MixingBowlContainer(i, ModContainers.MIXING_BOWL_CT, ModBlocks.MIXING_BOWL, world,
        pos, playerInventory);
  }

}
