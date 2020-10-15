package jake.kitchenmod.containers;

import jake.kitchenmod.screens.components.ScreenPos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class MixingBowlContainer extends KMContainer {

  public MixingBowlContainer(int windowID, ContainerType<?> type, Block block, World world,
      BlockPos pos, PlayerInventory playerInventory) {
    super(windowID, type, block, world, pos, playerInventory);
  }

  @Override
  protected void layoutSlots(IItemHandler h) {
    // (8,8) -> (62,62);
    ScreenPos[] slotLocs = new ScreenPos[19];

    int i = 0;
    for (int y = 8; y <= 62; y += 18) {
      for (int x = 8; x <= 62; x += 18) {
        if ((x == 62 && y == 62) || (x == 8 && y == 8) || (x == 62 && y == 8) || (x == 8
            && y == 62)) {
          continue;
        }

        slotLocs[i] = new ScreenPos(x, y);
        i++;
      }
    }

    // 99| 17,35,53
    slotLocs[i] = new ScreenPos(99, 17);
    i++;
    slotLocs[i] = new ScreenPos(99, 35);
    i++;
    slotLocs[i] = new ScreenPos(99, 53);
    i++;

    // 144 | 8, 26, 44, 62
    slotLocs[i] = new ScreenPos(134, 8);
    i++;
    slotLocs[i] = new ScreenPos(134, 26);
    i++;
    slotLocs[i] = new ScreenPos(134, 44);
    i++;
    slotLocs[i] = new ScreenPos(134, 62);

    layoutFromSlotMap(h, tileEntity.getSlotMap(), slotLocs);
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);

    if (slot != null && slot.getHasStack()) {
      ItemStack stack = slot.getStack();
      itemstack = stack.copy();

      if (index < 19) {
        if (!this.mergeItemStack(stack, 19, 39, false)) {
          return ItemStack.EMPTY;
        }
        slot.onSlotChange(stack, itemstack);
      } else if (stackIsValidForSlotRange("TOOLS", stack)) {
        if (!this.mergeItemStack(stack, 12, 15, false)) {
          return ItemStack.EMPTY;
        }
      } else {
        if (!this.mergeItemStack(stack, 15, 19, false)) {
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
