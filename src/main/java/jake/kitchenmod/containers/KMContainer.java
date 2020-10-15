package jake.kitchenmod.containers;

import static jake.kitchenmod.inventory.KMSlot.Types.PLAYER_HOTBAR;
import static jake.kitchenmod.inventory.KMSlot.Types.PLAYER_INVENTORY;

import jake.kitchenmod.inventory.KMSlot;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.screens.components.ScreenPos;
import jake.kitchenmod.tiles.KMTileBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class KMContainer extends Container {

  protected KMTileBase<?> tileEntity;
  protected IItemHandler playerInventory;
  private final Block blockType;

  protected KMContainer(int windowID, ContainerType<?> type, Block block, World world, BlockPos pos,
      PlayerInventory playerInventory) {
    super(type, windowID);

    tileEntity = (KMTileBase<?>) world.getTileEntity(pos);
    this.playerInventory = new InvWrapper(playerInventory);
    this.blockType = block;

    assert tileEntity != null;
    tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        .ifPresent(this::layoutSlots);
    this.layoutPlayerSlots();
  }

  protected abstract void layoutSlots(IItemHandler h);

  protected void layoutFromSlotMap(IItemHandler h, KMSlotMap slotMap, ScreenPos... slotPositions) {
    if (slotMap.slotCount() != slotPositions.length) {
      throw new IllegalArgumentException("Not all slot positions were specified");
    }

    for (int i = 0; i < slotPositions.length; i++) {
      KMSlot slot = new KMSlot(h, i, slotPositions[i].x, slotPositions[i].y, slotMap.getType(i));
      this.addSlot(slot);
    }
  }

  private void layoutPlayerSlots() {
    // (8,84) -> (152,142); Sep 18
    int i = 0;

    for (int x = 8; x <= 152; x += 18) {
      KMSlot niSlot = new KMSlot(playerInventory, i, x, 142, PLAYER_HOTBAR);
      addSlot(niSlot);
      i++;
    }

    for (int y = 84; y <= 120; y += 18) {
      for (int x = 8; x <= 152; x += 18) {
        KMSlot niSlot = new KMSlot(playerInventory, i, x, y, PLAYER_INVENTORY);
        addSlot(niSlot);
        i++;
      }
    }


  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
        playerIn, blockType);
  }

  public TileEntity getTileEntity() {
    return tileEntity;
  }

  public IItemHandler getPlayerInventory() {
    return playerInventory;
  }

  @Override
  public void onCraftMatrixChanged(IInventory p_75130_1_) {
    super.onCraftMatrixChanged(p_75130_1_);
  }

  protected boolean stackIsValidForSlot(int slot, ItemStack stack) {
    return tileEntity.getSlotMap().validForSlotIndex(slot, stack);
  }

  protected boolean stackIsValidForSlotRange(String range, ItemStack stack) {
    return tileEntity.getSlotMap().isValidForRange(range, stack);
  }
}
