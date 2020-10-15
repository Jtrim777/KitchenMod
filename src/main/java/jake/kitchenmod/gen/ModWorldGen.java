package jake.kitchenmod.gen;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModWorldGen {

  static {
    ModGen.registerFx(ModWorldGen::registerOres);
  }

  private static List<ConfiguredFeature<?>> ORES = new ArrayList<>();

  private static void registerOres() {
    KitchenMod.LOGGER.log("Registering ore veins", "ModWorldGen");

    // Register Salt Block
    // Spawn 60/chunk in Ocean biome; Vein size 8; Between level 5 and 64
    ConfiguredFeature saltBlockFeature = createOreFeature(ModBlocks.SALT_BLOCK, 60, 8, 40, 80);
    Biome[] oceanBiomes = new Biome[]{Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN,
        Biomes.DEEP_LUKEWARM_OCEAN,
        Biomes.DEEP_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN,
        Biomes.OCEAN, Biomes.BEACH};
    for (Biome b : oceanBiomes) {
      registerOreForBiome(b, saltBlockFeature);
    }

    ORES.forEach(ModWorldGen::registerOreForAllBiomes);
  }

  public static ConfiguredFeature<?> createOreFeature(Block oreBlock, int count, int size,
      int minHeight, int maxHeight) {
    return Biome.createDecoratedFeature(Feature.ORE,
        new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
            oreBlock.getDefaultState(), size), Placement.COUNT_RANGE,
        new CountRangeConfig(count, minHeight, 0, maxHeight));
  }

  private static void registerOreForBiome(Biome biome, ConfiguredFeature oreFeature) {
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, oreFeature);
  }

  private static void registerOreForAllBiomes(ConfiguredFeature oreFeature) {
    Biome.BIOMES.forEach(biome -> registerOreForBiome(biome, oreFeature));
  }

  public static void addOreToRegister(ConfiguredFeature<?> ore) {
    ORES.add(ore);
  }
}
