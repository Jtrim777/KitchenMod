package jake.kitchenmod.horticulture;

import jake.kitchenmod.items.IFertilizer;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFertilizable extends IGrowable {

  @Override
  default void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
    applyFertilizer(worldIn, pos, state, () -> 1f);
  }

  @Override
  default boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  void applyFertilizer(World worldIn, BlockPos pos, BlockState state, IFertilizer fertilizer);

  int getMinFertilizerGrowth();

  int getMaxFertilizerGrowth();

  float getFertilizeGrowthChance();

  default int getGrowthForFertilizer(IFertilizer fertilizer, Random rgen) {
    int range = getMaxFertilizerGrowth() - getMinFertilizerGrowth();
    int addition = rgen.nextInt(range);

    boolean doGrow =
        rgen.nextInt(Math.round(1f / (getFertilizeGrowthChance() * fertilizer.getFertilityLevel())))
            == 0;

    return doGrow ? getMinFertilizerGrowth() + addition : 0;
  }
}
