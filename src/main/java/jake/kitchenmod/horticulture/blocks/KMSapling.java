package jake.kitchenmod.horticulture.blocks;

import jake.kitchenmod.horticulture.features.KMTreeFeature;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public class KMSapling extends KMPlant {

  public KMSapling(Properties props) {
    super(props);
  }

  @Override
  protected int getMaxAge() {
    return 3;
  }

  @Override
  protected boolean canPickFruit() {
    return false;
  }

  @Override
  protected ItemStack getHarvest(MethodMeta data) {
    return null;
  }

  @Override
  protected boolean canHarvest(MethodMeta data) {
    return false;
  }

  @Override
  protected int getPostHarvestAge(MethodMeta data) {
    return 0;
  }

  @Override
  protected void doGrow(MethodMeta data) {
    if (data.age < getMaxAge()) {
      setAge(data, data.age + 1);
    } else {
      getTree(data).spawn((World)data.world, data.pos, data.state, ((World)data.world).rand);
    }
  }

  @Override
  protected boolean harvest(MethodMeta data) {
    return false;
  }

  @Override
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
    MethodMeta data = meta(worldIn, pos);

    return data.age < getMaxAge() || ((World)worldIn).getLight(pos.up()) >= 9;
  }

  private Tree getTree(MethodMeta data) {
    return new Tree() {
      @Nullable
      @Override
      protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
        return new KMTreeFeature(data.gene);
      }
    };
  }
}
