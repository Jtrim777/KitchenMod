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

public class MillContainer extends KMContainer {

  public MillContainer(int windowID, ContainerType<?> type, Block block, World world, BlockPos pos,
      PlayerInventory playerInventory) {
    super(windowID, type, block, world, pos, playerInventory);
  }

  @Override
  protected void layoutSlots(IItemHandler h) {
    layoutFromSlotMap(h, tileEntity.getSlotMap(),
        new ScreenPos(8, 10),
        new ScreenPos(98, 10),
        new ScreenPos(98, 56));
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack stack = slot.getStack();
      itemstack = stack.copy();
      if (index == 0 || index == 1 || index == 2) {
        if (!this.mergeItemStack(stack, 3, 39, true)) {
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
        } else if (index < 30) {
          if (!this.mergeItemStack(stack, 30, 30, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < 39 && !this.mergeItemStack(stack, 3, 30, false)) {
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
