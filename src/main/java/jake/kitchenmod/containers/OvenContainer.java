package jake.kitchenmod.containers;

import static jake.kitchenmod.inventory.KMSlot.Types.FUEL;
import static jake.kitchenmod.inventory.KMSlot.Types.GENERIC_INGREDIENT;

import jake.kitchenmod.inventory.KMSlot;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.screens.components.ScreenPos;
import jake.kitchenmod.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class OvenContainer extends KMContainer {

  public OvenContainer(int windowID, ContainerType<?> type, Block block, World world, BlockPos pos,
      PlayerInventory playerInventory) {
    super(windowID, type, block, world, pos, playerInventory);
  }

  @Override
  protected void layoutSlots(IItemHandler h) {
    ScreenPos[] slotLocs = new ScreenPos[11];

    // 44, 11 -> 116, 29
    int i = 0;
    for (int x = 44; x <= 116; x += 18) {
      for (int y = 11; y <= 29; y += 18) {
        slotLocs[i] = new ScreenPos(x, y);
        i++;
      }
    }

    slotLocs[9] = new ScreenPos(62, 64);
    slotLocs[10] = new ScreenPos(98, 64);

    layoutFromSlotMap(h, tileEntity.getSlotMap(), slotLocs);
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack stack = slot.getStack();
      itemstack = stack.copy();
      if (index < 12) {
        if (!this.mergeItemStack(stack, 12, 39, true)) {
          return ItemStack.EMPTY;
        }
        slot.onSlotChange(stack, itemstack);
      } else if (FUEL.isItemValid(stack.getItem())) {
        if (!this.mergeItemStack(stack, 10, 12, true)) {
          return ItemStack.EMPTY;
        }
      } else {
        if (!this.mergeItemStack(stack, 0, 10, true)) {
          return ItemStack.EMPTY;
        }
      }

      if (stack.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      if (stack.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(playerIn, stack);
    }

    return itemstack;
  }
}
