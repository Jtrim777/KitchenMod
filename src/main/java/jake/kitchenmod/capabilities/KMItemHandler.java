package jake.kitchenmod.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;


public class KMItemHandler implements IItemHandler, IItemHandlerModifiable,
    INBTSerializable<CompoundNBT> {

  protected NonNullList<ItemStack> stacks;

  public KMItemHandler() {
    this(1);
  }

  public KMItemHandler(int size) {
    stacks = NonNullList.withSize(size, ItemStack.EMPTY);
  }

  public KMItemHandler(NonNullList<ItemStack> stacks) {
    this.stacks = stacks;
  }

  public void setSize(int size) {
    stacks = NonNullList.withSize(size, ItemStack.EMPTY);
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    validateSlotIndex(slot);
    this.stacks.set(slot, stack);
    onContentsChanged(slot);
  }

  @Override
  public int getSlots() {
    return stacks.size();
  }

//  @Override
  public int getSizeInventory() {
    return getSlots();
  }

//  @Override
  public boolean isEmpty() {
    return stacks.stream().map(ItemStack::isEmpty).reduce(Boolean::logicalAnd).get();
  }

//  @Override
  public ItemStack decrStackSize(int index, int count) {
    ItemStack itemstack = ItemStackHelper.getAndSplit(this.stacks, index, count);
    if (!itemstack.isEmpty()) {
      this.markDirty();
    }

    return itemstack;
  }

//  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack out = getStackInSlot(index);
    stacks.set(index, ItemStack.EMPTY);

    return out;
  }

  public int getInventoryStackLimit() {
    return 64;
  }

//  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    this.stacks.set(index, stack);
    if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
      stack.setCount(this.getInventoryStackLimit());
    }

    this.markDirty();
  }

//  @Override
  public void markDirty() { }

//  @Override
  public boolean isUsableByPlayer(PlayerEntity player) {
    return true;
  }

