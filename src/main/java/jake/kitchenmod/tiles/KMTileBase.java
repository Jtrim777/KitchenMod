package jake.kitchenmod.tiles;

import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.recipes.KMRecipe;
import jake.kitchenmod.util.ModUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class KMTileBase<R extends KMRecipe> extends TileEntity implements
    ITickableTileEntity, INamedContainerProvider {

  protected final int invSize;
  protected final IRecipeType<R> recipeType;
  protected final KMSlotMap slots;

  protected KMItemHandler inventory;
  protected boolean isFrozen = false;

  public KMTileBase(TileEntityType type, int inventorySz, IRecipeType<R> recipeType,
      KMSlotMap slots) {
    super(type);

    this.invSize = inventorySz;
    this.recipeType = recipeType;
    this.slots = slots;

    this.inventory = createHandler();
  }

  @Override
  public void tick() {
    if (isFrozen || this.world == null || !ModUtil.isServerWorld(world)) {
      return;
    }

    innerTick();
  }

  protected abstract void innerTick();

  private KMItemHandler createHandler() {
    return new KMItemHandler(this.invSize) {
      @Override
      protected void onContentsChanged(int slot) {
        markDirty();
      }

      @Override
      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return slots.validForSlotIndex(slot, stack);
      }

      @Nonnull
      public ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack) {
        return super.insertItemNoValidate(slot, stack);
      }
    };
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return LazyOptional.of(() -> inventory).cast();
    } else {
      return super.getCapability(cap, side);
    }
  }

  public KMSlotMap getSlotMap() {
    return slots;
  }

  protected R getRecipeForInput(KMItemHandler handler) {
    return this.world.getRecipeManager().getRecipes(this.recipeType, handler.wrap(), this.world).stream()
        .findFirst().orElse(null);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    super.onDataPacket(net, pkt);
    handleUpdateTag(pkt.getNbtCompound());
  }

  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
  }

  protected void sendUpdates() {
    world.notifyBlockUpdate(pos, getState(), getState(), 2);

    markDirty();
  }

  private BlockState getState() {
    return world.getBlockState(pos);
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("primary_inventory");
    inventory.deserializeNBT(invTag);

    isFrozen = tag.getBoolean("is_frozen");

    super.read(tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT compound = inventory.serializeNBT();
    tag.put("primary_inventory", compound);

    tag.putBoolean("is_frozen", isFrozen);

    return super.write(tag);
  }

  public void freeze() {
    this.isFrozen = true;
  }

  public void unFreeze() {
    this.isFrozen = false;
  }

}
