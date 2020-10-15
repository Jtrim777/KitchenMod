package jake.kitchenmod.util;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.fluid.ModFluids;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import jake.kitchenmod.util.ModColors.KMBlockColor.ColoringContext;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

/**
 * Class for all functionality relating to block/item coloring.
 */
public class ModColors {

  public static void registerItemColors() {
    KitchenMod.LOGGER.log("Registering tint colors for mod items", "ModColors");
    KitchenMod.LOGGER.startTask("registerItemColors");

    // Register tints for fluid containers
    ItemColors itemcolors = Minecraft.getInstance().getItemColors();
    ModFluids.registerColorsForContainers(itemcolors);

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getItemColors)
        .forEach(ic -> ic.register(itemcolors));

    KitchenMod.LOGGER.endTask("registerItemColors");
  }

  public static void registerBlockColors() {
    KitchenMod.LOGGER.log("Registering tint colors for mod blocks", "ModColors");

    BlockColors blockColors = Minecraft.getInstance().getBlockColors();

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getBlockColors)
        .forEach(bc -> bc.register(blockColors));
  }

  public static KMBlockColor blockColor(Block rb, int tint, Predicate<ColoringContext> conds) {
    return new KMBlockColor(rb, tint, conds);
  }

  public static class KMBlockColor {
    private Predicate<ColoringContext> conditions;
    private int tintColor;
    private Block relevantBlock;

    KMBlockColor(Block relevantBlock, int tintColor, Predicate<ColoringContext> conds) {
      this.conditions = conds;
      this.tintColor = tintColor;
      this.relevantBlock = relevantBlock;
    }

    public static class ColoringContext {
      public final BlockState state;
      public final IEnviromentBlockReader environment;
      public final BlockPos position;
      public final int layer;

      public ColoringContext(BlockState state, IEnviromentBlockReader environment,
          BlockPos position, int layer) {
        this.state = state;
        this.environment = environment;
        this.position = position;
        this.layer = layer;
      }
    }

    void register(BlockColors registry) {
      registry.register((state, env, pos, layer) ->
          conditions.test(new ColoringContext(state, env, pos, layer)) ? tintColor : -1,
          relevantBlock);
    }
  }

  public static class KMItemColor {
    private Predicate<ColoringContext> conditions;
    private int tintColor;
    private Item relevantItem;

    KMItemColor(Item ri, int tintColor, Predicate<ColoringContext> conds) {
      this.conditions = conds;
      this.tintColor = tintColor;
      this.relevantItem = ri;
    }

    public static class ColoringContext {
      public final ItemStack stack;
      public final int layer;

      public ColoringContext(ItemStack stack, int layer) {
        this.stack = stack;
        this.layer = layer;
      }
    }

    void register(ItemColors registry) {
      registry.register((stack, layer) ->
              conditions.test(new ColoringContext(stack, layer)) ? tintColor : -1,
          relevantItem);
    }
  }

}
