package jake.kitchenmod.tiles;

import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.blocks.work_blocks.Oven;
import jake.kitchenmod.containers.ModContainers;
import jake.kitchenmod.containers.OvenContainer;
import jake.kitchenmod.inventory.KMSlot.Types;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.items.ModItems;
import jake.kitchenmod.recipes.ModRecipes;
import jake.kitchenmod.recipes.OvenRecipe;
import jake.kitchenmod.util.ModUtil;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class OvenTile extends KMTileBase<OvenRecipe> {
  public static KMSlotMap SLOTS;
  private static KMSlotMap initSlots() {
    SLOTS = new KMSlotMap()
        .addRange("INPUT", Types.GENERIC_INGREDIENT,0, 8)
        .addOne("FUEL1", Types.FUEL)
        .addOne("FUEL2", Types.FUEL);

    return SLOTS;
  }

  private ArrayList<CookingSlot> activeSlots;

  private int fuelLevel1 = 0;
  private int fuelLevel2 = 0;

  private double temperature = 0;
  private double partialTemp1 = 0;
  private double partialTemp2 = 0;

  public OvenTile() {
    super(ModTileEntities.OVEN_TILE, 19, ModRecipes.OVEN_RT, initSlots());

    activeSlots = new ArrayList<>();
  }

  @Override
  public void read(CompoundNBT tag) {
    ListNBT lst = tag.getList("active_slots", 10);
    for (int i = 0; i < lst.size(); i++) {
      activeSlots.add(new CookingSlot(lst.getCompound(i)));
    }

    fuelLevel1 = tag.getInt("fuel_1");
    fuelLevel2 = tag.getInt("fuel_2");

    temperature = tag.getDouble("temperature");

    partialTemp1 = tag.getDouble("temp_part1");
    partialTemp2 = tag.getDouble("temp_part2");

    super.read(tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    ListNBT lst = new ListNBT();
    for (CookingSlot cs : activeSlots) {
      lst.add(cs.write());
    }
    tag.put("active_slots", lst);

    tag.putInt("fuel_1", fuelLevel1);
    tag.putInt("fuel_2", fuelLevel2);

    tag.putDouble("temperature", temperature);

    tag.putDouble("temp_part1", partialTemp1);
    tag.putDouble("temp_part2", partialTemp2);

    return super.write(tag);
  }

  @Override
  public void innerTick() {
    boolean shouldSendUpdates = false;
    boolean oldWasBurning = temperature > 0;

    shouldSendUpdates = handleFuel();

    if ((temperature > 0) != oldWasBurning) {
      this.world.setBlockState(this.pos,
          this.world.getBlockState(this.pos)
              .with(Oven.IS_FUELED, temperature > 0));
    }

    if (cookingSlotsHaveContent()) {

      if (temperature > 0) {
        for (Integer i : slots.getRangeIndices("INPUT")) {
          shouldSendUpdates = handleSlot(i) || shouldSendUpdates;
        }
      }
    } else if (activeSlots.size() > 0) {
      activeSlots = new ArrayList<>();
      shouldSendUpdates = true;
    }

    if (shouldSendUpdates) {
      sendUpdates();
    }
  }

  private boolean cookingSlotsHaveContent() {
    for (Integer i : slots.getRangeIndices("INPUT")) {
      if (inventory.getStackInSlot(i) != ItemStack.EMPTY) {
        return true;
      }
    }
    return false;
  }

  private boolean handleFuel() {
    boolean rez = false;

    if (!slots.getStackInSlot("FUEL1", inventory).isEmpty() && fuelLevel1 == 0) {
      ItemStack fuelStack = slots.getStackInSlot("FUEL1", inventory);

      int time = Oven.AssociatedValues.getBurnTime(fuelStack.getItem());
      double temp = Oven.AssociatedValues.getTemperature(fuelStack.getItem());

      inventory.extractItem(slots.slotIndexFor("FUEL1"), 1, false);

      fuelLevel1 += time;
      partialTemp1 = temp * 0.5;
      rez = true;
    } else if (fuelLevel1 == 0) {
      partialTemp1 = 0;
      rez = true;
    } else if (fuelLevel1 > 0) {
      fuelLevel1 -= 1;
      rez = true;
    }

    if (!slots.getStackInSlot("FUEL2", inventory).isEmpty() && fuelLevel2 == 0) {
      ItemStack fuelStack = slots.getStackInSlot("FUEL2", inventory);

      int time = Oven.AssociatedValues.getBurnTime(fuelStack.getItem());
      double temp = Oven.AssociatedValues.getTemperature(fuelStack.getItem());

      inventory.extractItem(slots.slotIndexFor("FUEL2"), 1, false);

      fuelLevel2 += time;
      partialTemp2 = temp * 0.5;
      rez = true;
    } else if (fuelLevel2 == 0) {
      partialTemp2 = 0;
      rez = true;
    } else if (fuelLevel2 > 0) {
      fuelLevel2 -= 1;
      rez = true;
    }

    double avTemp = partialTemp1 + partialTemp2;

    if (temperature - avTemp > 0.05) {
      temperature -= 0.05;
    } else if (temperature - avTemp < -0.05) {
      temperature += 0.05;
    }

    if (temperature > 2) {
      temperature = 2;
    } else if (temperature < 0) {
      temperature = 0;
    }

    return rez;
  }

  private boolean handleSlot(int slot) {
    ItemStack stackInSlot = inventory.getStackInSlot(slot);

    if (stackInSlot.isEmpty()) {
      return activeSlots.removeIf(cookingSlot -> cookingSlot.index == slot);
    }

    CookingSlot slotSentry = ModUtil.getWhere(activeSlots,
        cookingSlot -> cookingSlot.index == slot);
    if (slotSentry != null) {
      if (!slotSentry.knownItem.equals(stackInSlot.getItem().getRegistryName().toString())) {
        slotSentry.ticks = 0;
        slotSentry.state = SlotState.RAW;
      } else if (slotSentry.state != SlotState.BURNT) {
        slotSentry.ticks += temperature;
        slotSentry.updateState();
      }

      if (slotSentry.state == SlotState.COOKED && !slotSentry.cookChecked) {
        this.cook(slot);
        slotSentry.ticks = 0;
        slotSentry.updateState();
        slotSentry.cookChecked = true;
      } else if (slotSentry.state == SlotState.BURNT && !slotSentry.burnChecked) {
        this.burn(slot);
        slotSentry.burnChecked = true;
      }
    } else {
      slotSentry = new CookingSlot(slot, stackInSlot.getItem().getRegistryName().toString());
      activeSlots.add(slotSentry);
    }

    return true;
  }

  private void cook(int slot) {
    ItemStack stackInSlot = inventory.getStackInSlot(slot);

    if (stackInSlot.isEmpty()) {
      return;
    }

    OvenRecipe recipe = recipeForInput(stackInSlot.getItem());

    if (recipe == null) {
      return;
    }

    ItemStack newStack = new ItemStack(recipe.getOutput().getItem(), stackInSlot.getCount());
    ModUtil.getWhere(activeSlots, cookingSlot -> cookingSlot.index == slot).knownItem =
        newStack.getItem().getRegistryName().toString();

    inventory.setStackInSlot(slot, newStack);

  }

  private void burn(int slot) {
    ItemStack stackInSlot = inventory.getStackInSlot(slot);

    if (stackInSlot.isEmpty()) {
      return;
    } else if (!Oven.AssociatedValues.isBurnable(stackInSlot.getItem())
        && recipeForInput(stackInSlot.getItem()) == null) {
//      KitchenMod.log("Could not burn item "+stackInSlot.getItem()+", as it is not organic", "Oven");
      return;
    }
//    KitchenMod.log("Disintegrating "+stackInSlot.getItem(), "Oven");
    ItemStack newStack = new ItemStack(ModItems.ash, stackInSlot.getCount());

    ModUtil.getWhere(activeSlots, cookingSlot -> cookingSlot.index == slot).knownItem =
        "kitchenmod:ash";

    inventory.setStackInSlot(slot, newStack);

  }

  private OvenRecipe recipeForInput(Item inp) {
    return world.getRecipeManager().getRecipes(recipeType, inventory.wrap(), world)
        .stream().filter(recipe -> recipe.getInput().getItem() == inp).findFirst().orElse(null);
  }

  public float getFuelPercent1() {
    return Math.min((float) fuelLevel1, 1600f) / 1600f;
  }

  public float getFuelPercent2() {
    return Math.min((float) fuelLevel2, 1600f) / 1600f;
  }

  public float getTempPercent() {
    return (float) this.temperature / 2f;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("block.kitchenmod.mixing_bowl");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new OvenContainer(i, ModContainers.OVEN_CT, ModBlocks.OVEN, world, pos,
        playerInventory);
  }

  private enum SlotState {
    EMPTY(0),
    RAW(0),
    COOKED(160),
    BURNT(330);

    int ticks;

    SlotState(int t) {
      this.ticks = t;
    }
  }

  private static class CookingSlot {

    double ticks;
    SlotState state;
    int index;
    String knownItem;
    boolean cookChecked;
    boolean burnChecked;

    CookingSlot(int index, double ticks, SlotState state, String knownItem, boolean cc,
        boolean bc) {
      this.index = index;
      this.ticks = ticks;
      this.state = state;
      this.knownItem = knownItem;
      this.cookChecked = cc;
      this.burnChecked = bc;
    }

    CookingSlot(int index, String knownItem) {
      this.index = index;
      this.ticks = 0;
      this.state = SlotState.RAW;
      this.knownItem = knownItem;
      this.cookChecked = false;
      this.burnChecked = false;
    }

    CookingSlot(CompoundNBT tag) {
      this.index = tag.getInt("index");
      this.state = SlotState.valueOf(tag.getString("state"));
      this.ticks = tag.getDouble("ticks");
      this.knownItem = tag.getString("known_item");
      if (tag.hasUniqueId("cooked")) {
        this.cookChecked = true;
      }
      if (tag.hasUniqueId("burnt")) {
        this.burnChecked = true;
      }
    }

    CompoundNBT write() {
      CompoundNBT out = new CompoundNBT();

      out.putDouble("ticks", this.ticks);
      out.putInt("index", this.index);
      out.putString("state", this.state.name());
      out.putString("known_item", this.knownItem);

      if (this.cookChecked) {
        out.putInt("cooked", 1);
      }
      if (this.burnChecked) {
        out.putInt("burnt", 1);
      }

      return out;
    }

    void updateState() {
      for (SlotState s : SlotState.values()) {
        if (ticks >= s.ticks) {
          this.state = s;
        }
      }
    }
  }
}
