package jake.kitchenmod.blocks;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.overrides.KMComposterBlock;
import jake.kitchenmod.blocks.plants.BasicVine;
import jake.kitchenmod.blocks.plants.StakeBlock;
import jake.kitchenmod.blocks.plants.StrawberryBush;
import jake.kitchenmod.blocks.plants.VanillaVine;
import jake.kitchenmod.blocks.village.VillageManagerBlock;
import jake.kitchenmod.blocks.work_blocks.Mill;
import jake.kitchenmod.blocks.work_blocks.MixingBowl;
import jake.kitchenmod.blocks.work_blocks.Oven;
import jake.kitchenmod.blocks.work_blocks.Press;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModBlocks {

  @ObjectHolder("salt_block")
  public static final Block SALT_BLOCK = null;

  @ObjectHolder("mill")
  public static final Mill MILL = null;

  @ObjectHolder("mixing_bowl")
  public static final MixingBowl MIXING_BOWL = null;

  @ObjectHolder("press")
  public static final Press PRESS = null;

  @ObjectHolder("oven")
  public static final Oven OVEN = null;

  @ObjectHolder("strawberry_bush")
  public static final StrawberryBush STRAWBERRY_BUSH = null;

  @ObjectHolder("chocolate_cake")
  public static final ChocolateCake CHOCOLATE_CAKE = null;

  @ObjectHolder("grape_vine")
  public static final BasicVine GRAPE_VINE = null;

  @ObjectHolder("tomato_vine")
  public static final BasicVine TOMATO_VINE = null;

  @ObjectHolder("vanilla_vine")
  public static final VanillaVine VANILLA_VINE = null;

  @ObjectHolder("stake")
  public static final StakeBlock STAKE = null;

  @ObjectHolder("minecraft:composter")
  public static final ComposterBlock COMPOSTER = null;

  @ObjectHolder("village_manager")
  public static final VillageManagerBlock VILLAGE_MANAGER = null;

  public static Block entry(String name, Block block) {
    return block.setRegistryName(KitchenMod.MODID, name);
  }

  public static Block override(String name, Block replacement) {
    return replacement.setRegistryName("minecraft", name);
  }

  public static Block metalBlock(String metalName) {
    return entry(metalName +"_block", new Block(
        Block.Properties.create(Material.IRON, MaterialColor.IRON)
            .hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    registry.registerAll(
        entry("salt_block", new Block(Block.Properties.from(Blocks.STONE))),
        entry("mill", new Mill()),
        entry("oven", new Oven()),
        entry("press", new Press()),
        entry("mixing_bowl", new MixingBowl()),
        entry("strawberry_bush", new StrawberryBush()),
        entry("chocolate_cake", new ChocolateCake()),
        entry("grape_vine", new BasicVine("kitchenmod:grape_seeds", "kitchenmod:grape")),
        entry("tomato_vine", new BasicVine("kitchenmod:tomato_seeds", "kitchenmod:tomato")),
        entry("vanilla_vine", new VanillaVine()),
        entry("stake", new StakeBlock()),
//        entry("village_manager", new VillageManagerBlock()),

        override("composter", new KMComposterBlock())
    );

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getBlocks)
        .forEach(registry::register);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_BLOCKS);
  }



  /**
   * Ensures that any blocks which have static fields that must be initialized during common setup
   * do so.
   */
  public static void initializeBlockFields() {
    KitchenMod.LOGGER.log("Initializing static block fields");
    KMComposterBlock.rinit();

    StakeBlock.registerPlant(GRAPE_VINE);
    StakeBlock.registerPlant(TOMATO_VINE);
    StakeBlock.registerPlant(VANILLA_VINE);
  }
}

