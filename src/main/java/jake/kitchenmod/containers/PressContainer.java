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

public class PressContainer extends KMContainer {

  public PressContainer(int windowID, ContainerType<?> type, Block block, World world, BlockPos pos,
      PlayerInventory playerInventory) {
    super(windowID, type, block, world, pos, playerInventory);
  }

  @Override
  protected void layoutSlots(IItemHandler h) {
    layoutFromSlotMap(h, tileEntity.getSlotMap(),
        new ScreenPos(46, 33),
        new ScreenPos(98, 14),
        new ScreenPos(152, 62),
        new ScreenPos(152, 41)
    );
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack stack = slot.getStack();
      itemstack = stack.copy();
      if (index >= 0 && index <= 3) {
        if (!this.mergeItemStack(stack, 4, 40, true)) {
          return ItemStack.EMPTY;
        }
        slot.onSlotChange(stack, itemstack);
      } else {
        if (stackIsValidForSlot(0, stack)) {
          if (!this.mergeItemStack(stack, 0, 1, false)) {
            return ItemStack.EMPTY;
          }
        } else if (stackIsValidForSlot(1, stack)) {
          if (!this.mergeItemStack(stack, 1, 2, false)) {
            return ItemStack.EMPTY;
          }
        } else if (stackIsValidForSlot(2, stack)) {
          if (!this.mergeItemStack(stack, 2, 3, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < 40 && index > 31 && !this.mergeItemStack(stack, 4, 31, false)) {
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
