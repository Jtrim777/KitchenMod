package jake.kitchenmod.tiles;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.horticulture.tiles.PlantTileEntity;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModTileEntities {

  @ObjectHolder("mill")
  public static final TileEntityType<MillTile> MILL_TILE = null;

  @ObjectHolder("mixing_bowl")
  public static final TileEntityType<MixingBowlTile> MIXING_BOWL = null;

  @ObjectHolder("press")
  public static final TileEntityType<PressTile> PRESS_TILE = null;

  @ObjectHolder("oven")
  public static final TileEntityType<OvenTile> OVEN_TILE = null;

  @ObjectHolder("village_manager")
  public static final TileEntityType<VillageManagerTile> VILLAGE_MANAGER_TILE = null;

  @ObjectHolder("plant")
  public static final TileEntityType<PlantTileEntity> PLANT_TILE = null;


  public static <T extends TileEntity> TileEntityType tileEntity(String name,
      Supplier<T> tileMaker,
      Block parentBlock) {
    return TileEntityType.Builder
        .create(tileMaker, parentBlock)
        .build(null)
        .setRegistryName(KitchenMod.MODID, name);
  }

  @SubscribeEvent
  public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
    event.getRegistry().registerAll(
        tileEntity("mill", MillTile::new, ModBlocks.MILL),

        tileEntity("mixing_bowl", MixingBowlTile::new, ModBlocks.MIXING_BOWL),

        tileEntity("press", PressTile::new, ModBlocks.PRESS),

        tileEntity("oven", OvenTile::new, ModBlocks.OVEN)//,
    );

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getTileEntities)
        .forEach(event.getRegistry()::register);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_TILES);
  }
}
