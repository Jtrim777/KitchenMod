package jake.kitchenmod.tiles;

import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.capabilities.KMItemHandler;
import jake.kitchenmod.containers.MillContainer;
import jake.kitchenmod.containers.ModContainers;
import jake.kitchenmod.inventory.KMSlot.Types;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.inventory.KMSlotMap.NamedSlot;
import jake.kitchenmod.recipes.MillRecipe;
import jake.kitchenmod.recipes.ModRecipes;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class MillTile extends KMWPWorkTile<MillRecipe> {

  public static KMSlotMap SLOTS;
  private static KMSlotMap initSlots() {
    SLOTS = KMSlotMap.of(
        new NamedSlot("WATER", Types.WATER),
        new NamedSlot("INPUT", Types.GENERIC_INGREDIENT),
        new NamedSlot("OUTPUT", Types.OUTPUT)
    );

    return SLOTS;
  }

  private int grindTicksRemaining = -1;

  public MillTile() {
    super(ModTileEntities.MILL_TILE, 3, ModRecipes.MILL_RT, initSlots());

    WORK_TIME = 160;
    WATER_PER_TICK = 250 / WORK_TIME;
  }


  @Override
  public void read(CompoundNBT tag) {
    grindTicksRemaining = tag.getInt("grind_ticks_remaining");
    super.read(tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.putInt("grind_ticks_remaining", grindTicksRemaining);
    return super.write(tag);
  }

  protected void progressAndCheck(KMItemHandler handler) {
    if (grindTicksRemaining == -1) {
      grindTicksRemaining = WORK_TIME;
    } else if (grindTicksRemaining == 0) {
      handler.extractItem(slots.slotIndexFor("INPUT"), 1, false);
      handler.insertItemNoValidate(2, activeRecipe.getOutput());

      resetCounters();
    } else {
      grindTicksRemaining--;
      fluidHandler.getTank(0).drain(WATER_PER_TICK, FluidAction.EXECUTE);
    }
  }

  public boolean isOutputSlotValid(KMItemHandler handler) {
    return handler.slotIsEmpty(slots.slotIndexFor("OUTPUT"))
        || (handler.slotMatchesItem(slots.slotIndexFor("OUTPUT"),
        activeRecipe.getOutput().getItem())
        && handler.stackInSlotCanGrow(slots.slotIndexFor("OUTPUT")));
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("block.kitchenmod.mill");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new MillContainer(i, ModContainers.MILL_CT, ModBlocks.MILL, world, pos, playerInventory);
  }

  protected void resetCounters() {
    if (activeRecipe != null) {
      grindTicksRemaining = WORK_TIME;
    } else {
      grindTicksRemaining = -1;
    }
  }

  public float getPercentComplete() {
    if (grindTicksRemaining == -1) {
      return 0;
    }

    return (float) (WORK_TIME - grindTicksRemaining) / (float) WORK_TIME;
  }

  @Override
  public String formatBlockstate() {
    if (activeRecipe == null) {
      return String.format("[Mill | Working: false; Water Level: %d mB]",
          fluidHandler.getTank(0).getFluidAmount());
    } else {
      return String.format("[Mill | Working: true; Ticks Remaining: %d; Recipe: %s; Water Level: " +
              "%d mB", this.grindTicksRemaining, this.activeRecipe.getId().toString(),
          fluidHandler.getTank(0).getFluidAmount());
    }
  }
}