//  @Override
  public void clear() {
    this.stacks.clear();
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    validateSlotIndex(slot);
    return this.stacks.get(slot);
  }

  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    if (!isItemValid(slot, stack)) {
      return stack;
    }

    validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    int limit = getStackLimit(slot, stack);

    if (!existing.isEmpty()) {
      if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
        return stack;
      }

      limit -= existing.getCount();
    }

    if (limit <= 0) {
      return stack;
    }

    boolean reachedLimit = stack.getCount() > limit;

    if (!simulate) {
      if (existing.isEmpty()) {
        this.stacks
            .set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
      } else {
        existing.grow(reachedLimit ? limit : stack.getCount());
      }
      onContentsChanged(slot);
    }

    return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit)
        : ItemStack.EMPTY;
  }

  public static class InsertionResult {

    public int slot;
    public ItemStack stack;

    public InsertionResult(int sl, ItemStack st) {
      slot = sl;
      stack = st;
    }
  }

  public InsertionResult insertItemInRange(int slotMin, int slotMax, @Nonnull ItemStack stack,
      boolean simulate, boolean validate) {
    int successSlot = -1;

    if (validate) {
      for (int i = slotMin; i < slotMax; i++) {
        if (!insertItem(i, stack, true).equals(stack)) {
          successSlot = i;
          break;
        }
      }
      return new InsertionResult(successSlot, insertItem(successSlot, stack, simulate));
    } else {
      for (int i = slotMin; i < slotMax; i++) {
        if (!insertItemNoValidate(i, stack, true).equals(stack)) {
          successSlot = i;
          break;
        }
      }

      if (successSlot == -1) {
        return new InsertionResult(-1, ItemStack.EMPTY);
      }

      return new InsertionResult(successSlot, insertItemNoValidate(successSlot, stack, simulate));
    }


  }

  @Nonnull
  public ItemStack insertItemNoValidate(int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    int limit = getStackLimit(slot, stack);

    if (!existing.isEmpty()) {
      if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
        return stack;
      }

      limit -= existing.getCount();
    }

    if (limit <= 0) {
      return stack;
    }

    boolean reachedLimit = stack.getCount() > limit;

    if (existing.isEmpty()) {
      this.stacks
          .set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
    } else {
      existing.grow(reachedLimit ? limit : stack.getCount());
    }
    onContentsChanged(slot);

    return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit)
        : ItemStack.EMPTY;
  }

  public ItemStack insertItemNoValidate(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    int limit = getStackLimit(slot, stack);

    if (!existing.isEmpty()) {
      if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
        return stack;
      }

      limit -= existing.getCount();
    }

    if (limit <= 0) {
      return stack;
    }

    boolean reachedLimit = stack.getCount() > limit;

    if (!simulate) {
      if (existing.isEmpty()) {
        this.stacks
            .set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
      } else {
        existing.grow(reachedLimit ? limit : stack.getCount());
      }
      onContentsChanged(slot);
    }

    return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit)
        : ItemStack.EMPTY;
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0) {
      return ItemStack.EMPTY;
    }

    validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    if (existing.isEmpty()) {
      return ItemStack.EMPTY;
    }

    int toExtract = Math.min(amount, existing.getMaxStackSize());

    if (existing.getCount() <= toExtract) {
      if (!simulate) {
        this.stacks.set(slot, ItemStack.EMPTY);
        onContentsChanged(slot);
      }
      return existing;
    } else {
      if (!simulate) {
        this.stacks.set(slot,
            ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
        onContentsChanged(slot);
      }

      return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }
  }

  @Override
  public int getSlotLimit(int slot) {
    return 64;
  }

  public boolean stackInSlotCanGrow(int slot) {
    ItemStack stack = this.getStackInSlot(slot);
    return stack.getCount() < stack.getMaxStackSize();
  }

  protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
    return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
  }

  @Override
  public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
    return true;
  }

  @Override
  public CompoundNBT serializeNBT() {
    ListNBT nbtTagList = new ListNBT();
    for (int i = 0; i < stacks.size(); i++) {
      if (!stacks.get(i).isEmpty()) {
        CompoundNBT itemTag = new CompoundNBT();
        itemTag.putInt("Slot", i);
        stacks.get(i).write(itemTag);
        nbtTagList.add(itemTag);
      }
    }
    CompoundNBT nbt = new CompoundNBT();
    nbt.put("Items", nbtTagList);
    nbt.putInt("Size", stacks.size());
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
    ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tagList.size(); i++) {
      CompoundNBT itemTags = tagList.getCompound(i);
      int slot = itemTags.getInt("Slot");

      if (slot >= 0 && slot < stacks.size()) {
        stacks.set(slot, ItemStack.read(itemTags));
      }
    }
    onLoad();
  }

  public boolean slotIsEmpty(int ind) {
    return this.getStackInSlot(ind).equals(ItemStack.EMPTY);
  }

  public boolean slotMatchesItem(int ind, Item item) {
    return this.getStackInSlot(ind).getItem().equals(item);
  }

  protected void validateSlotIndex(int slot) {
    if (slot < 0 || slot >= stacks.size()) {
      throw new RuntimeException(
          "Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
  }

  protected void onLoad() {

  }

  protected void onContentsChanged(int slot) {

  }

  public interface IInventoryWrapper extends IInventory {
    KMItemHandler unwrap();
  }

  public IInventoryWrapper wrap() {
    return new IInventoryWrapper() {
      private final KMItemHandler wrapped = KMItemHandler.this;

      @Override
      public KMItemHandler unwrap() {
        return wrapped;
      }

      @Override
      public int getSizeInventory() {
        return wrapped.getSizeInventory();
      }

      @Override
      public boolean isEmpty() {
        return wrapped.isEmpty();
      }

      @Override
      public ItemStack getStackInSlot(int index) {
        return wrapped.getStackInSlot(index);
      }

      @Override
      public ItemStack decrStackSize(int index, int count) {
        return wrapped.decrStackSize(index, count);
      }

      @Override
      public ItemStack removeStackFromSlot(int index) {
        return wrapped.removeStackFromSlot(index);
      }

      @Override
      public void setInventorySlotContents(int index, ItemStack stack) {
        wrapped.setStackInSlot(index, stack);
      }

      @Override
      public void markDirty() {
        wrapped.markDirty();
      }

      @Override
      public boolean isUsableByPlayer(PlayerEntity player) {
        return wrapped.isUsableByPlayer(player);
      }

      @Override
      public void clear() {
        wrapped.clear();
      }
    };
  }
}

