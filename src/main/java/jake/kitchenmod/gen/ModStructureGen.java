package jake.kitchenmod.gen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.gen.ModGen.LoadPhase;
import jake.kitchenmod.gen.village_util.ModJPR;
import jake.kitchenmod.util.ModHacker;
import jake.kitchenmod.util.ModLogger.LogLevel;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.RandomBlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = KitchenMod.MODID)
public class ModStructureGen {

  static {
    ModGen.registerFx(ModStructureGen::modifyVillagePools, LoadPhase.COMMON);
  }

  public static final ImmutableList<StructureProcessor> BUILDING_DECORATOR = ImmutableList.of(
      new RuleStructureProcessor(
          ImmutableList.of(
              new RuleEntry(
                  new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F),
                  AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))));


  private static void modifyVillagePools() {

    KitchenMod.LOGGER.log("Modifying village generation pools", "ModStructureGen");

    KitchenMod.LOGGER.log("Hacking fields...", 1, "ModStructureGen");

    try {
      ModHacker.updateClassConstant(JigsawManager.class, "field_214891_a", new ModJPR());
      KitchenMod.LOGGER.log("Success!", 1, "ModStructureGen");
    } catch (NoSuchFieldException | IllegalAccessException e) {
      KitchenMod.LOGGER.log("Failed!", 1, "ModStructureGen", LogLevel.WARNING);
      e.printStackTrace();
    }

    addToPool("village/plains/houses",
        poolEntry("plains/houses/plains_bakery", BUILDING_DECORATOR, 8),
        poolEntry("plains/houses/plains_large_farm_2", ImmutableList.of(), 10));

    addToPool("village/snowy/houses",
        poolEntry("snowy/houses/snowy_bakery", BUILDING_DECORATOR, 8));

    addToPool("village/taiga/houses",
        poolEntry("taiga/houses/taiga_bakery", BUILDING_DECORATOR, 8));

    addToPool("village/savanna/houses",
        poolEntry("savanna/houses/savanna_bakery", BUILDING_DECORATOR, 8));
  }

  /**
   * Generates an entry for a structure pool.
   *
   * @param location The resource location of the NBT file (implied prefix "kitchenmod:village/")
   * @param mods A list of structure processors for modifying the structure
   * @param poolWeight The number of times this entry will occur in the pool
   */
  public static PoolEntry poolEntry(String location,
      List<StructureProcessor> mods, int poolWeight) {
    return new PoolEntry(new SingleJigsawPiece("kitchenmod:village/" + location, mods),
        poolWeight);
  }

  private static void addToPool(String poolName, PoolEntry... entries) {
    ((ModJPR)JigsawManager.field_214891_a).registerOverride(poolName, (pool) ->
        pool.addAll(Arrays.asList(entries)));
  }

  private static void replacePool(String poolName, PoolEntry... entries) {
    ((ModJPR)JigsawManager.field_214891_a).registerOverride(poolName, (pool) -> {
      pool.clear();
      pool.addAll(Arrays.asList(entries));
    });
  }

  public static class PoolEntry extends Pair<JigsawPiece, Integer> {

    public PoolEntry(JigsawPiece first, Integer second) {
      super(first, second);
    }
  }
}
