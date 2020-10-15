package jake.kitchenmod.tiles;

import jake.kitchenmod.capabilities.KMFluidHandler;
import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.recipes.KMRecipe;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public abstract class KMWPWorkTile<R extends KMRecipe> extends KMTileBase<R> {

  protected int WORK_TIME;
  protected int WATER_PER_TICK;

  KMFluidHandler fluidHandler;

  R activeRecipe = null;

  public KMWPWorkTile(TileEntityType type, int inventorySz, IRecipeType<R> recipeType,
      KMSlotMap slots) {
    super(type, inventorySz, recipeType, slots);

    this.fluidHandler = KMFluidHandler.builder()
        .addOneFluidTank(3000, Fluids.WATER).build();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return LazyOptional.of(() -> fluidHandler).cast();
    } else {
      return super.getCapability(cap, side);
    }
  }

  public FluidTank getWaterTank() {
    return fluidHandler.getTank(0);
  }

  @Override
  public void innerTick() {
    boolean shouldSendUpdates;

    // Check for water source in water input slot
    shouldSendUpdates = handleWaterIO(slots.slotIndexFor("WATER"), inventory);

    shouldSendUpdates |= handleWork(slots.slotIndexFor("INPUT"), inventory);

    shouldSendUpdates |= KMTileConvenience.removeDestroyedItems(inventory, 3, "Mill");

    if (shouldSendUpdates) {
      sendUpdates();
    }
  }

  protected boolean handleWaterIO(int waterSlot, KMItemHandler handler) {
    ItemStack stackInWaterSource = handler.getStackInSlot(waterSlot);
    if (!stackInWaterSource.equals(ItemStack.EMPTY)) {
      FluidStack fluid = FluidUtil.getFluidContained(stackInWaterSource).orElse(FluidStack.EMPTY);

      if (fluid != FluidStack.EMPTY && fluid.getFluid() == Fluids.WATER) {
        int amt = fluid.getAmount();

        if (fluidHandler.getTank(0).getSpace() >= amt) {
          FluidActionResult actionResult = FluidUtil.tryEmptyContainer(stackInWaterSource,
              fluidHandler.getTank(0), amt, null, true);

          handler.setStackInSlot(waterSlot, actionResult.result);
          markDirty();
          return true;
        }
      }
    }

    return false;
  }

  protected boolean handleWork(int inputSlot, KMItemHandler handler) {
    boolean rez = false;

    ItemStack stackInInput = handler.getStackInSlot(inputSlot);

    if (!stackInInput.equals(ItemStack.EMPTY)) {
      if (activeRecipe != null
          && activeRecipe.doesMatch(handler, this.world)
          && fluidHandler.getTank(0).getFluidAmount() >= WATER_PER_TICK) {
        if (isOutputSlotValid(handler)) {
          progressAndCheck(handler);

          rez = true;
        }

      } else {
        activeRecipe = this.getRecipeForInput(handler);
        resetCounters();
        rez = true;
      }
    } else {
      activeRecipe = null;
      resetCounters();
      rez = true;
    }

    return rez;
  }

  @Override
  public void read(CompoundNBT tag) {
    fluidHandler.deserializeNBT(tag.getList("tanks", 10));
    super.read(tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.put("tanks", this.fluidHandler.writeToNBT());
    return super.write(tag);
  }

  protected abstract boolean isOutputSlotValid(KMItemHandler handler);

  protected abstract void resetCounters();

  protected abstract void progressAndCheck(KMItemHandler handler);

  public abstract float getPercentComplete();

  public abstract String formatBlockstate();
}
