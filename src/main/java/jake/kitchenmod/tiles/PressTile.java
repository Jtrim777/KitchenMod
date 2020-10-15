package jake.kitchenmod.tiles;

import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.containers.ModContainers;
import jake.kitchenmod.containers.PressContainer;
import jake.kitchenmod.inventory.KMSlot.Types;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.inventory.KMSlotMap.NamedSlot;
import jake.kitchenmod.recipes.ModRecipes;
import jake.kitchenmod.recipes.PressRecipe;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;

public class PressTile extends KMWPWorkTile<PressRecipe> {
  public static KMSlotMap SLOTS;
  private static KMSlotMap initSlots() {
    SLOTS = KMSlotMap.of(
        new NamedSlot("WATER", Types.WATER),
        new NamedSlot("INPUT", Types.GENERIC_INGREDIENT),
        new NamedSlot("OUT_CONTAINER", Types.EMPTY_CONTAINER),
        new NamedSlot("BYPRODUCT", Types.OUTPUT)
    );

    return SLOTS;
  }

  private int workTicksRemaining = -1;

  private PressContainer container = null;

  public PressTile() {
    super(ModTileEntities.PRESS_TILE, 4, ModRecipes.PRESS_RT, initSlots());

    WORK_TIME = 160;
    WATER_PER_TICK = 250 / WORK_TIME;

    fluidHandler.addUniversalTank(3000);
  }

  @Override
  public void read(CompoundNBT tag) {
    workTicksRemaining = tag.getInt("work_ticks_remaining");
    super.read(tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.putInt("work_ticks_remaining", workTicksRemaining);
    return super.write(tag);
  }

  @Override
  public void tick() {
    super.tick();

    checkAndHandleOutput();
  }

  @Override
  public boolean isOutputSlotValid(KMItemHandler handler) {
    boolean rez = true;

    if (this.activeRecipe.getByproduct() != ItemStack.EMPTY) {
      rez = inventory.slotIsEmpty(slots.slotIndexFor("BYPRODUCT"))
          || (inventory.slotMatchesItem(slots.slotIndexFor("BYPRODUCT"),
          activeRecipe.getByproduct().getItem())
          && inventory.stackInSlotCanGrow(slots.slotIndexFor("BYPRODUCT")));
    }

    FluidTank outputTank = fluidHandler.getTank(1);

    rez &= (outputTank.isEmpty()
        || outputTank.getFluid().getFluid() == activeRecipe.getOutput().getFluid())
        && activeRecipe.getOutput().getAmount() <= outputTank.getSpace();

    return rez;
  }

  @Override
  protected void progressAndCheck(KMItemHandler handler) {
    if (workTicksRemaining == -1) {
      workTicksRemaining = WORK_TIME;
    } else if (workTicksRemaining == 0) {
      handler.extractItem(slots.slotIndexFor("INPUT"), 1, false);

      FluidTank outputTank = fluidHandler.getTank(1);

      outputTank.fill(activeRecipe.getOutput(), FluidAction.EXECUTE);

      ItemStack by = activeRecipe.requestByproduct();
      if (!by.isEmpty()) {
        handler.insertItemNoValidate(slots.slotIndexFor("BYPRODUCT"), by, false);
      }

      resetCounters();
    } else {
      workTicksRemaining--;
      fluidHandler.getTank(0).drain(WATER_PER_TICK, FluidAction.EXECUTE);
    }
  }

  protected void resetCounters() {
    if (activeRecipe != null) {
      workTicksRemaining = WORK_TIME;
    } else {
      workTicksRemaining = -1;
    }
  }

  private boolean checkAndHandleOutput() {
    ItemStack containerItem = slots.getStackInSlot("OUT_CONTAINER", inventory);

    if (container == null) {
      return false;
    }

    if (!containerItem.isEmpty()) {

      FluidActionResult actionResult = FluidUtil.tryFillContainer(containerItem,
          fluidHandler.getTank(1), 3000, null, true);

      if (actionResult.success) {
        IItemHandler playerInventory = container.getPlayerInventory();

        for (int slot = 0; slot < 36; slot++) {
          if (playerInventory.insertItem(slot, actionResult.result, false)
              .equals(ItemStack.EMPTY)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("block.kitchenmod.press");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    PressContainer menu = new PressContainer(i, ModContainers.PRESS_CT, ModBlocks.PRESS, world, pos,
        playerInventory);
    this.container = menu;
    return menu;
  }

  public float getPercentComplete() {
    float result = 0;
    if (workTicksRemaining > 0) {
      result = (float) (WORK_TIME - workTicksRemaining) / (float) WORK_TIME;
    }

    return result;
  }

  public FluidTank getOutputTank() {
    return fluidHandler.getTank(1);
  }

  @Override
  public String formatBlockstate() {
    int wl = fluidHandler.getTank(0).getFluidAmount();
    int ol = fluidHandler.getTank(1).getFluidAmount();

    String on = fluidHandler.getTank(1).getFluid().getTranslationKey();

    if (activeRecipe == null) {
      return String.format("[Press | Working: false; Water Level: %d mB; Output Level: %d " +
              "mB]; Output Contents: %s", wl, ol, on);
    } else {
      return String.format("[Press | Working: true; Ticks Remaining: %d; Recipe: %s; Water " +
              "Level: %d mB; Output Level: %d mB; Output Contents: %s",
          this.workTicksRemaining, this.activeRecipe.getId().toString(),
          wl, ol, on);
    }
  }
}
