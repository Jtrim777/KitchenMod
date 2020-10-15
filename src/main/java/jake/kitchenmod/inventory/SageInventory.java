package jake.kitchenmod.inventory;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.containers.SageContainer;
import jake.kitchenmod.entities.SageVillagerEntity;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class SageInventory extends KMItemHandler {
  private SageVillagerEntity sage;

  public SageInventory(SageVillagerEntity sage) {
    super(5);

    this.sage = sage;
  }

  @Override
  public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
    return SageContainer.SLOTS.validForSlotIndex(slot, stack);
  }

  public boolean containsStackInRange(ItemStack stack, int lb, int ub) {
    for (int i=lb; i<ub; i++) {
      if (getStackInSlot(i).equals(stack)) {
        return true;
      }
    }

    return false;
  }
}
