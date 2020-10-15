package jake.kitchenmod.items;

import jake.kitchenmod.util.ModUtil;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;

public class KMFluidContainerItem extends ItemFluidContainer {

  public KMFluidContainerItem(String name, ItemGroup group, int vol) {
    super(new Item.Properties()
        .group(group)
    , vol);

//    this.setRegistryName(KitchenMod.MODID, name);
  }

  public ItemStack getEmpty(int quantity) {
    ItemStack result = getDefaultInstance();
    result.setCount(quantity);
    return result;
  }

  @Override
  public ITextComponent getDisplayName(ItemStack stack) {
    ITextComponent base = super.getDisplayName(stack);

    if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) return base;

    FluidStack containedFluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);

    if (containedFluid != FluidStack.EMPTY) {
      base.appendText(" of " + containedFluid.getDisplayName());
    }

    return base;
  }

  public static boolean isFluidContainer(ItemStack inp) {
    return inp.getItem() instanceof KMFluidContainerItem
        || VANILLA_MAP.containsKey(inp.getItem());
  }

  public static FluidStack getFluid(ItemStack inp) {
    return FluidUtil.getFluidContained(inp).orElse(FluidStack.EMPTY);
  }

  private static Map<Item, Fluid> VANILLA_MAP = ModUtil.makeMap(
      new SimpleEntry<>(Items.WATER_BUCKET, Fluids.WATER),
      new SimpleEntry<>(Items.LAVA_BUCKET, Fluids.LAVA),
      new SimpleEntry<>(Items.POTION, Fluids.WATER),
      new SimpleEntry<>(Items.BUCKET, Fluids.EMPTY),
      new SimpleEntry<>(Items.GLASS_BOTTLE, Fluids.EMPTY)//,
//      new SimpleEntry<>(Items.MILK_BUCKET, ModFluids.MILK)
  );
//
//    // Registers all of the models for the item. Add any new fluid overlay types here
//    @OnlyIn(Dist.CLIENT)
//    public void initModels() {
//        ModelResourceLocation standardModel = new ModelResourceLocation(getRegistryName()+"_standard", "inventory");
//        ModelResourceLocation lavaModel = new ModelResourceLocation(getRegistryName()+"_lava", "inventory");
//
//        ModelLoader ml = new ModelLoader()
//    }
}
