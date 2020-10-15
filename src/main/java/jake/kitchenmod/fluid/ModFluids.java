package jake.kitchenmod.fluid;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.items.ModItems;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import jake.kitchenmod.util.ModUtil;
import java.util.function.Consumer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = KitchenMod.MODID, bus = Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModFluids {

  @ObjectHolder("apple_juice")
  public static final Fluid APPLE_JUICE = null;

  @ObjectHolder("pumpkin_juice")
  public static final Fluid PUMPKIN_JUICE = null;

  @ObjectHolder("cocoa_butter")
  public static final Fluid COCOA_BUTTER = null;

  private static final KMFluidBundle AJ_BUNDLE = new KMFluidBundle("apple_juice");
  private static final KMFluidBundle PJ_BUNDLE = new KMFluidBundle("pumpkin_juice");
  private static final KMFluidBundle CB_BUNDLE = new KMFluidBundle("cocoa_butter");

  @SubscribeEvent
  public static void registerFluids(RegistryEvent.Register<Fluid> event) {
    IForgeRegistry<Fluid> registry = event.getRegistry();

    AJ_BUNDLE.initializeDrink("block/water", setColor(100, 55, 15), 3, 1f)
        .register(registry);
    PJ_BUNDLE.initializeDrink("block/water", setColor(180, 200, 55), 3, 1f)
        .register(registry);
    CB_BUNDLE.initialize("block/water", setColor(240, 130, 40)).register(registry);

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getFluids)
        .stream().map(KMFluidBundle::getParts).forEach(registry::registerAll);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_FLUIDS);
  }

  private static int color(int r, int g, int b) {
    return ModUtil.colorFromRGB(r, g, b);
  }

  private static Consumer<FluidAttributes.Builder> setColor(int r, int g, int b) {
    return (attr) -> attr.color(color(r, g, b));
  }

  public static void registerColorsForContainers(ItemColors itemColors) {
    itemColors.register(ModFluids::containerColorer, ModItems.bottle);
    itemColors.register(ModFluids::containerColorer, ModItems.can);
  }

  private static int containerColorer(ItemStack stack, int layer) {
    if (layer != 0) return -1;

    LazyOptional<FluidStack> fluid = FluidUtil.getFluidContained(stack);

    if (fluid.isPresent()) {
      return fluid.orElse(FluidStack.EMPTY).getFluid().getAttributes().getColor();
    } else {
      return -1;
    }
  }

  public static Fluid getFluid(String name) {
    return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(KitchenMod.MODID, name));
  }
}
