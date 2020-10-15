package jake.kitchenmod.metallurgy.tiles;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.capabilities.KMFluidHandler;
import jake.kitchenmod.inventory.KMSlot.Type;
import jake.kitchenmod.inventory.KMSlot.Types;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.inventory.KMSlotMap.NamedSlot;
import jake.kitchenmod.items.KMFluidContainerItem;
import jake.kitchenmod.metallurgy.blocks.MllgyBlocks;
import jake.kitchenmod.metallurgy.blocks.SmelteryBlock;
import jake.kitchenmod.metallurgy.containers.MllgyContainers;
import jake.kitchenmod.metallurgy.containers.SmelteryContainer;
import jake.kitchenmod.metallurgy.data.KMMetal;
import jake.kitchenmod.metallurgy.data.KMMetal.Forms;
import jake.kitchenmod.metallurgy.data.MetalUtil;
import jake.kitchenmod.metallurgy.items.MetalMoldItem;
import jake.kitchenmod.metallurgy.recipes.MllgyRecipes;
import jake.kitchenmod.metallurgy.recipes.SmelteryRecipe;
import jake.kitchenmod.tiles.KMTileBase;
import jake.kitchenmod.util.ModLogger.LogLevel;
import jake.kitchenmod.util.ModUtil;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class SmelteryTile extends KMTileBase<SmelteryRecipe> {

  private static final int LAVA_TANK = 0;
  private static final int OUTPUT_TANK = 1;

  private final KMFluidHandler fluidInv;

  private float temperature = 0f;
  private float ticksToTempDecrease = 5;

  private float coldPower = 0f;
  private float workProgress = 0f;

  private SmelteryRecipe activeRecipe = null;

  private SmelteryContainer container = null;

  public SmelteryTile() {
    super(MllgyTiles.SMELTERY, 7, MllgyRecipes.SMELT_RT, KMSlotMap.of(
        new NamedSlot("IN1", Types.GENERIC_INGREDIENT),
        new NamedSlot("IN2", Types.GENERIC_INGREDIENT),
        new NamedSlot("LAVA", Types.LAVA),
        new NamedSlot("ICE", new Type("ice", SmelteryTile::isIce)),
        new NamedSlot("MOLD", Types.CONFORMS_TO(MetalMoldItem.class).or(Types.EMPTY_CONTAINER)),
        new NamedSlot("METAL_OUT", Types.OUTPUT),
        new NamedSlot("BYPRODUCT", Types.OUTPUT)
    ));

    this.fluidInv = KMFluidHandler.builder()
        .addOneFluidTank(3000, Fluids.LAVA)
        .addUniversalTank(3000).build();
  }

  // region NBT METHODS
  @Override
  public void read(CompoundNBT tag) {
    fluidInv.deserializeNBT(tag.getList("tanks", 10));

    temperature = tag.getFloat("temperature");
    coldPower = tag.getFloat("cold_power");

    workProgress = tag.getFloat("work_progress");

    super.read(tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.put("tanks", fluidInv.writeToNBT());

    tag.putFloat("temperature", temperature);
    tag.putFloat("cold_power", coldPower);

    tag.putFloat("work_progress", workProgress);

    return super.write(tag);
  }
  // endregion NBT METHODS

  // region BEHAVIOR METHODS
  @Override
  public void innerTick() {
    if (world == null || world.isRemote) {
      return;
    }

    boolean shouldSendUpdates = false;

    if (activeRecipe != null) {
      if (activeRecipe.doesMatch(inventory, this.world)) {
        continueSmelting();

        shouldSendUpdates = true;
        markDirty();
      } else {
        activeRecipe = null;
        cancelSmelting();
      }
    } else if (inputsExist()) {
//      KitchenMod.LOGGER.log("Checking for recipe...", "SmelteryTile", LogLevel.DEBUG);
      SmelteryRecipe validRecipe = getRecipeForInput(inventory);

      if (validRecipe != null && spaceInOutput(validRecipe)) {
        KitchenMod.LOGGER.log("Found Recipe: " + validRecipe.toString(),
            "SmelteryTile", LogLevel.DEBUG);

        activeRecipe = validRecipe;
        continueSmelting();

        shouldSendUpdates = true;
        markDirty();
      } else {
//        KitchenMod.LOGGER.log("No valid recipe for inputs, or output is full",
//            "SmelteryTile", LogLevel.DEBUG);
      }
    }

    shouldSendUpdates |= handleLava();
    shouldSendUpdates |= handleTemperature();

    shouldSendUpdates |= handleIce();
    shouldSendUpdates |= handleFreezing();

    shouldSendUpdates |= handleFluidOut();

    if (shouldSendUpdates) {
      sendUpdates();
    }
  }

  private void continueSmelting() {
    workProgress += getWorkIncrease();

    if (workProgress >= 1f) {
      workProgress = 0f;

      inventory.extractItem(0, 1, false);
      inventory.extractItem(1, 1, false);

      fluidInv.fillTank(OUTPUT_TANK, activeRecipe.getMoltenOutput(), FluidAction.EXECUTE);

      if (activeRecipe.hasByproduct()) {
        inventory.insertItemNoValidate(slots.slotIndexFor("BYPRODUCT"),
            activeRecipe.getByproduct(), false);
      }
    }
  }

  private void cancelSmelting() {
    workProgress = 0f;
  }

  private boolean handleLava() {
    ItemStack lavaContainer = slots.getStackInSlot("LAVA", inventory);

    if (lavaContainer.isEmpty()) return false;

    FluidStack fluidInContainer = FluidUtil.getFluidContained(lavaContainer)
        .orElse(FluidStack.EMPTY);

    if (fluidInContainer.getFluid() == Fluids.LAVA
        && fluidInContainer.getAmount() <= fluidInv.getTank(LAVA_TANK).getSpace()) {

      FluidActionResult actionResult;
      if (container != null) {
        actionResult = FluidUtil.tryEmptyContainerAndStow(lavaContainer,
            fluidInv.getTank(LAVA_TANK), container.getPlayerInventory(), 3000, null, true);

        if (actionResult.success) {
          slots.setStackInSlot("LAVA", actionResult.result, inventory);
        }
      } else {
        actionResult = FluidUtil.tryEmptyContainer(lavaContainer, fluidInv.getTank(LAVA_TANK),
            3000, null, true);

        if (actionResult.success) {
          Block.spawnAsEntity(world, pos, actionResult.result);

          lavaContainer.shrink(1);
        }
      }
      if (actionResult.success) {
        return true;
      }
    }

    return false;
  }

  private boolean handleTemperature() {
    int lavaLevel = fluidInv.getTank(LAVA_TANK).getFluidAmount();

    float maxTemp = Math.min((float)lavaLevel/100f, 1f);

    if (temperature < maxTemp) {
      temperature += 0.01f;

      try {
        fluidInv.getTank(LAVA_TANK).drain(1, FluidAction.EXECUTE);
      } catch (Exception e) {
        KitchenMod.LOGGER.log("Couldn't drain: " +this.toString(),
            "SmelteryTile", LogLevel.ERROR);
      }

      if (temperature > 1f) {
        temperature = 1f;
      }

      return true;
    } else if (temperature > maxTemp) {
      temperature -= 0.05f;

      if (temperature < 0f) {
        temperature = 0f;
      }

      return true;
    } else if (temperature == maxTemp && maxTemp > 0) {
      ticksToTempDecrease--;

      if (ticksToTempDecrease <= 0){
        temperature -= 0.02;
        ticksToTempDecrease = 5;
      }

      if (temperature < 0f) {
        temperature = 0f;
      }
    }

    BlockState stateNow = world.getBlockState(pos);
    boolean isLit = temperature > 0f;
    if (stateNow.get(SmelteryBlock.LIT) != isLit) {
      world.setBlockState(pos, stateNow.with(SmelteryBlock.LIT, isLit));
    }

    return false;
  }

  private boolean handleIce() {
    ItemStack iceSource = slots.getStackInSlot("ICE", inventory);

    if (iceSource != null && !iceSource.isEmpty()) {
      float coldInc = ICE_MAP.get(iceSource.getItem());

      if (coldPower + coldInc <= 2.1f) {
        iceSource.shrink(1);

        coldPower += coldInc;

        return true;
      }
    }

    return false;
  }

  private boolean handleFreezing() {
    ItemStack moldStack = slots.getStackInSlot("MOLD", inventory);
    FluidStack output = fluidInv.getFluidInTank(OUTPUT_TANK);

    if (moldStack.isEmpty() || output.isEmpty()
        || !(moldStack.getItem() instanceof MetalMoldItem)) {
      return false;
    }

    KMMetal metal = MetalUtil.getMetal(output.getFluid());
    KMMetal.FormType metalForm = ((MetalMoldItem) moldStack.getItem()).getMetalForm();

    if (!metal.hasForm(metalForm)) {
      return false;
    }

    int requiredVol = KMMetal.getVolumeForType(metalForm);

    float requiredCold = (float)requiredVol/
        (float)KMMetal.getVolumeForType(Forms.BLOCK);

    if (coldPower < requiredCold) {
      return false;
    }

    if (fluidInv.getTank(OUTPUT_TANK).getFluidAmount() < requiredVol) {
      return false;
    }

    Item out = metal.getForm(metalForm);

    ItemStack currentOutSlot = slots.getStackInSlot("METAL_OUT", inventory);
    if (currentOutSlot != ItemStack.EMPTY) {
      if (currentOutSlot.getItem() != out
          || currentOutSlot.getCount() >= currentOutSlot.getMaxStackSize()) {
        return false;
      }
    }

    fluidInv.getTank(OUTPUT_TANK).drain(requiredVol, FluidAction.EXECUTE);
    if (currentOutSlot.getItem() == out) {
      currentOutSlot.grow(1);
    } else {
      slots.setStackInSlot("METAL_OUT", new ItemStack(out), inventory);
    }

    int damage = moldStack.getDamage();

    moldStack.setDamage(damage + 1);

    coldPower -= requiredCold;
    if (coldPower < 0) {
      coldPower = 0;
    }

    if (moldStack.getDamage() > moldStack.getMaxDamage()) {
      slots.setStackInSlot("MOLD", ItemStack.EMPTY, inventory);
    }

    KitchenMod.LOGGER.log("Drained "+requiredVol+"mB of "
        +output.getFluid().getRegistryName().toString()+" to create one "+metal.getForm(metalForm)
        +"; Tank now contains "+ KMFluidHandler.formatTank(fluidInv.getTank(OUTPUT_TANK)));

    return true;
  }

  private boolean handleFluidOut() {
    ItemStack containerStack = slots.getStackInSlot("MOLD", inventory);
    FluidStack output = fluidInv.getFluidInTank(OUTPUT_TANK);

    if (KMFluidContainerItem.isFluidContainer(containerStack)) {
      if (container != null) {
        FluidActionResult actionResult = FluidUtil.tryFillContainerAndStow(containerStack,
            fluidInv.getTank(OUTPUT_TANK), container.getPlayerInventory(), Integer.MAX_VALUE,
            null, true);

        if (actionResult.success) {
          slots.setStackInSlot("MOLD", actionResult.result, inventory);

          return true;
        }
      } else {
        FluidActionResult actionResult = FluidUtil.tryFillContainer(containerStack,
            fluidInv.getTank(OUTPUT_TANK), Integer.MAX_VALUE, null, true);

        if (actionResult.success) {
          if (containerStack.getCount() == 1) {
            slots.setStackInSlot("MOLD", actionResult.result, inventory);
          } else {
            containerStack.shrink(1);

            Block.spawnAsEntity(world, pos, actionResult.result);
          }

          return true;
        }
      }
    }

    return false;
  }
  // endregion

  // region PREDICATES
  private boolean inputsExist() {
    return slots.getStackInSlot("IN1", inventory) != ItemStack.EMPTY
        || slots.getStackInSlot("IN2", inventory) != ItemStack.EMPTY;
  }

  private boolean spaceInOutput(SmelteryRecipe recipe) {
    Fluid recipeOut = recipe.getMoltenOutput().getFluid();
    Fluid currentOut = fluidInv.getFluidInTank(OUTPUT_TANK).getFluid();

    ItemStack recipeBy = recipe.hasByproduct() ? recipe.getByproduct() : null;
    ItemStack currentBy = slots.getStackInSlot("BYPRODUCT", inventory);

    boolean mainCheck = (recipeOut == currentOut || currentOut == Fluids.EMPTY)
        && recipe.getMoltenOutput().getAmount() <=
        fluidInv.getTank(OUTPUT_TANK).getSpace();

    boolean byCheck = !recipe.hasByproduct()
        || (recipeBy.getItem() == currentBy.getItem()
        && recipeBy.getCount() <= 64 - currentBy.getCount())
        || currentBy.isEmpty();

    return mainCheck && byCheck;
  }

  private static boolean isIce(ItemStack i) {
    return ICE_MAP.containsKey(i.getItem());
  }
  // endregion

  // region GETTERS
  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return LazyOptional.of(() -> fluidInv).cast();
    } else {
      return super.getCapability(cap, side);
    }
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("block.kitchenmod.smeltery");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    SmelteryContainer menu = new SmelteryContainer(i, MllgyContainers.SMELTERY_CT,
        MllgyBlocks.SMELTERY, world, pos, playerInventory);
    this.container = menu;
    return menu;
  }

  private float getWorkIncrease() {
    return 0.005f * temperature;
  }

  public float getWorkProgress() {
//    if (workProgress != 0) {
//      KitchenMod.LOGGER.log("Requested WP ["+workProgress+"]", "SmelteryTile",
//          LogLevel.DEBUG);
//    }

    return workProgress;
  }

  public FluidTank getLavaTank() {
    return fluidInv.getTank(0);
  }

  public FluidTank getOutputTank() {
    return fluidInv.getTank(1);
  }

  public float getTemp() {
    return temperature;
  }

  public float getColdPower() {
    return Math.min(coldPower, 1f);
  }

  @Override
  public String toString() {
    return "SmelteryTile{" +
        "fluidInv=" + fluidInv +
        ", temperature=" + temperature +
        ", coldPower=" + coldPower +
        ", workProgress=" + workProgress +
        ", activeRecipe=" + activeRecipe +
        ", inventory=" + inventory +
        '}';
  }

  // endregion

  private static Map<Item, Float> ICE_MAP = ModUtil.makeMap(
      new SimpleEntry<>(Items.BLUE_ICE, 2f),
      new SimpleEntry<>(Items.PACKED_ICE, 1.5f),
      new SimpleEntry<>(Items.ICE, 1f),
      new SimpleEntry<>(Items.SNOW, 0.8f),
      new SimpleEntry<>(Items.SNOWBALL, 0.2f)
  );
}
