//package jake.kitchenmod.fluid;
//
//import jake.kitchenmod.KitchenMod;
//import jake.kitchenmod.items.KMDrink;
//import jake.kitchenmod.items.fluid_container.KMBottle;
//import jake.kitchenmod.items.fluid_container.KMBucket;
//import jake.kitchenmod.util.ModUtil;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.function.BiFunction;
//import net.minecraft.client.renderer.color.ItemColors;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemGroup;
//import net.minecraft.item.Items;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.registries.IForgeRegistry;
//import net.minecraftforge.registries.ObjectHolder;
//
//@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//@ObjectHolder(KitchenMod.MODID)
//public class ModFluids {
//
//  @ObjectHolder("water")
//  public static final KMFluid WATER = null;
//
//  @ObjectHolder("lava")
//  public static final KMFluid LAVA = null;
//
//  @ObjectHolder("milk")
//  public static final KMFluid MILK = null;
//
//  @ObjectHolder("empty")
//  public static final KMFluid EMPTY = null;
//
//  @ObjectHolder("apple_juice")
//  public static final KMFluid APPLE_JUICE = null;
//
//  @ObjectHolder("pumpkin_juice")
//  public static final KMFluid PUMPKIN_JUICE = null;
//
//  @ObjectHolder("cocoa_butter")
//  public static final KMFluid COCOA_BUTTER = null;
//
//  private static final HashMap<String, ItemGroup> containerGroups = new HashMap<>();
//
//  private static final List<KMFluid> ENTRIES = Arrays.asList(
//      fluid("empty", 0xFFFFFFFF, null),
//      fluid("water", 0xff385dc6, null),
//      fluid("lava", 0xFF000000, null),
//      fluid("cocoa_butter", color(100, 55, 15), ItemGroup.FOOD),
//      drink("apple_juice", color(180, 200, 55), 3, 1f),
//      drink("pumpkin_juice", color(230, 140, 30), 3, 1f),
//      drink("milk", color(255, 255, 255), 1, 1f)
//  );
//
//  private static final List<BiFunction<KMFluid, ItemGroup, Item>> CONTAINER_MAKERS = Arrays.asList(
//      /* Bucket */ (fluid, group) -> new KMBucket(group, fluid),
//      /* Bottle */ (fluid, group) -> fluid instanceof KMConsumableFluid ?
//          new KMDrink((KMConsumableFluid) fluid) : new KMBottle(group, fluid)
//  );
//
//  private static KMFluid fluid(String name, int color, ItemGroup cGroup) {
//    containerGroups.put(name, cGroup);
//    return new KMFluid(name, color);
//  }
//
//  private static KMFluid drink(String name, int color, int hunger, float sat) {
//    containerGroups.put(name, ItemGroup.FOOD);
//    return new KMConsumableFluid(name, color, hunger, sat);
//  }
//
//  private static int color(int r, int g, int b) {
//    return ModUtil.colorFromRGB(r, g, b);
//  }
//
//  @SubscribeEvent
//  public static void registerKMFluids(RegistryEvent.Register<KMFluid> event) {
//    IForgeRegistry<KMFluid> registry = event.getRegistry();
//
//    ENTRIES.forEach(registry::register);
//  }
//
//  public static Item fillBottle(KMFluid fluid) {
//    if (fluid == WATER) {
//      return Items.POTION;
//    } else if (fluid == EMPTY) {
//      return Items.GLASS_BOTTLE;
//    } else {
//      Item attempt = ForgeRegistries.ITEMS.getValue(
//          new ResourceLocation(fluid.getRegistryName().toString() + "_bottle"));
//
//      if (attempt != null) {
//        return attempt;
//      }
//    }
//
//    throw new IllegalArgumentException("No bottle exists for the fluid " +
//        fluid.getRegistryName().toString());
//  }
//
//  public static Item fillBucket(KMFluid fluid) {
//    if (fluid == WATER) {
//      return Items.WATER_BUCKET;
//    } else if (fluid == EMPTY) {
//      return Items.BUCKET;
//    } else {
//      Item attempt = ForgeRegistries.ITEMS.getValue(
//          new ResourceLocation(fluid.getRegistryName().toString() + "_bucket"));
//
//      if (attempt != null) {
//        return attempt;
//      }
//    }
//
//    throw new IllegalArgumentException("No bucket exists for the fluid " +
//        fluid.getRegistryName().toString());
//  }
//
//  public static void registerContainers(IForgeRegistry<Item> itemRegistry) {
//    ENTRIES.forEach((fluid) -> {
//      ItemGroup group = containerGroups.get(fluid.getRegistryName().getPath());
//      if (group == null) {
//        return;
//      }
//
//      CONTAINER_MAKERS.stream().map(cm -> cm.apply(fluid, group)).forEach(itemRegistry::register);
//    });
//
//  }
//
//  public static void registerColorsForContainers(ItemColors itemColors) {
//    KitchenMod.LOGGER.log("Registering tint colors for fluid containers", "ModFluids");
//    KitchenMod.LOGGER.startTask("containerColors");
//
//    for (KMFluid fluid : ENTRIES) {
//      if (fluid == WATER || fluid == EMPTY) {
//        continue;
//      }
//
//      itemColors.register((stack, index) -> index == 1 ? fluid.getItemColor() : -1,
//          fillBucket(fluid)
//      );
//
//      itemColors.register((stack, index) -> index == 0 ? fluid.getItemColor() : -1,
//          fillBottle(fluid)
//      );
//    }
//
//    KitchenMod.LOGGER.endTask("containerColors");
//  }
//
//  public static Item[] getContainersForFluid(KMFluid fluid) {
//    return new Item[]{fillBucket(fluid), fillBottle(fluid)};
//  }
//
//}
