package jake.kitchenmod.items;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.fluid.ModFluids;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModItems {

  public static final Item salt_block = null;
  public static final Item mixing_bowl = null;
  public static final Item mill = null;
  public static final Item press = null;
  public static final Item chocolate_cake = null;
  public static final Item chocolate_cake_slice = null;
  public static final Item salt = null;
  public static final Item flour = null;
  public static final Item dough = null;
  public static final CompostItem compost = null;
  public static final Item cocoa_mass = null;
  public static final Item bread_starter = null;
  public static final StirRod stir_rod = null;
  public static final Item ash = null;
  public static final Item axle = null;
  public static final Item stake = null;
  public static final Item strawberry = null;
  public static final Item strawberry_seeds = null;
  public static final Item grape = null;
  public static final Item grape_seeds = null;
  public static final Item tomato = null;
  public static final Item tomato_seeds = null;
  public static final Item vanilla_bean = null;
  public static final Item chocolate = null;
  public static final Item bottle = null;
  public static final Item can = null;

  public static Item entry(String name, Item value) {
    return value.setRegistryName(KitchenMod.MODID, name);
  }

  public static Item item(String name, ItemGroup group) {
    return entry(name, (new Item(new Properties().group(group))));
  }

  public static Item material(String name) {
    return item(name, ItemGroup.MATERIALS);
  }

  public static Item blockItem(Block block, ItemGroup group) {
    return entry(block.getRegistryName().getPath(),
        new BlockItem(block, new Properties().group(group)));
  }

  public static Item food(String name, int hunger, float sat, boolean fast) {
    Food.Builder foodProps = new Food.Builder().hunger(hunger).saturation(sat);
    if (fast) {
      foodProps.fastToEat();
    }

    Properties itemProps = new Properties().group(ItemGroup.FOOD).food(foodProps.build());

    return entry(name, (new Item(itemProps)));
  }

  public static Item seed(String name, Block plant) {
    return entry(name, new BlockNamedItem(plant, new Item.Properties().group(ItemGroup.MISC)));
  }

  public static Item override(String name, Item item) {
    return item.setRegistryName("minecraft", name);
  }

  public static Item overrideBlockItem(Block block, ItemGroup group) {
    return (new BlockItem(block, new Item.Properties().group(group)))
        .setRegistryName("minecraft", block.getRegistryName().getPath());
  }

  public static Consumer<AttachCapabilitiesEvent<ItemStack>>
  capability(Predicate<ItemStack> check, Capability<?> cap) {
    return (event) -> {
      if (check.test(event.getObject())) {
        capabilityAttachHelper(event, cap);
      }
    };
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {

    IForgeRegistry<Item> registry = event.getRegistry();

    registry.registerAll(
        blockItem(ModBlocks.SALT_BLOCK, ItemGroup.BUILDING_BLOCKS),
        blockItem(ModBlocks.MILL, ItemGroup.DECORATIONS),
        blockItem(ModBlocks.OVEN, ItemGroup.DECORATIONS),
        blockItem(ModBlocks.PRESS, ItemGroup.DECORATIONS),
        blockItem(ModBlocks.MIXING_BOWL, ItemGroup.DECORATIONS),
        blockItem(ModBlocks.CHOCOLATE_CAKE, ItemGroup.FOOD),
        blockItem(ModBlocks.STAKE, ItemGroup.DECORATIONS),

        item("salt", ItemGroup.MATERIALS),
        item("ash", ItemGroup.MATERIALS),
        item("wax", ItemGroup.MATERIALS),
        item("axle", ItemGroup.MATERIALS),
        item("grape_seeds", ItemGroup.MATERIALS),
        item("tomato_seeds", ItemGroup.MATERIALS),
        item("cocoa_mass", ItemGroup.FOOD),
        item("chocolate_cake_batter", ItemGroup.FOOD),
        item("flour", ItemGroup.FOOD),
        item("dough", ItemGroup.FOOD),
        item("bread_starter", ItemGroup.FOOD),
        item("vanilla_bean", ItemGroup.FOOD),

        food("chocolate_cake_slice", 2, 0.45f, true),
        food("strawberry", 2, 0.4f, true),
        food("grape", 2, 0.4f, true),
        food("tomato", 2, 0.4f, true),

        seed("strawberry_seeds", ModBlocks.STRAWBERRY_BUSH),

        entry("stir_rod", new StirRod()),
        entry("compost", new CompostItem()),
        entry("bottle", new KMFluidContainerItem("bottle", ItemGroup.MISC, 333)),
        entry("can", new KMFluidContainerItem("can", ItemGroup.MISC, 500)),

        overrideBlockItem(ModBlocks.COMPOSTER, ItemGroup.DECORATIONS)
    );

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getItems)
        .forEach(registry::register);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_ITEMS);

//    ModFluids.registerContainers(registry);
  }

  @SubscribeEvent
  public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getItemCaps)
        .forEach(c -> c.accept(event));

    capability(s -> s.getItem() instanceof KMFluidContainerItem,
        CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).accept(event);
  }

  private static void capabilityAttachHelper(AttachCapabilitiesEvent<ItemStack> event, Capability<?> cap) {
    ResourceLocation name = new ResourceLocation(KitchenMod.MODID, cap.getName());
    ICapabilityProvider provider = new ICapabilityProvider() {
      @Nonnull
      @Override
      public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
          @Nullable Direction side) {
        return LazyOptional.of(() -> cap).cast();
      }
    };

    event.addCapability(name, provider);
  }

  public static void initializeItemFields() {
    KitchenMod.LOGGER.log("Initializing static item fields");

//    KMHoeItem.initLookupMap();
  }
}

